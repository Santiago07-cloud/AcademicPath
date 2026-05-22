import {
  ChangeDetectionStrategy, ChangeDetectorRef, Component,
  HostListener, OnInit, inject, signal, computed
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';
import { AuthService } from '../../../core/services/auth.service';
import { MateriaService } from '../../../core/services/materia.service';
import { ProgresoService } from '../../../core/services/progreso.service';
import { AcademicStatsService } from '../../../core/services/academic-stats.service';
import { DropdownPositionService, PanelPosition } from '../../../core/services/dropdown-position.service';
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
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MisMateriasComponent implements OnInit {
  private readonly auth       = inject(AuthService);
  private readonly svc        = inject(MateriaService);
  private readonly progreso   = inject(ProgresoService);
  private readonly statsSvc   = inject(AcademicStatsService);
  private readonly fb       = inject(FormBuilder);
  private readonly cdr      = inject(ChangeDetectorRef);
  private readonly dropPos  = inject(DropdownPositionService);

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

  // ── Modals ──
  mostrarModalInscribir    = signal(false);
  mostrarModalActividad    = signal(false);
  mostrarModalCalificacion = signal(false);
  mostrarModalConfirmar    = signal(false);
  mostrarModalCerrar       = signal(false);
  inscripcionACerrar       = signal<UsuarioMateria | null>(null);
  actividadSeleccionada    = signal<Actividad | null>(null);
  calificacionEditando     = signal<Calificacion | null>(null);

  // ── Dropdowns ──
  // Materia del catálogo — signal separado, NO form control
  materiaDropdownOpen   = signal(false);
  materiaIdSeleccionada = signal<number>(0);

  semestreDropdownOpen   = signal(false);
  tipoDropdownOpen       = signal(false);
  actividadCalendarOpen  = signal(false);
  actividadCalendarMonth = signal(this.startOfMonth(new Date()));

  // Posiciones calculadas para los panels con position:fixed
  semestrePanelPos = signal<PanelPosition | null>(null);
  tipoPanelPos     = signal<PanelPosition | null>(null);
  calendarPanelPos = signal<PanelPosition | null>(null);

  readonly actividadCalendarDays = computed(() => {
    const month    = this.actividadCalendarMonth();
    const year     = month.getFullYear();
    const mi       = month.getMonth();
    const leading  = new Date(year, mi, 1).getDay();
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

  @HostListener('document:click')
  onDocumentClick(): void { this.closeDropdowns(); }

  closeDropdowns(): void {
    this.materiaDropdownOpen.set(false);
    this.semestreDropdownOpen.set(false);
    this.tipoDropdownOpen.set(false);
    this.actividadCalendarOpen.set(false);
  }

  // ── Materia dropdown (catálogo) ──
  toggleMateriaDropdown(event: Event): void {
    event.stopPropagation();
    this.semestreDropdownOpen.set(false);
    this.tipoDropdownOpen.set(false);
    this.actividadCalendarOpen.set(false);
    this.materiaDropdownOpen.update(o => !o);
  }

  selectMateriaInscribir(id: number): void {
    this.materiaIdSeleccionada.set(id);
    this.materiaDropdownOpen.set(false);
  }

  materiaSeleccionadaLabel(): string {
    const id = this.materiaIdSeleccionada();
    if (!id) return 'Selecciona una materia...';
    const m = this.catalogo().find(x => x.id === id);
    return m ? `${m.codigo} — ${m.nombre}` : 'Selecciona una materia...';
  }

  // ── Semestre dropdown ──
  toggleSemestreDropdown(event: MouseEvent): void {
    event.stopPropagation();
    const trigger = event.currentTarget as HTMLElement;
    const open = !this.semestreDropdownOpen();
    this.closeDropdowns();
    if (open) {
      this.semestrePanelPos.set(this.dropPos.calcular(trigger, 280));
      this.semestreDropdownOpen.set(true);
    }
  }

  selectSemestre(s: number): void {
    this.formInscribir.controls.semestre.setValue(s);
    this.formInscribir.controls.semestre.markAsTouched();
    this.semestreDropdownOpen.set(false);
  }

  semestreSeleccionadoLabel(): string {
    const v = this.formInscribir.controls.semestre.value;
    return v ? `${v}°` : 'Semestre';
  }

  // ── Tipo dropdown ──
  toggleTipoDropdown(event: MouseEvent): void {
    event.stopPropagation();
    const trigger = event.currentTarget as HTMLElement;
    const open = !this.tipoDropdownOpen();
    this.closeDropdowns();
    if (open) {
      this.tipoPanelPos.set(this.dropPos.calcular(trigger, 260));
      this.tipoDropdownOpen.set(true);
    }
  }

  selectTipo(t: string): void {
    this.formActividad.controls.tipo.setValue(t as any);
    this.formActividad.controls.tipo.markAsTouched();
    this.tipoDropdownOpen.set(false);
  }

  tipoSeleccionadoLabel(): string {
    const v = this.formActividad.controls.tipo.value;
    return v ? this.tipoLabel(v) : 'Tipo';
  }

  // ── Calendario actividad ──
  toggleActividadCalendar(event: MouseEvent): void {
    event.stopPropagation();
    const trigger = event.currentTarget as HTMLElement;
    const open = !this.actividadCalendarOpen();
    this.closeDropdowns();
    if (open) {
      this.calendarPanelPos.set(this.dropPos.calcular(trigger, 320));
      this.actividadCalendarOpen.set(true);
      const base = this.formActividad.controls.fechaEntrega.value
        ? this.parseDate(this.formActividad.controls.fechaEntrega.value)
        : new Date();
      this.actividadCalendarMonth.set(this.startOfMonth(base));
    }
  }

  selectActividadDate(date: Date): void {
    this.formActividad.controls.fechaEntrega.setValue(this.dateInputValue(date));
    this.formActividad.controls.fechaEntrega.markAsTouched();
    this.actividadCalendarOpen.set(false);
  }

  actividadCalendarLabel(): string {
    return new Intl.DateTimeFormat('es-CO', { month: 'long', year: 'numeric' })
      .format(this.actividadCalendarMonth()).replace(/^./, c => c.toUpperCase());
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

  panelStyle(pos: PanelPosition | null): Record<string, string> {
    if (!pos) return {};
    return {
      left:  pos.left,
      width: pos.width,
      ...(pos.top    ? { top: pos.top }       : {}),
      ...(pos.bottom ? { bottom: pos.bottom } : {}),
    };
  }

  // ── Formularios ──
  formInscribir = this.fb.nonNullable.group({
    codigo:      ['', [Validators.required, Validators.minLength(2), Validators.maxLength(20)]],
    nombre:      ['', [Validators.required, Validators.minLength(3)]],
    creditos:    [3,  [Validators.required, Validators.min(1), Validators.max(20)]],
    descripcion: [''],
    semestre:    [1,  Validators.required],
    anio:        [
      new Date().getFullYear(),
      [Validators.required, Validators.min(new Date().getFullYear()), Validators.max(new Date().getFullYear() + 1)]
    ],
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
  readonly pesoAcumulado  = computed(() => this.actividades().reduce((s, a) => s + (a.peso ?? 0), 0));
  readonly pesoRestante   = computed(() => Math.max(0, 100 - this.pesoAcumulado()));
  readonly pesoCompleto   = computed(() => this.pesoAcumulado() >= 100);

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

  // avanceActual: usa avancePorcentaje del backend si existe, si no calcula local
  readonly avanceActual = computed(() => {
    const insc = this.inscripcionActiva();
    if (!insc) return this.pesoAcumulado();
    return insc.avancePorcentaje != null ? insc.avancePorcentaje : this.pesoAcumulado();
  });

  // Solo permite cerrar si peso == 100% y todas las actividades tienen nota
  readonly puedesCerrar = computed(() => {
    const acts = this.actividades();
    if (!acts.length) return false;
    if (this.pesoAcumulado() < 100) return false;
    const cals = this.calificacionesPorActividad();
    return acts.every(a => (cals[a.id]?.length ?? 0) > 0);
  });

  // Materia cerrada = solo lectura
  readonly esModoLectura = computed(() => {
    const insc = this.inscripcionActiva();
    return insc ? this.esCerrada(insc.estado) : false;
  });

  readonly notaInscripcionActiva = computed(() =>
    this.calcularNotaActual(this.actividades(), this.calificacionesPorActividad())
  );
  readonly notaMaximaPosible = computed(() => {
    const acts = this.actividades(); const cals = this.calificacionesPorActividad();
    if (!acts.length) return null;
    let np = 0;
    for (const act of acts) { const cal = cals[act.id]?.[0]; np += ((cal?.nota ?? act.notaMaxima) / act.notaMaxima) * act.peso; }
    return Math.round((np / 100) * 5 * 100) / 100;
  });

  pesoMaximoDisponible(): number {
    return this.pesoRestante() + (this.actividadSeleccionada()?.peso ?? 0);
  }

  // ── Lifecycle ──
  ngOnInit(): void { this.cargarDatos(); }

  cargarDatos(): void {
    const userId = this.currentUser?.id;
    if (!userId) { this.error.set('No hay usuario autenticado.'); return; }
    this.cargando.set(true);
    this.error.set('');
    forkJoin({
      catalogo:      this.svc.obtenerMaterias().pipe(catchError(() => of([]))),
      inscripciones: this.svc.obtenerMisMateriasInscritas(userId).pipe(catchError(() => of([]))),
    }).pipe(finalize(() => { this.cargando.set(false); this.cdr.markForCheck(); }))
    .subscribe({
      next: ({ catalogo, inscripciones }) => {
        const cats  = Array.isArray(catalogo) ? catalogo : [];
        const inscs = Array.isArray(inscripciones) ? inscripciones : [];
        this.catalogo.set(cats);
        this.misInscripciones.set(inscs.map(i => ({
          ...i, materiaId: Number(i.materiaId),
          materia: i.materia ?? cats.find(m => m.id === Number(i.materiaId)),
        })));
        this.cargarPromediosActuales(this.misInscripciones());
      },
      error: () => { this.error.set('Error al cargar datos. Intenta de nuevo.'); setTimeout(() => this.error.set(''), 5000); },
    });
  }

  // ── Inscribir (crea materia nueva y la inscribe) ──
  abrirModalInscribir(): void {
    this.formInscribir.reset({
      codigo: '', nombre: '', creditos: 3, descripcion: '',
      semestre: 1, anio: new Date().getFullYear(), estado: 'CURSANDO',
    });
    this.materiaIdSeleccionada.set(0);
    this.mostrarModalInscribir.set(true);
  }

  inscribir(): void {
    this.formInscribir.markAllAsTouched();
    if (this.formInscribir.invalid || !this.currentUser) return;
    const v = this.formInscribir.getRawValue();
    this.svc.crearMateria({
      codigo: v.codigo.toUpperCase().trim(),
      nombre: v.nombre.trim(),
      creditos: Number(v.creditos),
      descripcion: v.descripcion?.trim() || undefined,
    }).subscribe({
      next: materia => {
        this.svc.inscribirMateria({
          usuarioId: this.currentUser!.id, materiaId: materia.id,
          semestre: Number(v.semestre), anio: Number(v.anio), estado: 'CURSANDO',
        }).subscribe({
          next: () => {
            this.mostrarModalInscribir.set(false);
            this.error.set('');
            this.cargarDatos();
            this.progreso.recalcularProgreso(this.currentUser!.id).subscribe();
            this.statsSvc.refresh();
          },
          error: (e: any) => { this.error.set(e?.error?.message ?? 'Error al inscribir'); setTimeout(() => this.error.set(''), 4000); },
        });
      },
      error: (e: any) => {
        const msg = e?.error?.message ?? e?.message ?? '';
        this.error.set(
          msg.toLowerCase().includes('unique') || msg.toLowerCase().includes('duplicate')
            ? `El código "${v.codigo.toUpperCase()}" ya existe. Usa uno diferente.`
            : msg || 'Error al crear la materia'
        );
        setTimeout(() => this.error.set(''), 5000);
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
    this.svc.obtenerActividades(umId).pipe(catchError(() => of([]))).subscribe({
      next: (acts: Actividad[]) => {
        this.actividades.set(acts);
        if (!acts.length) { this.cdr.markForCheck(); return; }
        forkJoin(acts.map(a => this.svc.obtenerCalificaciones(a.id).pipe(catchError(() => of([]))))).subscribe({
          next: (calLists: Calificacion[][]) => {
            const calMap: Record<number, Calificacion[]> = {};
            acts.forEach((a, idx) => { calMap[a.id] = calLists[idx] ?? []; });
            this.calificacionesPorActividad.set(calMap);
            this.cdr.markForCheck();
          },
        });
      },
    });
  }

  cargarCalificaciones(actividadId: number): void {
    this.svc.obtenerCalificaciones(actividadId).pipe(catchError(() => of([]))).subscribe({
      next: (cals: Calificacion[]) => {
        this.calificacionesPorActividad.update(p => ({ ...p, [actividadId]: cals }));
        this.cdr.markForCheck();
      },
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
    const insc = this.inscripcionActiva()!;

    // Bloquear si la materia está cerrada
    if (this.esCerrada(insc.estado)) {
      this.error.set('No se pueden agregar actividades a una materia finalizada.');
      setTimeout(() => this.error.set(''), 3000);
      return;
    }
    const v = this.formActividad.getRawValue();
    if (Number(v.peso) > this.pesoMaximoDisponible()) {
      this.error.set(`El peso excede el disponible (${this.pesoRestante()}% libres).`);
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
    (actEdit ? this.svc.actualizarActividad(actEdit.id, payload) : this.svc.crearActividad(payload)).subscribe({
      next: (act) => {
        this.mostrarModalActividad.set(false);
        if (actEdit) { this.actividades.update(l => l.map(a => a.id === act.id ? act : a)); }
        else         { this.actividades.update(l => [...l, act]); }
        this.cdr.markForCheck();
      },
      error: (e: any) => this.error.set(e?.message ?? 'Error al guardar actividad'),
    });
  }

  eliminarActividad(id: number): void {
    this.confirmar('Eliminar actividad', 'Se eliminará la actividad y sus calificaciones.', () =>
      this.svc.eliminarActividad(id).subscribe({ next: () => this.cargarActividades(this.inscripcionActiva()!.id) })
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
    (calEdit ? this.svc.actualizarCalificacion(calEdit.id, payload) : this.svc.crearCalificacion(payload)).subscribe({
      next: (cal) => {
        this.mostrarModalCalificacion.set(false);
        this.calificacionesPorActividad.update(p => ({ ...p, [act.id]: [cal] }));
        this.cdr.markForCheck();
        this.sincronizarNotaFinal();
      },
      error: (e: any) => this.error.set(e?.message ?? 'Error al guardar calificación'),
    });
  }

  sincronizarNotaFinal(): void {
    const nota = this.notaInscripcionActiva();
    const insc = this.inscripcionActiva();
    if (!insc) return;
    const avance = this.pesoAcumulado();
    const todasCalificadas = this.actividades().every(
      a => (this.calificacionesPorActividad()[a.id]?.length ?? 0) > 0
    );
    const avanceFinal = (avance >= 100 && todasCalificadas) ? 100 : avance;

    // Actualizar avancePorcentaje en el objeto local para que la card reaccione
    this.misInscripciones.update(l =>
      l.map(i => i.id === insc.id ? { ...i, avancePorcentaje: avanceFinal } : i)
    );

    if (nota == null) return;
    this.svc.actualizarInscripcion(insc.id, {
      usuarioId: insc.usuarioId, materiaId: insc.materiaId,
      semestre: Number(insc.semestre), anio: insc.anio,
      notaFinal: Math.round(nota * 100) / 100,
    }).subscribe({
      next: () => {
        if (this.currentUser) this.progreso.recalcularProgreso(this.currentUser.id).subscribe();
        this.misInscripciones.update(l => l.map(i => i.id === insc.id ? { ...i, notaFinal: nota } : i));
        this.promediosActuales.update(m => ({ ...m, [insc.id]: nota }));
        this.statsSvc.refresh();
      },
    });
  }

  abrirModalCerrar(insc: UsuarioMateria): void { this.inscripcionACerrar.set(insc); this.mostrarModalCerrar.set(true); }

  cerrarMateria(nuevoEstado: 'APROBADA' | 'REPROBADA'): void {
    const insc = this.inscripcionACerrar();
    if (!insc) return;
    this.mostrarModalCerrar.set(false);

    // Usar la nota calculada del promedio actual, NO la nota guardada en el objeto
    // Esto evita el bug donde insc.notaFinal es null o 0 y se sobrescribe
    const notaCalculada = this.promediosActuales()[insc.id] ?? insc.notaFinal ?? null;

    this.svc.actualizarInscripcion(insc.id, {
      usuarioId:  insc.usuarioId,
      materiaId:  insc.materiaId,
      semestre:   Number(insc.semestre),
      anio:       insc.anio,
      estado:     nuevoEstado,
      // Solo enviar notaFinal si hay una nota real calculada
      ...(notaCalculada != null ? { notaFinal: Math.round(notaCalculada * 100) / 100 } : {}),
    }).subscribe({
      next: (updated) => {
        this.misInscripciones.update(l =>
          l.map(i => i.id === insc.id
            ? { ...i, estado: nuevoEstado, notaFinal: updated.notaFinal ?? notaCalculada }
            : i
          )
        );
        if (this.currentUser) this.progreso.recalcularProgreso(this.currentUser.id).subscribe();
        this.cdr.markForCheck();
      },
    });
  }

  retirarMateria(id: number): void {
    this.confirmar('Retirar materia', 'Se eliminará esta materia con todas sus actividades y calificaciones.', () =>
      this.svc.eliminarInscripcion(id).subscribe({
        next: () => { this.cargarDatos(); if (this.currentUser) this.progreso.recalcularProgreso(this.currentUser.id).subscribe(); this.statsSvc.refresh(); },
      })
    );
  }

  // ── Helpers ──
  /** Verifica si una materia específica puede cerrarse (para usar desde la card) */
  puedesCerrarDesdeCard(insc: UsuarioMateria): boolean {
    const acts = this.actividades();
    // Si estamos viendo el detalle de esta materia, usar los datos cargados
    if (this.inscripcionActiva()?.id === insc.id) {
      return this.puedesCerrar();
    }
    // Si no está cargada en detalle, habilitar el botón (el backend lo validará)
    return true;
  }
  esCursando(e: string): boolean { return e === 'CURSANDO' || e === 'activa'; }
  esCerrada(e: string): boolean  { return e === 'APROBADA' || e === 'REPROBADA'; }

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
    const m: Record<string, string> = {
      parcial: 'Parcial', quiz: 'Quiz', tarea: 'Tarea',
      proyecto: 'Proyecto', laboratorio: 'Lab', otro: 'Otro',
    };
    return m[tipo] ?? tipo;
  }

  estadoClass(e: string): string {
    const m: Record<string, string> = {
      CURSANDO: 'chip-info', activa: 'chip-info',
      APROBADA: 'chip-success', aprobada: 'chip-success',
      REPROBADA: 'chip-danger', reprobada: 'chip-danger',
      RETIRADA: 'chip-warning', retirada: 'chip-warning',
    };
    return m[e] ?? 'chip';
  }

  notaColor(nota: number | null | undefined, max = 5): string {
    if (nota == null) return 'var(--text-2)';
    const pct = nota / max;
    return pct >= 0.7 ? 'var(--success)' : pct >= 0.5 ? 'var(--warning)' : 'var(--danger)';
  }

  private calcularNotaActual(acts: Actividad[], cals: Record<number, Calificacion[]>): number | null {
    if (!acts.length) return null;
    let pesoConNota = 0, np = 0;
    for (const act of acts) {
      const cal = cals[act.id]?.[0];
      if (cal != null) { np += (cal.nota / act.notaMaxima) * act.peso; pesoConNota += act.peso; }
    }
    if (!pesoConNota) return null;
    return Math.round(((np / pesoConNota) * 5) * 100) / 100;
  }

  // FIX: paréntesis correctamente cerrados en forkJoin anidado
  private cargarPromediosActuales(inscs: UsuarioMateria[]): void {
    const activas = inscs.filter(i => this.esCursando(i.estado));
    this.promediosActuales.set({});
    if (!activas.length) return;

    forkJoin(activas.map(insc => this.svc.obtenerActividades(insc.id).pipe(catchError(() => of([]))))).subscribe({
      next: (actsPorMateria: Actividad[][]) => {
        const conActs = activas.filter((_, i) => actsPorMateria[i].length > 0);
        const actsArr = actsPorMateria.filter(a => a.length > 0);
        activas.forEach((insc, i) => {
          if (!actsPorMateria[i].length) this.promediosActuales.update(m => ({ ...m, [insc.id]: null }));
        });
        if (!conActs.length) return;

        // Paréntesis corregidos: forkJoin(...) cierra antes de .subscribe
        forkJoin(
          actsArr.map(acts =>
            forkJoin(acts.map(a => this.svc.obtenerCalificaciones(a.id).pipe(catchError(() => of([])))))
          )
        ).subscribe({
          next: (calsPorMateria: Calificacion[][][]) => {
            conActs.forEach((insc, mi) => {
              const acts    = actsArr[mi];
              const calLists = calsPorMateria[mi];
              const calMap: Record<number, Calificacion[]> = {};
              acts.forEach((a, ai) => { calMap[a.id] = calLists[ai] ?? []; });
              this.promediosActuales.update(m => ({ ...m, [insc.id]: this.calcularNotaActual(acts, calMap) }));
            });
            this.cdr.markForCheck();
          },
        });
      },
    });
  }

  private parseDate(v: string): Date { return new Date(`${v}T12:00:00`); }
  private startOfMonth(d: Date): Date { return new Date(d.getFullYear(), d.getMonth(), 1); }
  private startOfDay(d: Date): Date { const r = new Date(d); r.setHours(0, 0, 0, 0); return r; }
  private sameDay(a: Date, b: Date): boolean {
    return a.getFullYear() === b.getFullYear() && a.getMonth() === b.getMonth() && a.getDate() === b.getDate();
  }
  private dateInputValue(d: Date): string {
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
  }

  readonly tiposActividad = ['parcial', 'quiz', 'tarea', 'proyecto', 'laboratorio', 'otro'];
  readonly semestres      = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
}
