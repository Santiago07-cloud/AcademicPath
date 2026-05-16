import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'aprobadas', standalone: true, pure: false })
export class AprobadasPipe implements PipeTransform {
  transform(materias: any[]): number {
    return materias.filter(m => m.estado === 'APROBADO').length;
  }
}

@Pipe({ name: 'cursando', standalone: true, pure: false })
export class CursandoPipe implements PipeTransform {
  transform(materias: any[]): number {
    return materias.filter(m => m.estado === 'CURSANDO').length;
  }
}
