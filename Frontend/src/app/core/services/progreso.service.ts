import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, map, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiWrapper } from '../models/materia.model';
import { ProgresoAcademico, PrerrequisitoResponse, PrerrequisitoRequest } from '../models/progreso.model';

@Injectable({ providedIn: 'root' })
export class ProgresoService {
  private readonly http = inject(HttpClient);
  private readonly api  = environment.apiUrl;

  // ── Progreso académico ──────────────────────────────────────────────────────
  obtenerProgreso(usuarioId: number): Observable<ProgresoAcademico> {
    return this.http
      .get<ApiWrapper<ProgresoAcademico>>(`${this.api}/progreso/${usuarioId}`)
      .pipe(map(r => r.data), catchError(this.handleError));
  }

  recalcularProgreso(usuarioId: number): Observable<void> {
    return this.http
      .post<void>(`${this.api}/progreso/${usuarioId}/recalcular`, {})
      .pipe(catchError(this.handleError));
  }

  // ── Prerrequisitos ──────────────────────────────────────────────────────────
  obtenerPrerrequisitosMateria(materiaId: number): Observable<PrerrequisitoResponse[]> {
    return this.http
      .get<ApiWrapper<{ materiaId: number; prerequisitos: PrerrequisitoResponse[] }>>(
        `${this.api}/prerrequisitos/materia/${materiaId}`
      )
      .pipe(
        map(r => r.data?.prerequisitos ?? []),
        catchError(this.handleError)
      );
  }

  obtenerMateriasDisponibles(usuarioId: number): Observable<number[]> {
    return this.http
      .get<ApiWrapper<{ usuarioId: number; materiasDisponibles: number[] }>>(
        `${this.api}/prerrequisitos/disponibles/${usuarioId}`
      )
      .pipe(
        map(r => r.data?.materiasDisponibles ?? []),
        catchError(this.handleError)
      );
  }

  verificarPrerrequisitos(usuarioId: number, materiaId: number): Observable<boolean> {
    return this.http
      .get<ApiWrapper<{ cumplePrerrequisitos: boolean }>>(
        `${this.api}/prerrequisitos/verificar/${usuarioId}/${materiaId}`
      )
      .pipe(
        map(r => r.data?.cumplePrerrequisitos ?? false),
        catchError(this.handleError)
      );
  }

  crearPrerrequisito(payload: PrerrequisitoRequest): Observable<PrerrequisitoResponse> {
    return this.http
      .post<ApiWrapper<PrerrequisitoResponse>>(`${this.api}/prerrequisitos`, payload)
      .pipe(map(r => r.data), catchError(this.handleError));
  }

  eliminarPrerrequisito(id: number): Observable<void> {
    return this.http
      .delete<void>(`${this.api}/prerrequisitos/${id}`)
      .pipe(catchError(this.handleError));
  }

  private handleError(err: HttpErrorResponse) {
    return throwError(() => new Error(err.error?.message ?? err.message ?? 'Error de servidor'));
  }
}
