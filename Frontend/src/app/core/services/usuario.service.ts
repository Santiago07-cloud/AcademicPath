import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, map, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiWrapper } from '../models/materia.model';
import { Usuario, UpdateUsuarioRequest } from '../models/usuario.model';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly http    = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/usuarios`;

  obtenerTodos(): Observable<Usuario[]> {
    return this.http
      .get<ApiWrapper<Usuario[]>>(this.baseUrl)
      .pipe(map(r => r.data ?? []), catchError(this.handleError));
  }

  obtenerPorId(id: number): Observable<Usuario> {
    return this.http
      .get<ApiWrapper<Usuario>>(`${this.baseUrl}/${id}`)
      .pipe(map(r => r.data), catchError(this.handleError));
  }

  actualizar(id: number, payload: UpdateUsuarioRequest): Observable<Usuario> {
    return this.http
      .put<ApiWrapper<Usuario>>(`${this.baseUrl}/${id}`, payload)
      .pipe(map(r => r.data), catchError(this.handleError));
  }

  private handleError(err: HttpErrorResponse) {
    return throwError(() => new Error(err.error?.message ?? err.message ?? 'Error de servidor'));
  }
}
