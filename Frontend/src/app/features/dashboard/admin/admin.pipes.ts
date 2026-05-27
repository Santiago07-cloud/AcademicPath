import { Pipe, PipeTransform } from '@angular/core';

const APROBADA_ESTADOS = ['APROBADA', 'APROBADO', 'aprobada', 'aprobado'];
const CURSANDO_ESTADOS  = ['CURSANDO', 'cursando', 'activa', 'ACTIVA'];
const REPROBADA_ESTADOS = ['REPROBADA', 'REPROBADO', 'reprobada', 'reprobado'];

@Pipe({ name: 'aprobadas', standalone: true, pure: false })
export class AprobadasPipe implements PipeTransform {
  transform(materias: any[]): number {
    return materias.filter(m => APROBADA_ESTADOS.includes(m.estado)).length;
  }
}

@Pipe({ name: 'cursando', standalone: true, pure: false })
export class CursandoPipe implements PipeTransform {
  transform(materias: any[]): number {
    return materias.filter(m => CURSANDO_ESTADOS.includes(m.estado)).length;
  }
}

@Pipe({ name: 'reprobadas', standalone: true, pure: false })
export class ReprobadasPipe implements PipeTransform {
  transform(materias: any[]): number {
    return materias.filter(m => REPROBADA_ESTADOS.includes(m.estado)).length;
  }
}
