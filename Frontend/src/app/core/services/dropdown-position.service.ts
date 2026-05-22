import { Injectable } from '@angular/core';

export interface PanelPosition {
  top?: string;
  bottom?: string;
  left: string;
  width: string;
  openUp: boolean;
}

@Injectable({ providedIn: 'root' })
export class DropdownPositionService {
  /**
   * Calcula la posición fixed del panel basándose en el trigger.
   * Si no hay espacio abajo, abre hacia arriba.
   */
  calcular(triggerEl: HTMLElement, panelHeight = 220): PanelPosition {
    const rect = triggerEl.getBoundingClientRect();
    const spaceBelow = window.innerHeight - rect.bottom;
    const spaceAbove = rect.top;
    const openUp = spaceBelow < panelHeight + 16 && spaceAbove > spaceBelow;

    return {
      left:    `${rect.left}px`,
      width:   `${rect.width}px`,
      openUp,
      top:    openUp ? undefined : `${rect.bottom + 6}px`,
      bottom: openUp ? `${window.innerHeight - rect.top + 6}px` : undefined,
    };
  }
}
