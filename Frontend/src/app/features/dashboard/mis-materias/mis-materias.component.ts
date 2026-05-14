import {
  ChangeDetectionStrategy, Component, HostListener, OnInit,
  inject, signal, computed
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';
import { AuthService } from '../../../core/services/auth.service';
import { MateriaService } from '../../../core/services/materia.service';
import { ProgresoService } from '../../../core/services/progreso.service';
import { ProfesorService } from '../../../core/services/profesor.service';
import {
  Materia, UsuarioMateria, Actividad, Calificacion,
  ActividadRequest, CalificacionRequest
} from '../../../core/models/materia.model';
import { Profesor } from '../../../core/models/profesor.model';

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
  private readonly auth     = inject(AuthService);
  private readonly svc      = inject(MateriaService);
  private readonly progreso = inject(ProgresoService);
  private readonly profSvc  = inject(ProfesorService);
  private readonly fb       = inject(FormBuilder);

  readonly currentUser = this.auth.currentUser;

  // ── Estado ──
  vista    = signal<Vista>('lista');
  cargando = signal(false);
  error    = signal('');

  // ── Datos ──
  catalogo                   = signal<Materia[]>([]);
  misInscripciones           = signal<UsuarioMateria[]>([]);
  inscripcionActiva          = signal<UsuarioMateria | null>(null);
  actividades                = signal<Actividad[]>([]);
  calificacionesPorActividad = signal<Record<number, Calificacion[]>>({});
  promediosActuales          = signal<Record<number, number | null>>({});
  profesores                 = signal<Profesor[]>([]);

  // ── Modals ──
  mostrarModalInscribir    = signal(false);
  mostrarModalActividad    = signal(false);
  mostrarModalCalificacion = signal(false);
  mostrarModalConfirmar    = signal(false);
  mostrarModalCerrar       = signal(false);
  inscripcionACerrar       = signal<UsuarioMateria | null>(null);
  actividadSeleccionada    = signal<Actividad | null>(null);
  calificacionEditando     = signal<Calificacion | null>(null);

  // Dropdowns
  materiaDropdownOpen    = signal(false);
  semestreDropdownOpen   = signal(false);
  tipoDropdownOpen       = signal(false);
  profesorDropdownOpen   = signal(false);
  actividadCalendarOpen  = signal(false);
  actividadCalendarMonth = signal(this.startOfMonth(new Date()));

  readonly actividadCalendarDays = computed(() => {
    const month = this.actividadCalendarMonth();
    const year  = month.getFullYear();
    const mi    = month.getMonth();
    const leading   = new Date(year, mi, 1).getDay();
    const totalDays = new Date(year, mi + 1, 0).getDate();
    const currentValue = this.formActividad.controls.fechaEntrega.value || this.dateInputValue(new Date());
    const selected = this.parseDate(currentValue);
    const today    = this.startOfDay(new Date());

    const days: Array<{ date: Date | null; label: string; isSelected: boolean; isToday: boolean }> = [];
    for (let i = 0; i < leading; i++) days.push({ date: null, label: '', isSelected: false, isToday: false });
    for (let d = 1; d <= totalDays; d++) {
      const date = new Date(year, mi, d);
      days.push({ date, label: String(d), isSelected: this.sameDay(date, selected), isToday: this.sameDay(date, today) });
    }
    while (days.length % 7 !== 0) days.push({ date: null, label: '', isSelected: false, isToday: false });
    return days;
  });

  // ── Confirmación ──
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
    this.pendingAction?.();
    this.pendingAction = null;
    this.mostrarModalConfirmar.set(false);
  }

  // ── Dropdown helpers ──
  @HostListener('document:click')
  onDocumentClick(): void { this.closeDropdowns(); }

  closeDropdowns(): void {
    this.materiaDropdownOpen.set(false);
    this.semestreDropdownOpen.set(false);
    this.tipoDropdownOpen.set(false);
    this.profesorDropdownOpen.set(false);
    this.actividadCalendarOpen.set(false);
  }

  toggleMateriaDropdown(): void {
    this.semestreDropdownOpen.set(false); this.tipoDropdownOpen.set(false);
    this.profesorDropdownOpen.set(false); this.actividadCalendarOpen.set(false);
    this.materiaDropdownOpen.update(o => !o);
  }

  selectMateriaInscribir(id: number): void {
    this.formInscribir.controls.materiaId.setValue(id);
    this.formInscribir.controls.materiaId.markAsTouched();
    this.materiaDropdownOpen.set(false);
  }

  materiaSeleccionadaLabel(): string {
    const id = this.formInscribir.controls.materiaId.value;
    if (!id) return 'Selecciona una materia...';
    const m = this.catalogo().find(x => x.id === Number(id));
    return m ? `${m.codigo} — ${m.nombre}` : 'Selecciona una materia...';
  }

  toggleSemestreDropdown(): void {
    this.materiaDropdownOpen.set(false); this.tipoDropdownOpen.set(false);
    this.profesorDropdownOpen.set(false); this.actividadCalendarOpen.set(false);
    this.semestreDropdownOpen.update(o => !o);
  }

  toggleTipoDropdown(): void {
    this.materiaDropdownOpen.set(false); this.semestreDropdownOpen.set(false);
    this.profesorDropdownOpen.set(false); this.actividadCalendarOpen.set(false);
    this.tipoDropdownOpen.update(o => !o);
  }

  toggleProfesorDropdown(): void {
    this.materiaDropdownOpen.set(false); this.semestreDropdownOpen.set(false);
    this.tipoDropdownOpen.set(false); this.actividadCalendarOpen.set(false);
    this.profesorDropdownOpen.update(o => !o);
  }

  selectSemestre(s: number): void {
    this.formInscribir.controls.semestre.setValue(s);
    this.formInscribir.controls.semestre.markAsTouched();
    this.semestreDropdownOpen.set(false);
  }

  selectTipo(t: string): void {
    this.formActividad.controls.tipo.setValue(t as any);
    this.formActividad.controls.tipo.markAsTouched();
    this.tipoDropdownOpen.set(false);
  }

  selectProfesor(id: number | null): void {
    this.formInscribir.controls.profesorId.setValue(id);
    this.profesorDropdownOpen.set(false);
  }

  semestreSeleccionadoLabel(): string {
    const v = this.formInscribir.controls.semestre.value;
    return v ? `${v}°` : 'Selecciona semestre';
  }

  tipoSeleccionadoLabel(): string {
    const v = this.formActividad.controls.tipo.value;
    return v ? this.tipoLabel(v) : 'Selecciona tipo';
  }

  profesorSeleccionadoLabel(): string {
    const id = this.formInscribir.controls.profesorId.value;
    if (!id) return 'Sin profesor (opcional)';
    return this.profesores().find(p => p.id === id)?.nombre ?? 'Sin profesor';
  }

  // ── Calendario de actividad ──
  toggleActividadCalendar(): void {
    this.semestreDropdownOpen.set(false); this.tipoDropdownOpen.set(false); this.profesorDropdownOpen.set(false);
    this.actividadCalendarOpen.update(o => !o);
    const base = this.formActividad.controls.fechaEntrega.value
      ? this.parseDate(this.formActividad.controls.fechaEntrega.value)
      : new Date();
    this.actividadCalendarMonth.set(this.startOfMonth(base));
  }

  selectActividadDate(date: Date): void {
    this.formActividad.controls.fechaEntrega.setValue(this.dateInputValue(date));
    this.formActividad.controls.fechaEntrega.markAsTouched();
    this.actividadCalendarOpen.set(false);
  }

  actividadCalendarLabel(): string {
    return new Intl.DateTimeFormat('es-CO', { month: 'long', year: 'numeric' })
      .format(this.actividadCalendarMonth())
      .replace(/^./, c => c.toUpperCase());
  }

  actividadCalendarInputLabel(): string {
    const value = this.formActividad.controls.fechaEntrega.value || this.dateInputValue(new Date());
    return new Intl.DateTimeFormat('es-CO', { day: '2-digit', month: 'short', year: 'numeric' })
      .format(this.parseDate(value)).replace('.', '');
  }

  actividadCalendarNextMonth(): void {
    const c = this.actividadCalendarMonth();
    this.actividadCalendarMonth.set(new Date(c.getFullYear(), c.getMonth() + 1, 1));
  }

  actividadCalendarPrevMonth(): void {
    const c = this.actividadCalendarMonth();
    this.actividadCalendarMonth.set(new Date(c.getFullYear(), c.getMonth() - 1, 1));
  }

  // ── Formularios ──
  formInscribir = this.fb.nonNullable.group({
    materiaId:   [0,  Validators.required],
    semestre:    [1,  Validators.required],
    anio:        [new Date().getFullYear(), Validators.required],
    estado:      ['CURSANDO'],
    profesorId:  [null as number | null],
  });

  readonly materiaIdSeleccionada = computed(() => (this.formInscribir.controls.materiaId.value ?? 0) > 0);

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

  readonly pesoAcumulado = computed(() =>
    this.actividades().reduce((s, a) => s + (a.peso ?? 0), 0)
  );

  readonly pesoRestante = computed(() => Math.max(0, 100 - this.pesoAcumulado()));
  readonly pesoCompleto = computed(() => this.pesoAcumulado() >= 100);

  readonly notaInscripcionActiva = computed(() =>
    this.calcularNotaActual(this.actividades(), this.calificacionesPorActividad())
  );

  readonly notaMaximaPosible = computed(() => {
    const acts = this.actividades();
    const cals = this.calificacionesPorActividad();
    if (!acts.length) return null;
    let notaPonderada = 0;
    for (const act of acts) {
      const cal = cals[act.id]?.[0];
      const nota = cal != null ? cal.nota : act.notaMaxima;
      notaPonderada += (nota / act.notaMaxima) * act.peso;
    }
    return Math.round((notaPonderada / 100) * 5 * 100) / 100;
  });

  pesoMaximoDisponible(): number {
    const actEdit = this.actividadSeleccionada();
    return this.pesoRestante() + (actEdit ? actEdit.peso : 0);
  }

  // ── Lifecycle ──
  ngOnInit(): void { this.cargarDatos(); }

  // ── Carga en paralelo con forkJoin ──
  cargarDatos(): void {
    const userId = this.currentUser?.id;
    if (!userId) { this.error.set('No hay usuario autenticado.'); return; }

    this.cargando.set(true);
    this.error.set('');

    forkJoin({
      catalogo:      this.svc.obtenerMaterias().pipe(catchError(() => of([]))),
      inscripciones: this.svc.obtenerMisMateriasInscritas(userId).pipe(catchError(() => of([]))),
      profesores:    this.profSvc.obtenerTodos().pipe(catchError(() => of([]))),
    }).pipe(
      finalize(() => this.cargando.set(false))
    ).subscribe({
      next: ({ catalogo, inscripciones, profesores }) => {
        const cats = Array.isArray(catalogo) ? catalogo : [];
        const inscs = Array.isArray(inscripciones) ? inscripciones : [];

        this.catalogo.set(cats);
        this.profesores.set(Array.isArray(profesores) ? profesores : []);

        const enriquecidas = inscs.map(i => ({
          ...i,
          materiaId: Number(i.materiaId),
          materia: i.materia ?? cats.find(m => m.id === Number(i.materiaId)),
        }));

        this.misInscripciones.set(enriquecidas);
        this.cargarPromediosActuales(enriquecidas);
      },
      error: () => {
        this.error.set('Error al cargar los datos. Intenta de nuevo.');
        setTimeout(() => this.error.set(''), 5000);
      },
    });
  }

  // ── Inscribir ──
  abrirModalInscribir(): void {
    this.formInscribir.reset({
      materiaId: 0, semestre: 1,
      anio: new Date().getFullYear(), estado: 'CURSANDO', profesorId: null,
    });
    this.mostrarModalInscribir.set(true);
  }

  inscribir(): void {
    if (this.formInscribir.invalid || !this.currentUser) return;
    const v = this.formInscribir.getRawValue();
    const materiaId = Number(v.materiaId);
    if (!materiaId) return;

    this.svc.inscribirMateria({
      usuarioId:  this.currentUser!.id,
      materiaId,
      profesorId: v.profesorId ?? null,
      semestre:   Number(v.semestre),
      anio:       Number(v.anio),
      estado:     v.estado || 'CURSANDO',
    }).subscribe({
      next: () => {
        this.mostrarModalInscribir.set(false);
        this.error.set('');
        this.cargarDatos();
        this.progreso.recalcularProgreso(this.currentUser!.id).subscribe();
      },
      error: (e: any) => {
        this.mostrarModalInscribir.set(false);
        const msg = e?.error?.message ?? e?.message ?? 'Error al inscribir la materia';
        this.error.set(msg);
        setTimeout(() => this.error.set(''), 4000);
      },
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
    this.svc.obtenerActividades(umId).pipe(
      catchError(() => of([]))
    ).subscribe({
      next: (acts: Actividad[]) => {
        this.actividades.set(acts);
        if (!acts.length) return;
        // Cargar todas las calificaciones en paralelo
        forkJoin(
          acts.map(a => this.svc.obtenerCalificaciones(a.id).pipe(catchError(() => of([]))))
        ).subscribe({
          next: (calLists: Calificacion[][]) => {
            const calMap: Record<number, Calificacion[]> = {};
            acts.forEach((a, idx) => { calMap[a.id] = calLists[idx] ?? []; });
            this.calificacionesPorActividad.set(calMap);
          },
        });
      },
    });
  }

  cargarCalificaciones(actividadId: number): void {
    this.svc.obtenerCalificaciones(actividadId).pipe(
      catchError(() => of([]))
    ).subscribe({
      next: (cals: Calificacion[]) =>
        this.calificacionesPorActividad.update(p => ({ ...p, [actividadId]: cals })),
    });
  }

  // ── Actividades CRUD ──
  abrirModalActividad(act?: Actividad): void {
    if (act) {
      this.formActividad.patchValue({ titulo: act.titulo, tipo: act.tipo, peso: act.peso, notaMaxima: act.notaMaxima, fechaEntrega: act.fechaEntrega ?? '' });
      this.actividadSeleccionada.set(act);
    } else {
      this.formActividad.reset({ titulo: '', tipo: 'parcial', peso: Math.min(20, this.pesoRestante()), notaMaxima: 5, fechaEntrega: '' });
      this.actividadSeleccionada.set(null);
    }
    this.mostrarModalActividad.set(true);
  }

  guardarActividad(): void {
    if (this.formActividad.invalid || !this.inscripcionActiva()) return;
    const v = this.formActividad.getRawValue();
    const pesoMax = this.pesoMaximoDisponible();
    if (Number(v.peso) > pesoMax) {
      this.error.set(`El peso no puede superar ${pesoMax}%. Quedan ${this.pesoRestante()}% disponibles.`);
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
    const obs = actEdit ? this.svc.actualizarActividad(actEdit.id, payload) : this.svc.crearActividad(payload);
    obs.subscribe({
      next: () => { this.mostrarModalActividad.set(false); this.cargarActividades(this.inscripcionActiva()!.id); },
      error: (e: any) => this.error.set(e?.message ?? 'Error al guardar actividad'),
    });
  }

  eliminarActividad(id: number): void {
    this.confirmar('Eliminar actividad', 'Se eliminará la actividad y todas sus calificaciones.', () =>
      this.svc.eliminarActividad(id).subscribe({
        next: () => this.cargarActividades(this.inscripcionActiva()!.id),
        error: (e: any) => this.error.set(e?.message ?? 'Error al eliminar actividad'),
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
    const v   = this.formCalificacion.getRawValue();
    const act = this.actividadSeleccionada()!;
    const payload: CalificacionRequest = { actividadId: act.id, nota: Number(v.nota), retroalimentacion: v.retroalimentacion };
    const calEdit = this.calificacionEditando();
    const obs = calEdit ? this.svc.actualizarCalificacion(calEdit.id, payload) : this.svc.crearCalificacion(payload);
    obs.subscribe({
      next: () => {
        this.mostrarModalCalificacion.set(false);
        this.cargarCalificaciones(act.id);
        this.sincronizarNotaFinal();
      },
      error: (e: any) => this.error.set(e?.message ?? 'Error al guardar calificación'),
    });
  }

  sincronizarNotaFinal(): void {
    const nota = this.notaInscripcionActiva();
    const insc = this.inscripcionActiva();
    if (nota == null || !insc) return;
    this.svc.actualizarInscripcion(insc.id, {
      usuarioId: insc.usuarioId, materiaId: insc.materiaId,
      semestre: Number(insc.semestre), anio: insc.anio,
      notaFinal: Math.round(nota * 100) / 100,
    }).subscribe({
      next: () => {
        if (this.currentUser) this.progreso.recalcularProgreso(this.currentUser.id).subscribe();
        this.misInscripciones.update(list => list.map(i => i.id === insc.id ? { ...i, notaFinal: nota } : i));
        this.promediosActuales.update(map => ({ ...map, [insc.id]: nota }));
      },
      error: () => {},
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
      usuarioId: insc.usuarioId, materiaId: insc.materiaId,
      semestre: Number(insc.semestre), anio: insc.anio,
      estado: nuevoEstado, notaFinal: insc.notaFinal ?? undefined,
    }).subscribe({
      next: () => {
        this.misInscripciones.update(list => list.map(i => i.id === insc.id ? { ...i, estado: nuevoEstado } : i));
        if (this.currentUser) this.progreso.recalcularProgreso(this.currentUser.id).subscribe();
      },
      error: (e: any) => this.error.set(e?.message ?? 'Error al actualizar el estado'),
    });
  }

  retirarMateria(id: number): void {
    this.confirmar('Retirar materia', 'Se eliminará esta materia y todas sus actividades y calificaciones.', () =>
      this.svc.eliminarInscripcion(id).subscribe({
        next: () => {
          this.cargarDatos();
          if (this.currentUser) this.progreso.recalcularProgreso(this.currentUser.id).subscribe();
        },
        error: (e: any) => this.error.set(e?.message ?? 'Error al retirar materia'),
      })
    );
  }

  // ── Helpers ──
  esCursando(estado: string): boolean { return estado === 'CURSANDO' || estado === 'activa'; }
  esCerrada(estado: string): boolean  { return estado === 'APROBADA' || estado === 'REPROBADA'; }

  notaParaCard(insc: UsuarioMateria): number | null {
    if (this.esCerrada(insc.estado)) return insc.notaFinal ?? null;
    return this.promediosActuales()[insc.id] ?? null;
  }

  nombreMateria(materiaId: number): string {
    const insc = this.misInscripciones().find(i => i.materiaId === Number(materiaId));
    return insc?.materia?.nombre ?? this.catalogo().find(m => m.id === Number(materiaId))?.nombre ?? `Materia #${materiaId}`;
  }

  codigoMateria(materiaId: number): string {
    const insc = this.misInscripciones().find(i => i.materiaId === Number(materiaId));
    return insc?.materia?.codigo ?? this.catalogo().find(m => m.id === Number(materiaId))?.codigo ?? '';
  }

  tipoLabel(tipo: string): string {
    const map: Record<string, string> = { parcial:'Parcial', quiz:'Quiz', tarea:'Tarea', proyecto:'Proyecto', laboratorio:'Lab', otro:'Otro' };
    return map[tipo] ?? tipo;
  }

  estadoClass(estado: string): string {
    const map: Record<string, string> = {
      CURSANDO:'chip-info', activa:'chip-info',
      APROBADA:'chip-success', aprobada:'chip-success',
      REPROBADA:'chip-danger', reprobada:'chip-danger',
      RETIRADA:'chip-warning', retirada:'chip-warning',
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

  private calcularNotaActual(acts: Actividad[], cals: Record<number, Calificacion[]>): number | null {
    if (!acts.length) return null;
    let pesoConNota = 0, notaPonderada = 0;
    for (const act of acts) {
      const cal = cals[act.id]?.[0];
      if (cal != null) { notaPonderada += (cal.nota / act.notaMaxima) * act.peso; pesoConNota += act.peso; }
    }
    if (!pesoConNota) return null;
    return Math.round(((notaPonderada / pesoConNota) * 5) * 100) / 100;
  }

  private cargarPromediosActuales(inscs: UsuarioMateria[]): void {
    const activas = inscs.filter(i => this.esCursando(i.estado));
    this.promediosActuales.set({});
    if (!activas.length) return;

    // Cargar actividades de todas las materias activas en paralelo
    forkJoin(
      activas.map(insc =>
        this.svc.obtenerActividades(insc.id).pipe(catchError(() => of([])))
      )
    ).subscribe({
      next: (actsPorMateria: Actividad[][]) => {
        const materiasConActividades = activas.filter((_, i) => actsPorMateria[i].length > 0);
        const actsContenido = actsPorMateria.filter(a => a.length > 0);

        // Marcar las que no tienen actividades
        activas.forEach((insc, i) => {
          if (!actsPorMateria[i].length) {
            this.promediosActuales.update(m => ({ ...m, [insc.id]: null }));
          }
        });

        if (!materiasConActividades.length) return;

        // Cargar todas las calificaciones en paralelo
        forkJoin(
          actsContenido.map(acts =>
            forkJoin(
              acts.map(a => this.svc.obtenerCalificaciones(a.id).pipe(catchError(() => of([]))))
            )
          )
        ).subscribe({
          next: (calsPorMateria: Calificacion[][][]) => {
            materiasConActividades.forEach((insc, mi) => {
              const acts = actsContenido[mi];
              const calLists = calsPorMateria[mi];
              const calMap: Record<number, Calificacion[]> = {};
              acts.forEach((a, ai) => { calMap[a.id] = calLists[ai] ?? []; });
              this.promediosActuales.update(m => ({
                ...m,
                [insc.id]: this.calcularNotaActual(acts, calMap),
              }));
            });
          },
        });
      },
    });
  }

  private parseDate(v: string): Date { return new Date(`${v}T12:00:00`); }
  private startOfMonth(d: Date): Date { return new Date(d.getFullYear(), d.getMonth(), 1); }
  private startOfDay(d: Date): Date { const r = new Date(d); r.setHours(0,0,0,0); return r; }
  private sameDay(a: Date, b: Date): boolean {
    return a.getFullYear() === b.getFullYear() && a.getMonth() === b.getMonth() && a.getDate() === b.getDate();
  }
  private dateInputValue(d: Date): string {
    return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`;
  }

  readonly tiposActividad = ['parcial','quiz','tarea','proyecto','laboratorio','otro'];
  readonly semestres      = [1,2,3,4,5,6,7,8,9,10];
}
