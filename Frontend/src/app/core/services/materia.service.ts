import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map, shareReplay, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  Actividad, ActividadRequest,
  ApiWrapper,
  Calificacion, CalificacionRequest,
  InscripcionMateriaRequest,
  Materia, MateriaRequest,
  UsuarioMateria,
} from '../models/materia.model';

@Injectable({ providedIn: 'root' })
export class MateriaService {
  private readonly http = inject(HttpClient);
  private readonly api = environment.apiUrl;

  // ── Cache simple por sesión ──
  private _catalogoCache$: Observable<Materia[]> | null = null;
  private _inscripcionesCache = new Map<number, Observable<UsuarioMateria[]>>();
  private _actividadesCache   = new Map<number, Observable<Actividad[]>>();

  invalidarCaches(): void {
    this._catalogoCache$     = null;
    this._inscripcionesCache.clear();
    this._actividadesCache.clear();
  }

  invalidarInscripciones(userId: number): void {
    this._inscripcionesCache.delete(userId);
  }

  invalidarActividades(umId: number): void {
    this._actividadesCache.delete(umId);
  }

  private unwrap<T>(response: any): T {
    if (response && typeof response === 'object' && 'data' in response) return response.data as T;
    return response as T;
  }

  private unwrapList<T>(response: any): T[] {
    if (Array.isArray(response)) return response as T[];
    if (response && typeof response === 'object' && Array.isArray(response.data)) return response.data as T[];
    return [];
  }

  // ── Materias ──
  obtenerMaterias(): Observable<Materia[]> {
    if (!this._catalogoCache$) {
      this._catalogoCache$ = this.http.get<any>(`${this.api}/materias`).pipe(
        map(r => this.unwrapList<Materia>(r)),
        shareReplay(1),
      );
    }
    return this._catalogoCache$;
  }

  obtenerMateria(id: number): Observable<Materia> {
    return this.http.get<ApiWrapper<Materia>>(`${this.api}/materias/${id}`).pipe(
      map(r => this.unwrap<Materia>(r))
    );
  }

  crearMateria(payload: MateriaRequest): Observable<Materia> {
    return this.http.post<ApiWrapper<Materia>>(`${this.api}/materias`, payload).pipe(
      map(r => this.unwrap<Materia>(r)),
      tap(() => { this._catalogoCache$ = null; }) // invalida caché
    );
  }

  actualizarMateria(id: number, payload: MateriaRequest): Observable<Materia> {
    return this.http.put<ApiWrapper<Materia>>(`${this.api}/materias/${id}`, payload).pipe(
      map(r => this.unwrap<Materia>(r)),
      tap(() => { this._catalogoCache$ = null; })
    );
  }

  eliminarMateria(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/materias/${id}`).pipe(
      tap(() => { this._catalogoCache$ = null; })
    );
  }

  // ── Inscripciones ──
  obtenerMisMateriasInscritas(usuarioId: number): Observable<UsuarioMateria[]> {
    if (!this._inscripcionesCache.has(usuarioId)) {
      const req$ = this.http.get<any>(`${this.api}/usuario-materias/usuario/${usuarioId}`).pipe(
        map(r => this.unwrapList<UsuarioMateria>(r)),
        shareReplay(1),
      );
      this._inscripcionesCache.set(usuarioId, req$);
    }
    return this._inscripcionesCache.get(usuarioId)!;
  }

  inscribirMateria(payload: InscripcionMateriaRequest): Observable<UsuarioMateria> {
    return this.http.post<ApiWrapper<UsuarioMateria>>(`${this.api}/usuario-materias`, payload).pipe(
      map(r => this.unwrap<UsuarioMateria>(r)),
      tap(() => this._inscripcionesCache.delete(payload.usuarioId))
    );
  }

  actualizarInscripcion(id: number, payload: Partial<InscripcionMateriaRequest>): Observable<UsuarioMateria> {
    return this.http.put<ApiWrapper<UsuarioMateria>>(`${this.api}/usuario-materias/${id}`, payload).pipe(
      map(r => this.unwrap<UsuarioMateria>(r)),
      tap(() => {
        if (payload.usuarioId) this._inscripcionesCache.delete(payload.usuarioId);
      })
    );
  }

  eliminarInscripcion(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/usuario-materias/${id}`).pipe(
      tap(() => this._inscripcionesCache.clear())
    );
  }

  // ── Actividades ──
  obtenerActividades(usuarioMateriaId: number): Observable<Actividad[]> {
    if (!this._actividadesCache.has(usuarioMateriaId)) {
      const req$ = this.http.get<ApiWrapper<Actividad[]>>(`${this.api}/actividades/usuario-materia/${usuarioMateriaId}`).pipe(
        map(r => this.unwrapList<Actividad>(r)),
        shareReplay(1),
      );
      this._actividadesCache.set(usuarioMateriaId, req$);
    }
    return this._actividadesCache.get(usuarioMateriaId)!;
  }

  crearActividad(payload: ActividadRequest): Observable<Actividad> {
    return this.http.post<ApiWrapper<Actividad>>(`${this.api}/actividades`, payload).pipe(
      map(r => this.unwrap<Actividad>(r)),
      tap(() => this._actividadesCache.delete(payload.usuarioMateriaId))
    );
  }

  actualizarActividad(id: number, payload: ActividadRequest): Observable<Actividad> {
    return this.http.put<ApiWrapper<Actividad>>(`${this.api}/actividades/${id}`, payload).pipe(
      map(r => this.unwrap<Actividad>(r)),
      tap(() => this._actividadesCache.delete(payload.usuarioMateriaId))
    );
  }

  eliminarActividad(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/actividades/${id}`).pipe(
      tap(() => this._actividadesCache.clear())
    );
  }

  // ── Calificaciones (sin caché — siempre frescas) ──
  obtenerCalificaciones(actividadId: number): Observable<Calificacion[]> {
    return this.http.get<ApiWrapper<Calificacion[]>>(`${this.api}/calificaciones/actividad/${actividadId}`).pipe(
      map(r => this.unwrapList<Calificacion>(r))
    );
  }

  crearCalificacion(payload: CalificacionRequest): Observable<Calificacion> {
    return this.http.post<ApiWrapper<Calificacion>>(`${this.api}/calificaciones`, payload).pipe(
      map(r => this.unwrap<Calificacion>(r))
    );
  }

  actualizarCalificacion(id: number, payload: CalificacionRequest): Observable<Calificacion> {
    return this.http.put<ApiWrapper<Calificacion>>(`${this.api}/calificaciones/${id}`, payload).pipe(
      map(r => this.unwrap<Calificacion>(r))
    );
  }

  eliminarCalificacion(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/calificaciones/${id}`);
  }
}
