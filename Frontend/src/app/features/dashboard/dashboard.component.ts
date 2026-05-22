import { ChangeDetectionStrategy, Component, OnInit, computed, effect, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { AgendaService } from '../../core/services/agenda.service';
import { MateriaService } from '../../core/services/materia.service';
import { ProgresoService } from '../../core/services/progreso.service';
import { AgendaPrioridad, TareaAcademica } from '../../core/models/agenda.model';
import { UsuarioMateria } from '../../core/models/materia.model';
import { ProgresoAcademico } from '../../core/models/progreso.model';
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
  private readonly authService    = inject(AuthService);
  private readonly agendaService  = inject(AgendaService);
  private readonly materiaService = inject(MateriaService);
  private readonly progresoService = inject(ProgresoService);
  private readonly taskDateFormatter = new Intl.DateTimeFormat('es-CO', {
    day: 'numeric',
    month: 'short',
  });

  readonly currentUser = signal<Usuario | null>(this.authService.currentUser);

  readonly menuAbierto = signal(false);
  readonly seccionActiva = signal<SectionId>('resumen');
  readonly fechaActual = new Intl.DateTimeFormat('es-CO', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  }).format(new Date());

  readonly isAdmin = computed(() => (this.currentUser()?.rol ?? '').toUpperCase() === 'ADMIN');

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

  readonly progreso      = signal<ProgresoAcademico | null>(null);
  readonly inscripciones = signal<UsuarioMateria[]>([]);

  ngOnInit(): void {
    // Mantener el signal de usuario sincronizado con el observable
    this.authService.currentUser$.subscribe(u => this.currentUser.set(u));

    const user = this.authService.currentUser;
    if (!user) return;
    this.progresoService.obtenerProgreso(user.id).subscribe({
      next: (p: ProgresoAcademico) => this.progreso.set(p),
      error: () => this.progreso.set(null),
    });
    this.materiaService.obtenerMisMateriasInscritas(user.id).subscribe({
      next: (list: UsuarioMateria[]) => this.inscripciones.set(Array.isArray(list) ? list : []),
      error: () => {},
    });
  }

  readonly tareasAgenda = computed<TareaAcademica[]>(() => {
    const usuario = this.currentUser();

    return usuario ? this.agendaService.listByUser(usuario.id) : [];
  });

  readonly resumenAgenda = computed(() => {
    const usuario = this.currentUser();

    return usuario
      ? this.agendaService.getResumen(usuario.id)
      : {
          total: 0,
          pendientes: 0,
          completadas: 0,
          vencidas: 0,
        };
  });

  readonly pendientes = computed(() => this.resumenAgenda().pendientes);

  readonly vencidas = computed(() => this.resumenAgenda().vencidas);

  readonly proximaEntrega = computed(() => this.tareasAgenda().find((tarea) => tarea.estado === 'pendiente') ?? null);

  // Calcula promedio real desde inscripciones con nota registrada
  readonly promedioGeneral = computed(() => {
    const con = this.inscripciones().filter(i => i.notaFinal != null && i.notaFinal > 0);
    if (!con.length) return null;
    return con.reduce((s, i) => s + (i.notaFinal ?? 0), 0) / con.length;
  });

  // Materias cursando activamente
  readonly materiasActivas = computed(() =>
    this.inscripciones().filter(i => i.estado === 'CURSANDO' || i.estado === 'activa').length
  );

  // Avance = materias APROBADAS / total inscripciones × 100
  readonly avance = computed(() => {
    const total = this.inscripciones().length;
    if (!total) return 0;
    const aprobadas = this.inscripciones().filter(i => i.estado === 'APROBADA').length;
    return Math.round((aprobadas / total) * 100);
  });

  // Siempre muestra el promedio real desde inscripciones (nunca 0.0 falso)
  readonly promedioDisplay = computed(() => {
    const prom = this.promedioGeneral();
    return prom != null ? prom.toFixed(2) : '—';
  });

  readonly avanceDisplay = computed(() => {
    const total = this.inscripciones().length;
    if (!total) return '—';
    return `${this.avance()}%`;
  });

  readonly summaryCards = computed<SummaryCard[]>(() => {
    const siguiente = this.proximaEntrega();
    const promReal  = this.promedioGeneral();
    const nTotal    = this.inscripciones().length;
    const nActivas  = this.materiasActivas();
    const nAprobadas = this.inscripciones().filter(i => i.estado === 'APROBADA').length;

    return [
      {
        label: 'Promedio actual',
        value: promReal != null ? promReal.toFixed(2) : '—',
        detail: promReal != null
          ? `Calculado sobre ${this.inscripciones().filter(i => (i.notaFinal ?? 0) > 0).length} materia(s) con nota`
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
        value: `${this.pendientes()}`,
        detail: this.pendientes()
          ? `${this.vencidas()} vencidas en la agenda`
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
      resumen: 'Panel de control',
      agenda: 'Agenda estudiantil',
      materias: 'Materias',
      progreso: 'Progreso académico',
      perfil: 'Mi perfil',
      admin: 'Panel de administración',
    };

    return titles[this.seccionActiva()];
  });

  private readonly ensureSectionAllowed = effect(() => {
    if (this.seccionActiva() === 'admin' && !this.isAdmin()) {
      this.seccionActiva.set('resumen');
    }
  });

  readonly usuarioIniciales = computed(() => this.initials(this.currentUser()));

  setSection(section: SectionId): void {
    this.seccionActiva.set(section);
    this.menuAbierto.set(false);
  }

  toggleMenu(): void {
    this.menuAbierto.update((open) => !open);
  }

  closeSidebar(): void {
    this.menuAbierto.set(false);
  }

  logout(): void {
    this.authService.logout();
  }

  priorityLabel(priority: AgendaPrioridad): string {
    const labels: Record<AgendaPrioridad, string> = {
      alta: 'Alta prioridad',
      media: 'Prioridad media',
      baja: 'Prioridad baja',
    };

    return labels[priority];
  }

  priorityColor(priority: AgendaPrioridad): string {
    const colors: Record<AgendaPrioridad, string> = {
      alta: '#e4f0f2',
      media: '#aabbbf',
      baja: '#77888c',
    };

    return colors[priority];
  }

  formatDeadline(dateValue: string): string {
    const days = this.daysUntil(dateValue);

    if (days === 0) {
      return 'hoy';
    }

    if (days === 1) {
      return 'mañana';
    }

    if (days > 1 && days < 7) {
      return `en ${days} días`;
    }

    return this.taskDateFormatter.format(this.parseTaskDate(dateValue));
  }

  statusLabel(task: TareaAcademica): string {
    if (task.estado === 'completada') {
      return 'Completada';
    }

    if (this.isOverdue(task)) {
      return 'Vencida';
    }

    if (this.isDueToday(task.fechaLimite)) {
      return 'Hoy';
    }

    const days = this.daysUntil(task.fechaLimite);

    if (days === 1) {
      return 'Mañana';
    }

    if (days > 1 && days < 7) {
      return `En ${days} días`;
    }

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
    if (!usuario) {
      return 'AP';
    }

    return `${usuario.nombres?.[0] ?? ''}${usuario.apellidos?.[0] ?? ''}`.trim().toUpperCase() || 'AP';
  }

  private parseTaskDate(dateValue: string): Date {
    return new Date(`${dateValue}T12:00:00`);
  }

  private toDayTime(dateValue: string): number {
    const date = this.parseTaskDate(dateValue);
    date.setHours(0, 0, 0, 0);
    return date.getTime();
  }

  private todayStart(): number {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return today.getTime();
  }
}