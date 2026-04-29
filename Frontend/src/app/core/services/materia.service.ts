import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
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

  private unwrap<T>(response: any): T {
    if (response && typeof response === 'object' && 'data' in response) {
      return response.data as T;
    }
    return response as T;
  }

  private unwrapList<T>(response: any): T[] {
    if (Array.isArray(response)) return response as T[];
    if (response && typeof response === 'object' && Array.isArray(response.data)) {
      return response.data as T[];
    }
    return [];
  }

  // ── Materias (catálogo global) ──
  obtenerMaterias(): Observable<Materia[]> {
    return this.http.get<any>(`${this.api}/materias`).pipe(
      map(r => this.unwrapList<Materia>(r))
    );
  }

  obtenerMateria(id: number): Observable<Materia> {
    return this.http.get<ApiWrapper<Materia>>(`${this.api}/materias/${id}`).pipe(map(r => this.unwrap<Materia>(r)));
  }

  crearMateria(payload: MateriaRequest): Observable<Materia> {
    return this.http.post<ApiWrapper<Materia>>(`${this.api}/materias`, payload).pipe(map(r => this.unwrap<Materia>(r)));
  }

  actualizarMateria(id: number, payload: MateriaRequest): Observable<Materia> {
    return this.http.put<ApiWrapper<Materia>>(`${this.api}/materias/${id}`, payload).pipe(map(r => this.unwrap<Materia>(r)));
  }

  eliminarMateria(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/materias/${id}`);
  }

  // ── Usuario-Materias (inscripciones del usuario) ──
  obtenerMisMateriasInscritas(usuarioId: number): Observable<UsuarioMateria[]> {
    return this.http
      .get<any>(`${this.api}/usuario-materias/usuario/${usuarioId}`)
      .pipe(
        map(r => this.unwrapList<UsuarioMateria>(r))
      );
  }

  inscribirMateria(payload: InscripcionMateriaRequest): Observable<UsuarioMateria> {
    return this.http.post<ApiWrapper<UsuarioMateria>>(`${this.api}/usuario-materias`, payload).pipe(map(r => this.unwrap<UsuarioMateria>(r)));
  }

  actualizarInscripcion(id: number, payload: Partial<InscripcionMateriaRequest>): Observable<UsuarioMateria> {
    return this.http.put<ApiWrapper<UsuarioMateria>>(`${this.api}/usuario-materias/${id}`, payload).pipe(map(r => this.unwrap<UsuarioMateria>(r)));
  }

  eliminarInscripcion(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/usuario-materias/${id}`);
  }

  // ── Actividades ──
  obtenerActividades(usuarioMateriaId: number): Observable<Actividad[]> {
    return this.http
      .get<ApiWrapper<Actividad[]>>(`${this.api}/actividades/usuario-materia/${usuarioMateriaId}`)
      .pipe(map(r => this.unwrapList<Actividad>(r)));
  }

  crearActividad(payload: ActividadRequest): Observable<Actividad> {
    return this.http.post<ApiWrapper<Actividad>>(`${this.api}/actividades`, payload).pipe(map(r => this.unwrap<Actividad>(r)));
  }

  actualizarActividad(id: number, payload: ActividadRequest): Observable<Actividad> {
    return this.http.put<ApiWrapper<Actividad>>(`${this.api}/actividades/${id}`, payload).pipe(map(r => this.unwrap<Actividad>(r)));
  }

  eliminarActividad(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/actividades/${id}`);
  }

  // ── Calificaciones ──
  obtenerCalificaciones(actividadId: number): Observable<Calificacion[]> {
    return this.http
      .get<ApiWrapper<Calificacion[]>>(`${this.api}/calificaciones/actividad/${actividadId}`)
      .pipe(map(r => this.unwrapList<Calificacion>(r)));
  }

  crearCalificacion(payload: CalificacionRequest): Observable<Calificacion> {
    return this.http.post<ApiWrapper<Calificacion>>(`${this.api}/calificaciones`, payload).pipe(map(r => this.unwrap<Calificacion>(r)));
  }

  actualizarCalificacion(id: number, payload: CalificacionRequest): Observable<Calificacion> {
    return this.http.put<ApiWrapper<Calificacion>>(`${this.api}/calificaciones/${id}`, payload).pipe(map(r => this.unwrap<Calificacion>(r)));
  }

  eliminarCalificacion(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/calificaciones/${id}`);
  }
}
