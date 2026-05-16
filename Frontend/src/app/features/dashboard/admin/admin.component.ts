import {
  ChangeDetectionStrategy, Component, OnInit,
  inject, signal, computed
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MateriaService } from '../../../core/services/materia.service';
import { UsuarioService } from '../../../core/services/usuario.service';
import { ProgresoService } from '../../../core/services/progreso.service';
import { Materia, MateriaRequest, UsuarioMateria } from '../../../core/models/materia.model';
import { Usuario } from '../../../core/models/usuario.model';
import { PrerrequisitoResponse } from '../../../core/models/progreso.model';

type TabAdmin = 'usuarios' | 'prerrequisitos';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.scss',
  changeDetection: ChangeDetectionStrategy.Default,
})
export class AdminComponent implements OnInit {
  private readonly matSvc  = inject(MateriaService);
  private readonly usuSvc  = inject(UsuarioService);
  private readonly progSvc = inject(ProgresoService);
  private readonly fb      = inject(FormBuilder);

  readonly tabActiva = signal<TabAdmin>('usuarios');
  readonly cargando  = signal(false);
  readonly error     = signal('');
  readonly exito     = signal('');

  // ── Datos ──
  readonly usuarios = signal<Usuario[]>([]);
  readonly materias = signal<Materia[]>([]);
  readonly prereqs  = signal<PrerrequisitoResponse[]>([]);

  // ── Expansión de materias por usuario (accordion inline) ──
  readonly expandidoId         = signal<number | null>(null);
  readonly materiasExpandidas  = signal<UsuarioMateria[]>([]);
  readonly cargandoMaterias    = signal(false);

  // ── Modals ──
  readonly modalMateria   = signal(false);
  readonly modalPrereq    = signal(false);
  readonly modalConfirmar = signal(false);

  // Dropdowns para el modal de prerrequisitos
  prereqMateriaAOpen = signal(false);
  prereqMateriaBOpen = signal(false);

  togglePrereqMateriaA(): void {
    this.prereqMateriaBOpen.set(false);
    this.prereqMateriaAOpen.update(o => !o);
  }

  togglePrereqMateriaB(): void {
    this.prereqMateriaAOpen.set(false);
    this.prereqMateriaBOpen.update(o => !o);
  }

  seleccionarMateriaA(id: number): void {
    this.formPrereq.controls.materiaId.setValue(id);
    this.formPrereq.controls.materiaId.markAsTouched();
    this.prereqMateriaAOpen.set(false);
  }

  seleccionarMateriaB(id: number): void {
    this.formPrereq.controls.materiaPrerrequisitId.setValue(id);
    this.formPrereq.controls.materiaPrerrequisitId.markAsTouched();
    this.prereqMateriaBOpen.set(false);
  }

  labelMateriaA(): string {
    const id = this.formPrereq.controls.materiaId.value;
    if (!id) return 'Selecciona la materia...';
    const m = this.materias().find(x => x.id === Number(id));
    return m ? `${m.codigo} — ${m.nombre}` : 'Selecciona la materia...';
  }

  labelMateriaB(): string {
    const id = this.formPrereq.controls.materiaPrerrequisitId.value;
    if (!id) return 'Selecciona el prerrequisito...';
    const m = this.materias().find(x => x.id === Number(id));
    return m ? `${m.codigo} — ${m.nombre}` : 'Selecciona el prerrequisito...';
  }

  readonly materiaEditando = signal<Materia | null>(null);
  private pendingAction: (() => void) | null = null;

  readonly confirmTitulo  = signal('');
  readonly confirmMensaje = signal('');

  // ── Formularios ──
  formMateria = this.fb.nonNullable.group({
    codigo:      ['', Validators.required],
    nombre:      ['', Validators.required],
    creditos:    [3, [Validators.required, Validators.min(1), Validators.max(20)]],
    descripcion: [''],
  });

  formPrereq = this.fb.nonNullable.group({
    materiaId:            [0, [Validators.required, Validators.min(1)]],
    materiaPrerrequisitId: [0, [Validators.required, Validators.min(1)]],
  });

  readonly tabs: { id: TabAdmin; label: string; icon: string }[] = [
    { id: 'usuarios',       label: 'Usuarios',      icon: 'bi bi-people' },
    { id: 'prerrequisitos', label: 'Prerrequisitos', icon: 'bi bi-diagram-3' },
  ];

  ngOnInit(): void { this.cargarTodo(); }

  setTab(tab: TabAdmin): void {
    this.tabActiva.set(tab);
    this.expandidoId.set(null);
    this.materiasExpandidas.set([]);
  }

