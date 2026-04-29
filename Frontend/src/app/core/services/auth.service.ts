import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, catchError, map, of, tap, throwError } from 'rxjs';
import { AgendaService } from './agenda.service';
import { environment } from '../../../environments/environment';
import {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  RegisterResponse,
  Usuario,
} from '../models/usuario.model';

interface MockUserRecord extends RegisterRequest {
  id: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly agendaService = inject(AgendaService);
  private readonly tokenKey = 'academic_token';
  private readonly userKey = 'academic_user';
  private readonly mockUsersKey = 'academic_mock_users';
  private readonly baseUrl = `${environment.apiUrl}/auth`;
  private readonly demoUser: MockUserRecord = {
    id: 1,
    nombres: 'Juan Pablo',
    apellidos: 'Bedoya',
    correo: 'demo@academicpath.local',
    contrasena: 'Demo1234!',
    universidad: 'Universidad Demo',
    carrera: 'Ingeniería de Sistemas',
  };

  private readonly currentUserSubject = new BehaviorSubject<Usuario | null>(this.getStoredUser());
  readonly currentUser$ = this.currentUserSubject.asObservable();

  constructor() {
    if (environment.useMockAuth) {
      this.resetSeededDemoState();
      this.seedMockUsers();
    }
  }

  register(data: RegisterRequest): Observable<RegisterResponse> {
    if (environment.useMockAuth) {
      const nuevoUsuario: MockUserRecord = {
        id: Date.now(),
        ...data,
      };

      const usuarios = this.getMockUsers().filter((usuario) => usuario.correo !== nuevoUsuario.correo);
      usuarios.push(nuevoUsuario);
      this.saveMockUsers(usuarios);

      const usuarioPublico = this.toUsuario(nuevoUsuario);
      this.persistMockSession(usuarioPublico, this.buildToken(usuarioPublico.id));

      return of({
        message: 'Usuario registrado exitosamente',
        userId: usuarioPublico.id,
      });
    }

    return this.http.post<{ success: boolean; data: RegisterResponse }>(`${this.baseUrl}/register`, data).pipe(
      map((wrapper) => wrapper.data),
      catchError(this.handleError),
    );
  }

  login(data: LoginRequest): Observable<AuthResponse> {
    if (environment.useMockAuth) {
      const usuarioRegistrado = this.getMockUsers().find(
        (usuario) => usuario.correo.toLowerCase() === data.correo.toLowerCase(),
      );

      if (usuarioRegistrado && usuarioRegistrado.contrasena !== data.contrasena) {
        return throwError(() => new Error('Credenciales inválidas'));
      }

      const usuario = usuarioRegistrado ? this.toUsuario(usuarioRegistrado) : this.createDemoUserFromLogin(data);
      const response = this.buildAuthResponse(usuario);
      this.persistMockSession(response.usuario, response.accessToken);

      return of(response);
    }

    return this.http.post<{ success: boolean; data: AuthResponse }>(`${this.baseUrl}/login`, data).pipe(
      tap((wrapper) => this.persistSession(wrapper.data)),
      map((wrapper) => wrapper.data),
      catchError(this.handleError),
    );
  }

  profile(): Observable<Usuario> {
    if (environment.useMockAuth) {
      return of(this.currentUserSubject.value ?? this.toUsuario(this.demoUser));
    }

    return this.http.get<Usuario>(`${this.baseUrl}/profile`).pipe(catchError(this.handleError));
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    this.currentUserSubject.next(null);
    void this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    return !!this.currentUserSubject.value && !!this.getToken();
  }

  get currentUser(): Usuario | null {
    return this.currentUserSubject.value;
  }

  private persistSession(response: AuthResponse): void {
    localStorage.setItem(this.tokenKey, response.accessToken);
    localStorage.setItem(this.userKey, JSON.stringify(response.usuario));
    this.currentUserSubject.next(response.usuario);
  }

  private persistMockSession(usuario: Usuario, token: string): void {
    localStorage.setItem(this.tokenKey, token);
    localStorage.setItem(this.userKey, JSON.stringify(usuario));
    this.currentUserSubject.next(usuario);
  }

