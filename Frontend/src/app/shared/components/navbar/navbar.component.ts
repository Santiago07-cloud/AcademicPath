import { ChangeDetectionStrategy, Component, computed, input, output } from '@angular/core';
import { Usuario } from '../../../core/models/usuario.model';

@Component({
  selector: 'app-navbar',
  imports: [],
  template: `
    <header class="navbar-shell">
      <div class="navbar-shell__brand">
        <button
          type="button"
          class="icon-button navbar-shell__menu"
          [attr.aria-expanded]="menuAbierto()"
          aria-label="Abrir o cerrar menú"
          (click)="toggleMenu.emit()"
        >
          <i class="bi bi-list"></i>
        </button>

        <div>
          <p class="navbar-shell__eyebrow">Academic Path</p>
          <h1>{{ titulo() }}</h1>
        </div>
      </div>

      <div class="navbar-shell__actions">
        <span class="navbar-shell__date">{{ fecha() }}</span>

        @if (usuario(); as currentUser) {
          <div class="navbar-shell__user">
            <span class="navbar-shell__avatar">{{ iniciales() }}</span>
            <div>
              <strong>{{ currentUser.nombres }}</strong>
              <span>{{ currentUser.carrera || 'Panel académico' }}</span>
            </div>
          </div>
        }

        <button type="button" class="ghost-button" (click)="cerrarSesion.emit()">Cerrar sesión</button>
      </div>
    </header>
  `,
  styles: [
    `
      :host {
        display: block;
        position: sticky;
        top: 0;
        z-index: 40;
      }

      .navbar-shell {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 1rem;
        padding: 1.05rem 1.5rem;
        background: rgba(10, 12, 22, 0.82);
        backdrop-filter: blur(24px);
        border-bottom: 1px solid var(--border);
      }

      .navbar-shell__brand {
        display: flex;
        align-items: center;
        gap: 0.95rem;
        min-width: 0;
      }

      .navbar-shell__brand h1 {
        margin: 0;
        font-family: 'Syne', 'DM Sans', sans-serif;
        font-size: 1.25rem;
        font-weight: 800;
        letter-spacing: -0.05em;
      }

      .navbar-shell__eyebrow {
        margin: 0 0 0.25rem;
        color: var(--muted);
        text-transform: uppercase;
        letter-spacing: 0.2em;
        font-size: 0.7rem;
        font-weight: 800;
      }

      .navbar-shell__actions {
        display: flex;
        align-items: center;
        justify-content: flex-end;
        gap: 0.85rem;
        flex-wrap: wrap;
      }

      .navbar-shell__date {
        color: var(--muted);
        font-size: 0.9rem;
        white-space: nowrap;
      }

      .navbar-shell__user {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        padding: 0.5rem 0.8rem;
        border: 1px solid var(--border);
        border-radius: 999px;
        background: rgba(255, 255, 255, 0.035);
      }

      .navbar-shell__user strong {
        display: block;
        font-size: 0.92rem;
        line-height: 1.1;
      }

      .navbar-shell__user span {
        display: block;
        color: var(--muted);
        font-size: 0.78rem;
      }

      .navbar-shell__avatar {
        display: grid;
        place-items: center;
        width: 2rem;
        height: 2rem;
        border-radius: 50%;
        background: linear-gradient(135deg, var(--accent-strong), var(--accent));
        color: white;
        font-weight: 800;
        flex: none;
      }

      .navbar-shell__menu {
        display: none;
      }

      @media (max-width: 920px) {
        .navbar-shell {
          padding: 0.9rem 1rem;
        }

        .navbar-shell__date {
          display: none;
        }

        .navbar-shell__menu {
          display: inline-flex;
        }
      }

      @media (max-width: 640px) {
        .navbar-shell__user {
          display: none;
        }

        .navbar-shell__brand h1 {
          font-size: 1.02rem;
        }
      }
    `,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NavbarComponent {
  readonly titulo = input.required<string>();
  readonly usuario = input<Usuario | null>(null);
  readonly fecha = input('');
  readonly menuAbierto = input(false);
  readonly toggleMenu = output<void>();
  readonly cerrarSesion = output<void>();

  readonly iniciales = computed(() => {
    const currentUser = this.usuario();

    if (!currentUser) {
      return 'AP';
    }

    return `${currentUser.nombres?.[0] ?? ''}${currentUser.apellidos?.[0] ?? ''}`.trim().toUpperCase() || 'AP';
  });
}