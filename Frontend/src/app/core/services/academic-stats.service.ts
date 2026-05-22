import { Injectable, inject, computed } from '@angular/core';
import { toSignal, toObservable } from '@angular/core/rxjs-interop';
import { BehaviorSubject, combineLatest, Observable, of } from 'rxjs';
import { map, switchMap, catchError, distinctUntilChanged } from 'rxjs/operators';

import { AuthService } from './auth.service';
import { AgendaService } from './agenda.service';
import { MateriaService } from './materia.service';
import { ProgresoService } from './progreso.service';
import { UsuarioMateria } from '../models/materia.model';
import { TareaAcademica } from '../models/agenda.model';
import { ProgresoAcademico } from '../models/progreso.model';

/** Snapshot completo de estadísticas académicas del usuario activo */
export interface AcademicStats {
  // ── Desde Materias ──────────────────────────────────────────────────────────
  inscripciones: UsuarioMateria[];
  /** Número total de materias inscritas */
  totalMaterias: number;
  /** Créditos sumados de todas las materias inscritas */
  creditosTotales: number;
  /** Créditos de materias en estado APROBADA */
  creditosCursados: number;
  /** Promedio de notas registradas (null si no hay ninguna) */
  promedioGeneral: number | null;
  /** Porcentaje de avance = (creditosCursados / creditosTotales) × 100 */
  avancePorcentaje: number;

  // ── Desde Agenda ────────────────────────────────────────────────────────────
  tareas: TareaAcademica[];
  /** Tareas cuyo estado es 'pendiente' */
  pendientes: number;
  /** Tareas 'pendiente' con fecha vencida */
  vencidas: number;
  /** Tarea pendiente con la fecha más próxima */
  proximaEntrega: TareaAcademica | null;
  /** Tareas pendientes no vencidas */
  entregasActivas: TareaAcademica[];

  // ── Desde Backend (ProgresoAcademico) ───────────────────────────────────────
  progresoBackend: ProgresoAcademico | null;
}

@Injectable({ providedIn: 'root' })
export class AcademicStatsService {
  private readonly authSvc     = inject(AuthService);
  private readonly agendaSvc   = inject(AgendaService);
  private readonly materiaSvc  = inject(MateriaService);
  private readonly progresoSvc = inject(ProgresoService);

  /**
   * Dispara manualmente una recarga de inscripciones/progreso desde el backend.
   * Llama a refresh() tras agregar, editar o eliminar una materia.
   */
  private readonly _refresh$ = new BehaviorSubject<void>(undefined);

  /**
   * Observable del signal de tareas de agenda convertido a Observable RxJS.
   * Cada vez que AgendaService muta su signal (addTask, toggleTask, removeTask),
   * este observable emite y provoca que stats$ recalcule automáticamente.
   */
  private readonly tareas$ = toObservable(this.agendaSvc.tareas);

