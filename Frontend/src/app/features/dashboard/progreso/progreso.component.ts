import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { AcademicStatsService } from '../../../core/services/academic-stats.service';

@Component({
  selector: 'app-progreso',
  imports: [],
  template: `
    <section class="surface-card progreso-panel">
      <div class="section-header">
        <div>
          <p class="section-kicker">Progreso</p>
          <h2 class="section-heading">Resumen académico</h2>
          <p class="section-copy">Avance de créditos, promedio y tareas pendientes calculados automáticamente.</p>
        </div>
        <span class="chip">{{ avance() }}%</span>
      </div>

      @if (stats().totalMaterias > 0 || stats().progresoBackend) {

        <!-- Tarjetas de métricas -->
        <div class="progress-summary">

          <article class="metric-card">
            <p class="metric-card__label"><i class="bi bi-stars"></i> Promedio general</p>
            <p class="metric-card__value">{{ promedioDisplay() }}</p>
            <p class="metric-card__detail">
              @if (stats().promedioGeneral != null) {
                Calculado sobre {{ conNota() }} materia(s) con nota registrada
              } @else {
                Aún no hay notas registradas en tus materias
              }
            </p>
          </article>

          <article class="metric-card">
            <p class="metric-card__label"><i class="bi bi-journals"></i> Créditos cursados</p>
            <p class="metric-card__value">
              {{ stats().creditosCursados }}<span style="font-size:.9rem;opacity:.6">/{{ stats().creditosTotales }}</span>
            </p>
            <p class="metric-card__detail">
              {{ avance() }}% de avance. {{ stats().creditosTotales - stats().creditosCursados }} créditos restantes.
            </p>
          </article>

          <article class="metric-card">
            <p class="metric-card__label"><i class="bi bi-bell"></i> Pendientes</p>
            <p class="metric-card__value">{{ stats().pendientes }}</p>
            <p class="metric-card__detail">
              @if (stats().vencidas > 0) {
                {{ stats().vencidas }} vencida(s) · {{ stats().entregasActivas.length }} activa(s)
              } @else if (stats().pendientes > 0) {
                {{ stats().pendientes }} tarea(s) próximas en tu agenda
              } @else {
                Sin tareas pendientes. ¡Todo al día!
              }
            </p>
          </article>

        </div>

        <!-- Barra de avance visual -->
        <div class="progress-track-wrap">
          <div class="progress-track">
            <div class="progress-fill" [style.width.%]="avance()"></div>
          </div>
          <span>{{ avance() }}% completado</span>
        </div>

      } @else {
        <div class="empty-state">
          <i class="bi bi-bar-chart-line"></i>
          <h3>No hay progreso disponible</h3>
          <p>Tu avance se mostrará aquí cuando empieces a registrar materias y notas.</p>
        </div>
      }

      <!-- Checklist de estado rápido -->
      <div class="suggestions-block">
        <div class="section-header section-header--compact">
          <div>
            <p class="section-kicker">Checklist</p>
            <h3 class="section-heading">Estado rápido</h3>
          </div>
          <span class="chip">{{ stats().pendientes }} pendientes</span>
        </div>

        <div class="suggestions-list">

          <!-- Agenda -->
          <article class="suggestion-item">
            <div>
              <h4>Agenda al día</h4>
              <p>
                @if (stats().pendientes) {
                  Tienes {{ stats().pendientes }} tarea(s) pendiente(s)
                  @if (stats().vencidas > 0) {
                    , de las cuales {{ stats().vencidas }} ya están vencidas.
                  } @else {
                    en tu agenda.
                  }
                } @else {
                  No hay tareas pendientes. ¡Todo al día!
                }
              </p>
            </div>
            <span class="chip"
              [class.status--success]="stats().pendientes === 0"
              [class.status--danger]="stats().vencidas > 0"
              [class.status--warning]="stats().pendientes > 0 && stats().vencidas === 0">
              {{ stats().vencidas > 0 ? 'Vencidas' : stats().pendientes === 0 ? 'Ok' : 'Revisar' }}
            </span>
          </article>

          <!-- Materias inscritas -->
          <article class="suggestion-item">
            <div>
              <h4>Materias inscritas</h4>
              <p>
                @if (stats().totalMaterias) {
                  {{ stats().totalMaterias }} materia(s) — {{ materiasActivas() }} cursando · {{ aprobadas() }} aprobadas
                } @else {
                  Aún no tienes materias inscritas.
                }
              </p>
            </div>
            <span class="chip"
              [class.status--success]="stats().totalMaterias > 0"
              [class.status--warning]="stats().totalMaterias === 0">
              {{ stats().totalMaterias > 0 ? 'Activo' : 'Pendiente' }}
            </span>
          </article>

          <!-- Promedio -->
          <article class="suggestion-item">
            <div>
              <h4>Rendimiento académico</h4>
              <p>
                @if (stats().promedioGeneral != null) {
                  Promedio de {{ promedioDisplay() }} sobre {{ conNota() }} materia(s) con nota.
                } @else {
                  Registra notas en tus materias para ver tu rendimiento.
                }
              </p>
            </div>
            <span class="chip"
              [class.status--success]="(stats().promedioGeneral ?? 0) >= 3.5"
              [class.status--warning]="(stats().promedioGeneral ?? 0) > 0 && (stats().promedioGeneral ?? 0) < 3.5"
              [class.status--muted]="stats().promedioGeneral == null">
              @if (stats().promedioGeneral != null) {
                {{ (stats().promedioGeneral ?? 0) >= 3.5 ? 'Bueno' : 'Mejorar' }}
              } @else {
                Sin datos
              }
            </span>
          </article>

          <!-- Próxima entrega -->
          @if (stats().proximaEntrega; as prox) {
            <article class="suggestion-item">
              <div>
                <h4>Próxima entrega</h4>
                <p>{{ prox.titulo }} — {{ prox.materia }} · vence {{ prox.fechaLimite }}</p>
              </div>
              <span class="chip status--warning">Pronto</span>
            </article>
          }

          <!-- Sincronización backend -->
          <article class="suggestion-item">
            <div>
              <h4>Última sincronización</h4>
              <p>
                @if (stats().progresoBackend?.fechaActualizacion) {
                  Actualizado: {{ stats().progresoBackend?.fechaActualizacion }}
                } @else {
                  Aún sin sincronización registrada con el servidor.
                }
              </p>
            </div>
            <span class="chip">Estado</span>
          </article>

        </div>
      </div>
    </section>
  `,
  styles: [`
    :host { display: block; }

    .progreso-panel { padding: 1.35rem; }

    .progress-summary {
      display: grid;
      grid-template-columns: repeat(3, minmax(0, 1fr));
      gap: 1rem;
      margin-bottom: 1rem;
    }

    .progress-track-wrap {
      display: flex;
      align-items: center;
      gap: .75rem;
      color: var(--muted);
      font-size: .86rem;
      margin-bottom: 1rem;
    }
    .progress-track-wrap .progress-track { flex: 1; }

    .suggestions-block { margin-top: 1.4rem; }
    .section-header--compact { margin-bottom: .9rem; }
    .suggestions-list { display: grid; gap: .8rem; }

    .suggestion-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 1rem;
      padding: 1rem 1.05rem;
      border-radius: 18px;
      border: 1px solid var(--border);
      background: rgba(255,255,255,.03);
    }
    .suggestion-item h4 { margin: 0 0 .2rem; font-size: .98rem; }
    .suggestion-item p  { margin: 0; color: var(--muted); font-size: .9rem; }

    .status--muted { opacity: .65; }

    .empty-state {
      display: grid;
      place-items: center;
      min-height: 12rem;
      padding: 1rem;
      color: var(--muted);
      text-align: center;
    }
    .empty-state i { font-size: 2rem; margin-bottom: .8rem; color: var(--accent-soft); }
    .empty-state h3 { margin: 0 0 .25rem; color: var(--text); }
    .empty-state p  { margin: 0; line-height: 1.6; }

    @media (max-width: 900px) {
      .progress-summary { grid-template-columns: 1fr; }
      .suggestion-item  { align-items: flex-start; flex-direction: column; }
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProgresoComponent {
  private readonly statsSvc = inject(AcademicStatsService);

  /** Stats centralizadas: se actualizan automáticamente desde Materias y Agenda */
  readonly stats = this.statsSvc.stats;

  readonly avance = computed(() => this.stats().avancePorcentaje);

  readonly promedioDisplay = computed(() => {
    const p = this.stats().promedioGeneral;
    return p != null ? p.toFixed(2) : '—';
  });

  /** Materias con nota registrada mayor a 0 */
  readonly conNota = computed(() =>
    this.stats().inscripciones.filter(i => (i.notaFinal ?? 0) > 0).length,
  );

  /** Materias en estado CURSANDO */
  readonly materiasActivas = computed(() =>
    this.stats().inscripciones.filter(
      i => i.estado === 'CURSANDO' || i.estado === 'activa',
    ).length,
  );

  /** Materias aprobadas */
  readonly aprobadas = computed(() =>
    this.stats().inscripciones.filter(
      i => i.estado === 'APROBADA' || i.estado === 'aprobada',
    ).length,
  );
}
