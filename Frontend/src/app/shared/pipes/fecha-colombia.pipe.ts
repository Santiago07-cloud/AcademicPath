import { Pipe, PipeTransform } from '@angular/core';
import { DatePipe } from '@angular/common';

@Pipe({
  name: 'fechaColombia',
  standalone: true
})
export class FechaColombiaPipe implements PipeTransform {

  transform(value: string | Date | null | undefined, format: string = 'dd/MM/yyyy HH:mm'): string {
    if (!value) return '';
    try {
      const datePipe = new DatePipe('es-CO');
      return datePipe.transform(value, format, 'America/Bogota') ?? '';
    } catch {
      return String(value);
    }
  }
}