  /** Observable principal que emite cada vez que cambia cualquier fuente */
  readonly stats$: Observable<AcademicStats> = combineLatest([
    this.authSvc.currentUser$,
    this._refresh$,
    // Escucha cambios de agenda en tiempo real mediante el signal convertido.
    // Usamos un hash de ids+estados para detectar cualquier mutación
    // (agregar, eliminar, completar/restaurar) sin llamar al backend.
    this.tareas$.pipe(
      map(tareas => tareas.map(t => `${t.id}:${t.estado}`).join(',') + tareas.length),
      distinctUntilChanged(),
    ),
  ]).pipe(
    switchMap(([user]) => {
      if (!user) return of(this._emptyStats());

      const hoy = this._todayStart();

      return combineLatest([
        this.materiaSvc.obtenerMisMateriasInscritas(user.id).pipe(catchError(() => of([]))),
        this.progresoSvc.obtenerProgreso(user.id).pipe(catchError(() => of(null))),
      ]).pipe(
        map(([inscripciones, progresoBackend]) => {
          const inscs = Array.isArray(inscripciones) ? inscripciones : [];

          // ── Cálculos desde Materias ──────────────────────────────────────
          const creditosTotales = inscs.reduce(
            (s, i) => s + (i.materia?.creditos ?? 0), 0,
          );
          const aprobadas = inscs.filter(
            i => i.estado === 'APROBADA' || i.estado === 'aprobada',
          );
          const creditosCursados = aprobadas.reduce(
            (s, i) => s + (i.materia?.creditos ?? 0), 0,
          );

          const conNota = inscs.filter(i => (i.notaFinal ?? 0) > 0);
          const promedioGeneral = conNota.length
            ? conNota.reduce((s, i) => s + (i.notaFinal ?? 0), 0) / conNota.length
            : null;

          const avancePorcentaje = creditosTotales > 0
            ? Math.round((creditosCursados / creditosTotales) * 100)
            : 0;

          // ── Cálculos desde Agenda (leídos directamente del signal) ───────
          const tareas         = this.agendaSvc.listByUser(user.id);
          const pendientesList = tareas.filter(t => t.estado === 'pendiente');
          const vencidas       = pendientesList.filter(
            t => this._toDayTime(t.fechaLimite) < hoy,
          ).length;
          const entregasActivas = pendientesList.filter(
            t => this._toDayTime(t.fechaLimite) >= hoy,
          );
          // Próxima entrega: pendiente con fecha más cercana (lista ya ordenada por fecha asc)
          const proximaEntrega = entregasActivas.length > 0
            ? entregasActivas[0]
            : (pendientesList.length > 0 ? pendientesList[0] : null);

          return {
            inscripciones: inscs,
            totalMaterias: inscs.length,
            creditosTotales,
            creditosCursados,
            promedioGeneral,
            avancePorcentaje,
            tareas,
            pendientes: pendientesList.length,
            vencidas,
            proximaEntrega,
            entregasActivas,
            progresoBackend,
          } satisfies AcademicStats;
        }),
      );
    }),
  );

  /** Signal derivada del observable (compatible con la API de Signals de Angular 17+) */
  readonly stats = toSignal(this.stats$, { initialValue: this._emptyStats() });

  // ── Signals derivadas (shortcuts para usar directamente en templates) ───────
  readonly inscripciones    = computed(() => this.stats().inscripciones);
  readonly totalMaterias    = computed(() => this.stats().totalMaterias);
  readonly creditosTotales  = computed(() => this.stats().creditosTotales);
  readonly creditosCursados = computed(() => this.stats().creditosCursados);
  readonly promedioGeneral  = computed(() => this.stats().promedioGeneral);
  readonly avancePorcentaje = computed(() => this.stats().avancePorcentaje);
  readonly tareas           = computed(() => this.stats().tareas);
  readonly pendientes       = computed(() => this.stats().pendientes);
  readonly vencidas         = computed(() => this.stats().vencidas);
  readonly proximaEntrega   = computed(() => this.stats().proximaEntrega);
  readonly entregasActivas  = computed(() => this.stats().entregasActivas);
  readonly progresoBackend  = computed(() => this.stats().progresoBackend);

  /**
   * Invalida caché de inscripciones/progreso y fuerza re-emisión del observable.
   * Llámalo después de cualquier mutación en Materias (inscribir, retirar, editar nota).
   * Para cambios de Agenda NO es necesario: el signal de AgendaService dispara automáticamente.
   */
  refresh(): void {
    this.materiaSvc.invalidarCaches();
    this._refresh$.next();
  }

  // ── Helpers privados ────────────────────────────────────────────────────────
  private _emptyStats(): AcademicStats {
    return {
      inscripciones: [],
      totalMaterias: 0,
      creditosTotales: 0,
      creditosCursados: 0,
      promedioGeneral: null,
      avancePorcentaje: 0,
      tareas: [],
      pendientes: 0,
      vencidas: 0,
      proximaEntrega: null,
      entregasActivas: [],
      progresoBackend: null,
    };
  }

  private _todayStart(): number {
    const d = new Date();
    d.setHours(0, 0, 0, 0);
    return d.getTime();
  }

  private _toDayTime(dateValue: string): number {
    const d = new Date(`${dateValue}T12:00:00`);
    d.setHours(0, 0, 0, 0);
    return d.getTime();
  }
}
