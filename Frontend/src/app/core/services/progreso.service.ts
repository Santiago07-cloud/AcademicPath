import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ProgresoAcademico, SugerenciaMateria } from '../models/progreso.model';
import { ApiWrapper } from '../models/materia.model';

@Injectable({ providedIn: 'root' })
export class ProgresoService {
  private readonly http = inject(HttpClient);
  private readonly api = environment.apiUrl;

  obtenerProgreso(usuarioId: number): Observable<ProgresoAcademico> {
    return this.http
      .get<ApiWrapper<ProgresoAcademico>>(`${this.api}/progreso/${usuarioId}`)
      .pipe(map(r => r.data));
  }

  recalcularProgreso(usuarioId: number): Observable<void> {
    return this.http.post<void>(`${this.api}/progreso/${usuarioId}/recalcular`, {});
  }

  verificarPrerrequisitos(usuarioId: number, materiaId: number): Observable<boolean> {
    return this.http
      .get<ApiWrapper<{ cumplePrerrequisitos: boolean }>>(`${this.api}/prerrequisitos/verificar/${usuarioId}/${materiaId}`)
      .pipe(map(r => r.data?.cumplePrerrequisitos ?? false));
  }

  obtenerMateriasDisponibles(usuarioId: number): Observable<number[]> {
    return this.http
      .get<ApiWrapper<{ materiasDisponibles: number[] }>>(`${this.api}/prerrequisitos/disponibles/${usuarioId}`)
      .pipe(map(r => r.data?.materiasDisponibles ?? []));
  }
}
