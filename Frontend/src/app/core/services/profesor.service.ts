import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, map, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiWrapper } from '../models/materia.model';
import { Profesor, ProfesorRequest } from '../models/profesor.model';

@Injectable({ providedIn: 'root' })
export class ProfesorService {
  private readonly http = inject(HttpClient);
  private readonly api  = `${environment.apiUrl}/profesores`;

  obtenerTodos(): Observable<Profesor[]> {
    return this.http
      .get<ApiWrapper<Profesor[]>>(this.api)
      .pipe(map(r => r.data ?? []), catchError(this.handleError));
  }

  obtenerPorId(id: number): Observable<Profesor> {
    return this.http
      .get<ApiWrapper<Profesor>>(`${this.api}/${id}`)
      .pipe(map(r => r.data), catchError(this.handleError));
  }

  crear(payload: ProfesorRequest): Observable<Profesor> {
    return this.http
      .post<ApiWrapper<Profesor>>(this.api, payload)
      .pipe(map(r => r.data), catchError(this.handleError));
  }

  actualizar(id: number, payload: ProfesorRequest): Observable<Profesor> {
    return this.http
      .put<ApiWrapper<Profesor>>(`${this.api}/${id}`, payload)
      .pipe(map(r => r.data), catchError(this.handleError));
  }

  eliminar(id: number): Observable<void> {
    return this.http
      .delete<void>(`${this.api}/${id}`)
      .pipe(catchError(this.handleError));
  }

  private handleError(err: HttpErrorResponse) {
    return throwError(() => new Error(err.error?.message ?? err.message ?? 'Error de servidor'));
  }
}
