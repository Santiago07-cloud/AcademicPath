import {
  ChangeDetectionStrategy, Component, OnInit,
  computed, effect, inject, signal,
} from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { AgendaService } from '../../core/services/agenda.service';
import { AcademicStatsService } from '../../core/services/academic-stats.service';
import { AgendaPrioridad, TareaAcademica } from '../../core/models/agenda.model';
import { Usuario } from '../../core/models/usuario.model';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { AgendaComponent } from './agenda/agenda.component';
import { MisMateriasComponent } from './mis-materias/mis-materias.component';
import { ProgresoComponent } from './progreso/progreso.component';
import { AdminComponent } from './admin/admin.component';
import { PerfilComponent } from './perfil/perfil.component';

type SectionId = 'resumen' | 'agenda' | 'materias' | 'progreso' | 'perfil' | 'admin';

interface MenuItem {
  id: SectionId;
  label: string;
  icon: string;
}

interface SummaryCard {
  label: string;
  value: string;
  detail: string;
  color: string;
  icon: string;
}

@Component({
  selector: 'app-dashboard',
  imports: [
    RouterLink,
    NavbarComponent,
    AgendaComponent,
    MisMateriasComponent,
    ProgresoComponent,
    PerfilComponent,
    AdminComponent,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardComponent implements OnInit {
  private readonly authService  = inject(AuthService);
  private readonly agendaService = inject(AgendaService);
  readonly statsSvc             = inject(AcademicStatsService);

  private readonly taskDateFormatter = new Intl.DateTimeFormat('es-CO', {
    day: 'numeric',
    month: 'short',
  });

  readonly currentUser = signal<Usuario | null>(this.authService.currentUser);

  readonly menuAbierto    = signal(false);
  readonly seccionActiva  = signal<SectionId>('resumen');
  readonly fechaActual    = new Intl.DateTimeFormat('es-CO', {
    weekday: 'long', day: 'numeric', month: 'long', year: 'numeric',
  }).format(new Date());

  readonly isAdmin = computed(() =>
    (this.currentUser()?.rol ?? '').toUpperCase() === 'ADMIN',
  );

  readonly menuItems = computed<readonly MenuItem[]>(() => {
    const base: MenuItem[] = [
      { id: 'resumen',  label: 'Resumen',  icon: 'bi bi-grid' },
      { id: 'agenda',   label: 'Agenda',   icon: 'bi bi-calendar2-week' },
      { id: 'materias', label: 'Materias', icon: 'bi bi-book' },
      { id: 'progreso', label: 'Progreso', icon: 'bi bi-graph-up-arrow' },
    ];
    if (this.isAdmin()) {
      base.push({ id: 'admin', label: 'Admin', icon: 'bi bi-shield-lock' });
    }
    return base;
  });

  // ── Stats centralizadas (Materias + Agenda) ─────────────────────────────────
  readonly stats = this.statsSvc.stats;
  readonly cargandoStats = this.statsSvc.cargando;

  // Shortcuts sobre el stats signal
  readonly inscripciones   = computed(() => this.stats().inscripciones);
  readonly pendientes      = computed(() => this.stats().pendientes);
  readonly vencidas        = computed(() => this.stats().vencidas);
  readonly proximaEntrega  = computed(() => this.stats().proximaEntrega);
  readonly promedioGeneral = computed(() => this.stats().promedioGeneral);
  readonly avance          = computed(() => this.stats().avancePorcentaje);
  readonly progreso        = computed(() => this.stats().progresoBackend);

  readonly tareasAgenda = computed<TareaAcademica[]>(() => this.stats().tareas);

  readonly promedioDisplay = computed(() => {
    const p = this.promedioGeneral();
    return p != null ? p.toFixed(2) : '—';
  });

  readonly avanceDisplay = computed(() => {
    if (!this.stats().totalMaterias) return '—';
    return `${this.avance()}%`;
  });

  readonly materiasActivas = computed(() =>
    this.inscripciones().filter(
      i => i.estado === 'CURSANDO' || i.estado === 'activa',
    ).length,
  );

  readonly summaryCards = computed<SummaryCard[]>(() => {
    const s          = this.stats();
    const promReal   = s.promedioGeneral;
    const nTotal     = s.totalMaterias;
    const nActivas   = this.materiasActivas();
    const nAprobadas = s.inscripciones.filter(i => i.estado === 'APROBADA').length;
    const conNota    = s.inscripciones.filter(i => (i.notaFinal ?? 0) > 0).length;
    const siguiente  = s.proximaEntrega;

    return [
      {
        label: 'Promedio actual',
        value: promReal != null ? promReal.toFixed(2) : '—',
        detail: promReal != null
          ? `Calculado sobre ${conNota} materia(s) con nota`
          : 'Aún no tienes notas registradas',
        color: '#e4f0f2', icon: 'bi bi-stars',
      },
      {
        label: 'Materias inscritas',
        value: `${nTotal}`,
        detail: nTotal
          ? `${nActivas} cursando · ${nAprobadas} aprobadas`
          : 'Agrega tu primera materia',
        color: '#aabbbf', icon: 'bi bi-book',
      },
      {
        label: 'Pendientes',
        value: `${s.pendientes}`,
        detail: s.pendientes
          ? `${s.vencidas} vencidas en la agenda`
          : 'Sin entregas registradas',
        color: '#77888c', icon: 'bi bi-bell',
      },
      {
        label: 'Próxima entrega',
        value: siguiente ? this.formatDeadline(siguiente.fechaLimite) : 'Sin tareas',
        detail: siguiente
          ? `${siguiente.materia} · ${siguiente.titulo}`
          : 'Crea tu primera tarea en la agenda',
        color: '#d9e7ea', icon: 'bi bi-calendar2-event',
      },
    ];
  });

  readonly pageTitle = computed(() => {
    const titles: Record<SectionId, string> = {
      resumen:  'Panel de control',
      agenda:   'Agenda estudiantil',
      materias: 'Materias',
      progreso: 'Progreso académico',
      perfil:   'Mi perfil',
      admin:    'Panel de administración',
    };
    return titles[this.seccionActiva()];
  });

  private readonly ensureSectionAllowed = effect(() => {
    if (this.seccionActiva() === 'admin' && !this.isAdmin()) {
      this.seccionActiva.set('resumen');
    }
  });

  readonly usuarioIniciales = computed(() => this.initials(this.currentUser()));

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(u => this.currentUser.set(u));
  }

  setSection(section: SectionId): void {
    this.seccionActiva.set(section);
    this.menuAbierto.set(false);
    // Refrescar stats al volver al resumen o al progreso
    if (section === 'resumen' || section === 'progreso') {
      this.statsSvc.refresh();
    }
  }

  toggleMenu():   void { this.menuAbierto.update(o => !o); }
  closeSidebar(): void { this.menuAbierto.set(false); }
  logout():       void { this.authService.logout(); }

  priorityLabel(priority: AgendaPrioridad): string {
    const labels: Record<AgendaPrioridad, string> = {
      alta: 'Alta prioridad', media: 'Prioridad media', baja: 'Prioridad baja',
    };
    return labels[priority];
  }

  priorityColor(priority: AgendaPrioridad): string {
    const colors: Record<AgendaPrioridad, string> = {
      alta: '#e4f0f2', media: '#aabbbf', baja: '#77888c',
    };
    return colors[priority];
  }

  formatDeadline(dateValue: string): string {
    const days = this.daysUntil(dateValue);
    if (days === 0) return 'hoy';
    if (days === 1) return 'mañana';
    if (days > 1 && days < 7) return `en ${days} días`;
    return this.taskDateFormatter.format(this.parseTaskDate(dateValue));
  }

  statusLabel(task: TareaAcademica): string {
    if (task.estado === 'completada') return 'Completada';
    if (this.isOverdue(task)) return 'Vencida';
    if (this.isDueToday(task.fechaLimite)) return 'Hoy';
    const days = this.daysUntil(task.fechaLimite);
    if (days === 1) return 'Mañana';
    if (days > 1 && days < 7) return `En ${days} días`;
    return this.taskDateFormatter.format(this.parseTaskDate(task.fechaLimite));
  }

  isOverdue(task: TareaAcademica): boolean {
    return task.estado !== 'completada' && this.toDayTime(task.fechaLimite) < this.todayStart();
  }

  isDueToday(dateValue: string): boolean {
    return this.toDayTime(dateValue) === this.todayStart();
  }

  daysUntil(dateValue: string): number {
    return Math.round((this.toDayTime(dateValue) - this.todayStart()) / 86_400_000);
  }

  initials(usuario: Usuario | null): string {
    if (!usuario) return 'AP';
    return `${usuario.nombres?.[0] ?? ''}${usuario.apellidos?.[0] ?? ''}`.trim().toUpperCase() || 'AP';
  }

  private parseTaskDate(dateValue: string): Date {
    return new Date(`${dateValue}T12:00:00`);
  }

  private toDayTime(dateValue: string): number {
    const d = this.parseTaskDate(dateValue);
    d.setHours(0, 0, 0, 0);
    return d.getTime();
  }

  private todayStart(): number {
    const t = new Date();
    t.setHours(0, 0, 0, 0);
    return t.getTime();
  }
}
