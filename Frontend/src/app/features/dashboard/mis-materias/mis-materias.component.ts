import {
  ChangeDetectionStrategy, Component, HostListener, OnInit,
  inject, signal, computed
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { MateriaService } from '../../../core/services/materia.service';
import { ProgresoService } from '../../../core/services/progreso.service';
import {
  Materia, UsuarioMateria, Actividad, Calificacion,
  ActividadRequest, CalificacionRequest
} from '../../../core/models/materia.model';

type Vista = 'lista' | 'detalle';

@Component({
  selector: 'app-mis-materias',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './mis-materias.component.html',
  styleUrls: ['./mis-materias.component.scss'],
  changeDetection: ChangeDetectionStrategy.Default,
})
export class MisMateriasComponent implements OnInit {
  private readonly auth    = inject(AuthService);
  private readonly svc     = inject(MateriaService);
  private readonly progreso = inject(ProgresoService);
  private readonly fb      = inject(FormBuilder);

  readonly currentUser = this.auth.currentUser;

  // ── Estado ──
  vista    = signal<Vista>('lista');
  cargando = signal(false);
  error    = signal('');

  // ── Datos ──
  catalogo              = signal<Materia[]>([]);
  misInscripciones      = signal<UsuarioMateria[]>([]);
  inscripcionActiva     = signal<UsuarioMateria | null>(null);
  actividades           = signal<Actividad[]>([]);
  calificacionesPorActividad = signal<Record<number, Calificacion[]>>({});
  promediosActuales          = signal<Record<number, number | null>>({});

  // ── Modals ──
  mostrarModalInscribir    = signal(false);
  mostrarModalActividad    = signal(false);
  mostrarModalCalificacion = signal(false);
  mostrarModalConfirmar    = signal(false);
  mostrarModalCerrar       = signal(false);
  inscripcionACerrar       = signal<UsuarioMateria | null>(null);
  actividadSeleccionada    = signal<Actividad | null>(null);
  calificacionEditando     = signal<Calificacion | null>(null);
  semestreDropdownOpen     = signal(false);
  tipoDropdownOpen         = signal(false);
  actividadCalendarOpen    = signal(false);
  actividadCalendarMonth   = signal(this.startOfMonth(new Date()));
  readonly actividadCalendarDays = computed(() => {
    const month = this.actividadCalendarMonth();
    const year = month.getFullYear();
    const monthIndex = month.getMonth();
    const firstDay = new Date(year, monthIndex, 1);
    const lastDay = new Date(year, monthIndex + 1, 0);
    const leading = firstDay.getDay();
    const totalDays = lastDay.getDate();
    const currentValue = this.formActividad.controls.fechaEntrega.value || this.dateInputValue(new Date());
    const selected = this.parseDate(currentValue);
    const today = this.startOfDay(new Date());

    const days: Array<{
      date: Date | null;
      label: string;
      isSelected: boolean;
      isToday: boolean;
      isCurrentMonth: boolean;
    }> = [];

    for (let i = 0; i < leading; i += 1) {
      days.push({ date: null, label: '', isSelected: false, isToday: false, isCurrentMonth: false });
    }

    for (let day = 1; day <= totalDays; day += 1) {
      const date = new Date(year, monthIndex, day);
      const isSelected = this.sameDay(date, selected);
      const isToday = this.sameDay(date, today);
      days.push({
        date,
        label: String(day),
        isSelected,
        isToday,
        isCurrentMonth: true,
      });
    }

    while (days.length % 7 !== 0) {
      days.push({ date: null, label: '', isSelected: false, isToday: false, isCurrentMonth: false });
    }

    return days;
  });

  // ── Confirmacion ──
  confirmTitulo  = signal('');
  confirmMensaje = signal('');
  private pendingAction: (() => void) | null = null;

  confirmar(titulo: string, mensaje: string, accion: () => void): void {
    this.confirmTitulo.set(titulo);
    this.confirmMensaje.set(mensaje);
    this.pendingAction = accion;
    this.mostrarModalConfirmar.set(true);
  }

  ejecutarConfirmacion(): void {
    if (this.pendingAction) {
      this.pendingAction();
      this.pendingAction = null;
    }
    this.mostrarModalConfirmar.set(false);
  }

  toggleSemestreDropdown(): void {
    this.tipoDropdownOpen.set(false);
    this.actividadCalendarOpen.set(false);
    this.semestreDropdownOpen.update((open) => !open);
  }

  toggleTipoDropdown(): void {
    this.semestreDropdownOpen.set(false);
    this.actividadCalendarOpen.set(false);
    this.tipoDropdownOpen.update((open) => !open);
  }

  closeDropdowns(): void {
    this.semestreDropdownOpen.set(false);
    this.tipoDropdownOpen.set(false);
    this.actividadCalendarOpen.set(false);
  }

  selectSemestre(semestre: number): void {
    this.formInscribir.controls.semestre.setValue(semestre);
    this.formInscribir.controls.semestre.markAsTouched();
    this.semestreDropdownOpen.set(false);
  }

  selectTipo(tipo: string): void {
    this.formActividad.controls.tipo.setValue(tipo as any);
    this.formActividad.controls.tipo.markAsTouched();
    this.tipoDropdownOpen.set(false);
  }

  semestreSeleccionadoLabel(): string {
    const value = this.formInscribir.controls.semestre.value;
    return value ? `${value}°` : 'Selecciona semestre';
  }

  tipoSeleccionadoLabel(): string {
    const value = this.formActividad.controls.tipo.value;
    return value ? this.tipoLabel(value) : 'Selecciona tipo';
  }

  toggleActividadCalendar(): void {
    this.semestreDropdownOpen.set(false);
    this.tipoDropdownOpen.set(false);
    this.actividadCalendarOpen.update((open) => !open);
    const baseDate = this.formActividad.controls.fechaEntrega.value
      ? this.parseDate(this.formActividad.controls.fechaEntrega.value)
      : new Date();
    this.actividadCalendarMonth.set(this.startOfMonth(baseDate));
  }

  selectActividadDate(date: Date): void {
    this.formActividad.controls.fechaEntrega.setValue(this.dateInputValue(date));
    this.formActividad.controls.fechaEntrega.markAsTouched();
    this.actividadCalendarOpen.set(false);
  }

  actividadCalendarLabel(): string {
    return new Intl.DateTimeFormat('es-CO', { month: 'long', year: 'numeric' })
      .format(this.actividadCalendarMonth())
      .replace(/^./, (c) => c.toUpperCase());
  }

  actividadCalendarInputLabel(): string {
    const value = this.formActividad.controls.fechaEntrega.value || this.dateInputValue(new Date());
    return new Intl.DateTimeFormat('es-CO', { day: '2-digit', month: 'short', year: 'numeric' })
      .format(this.parseDate(value))
      .replace('.', '');
  }

  actividadCalendarNextMonth(): void {
    const current = this.actividadCalendarMonth();
    this.actividadCalendarMonth.set(new Date(current.getFullYear(), current.getMonth() + 1, 1));
  }

  actividadCalendarPrevMonth(): void {
    const current = this.actividadCalendarMonth();
    this.actividadCalendarMonth.set(new Date(current.getFullYear(), current.getMonth() - 1, 1));
  }

  @HostListener('document:click')
  onDocumentClick(): void {
    this.closeDropdowns();
  }

  // ── Formularios ──
  formInscribir = this.fb.nonNullable.group({
    // Datos de la nueva materia
    codigo:      ['', Validators.required],
    nombre:      ['', Validators.required],
    creditos:    [3,  [Validators.required, Validators.min(1), Validators.max(20)]],
    descripcion: [''],
    // Datos de inscripción
    semestre:    [1,  Validators.required],
    anio:        [new Date().getFullYear(), Validators.required],
    estado:      ['CURSANDO'],
  });

  formActividad = this.fb.nonNullable.group({
    titulo:       ['', Validators.required],
    tipo:         ['parcial', Validators.required],
    peso:         [20, [Validators.required, Validators.min(1), Validators.max(100)]],
    notaMaxima:   [5,  [Validators.required, Validators.min(0.1)]],
    fechaEntrega: [''],
  });

  formCalificacion = this.fb.nonNullable.group({
    nota:              [0, [Validators.required, Validators.min(0)]],
    retroalimentacion: [''],
  });

  // ── Computed ──
  readonly creditosTotales = computed(() =>
    this.misInscripciones().reduce((s, i) => s + (i.materia?.creditos ?? 0), 0)
  );

  readonly materiasActivas = computed(() =>
    this.misInscripciones().filter(i => i.estado === 'CURSANDO' || i.estado === 'activa').length
  );

  readonly promedioGeneral = computed(() => {
    const ap = this.misInscripciones().filter(i => i.notaFinal != null && i.notaFinal > 0);
    if (!ap.length) return null;
    return ap.reduce((s, i) => s + (i.notaFinal ?? 0), 0) / ap.length;
  });

  // ── Peso / nota de la materia activa ──
  readonly pesoAcumulado = computed(() =>
    this.actividades().reduce((s, a) => s + (a.peso ?? 0), 0)
  );

  readonly pesoRestante = computed(() => Math.max(0, 100 - this.pesoAcumulado()));

  readonly pesoCompleto = computed(() => this.pesoAcumulado() >= 100);

  readonly notaInscripcionActiva = computed(() =>
    this.calcularNotaActual(this.actividades(), this.calificacionesPorActividad())
  );

  // ── Nota maxima posible (con notas perfectas en lo que falta) ──
  readonly notaMaximaPosible = computed(() => {
    const acts = this.actividades();
    const cals = this.calificacionesPorActividad();
    if (!acts.length) return null;

    let notaPonderada = 0;
    for (const act of acts) {
      const cal = cals[act.id]?.[0];
      const nota = cal != null ? cal.nota : act.notaMaxima; // asume max en pendientes
      notaPonderada += (nota / act.notaMaxima) * act.peso;
    }
    return Math.round((notaPonderada / 100) * 5 * 100) / 100;
  });

  // ── Peso maximo permitido al crear nueva actividad ──
  pesoMaximoDisponible(): number {
    const actEdit = this.actividadSeleccionada();
    const pesoActual = actEdit ? actEdit.peso : 0;
    return this.pesoRestante() + pesoActual;
  }

  ngOnInit(): void { this.cargarDatos(); }

  cargarDatos(): void {
    const userId = this.currentUser?.id;
    if (!userId) { this.error.set('No hay usuario autenticado.'); return; }

    this.cargando.set(true);
    this.error.set('');

    this.svc.obtenerMaterias().subscribe({
      next: (mats) => {
        this.catalogo.set(Array.isArray(mats) ? mats : []);
        this.cargarInscripciones(userId);
      },
      error: () => this.cargarInscripciones(userId)
    });
  }

  cargarInscripciones(userId: number): void {
    this.svc.obtenerMisMateriasInscritas(userId).subscribe({
      next: (inscs) => {
        const lista = Array.isArray(inscs) ? inscs : [];
        const enriquecidas = lista.map(i => ({
          ...i,
          materiaId: Number(i.materiaId),
          materia: i.materia ?? this.catalogo().find(m => m.id === Number(i.materiaId))
        }));
        this.misInscripciones.set(enriquecidas);
        this.cargarPromediosActuales(enriquecidas);
        this.cargando.set(false);
      },
      error: () => {
        this.misInscripciones.set([]);
        this.cargando.set(false);
        this.error.set('No se pudieron cargar tus materias.');
      }
    });
  }

  // ── Inscribir ──
  abrirModalInscribir(): void {
    this.formInscribir.reset({
      codigo: '', nombre: '', creditos: 3, descripcion: '',
      semestre: 1, anio: new Date().getFullYear(), estado: 'CURSANDO',
    });
    this.mostrarModalInscribir.set(true);
  }

  inscribir(): void {
    if (this.formInscribir.invalid || !this.currentUser) return;
    const v = this.formInscribir.getRawValue();

    // Paso 1: crear la materia
    this.svc.crearMateria({
      codigo:      v.codigo.toUpperCase().trim(),
      nombre:      v.nombre.trim(),
      creditos:    Number(v.creditos),
      descripcion: v.descripcion.trim() || undefined,
    }).subscribe({
      next: (materia) => {
        // Paso 2: inscribir al usuario en la materia creada
        this.svc.inscribirMateria({
          usuarioId: this.currentUser!.id,
          materiaId: materia.id,
          semestre:  Number(v.semestre),
          anio:      Number(v.anio),
          estado:    v.estado || 'CURSANDO',
        }).subscribe({
          next: () => {
            this.mostrarModalInscribir.set(false);
            this.error.set('');
            this.cargarDatos();
            this.progreso.recalcularProgreso(this.currentUser!.id).subscribe();
          },
          error: (e) => {
            this.mostrarModalInscribir.set(false);
            this.error.set(e?.error?.message ?? 'Error al inscribir la materia');
            setTimeout(() => this.error.set(''), 4000);
          }
        });
      },
      error: (e) => {
        const msg = e?.error?.message ?? e?.message ?? '';
        this.error.set(msg.includes('unique') || msg.includes('Duplicate')
          ? `Ya existe una materia con el código ${v.codigo.toUpperCase()} — usa uno diferente`
          : msg || 'Error al crear la materia');
        setTimeout(() => this.error.set(''), 5000);
      }
    });
  }

  // ── Detalle ──
  verDetalle(insc: UsuarioMateria): void {
    this.inscripcionActiva.set(insc);
    this.actividades.set([]);
    this.calificacionesPorActividad.set({});
    this.vista.set('detalle');
    this.cargarActividades(insc.id);
  }

  volver(): void {
    this.vista.set('lista');
    this.inscripcionActiva.set(null);
    this.cargarPromediosActuales(this.misInscripciones());
  }

  cargarActividades(umId: number): void {
    this.svc.obtenerActividades(umId).subscribe({
      next: (acts) => {
        this.actividades.set(acts);
        acts.forEach(a => this.cargarCalificaciones(a.id));
      }
    });
  }

  cargarCalificaciones(actividadId: number): void {
    this.svc.obtenerCalificaciones(actividadId).subscribe({
      next: (cals) => this.calificacionesPorActividad.update(p => ({ ...p, [actividadId]: cals }))
    });
  }

  // ── Actividades CRUD ──
  abrirModalActividad(act?: Actividad): void {
    if (act) {
      this.formActividad.patchValue({
        titulo: act.titulo, tipo: act.tipo,
        peso: act.peso, notaMaxima: act.notaMaxima,
        fechaEntrega: act.fechaEntrega ?? '',
      });
      this.actividadSeleccionada.set(act);
    } else {
      // Peso por defecto = min(20, restante)
      const pesoDefault = Math.min(20, this.pesoRestante());
      this.formActividad.reset({ titulo: '', tipo: 'parcial', peso: pesoDefault, notaMaxima: 5, fechaEntrega: '' });
      this.actividadSeleccionada.set(null);
    }
    this.mostrarModalActividad.set(true);
  }

  guardarActividad(): void {
    if (this.formActividad.invalid || !this.inscripcionActiva()) return;
    const v = this.formActividad.getRawValue();

    // Validacion de peso en frontend
    const pesoMax = this.pesoMaximoDisponible();
    if (Number(v.peso) > pesoMax) {
      this.error.set(`El peso no puede superar ${pesoMax}%. Solo quedan ${this.pesoRestante()}% disponibles.`);
      setTimeout(() => this.error.set(''), 4000);
      return;
    }

    const payload: ActividadRequest = {
      usuarioMateriaId: this.inscripcionActiva()!.id,
      titulo: v.titulo, tipo: v.tipo,
      peso: Number(v.peso), notaMaxima: Number(v.notaMaxima),
      fechaEntrega: v.fechaEntrega || undefined,
    };
    const actEdit = this.actividadSeleccionada();
    const obs = actEdit
      ? this.svc.actualizarActividad(actEdit.id, payload)
      : this.svc.crearActividad(payload);

    obs.subscribe({
      next: () => {
        this.mostrarModalActividad.set(false);
        this.cargarActividades(this.inscripcionActiva()!.id);
      },
      error: (e) => this.error.set(e.message ?? 'Error al guardar actividad')
    });
  }

  eliminarActividad(id: number): void {
    this.confirmar(
      'Eliminar actividad',
      'Se eliminara la actividad y todas sus calificaciones. Esta accion no se puede deshacer.',
      () => this.svc.eliminarActividad(id).subscribe({
        next: () => this.cargarActividades(this.inscripcionActiva()!.id)
      })
    );
  }

  // ── Calificaciones CRUD ──
  abrirModalCalificacion(actividad: Actividad): void {
    const cal = this.calificacionesPorActividad()[actividad.id]?.[0];
    this.actividadSeleccionada.set(actividad);
    this.calificacionEditando.set(cal ?? null);
    this.formCalificacion.patchValue({ nota: cal?.nota ?? 0, retroalimentacion: cal?.retroalimentacion ?? '' });
    this.mostrarModalCalificacion.set(true);
  }

  guardarCalificacion(): void {
    if (this.formCalificacion.invalid || !this.actividadSeleccionada()) return;
    const v    = this.formCalificacion.getRawValue();
    const act  = this.actividadSeleccionada()!;
    const payload: CalificacionRequest = {
      actividadId: act.id,
      nota: Number(v.nota),
      retroalimentacion: v.retroalimentacion,
    };
    const calEdit = this.calificacionEditando();
    const obs = calEdit
      ? this.svc.actualizarCalificacion(calEdit.id, payload)
      : this.svc.crearCalificacion(payload);

    obs.subscribe({
      next: () => {
        this.mostrarModalCalificacion.set(false);
        this.cargarCalificaciones(act.id);
        this.sincronizarNotaFinal();
      },
      error: (e) => this.error.set(e.message ?? 'Error al guardar calificacion')
    });
  }

  sincronizarNotaFinal(): void {
    const nota = this.notaInscripcionActiva();
    const insc = this.inscripcionActiva();
    if (nota == null || !insc) return;
    this.svc.actualizarInscripcion(insc.id, {
      usuarioId: insc.usuarioId,
      materiaId: insc.materiaId,
      semestre:  Number(insc.semestre),
      anio:      insc.anio,
      notaFinal: Math.round(nota * 100) / 100,
    }).subscribe(() => {
      if (this.currentUser) this.progreso.recalcularProgreso(this.currentUser.id).subscribe();
      // Actualizar la inscripcion activa localmente para que la card se actualice
      this.misInscripciones.update(list =>
        list.map(i => i.id === insc.id ? { ...i, notaFinal: nota } : i)
      );
      this.promediosActuales.update(map => ({ ...map, [insc.id]: nota }));
    });
  }

  // ── Cerrar materia ──
  abrirModalCerrar(insc: UsuarioMateria): void {
    this.inscripcionACerrar.set(insc);
    this.mostrarModalCerrar.set(true);
  }

  cerrarMateria(nuevoEstado: 'APROBADA' | 'REPROBADA'): void {
    const insc = this.inscripcionACerrar();
    if (!insc) return;
    this.mostrarModalCerrar.set(false);
    this.svc.actualizarInscripcion(insc.id, {
      usuarioId: insc.usuarioId,
      materiaId: insc.materiaId,
      semestre:  Number(insc.semestre),
      anio:      insc.anio,
      estado:    nuevoEstado,
      notaFinal: insc.notaFinal ?? undefined,
    }).subscribe({
      next: () => {
        this.misInscripciones.update(list =>
          list.map(i => i.id === insc.id ? { ...i, estado: nuevoEstado } : i)
        );
        if (this.currentUser) {
          this.progreso.recalcularProgreso(this.currentUser.id).subscribe();
        }
      },
      error: (e) => this.error.set(e?.message ?? 'Error al actualizar el estado')
    });
  }

  esCursando(estado: string): boolean {
    return estado === 'CURSANDO' || estado === 'activa';
  }

  esCerrada(estado: string): boolean {
    return estado === 'APROBADA' || estado === 'REPROBADA';
  }

  retirarMateria(id: number): void {
    this.confirmar(
      'Retirar materia',
      'Se eliminara esta materia y todas sus actividades y calificaciones asociadas.',
      () => this.svc.eliminarInscripcion(id).subscribe({
        next: () => {
          this.cargarInscripciones(this.currentUser!.id);
          this.progreso.recalcularProgreso(this.currentUser!.id).subscribe();
        }
      })
    );
  }

  // ── Promedio actual para cards ──
  notaParaCard(insc: UsuarioMateria): number | null {
    if (this.esCerrada(insc.estado)) return insc.notaFinal ?? null;
    return this.promediosActuales()[insc.id] ?? null;
  }

  private calcularNotaActual(
    acts: Actividad[],
    cals: Record<number, Calificacion[]>
  ): number | null {
    if (!acts.length) return null;

    const pesoTotal = acts.reduce((s, a) => s + (a.peso ?? 0), 0);
    let pesoConNota = 0;
    let notaPonderada = 0;

    for (const act of acts) {
      const cal = cals[act.id]?.[0];
      if (cal != null) {
        notaPonderada += (cal.nota / act.notaMaxima) * act.peso;
        pesoConNota   += act.peso;
      }
    }

    if (!pesoConNota) return null;

    const notaSobreEvaluado = (notaPonderada / pesoConNota) * 5;
    const notaFinal = Math.round(notaSobreEvaluado * 100) / 100;

    if (pesoTotal >= 100 && pesoConNota >= 100) return notaFinal;
    return notaFinal;
  }

  private cargarPromediosActuales(inscs: UsuarioMateria[]): void {
    const activas = inscs.filter(i => this.esCursando(i.estado));
    this.promediosActuales.set({});
    if (!activas.length) {
      return;
    }

    activas.forEach(insc => {
      this.svc.obtenerActividades(insc.id).subscribe({
        next: (acts) => {
          if (!acts.length) {
            this.promediosActuales.update(map => ({ ...map, [insc.id]: null }));
            return;
          }

          const calRequests = acts.map(a => this.svc.obtenerCalificaciones(a.id));
          forkJoin(calRequests).subscribe({
            next: (calLists) => {
              const calMap: Record<number, Calificacion[]> = {};
              acts.forEach((a, idx) => { calMap[a.id] = calLists[idx] ?? []; });
              const nota = this.calcularNotaActual(acts, calMap);
              this.promediosActuales.update(map => ({ ...map, [insc.id]: nota }));
            },
            error: () => {
              this.promediosActuales.update(map => ({ ...map, [insc.id]: null }));
            }
          });
        },
        error: () => {
          this.promediosActuales.update(map => ({ ...map, [insc.id]: null }));
        }
      });
    });
  }

  // ── Helpers ──
  nombreMateria(materiaId: number): string {
    const insc = this.misInscripciones().find(i => i.materiaId === Number(materiaId));
    return insc?.materia?.nombre ?? this.catalogo().find(m => m.id === Number(materiaId))?.nombre ?? `Materia #${materiaId}`;
  }

  codigoMateria(materiaId: number): string {
    const insc = this.misInscripciones().find(i => i.materiaId === Number(materiaId));
    return insc?.materia?.codigo ?? this.catalogo().find(m => m.id === Number(materiaId))?.codigo ?? '';
  }

  tipoLabel(tipo: string): string {
    const map: Record<string, string> = {
      parcial: 'Parcial', quiz: 'Quiz', tarea: 'Tarea',
      proyecto: 'Proyecto', laboratorio: 'Lab', otro: 'Otro'
    };
    return map[tipo] ?? tipo;
  }

  estadoClass(estado: string): string {
    const map: Record<string, string> = {
      CURSANDO: 'chip-info',    activa:     'chip-info',
      APROBADA: 'chip-success', aprobada:   'chip-success',
      REPROBADA:'chip-danger',  reprobada:  'chip-danger',
      RETIRADA: 'chip-warning', retirada:   'chip-warning',
    };
    return map[estado] ?? 'chip';
  }

  notaColor(nota: number | null | undefined, max = 5): string {
    if (nota == null) return 'var(--text-2)';
    const pct = nota / max;
    if (pct >= 0.7) return 'var(--success)';
    if (pct >= 0.5) return 'var(--warning)';
    return 'var(--danger)';
  }

  private parseDate(dateValue: string): Date {
    return new Date(`${dateValue}T12:00:00`);
  }

  private startOfMonth(date: Date): Date {
    return new Date(date.getFullYear(), date.getMonth(), 1);
  }

  private startOfDay(date: Date): Date {
    const result = new Date(date);
    result.setHours(0, 0, 0, 0);
    return result;
  }

  private sameDay(a: Date, b: Date): boolean {
    return a.getFullYear() === b.getFullYear()
      && a.getMonth() === b.getMonth()
      && a.getDate() === b.getDate();
  }

  private dateInputValue(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  readonly tiposActividad = ['parcial','quiz','tarea','proyecto','laboratorio','otro'];
  readonly semestres      = [1,2,3,4,5,6,7,8,9,10];
}
