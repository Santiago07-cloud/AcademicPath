import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { ProgresoAcademico } from '../../../core/models/progreso.model';

@Component({
  selector: 'app-progreso',
  imports: [],
  template: `
    <section class="surface-card progreso-panel">
      <div class="section-header">
        <div>
          <p class="section-kicker">Progreso</p>
          <h2 class="section-heading">Resumen académico</h2>
          <p class="section-copy">Un vistazo al avance de créditos, promedio y sugerencias activas.</p>
        </div>

        <span class="chip">{{ avance() }}%</span>
      </div>

      @if (progreso(); as datos) {
        <div class="progress-summary">
          <article class="metric-card">
            <p class="metric-card__label">Promedio</p>
            <p class="metric-card__value">{{ datos.promedio.toFixed(1) }}</p>
            <p class="metric-card__detail">Meta sugerida: 4.5 para fortalecer la ruta académica.</p>
          </article>

          <article class="metric-card">
            <p class="metric-card__label">Créditos aprobados</p>
            <p class="metric-card__value">{{ datos.creditosAprobados }}/{{ datos.creditosTotales }}</p>
            <p class="metric-card__detail">{{ avance() }}% de avance sobre el plan total.</p>
          </article>

          <article class="metric-card">
            <p class="metric-card__label">Pendientes</p>
            <p class="metric-card__value">{{ pendientes() }}</p>
            <p class="metric-card__detail">Actividades por revisar o próximas a vencer.</p>
          </article>
        </div>

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
          <p>Tu avance se mostrará aquí cuando empieces a registrar tus datos académicos.</p>
        </div>
      }

      <div class="suggestions-block">
        <div class="section-header section-header--compact">
          <div>
            <p class="section-kicker">Checklist</p>
            <h3 class="section-heading">Estado rápido</h3>
          </div>
          <span class="chip">{{ pendientes() }} pendientes</span>
        </div>

        <div class="suggestions-list">
          <article class="suggestion-item">
            <div>
              <h4>Agenda al día</h4>
              <p>{{ pendientes() ? 'Tienes pendientes por revisar en tu agenda.' : 'No hay tareas pendientes.' }}</p>
            </div>
            <span class="chip" [class.status--success]="pendientes() === 0" [class.status--warning]="pendientes() > 0">
              {{ pendientes() === 0 ? 'Ok' : 'Revisar' }}
            </span>
          </article>

          <article class="suggestion-item">
            <div>
              <h4>Actualización</h4>
              <p>{{ progreso()?.fechaActualizacion ? ('Última actualización: ' + progreso()?.fechaActualizacion) : 'Aún sin actualización registrada.' }}</p>
            </div>
            <span class="chip">Estado</span>
          </article>
        </div>
      </div>
    </section>
  `,
  styles: [
    `
      :host {
        display: block;
      }

      .progreso-panel {
        padding: 1.35rem;
      }

      .progress-summary {
        display: grid;
        grid-template-columns: repeat(3, minmax(0, 1fr));
        gap: 1rem;
        margin-bottom: 1rem;
      }

      .progress-track-wrap {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        color: var(--muted);
        font-size: 0.86rem;
      }

      .progress-track-wrap .progress-track {
        flex: 1;
      }

      .suggestions-block {
        margin-top: 1.4rem;
      }

      .section-header--compact {
        margin-bottom: 0.9rem;
      }

      .suggestions-list {
        display: grid;
        gap: 0.8rem;
      }

      .suggestion-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 1rem;
        padding: 1rem 1.05rem;
        border-radius: 18px;
        border: 1px solid var(--border);
        background: rgba(255, 255, 255, 0.03);
      }

      .suggestion-item h4 {
        margin: 0 0 0.2rem;
        font-size: 0.98rem;
      }

      .suggestion-item p {
        margin: 0;
        color: var(--muted);
        font-size: 0.9rem;
      }

      .empty-state,
      .empty-mini {
        color: var(--muted);
        text-align: center;
      }

      .empty-state {
        display: grid;
        place-items: center;
        min-height: 12rem;
        padding: 1rem;
      }

      .empty-state i {
        font-size: 2rem;
        margin-bottom: 0.8rem;
        color: var(--accent-soft);
      }

      .empty-state h3 {
        margin: 0 0 0.25rem;
        color: var(--text);
      }

      .empty-state p,
      .empty-mini {
        margin: 0;
        line-height: 1.6;
      }

      @media (max-width: 900px) {
        .progress-summary {
          grid-template-columns: 1fr;
        }

        .suggestion-item {
          align-items: flex-start;
          flex-direction: column;
        }
      }
    `,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProgresoComponent {
  readonly progreso = input<ProgresoAcademico | null>(null);
  readonly pendientes = input(0);

  readonly avance = computed(() => {
    const datos = this.progreso();

    if (!datos || !datos.creditosTotales) {
      return 0;
    }

    return Math.round((datos.creditosAprobados / datos.creditosTotales) * 100);
  });
}