  cargarTodo(): void {
    this.cargando.set(true);
    this.usuSvc.obtenerTodos().subscribe({ next: v => this.usuarios.set(v), error: () => {} });
    this.matSvc.obtenerMaterias().subscribe({
      next: mats => {
        this.materias.set(mats);
        // Cargar todos los prerrequisitos de cada materia
        const all: PrerrequisitoResponse[] = [];
        if (!mats.length) { this.prereqs.set([]); this.cargando.set(false); return; }
        let pending = mats.length;
        mats.forEach(m => {
          this.progSvc.obtenerPrerrequisitosMateria(m.id).subscribe({
            next: ps => { all.push(...ps); if (!--pending) { this.prereqs.set(all); this.cargando.set(false); } },
            error: ()  => { if (!--pending) { this.prereqs.set(all); this.cargando.set(false); } },
          });
        });
      },
      error: () => this.cargando.set(false),
    });
  }

  // ── Accordion: expandir/contraer materias de un usuario ──
  toggleMaterias(u: Usuario): void {
    if (this.expandidoId() === u.id) {
      // ya abierto → cerrar
      this.expandidoId.set(null);
      this.materiasExpandidas.set([]);
      return;
    }
    this.expandidoId.set(u.id);
    this.materiasExpandidas.set([]);
    this.cargandoMaterias.set(true);
    this.matSvc.obtenerMisMateriasInscritas(u.id).subscribe({
      next: mats => { this.materiasExpandidas.set(mats); this.cargandoMaterias.set(false); },
      error: () => { this.cargandoMaterias.set(false); this.flashError('No se pudieron cargar las materias del usuario.'); },
    });
  }

  estadoChipClass(estado: string): string {
    switch (estado?.toLowerCase()) {
      case 'aprobada':   return 'chip chip-success';
      case 'reprobada':  return 'chip chip-danger';
      case 'cursando':   return 'chip chip-info';
      default:           return 'chip';
    }
  }

  // ── Materias ──
  abrirModalMateria(m?: Materia): void {
    this.materiaEditando.set(m ?? null);
    this.formMateria.reset({
      codigo: m?.codigo ?? '', nombre: m?.nombre ?? '',
      creditos: m?.creditos ?? 3, descripcion: m?.descripcion ?? '',
    });
    this.modalMateria.set(true);
  }

  guardarMateria(): void {
    if (this.formMateria.invalid) return;
    const v = this.formMateria.getRawValue();
    const payload: MateriaRequest = {
      codigo: v.codigo.toUpperCase().trim(),
      nombre: v.nombre.trim(),
      creditos: Number(v.creditos),
      descripcion: v.descripcion.trim() || undefined,
    };
    const edit = this.materiaEditando();
    const obs  = edit ? this.matSvc.actualizarMateria(edit.id, payload) : this.matSvc.crearMateria(payload);
    obs.subscribe({
      next: () => { this.modalMateria.set(false); this.flash('Materia guardada.'); this.matSvc.obtenerMaterias().subscribe(v => this.materias.set(v)); },
      error: (e: Error) => this.flashError(e.message),
    });
  }

  confirmarEliminarMateria(m: Materia): void {
    this.confirmar(`Eliminar "${m.nombre}"`, 'Esta acción es permanente y puede afectar inscripciones existentes.', () => {
      this.matSvc.eliminarMateria(m.id).subscribe({
        next: () => { this.flash('Materia eliminada.'); this.materias.update(l => l.filter(x => x.id !== m.id)); },
        error: (e: Error) => this.flashError(e.message),
      });
    });
  }

  // ── Prerrequisitos ──
  abrirModalPrereq(): void {
    this.formPrereq.reset({ materiaId: 0, materiaPrerrequisitId: 0 });
    this.prereqMateriaAOpen.set(false);
    this.prereqMateriaBOpen.set(false);
    this.modalPrereq.set(true);
  }

  guardarPrereq(): void {
    if (this.formPrereq.invalid) return;
    const v = this.formPrereq.getRawValue();
    if (Number(v.materiaId) === Number(v.materiaPrerrequisitId)) {
      this.flashError('La materia y el prerrequisito no pueden ser la misma.');
      return;
    }
    this.progSvc.crearPrerrequisito({
      materiaId: Number(v.materiaId),
      materiaPrerrequisitId: Number(v.materiaPrerrequisitId),
    }).subscribe({
      next: pr => { this.modalPrereq.set(false); this.flash('Prerrequisito creado.'); this.prereqs.update(l => [...l, pr]); },
      error: (e: Error) => this.flashError(e.message),
    });
  }

  confirmarEliminarPrereq(pr: PrerrequisitoResponse): void {
    this.confirmar(
      'Eliminar prerrequisito',
      `¿Eliminar "${pr.materiaPrerrequisitNombre}" como requisito de "${pr.materiaNombre}"?`,
      () => {
        this.progSvc.eliminarPrerrequisito(pr.id).subscribe({
          next: () => { this.flash('Prerrequisito eliminado.'); this.prereqs.update(l => l.filter(x => x.id !== pr.id)); },
          error: (e: Error) => this.flashError(e.message),
        });
      }
    );
  }

  // ── Confirm helper ──
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

  rolLabel(rol: string | undefined): string {
    return rol === 'ADMIN' ? 'Admin' : 'Estudiante';
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
