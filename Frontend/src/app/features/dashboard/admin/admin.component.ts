import {
  ChangeDetectionStrategy, Component, OnInit,
  inject, signal
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { MateriaService } from '../../../core/services/materia.service';
import { UsuarioService } from '../../../core/services/usuario.service';
import { UsuarioMateria } from '../../../core/models/materia.model';
import { Usuario } from '../../../core/models/usuario.model';
import { AprobadasPipe, CursandoPipe, ReprobadasPipe } from './admin.pipes';

// Solo la pestaña de usuarios permanece
type TabAdmin = 'usuarios';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, AprobadasPipe, CursandoPipe, ReprobadasPipe],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.scss',
  changeDetection: ChangeDetectionStrategy.Default,
})
export class AdminComponent implements OnInit {
  private readonly matSvc = inject(MateriaService);
  private readonly usuSvc = inject(UsuarioService);

  readonly tabActiva = signal<TabAdmin>('usuarios');
  readonly cargando  = signal(false);
  readonly error     = signal('');
  readonly exito     = signal('');

  // ── Datos ──
  readonly usuarios = signal<Usuario[]>([]);

  // ── Expansión accordion ──
  readonly expandidoId        = signal<number | null>(null);
  readonly materiasExpandidas = signal<UsuarioMateria[]>([]);
  readonly cargandoMaterias   = signal(false);

  // ── Modal materias de un usuario ──
  readonly usuarioSeleccionado  = signal<Usuario | null>(null);
  readonly materiasDeUsuario    = signal<UsuarioMateria[]>([]);
  readonly cargandoMateriasUser = signal(false);
  readonly modalMateriasUser    = signal(false);

  // ── Modal confirmación ──
  readonly modalConfirmar = signal(false);
  readonly confirmTitulo  = signal('');
  readonly confirmMensaje = signal('');
  private pendingAction: (() => void) | null = null;

  ngOnInit(): void { this.cargarTodo(); }

  get usuariosOrdenados(): Usuario[] {
    return [...this.usuarios()].sort((a, b) => a.id - b.id);
  }

  cargarTodo(): void {
    this.cargando.set(true);
    this.usuSvc.obtenerTodos().subscribe({
      next: v  => { this.usuarios.set(v); this.cargando.set(false); },
      error: () => this.cargando.set(false),
    });
  }

  setTab(tab: TabAdmin): void {
    this.tabActiva.set(tab);
    this.expandidoId.set(null);
    this.materiasExpandidas.set([]);
  }

  // ── Accordion ──
  toggleMaterias(u: Usuario): void {
    if (this.expandidoId() === u.id) {
      this.expandidoId.set(null);
      this.materiasExpandidas.set([]);
      return;
    }
    this.expandidoId.set(u.id);
    this.materiasExpandidas.set([]);
    this.cargandoMaterias.set(true);
    this.matSvc.obtenerMisMateriasInscritas(u.id).subscribe({
      next: mats => { this.materiasExpandidas.set(mats); this.cargandoMaterias.set(false); },
      error: ()  => { this.cargandoMaterias.set(false); this.flashError('No se pudieron cargar las materias del usuario.'); },
    });
  }

  // ── Modal materias de usuario ──
  verMateriasDeUsuario(u: Usuario): void {
    this.usuarioSeleccionado.set(u);
    this.materiasDeUsuario.set([]);
    this.cargandoMateriasUser.set(true);
    this.modalMateriasUser.set(true);
    this.matSvc.obtenerMisMateriasInscritas(u.id).subscribe({
      next: v  => { this.materiasDeUsuario.set(v); this.cargandoMateriasUser.set(false); },
      error: () => { this.cargandoMateriasUser.set(false); this.flashError('No se pudieron cargar las materias de este usuario.'); },
    });
  }

  // ── Confirmación ──
  confirmar(titulo: string, mensaje: string, accion: () => void): void {
    this.confirmTitulo.set(titulo);
    this.confirmMensaje.set(mensaje);
    this.pendingAction = accion;
    this.modalConfirmar.set(true);
  }

  ejecutarConfirmacion(): void {
    this.pendingAction?.();
    this.pendingAction = null;
    this.modalConfirmar.set(false);
  }

  // ── Helpers de UI ──
  rolLabel(rol: string | undefined): string {
    return rol === 'ADMIN' ? 'Admin' : 'Estudiante';
  }

  estadoChipClass(estado: string): string {
    switch (estado?.toLowerCase()) {
      case 'aprobada':   return 'chip chip-success';
      case 'reprobada':  return 'chip chip-danger';
      case 'cursando':   return 'chip chip-info';
      default:           return 'chip';
    }
  }

  estadoClass(estado: string): string {
    switch (estado) {
      case 'APROBADO':  return 'chip--success';
      case 'REPROBADO': return 'chip--danger';
      case 'RETIRADO':  return 'chip--muted';
      default:          return 'chip--info';
    }
  }

  estadoLabel(estado: string): string {
    const map: Record<string, string> = {
      CURSANDO: 'Cursando', APROBADO: 'Aprobado',
      REPROBADO: 'Reprobado', RETIRADO: 'Retirado',
    };
    return map[estado] ?? estado;
  }

  private flash(msg: string): void {
    this.exito.set(msg); this.error.set('');
    setTimeout(() => this.exito.set(''), 3500);
  }

  private flashError(msg: string): void {
    this.error.set(msg); this.exito.set('');
    setTimeout(() => this.error.set(''), 5000);
  }
}
