import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, map, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiWrapper } from '../models/materia.model';

export interface ForgotPasswordRequest {
  correo: string;
}

export interface ResetPasswordRequest {
  token: string;
  nuevaContrasena: string;
}

export interface PasswordResetResponse {
  message: string;
}

@Injectable({ providedIn: 'root' })
export class PasswordResetService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/auth`;

  /**
   * Solicita un correo de recuperación para el email dado.
   * El backend genera el token y envía el email.
   */
  solicitarRecuperacion(correo: string): Observable<PasswordResetResponse> {
    const payload: ForgotPasswordRequest = { correo };
    return this.http
      .post<ApiWrapper<PasswordResetResponse>>(`${this.baseUrl}/forgot-password`, payload)
      .pipe(map((w) => w.data), catchError(this.handleError));
  }

  /**
   * Valida si el token de recuperación existe y no ha expirado.
   */
  validarToken(token: string): Observable<PasswordResetResponse> {
    return this.http
      .get<ApiWrapper<PasswordResetResponse>>(`${this.baseUrl}/reset-password/validate?token=${token}`)
      .pipe(map((w) => w.data), catchError(this.handleError));
  }

  /**
   * Cambia la contraseña usando el token recibido por correo.
   */
  resetearContrasena(token: string, nuevaContrasena: string): Observable<PasswordResetResponse> {
    const payload: ResetPasswordRequest = { token, nuevaContrasena };
    return this.http
      .post<ApiWrapper<PasswordResetResponse>>(`${this.baseUrl}/reset-password`, payload)
      .pipe(map((w) => w.data), catchError(this.handleError));
  }

  private handleError = (error: HttpErrorResponse) => {
    let mensaje = 'No se pudo completar la operación. Intenta de nuevo.';
    if (error.status === 0) {
      mensaje = 'No se pudo conectar con el servidor. Verifica tu conexión.';
    } else if (error.status === 400) {
      mensaje = error.error?.message ?? 'La solicitud no es válida.';
    } else if (error.status === 404) {
      mensaje = 'No existe una cuenta con ese correo electrónico.';
    } else if (error.status === 410) {
      mensaje = 'El enlace de recuperación ha expirado. Solicita uno nuevo.';
    } else if (error.status >= 500) {
      mensaje = 'Error en el servidor. Intenta más tarde.';
    } else if (error.error?.message) {
      mensaje = error.error.message;
    }
    return throwError(() => new Error(mensaje));
  };
}