  private getStoredUser(): Usuario | null {
    const rawUser = localStorage.getItem(this.userKey);
    if (!rawUser) {
      return null;
    }

    try {
      return JSON.parse(rawUser) as Usuario;
    } catch {
      localStorage.removeItem(this.userKey);
      return null;
    }
  }

  private seedMockUsers(): void {
    const usuarios = this.getMockUsers();

    if (!usuarios.some((usuario) => usuario.correo === this.demoUser.correo)) {
      this.saveMockUsers([this.demoUser, ...usuarios]);
    }
  }

  private resetSeededDemoState(): void {
    this.agendaService.clearTasksForUser(this.demoUser.id);

    if (this.currentUserSubject.value?.correo === this.demoUser.correo) {
      localStorage.removeItem(this.tokenKey);
      localStorage.removeItem(this.userKey);
      this.currentUserSubject.next(null);
    }
  }

  private getMockUsers(): MockUserRecord[] {
    const rawUsers = localStorage.getItem(this.mockUsersKey);

    if (!rawUsers) {
      return [this.demoUser];
    }

    try {
      const usuarios = JSON.parse(rawUsers) as MockUserRecord[];
      return Array.isArray(usuarios) && usuarios.length ? usuarios : [this.demoUser];
    } catch {
      return [this.demoUser];
    }
  }

  private saveMockUsers(usuarios: MockUserRecord[]): void {
    localStorage.setItem(this.mockUsersKey, JSON.stringify(usuarios));
  }

  private toUsuario(usuario: MockUserRecord): Usuario {
    return {
      id: usuario.id,
      nombres: usuario.nombres,
      apellidos: usuario.apellidos,
      correo: usuario.correo,
      universidad: usuario.universidad,
      carrera: usuario.carrera,
    };
  }

  private createDemoUserFromLogin(data: LoginRequest): Usuario {
    const localPart = data.correo.split('@')[0] || 'usuario-demo';
    const nombres = localPart
      .split(/[._-]/)
      .filter(Boolean)
      .map((segment) => segment.charAt(0).toUpperCase() + segment.slice(1).toLowerCase())
      .join(' ');

    return {
      id: Date.now(),
      nombres: nombres || 'Usuario Demo',
      apellidos: 'Local',
      correo: data.correo,
      universidad: '',
      carrera: '',
    };
  }

  private buildToken(userId: number): string {
    return `academic-demo-${userId}-${Date.now().toString(36)}`;
  }

  private buildAuthResponse(usuario: Usuario): AuthResponse {
    return {
      accessToken: this.buildToken(usuario.id),
      tokenType: 'Bearer',
      expiresIn: 86400,
      usuario,
    };
  }

  private handleError = (error: HttpErrorResponse) => {
    let mensaje = 'No se pudo completar la operación. Intenta de nuevo.';

    if (error.status === 0) {
      mensaje = 'No se pudo conectar con el servidor. Verifica tu conexión.';
    } else if (error.status === 400) {
      // Errores de validación con mapa de campos
      if (error.error?.errors) {
        const campos = error.error.errors as Record<string, string>;
        mensaje = Object.values(campos).join(' ');
      } else {
        mensaje = error.error?.message ?? 'Datos inválidos. Revisa el formulario.';
      }
    } else if (error.status === 401) {
      mensaje = 'Correo o contraseña incorrectos.';
    } else if (error.status === 403) {
      mensaje = 'No tienes permiso para realizar esta acción.';
    } else if (error.status === 404) {
      mensaje = 'El recurso solicitado no existe.';
    } else if (error.status === 409) {
      mensaje = 'Este correo ya está registrado. Intenta iniciar sesión.';
    } else if (error.status >= 500) {
      mensaje = 'Error en el servidor. Intenta más tarde.';
    } else if (error.error?.message) {
      mensaje = error.error.message;
    }

    return throwError(() => new Error(mensaje));
  };
}