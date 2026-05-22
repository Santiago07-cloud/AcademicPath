export interface Materia {
  id: number;
  codigo: string;
  nombre: string;
  creditos: number;
  descripcion?: string;
  fechaCreacion?: string;
}

export interface MateriaRequest {
  codigo: string;
  nombre: string;
  creditos: number;
  descripcion?: string;
}

export interface UsuarioMateria {
  id: number;
  usuarioId: number;
  materiaId: number;
  profesorId?: number | null;
  profesorNombre?: string | null;
  semestre: number;
  anio: number;
  estado: string;
  notaFinal?: number | null;
  avancePorcentaje?: number | null;
  fechaCreacion?: string;
  materia?: Materia;
  materiaNombre?: string;
}

export interface InscripcionMateriaRequest {
  usuarioId: number;
  materiaId: number;
  semestre: number;
  anio: number;
  estado?: string;
  notaFinal?: number | null;
}

export interface Actividad {
  id: number;
  usuarioMateriaId: number;
  titulo: string;
  tipo: 'parcial' | 'quiz' | 'tarea' | 'proyecto' | 'laboratorio' | 'otro';
  peso: number;
  notaMaxima: number;
  fechaEntrega?: string;
  fechaCreacion?: string;
}

export interface ActividadRequest {
  usuarioMateriaId: number;
  titulo: string;
  tipo: string;
  peso: number;
  notaMaxima: number;
  fechaEntrega?: string;
}

export interface Calificacion {
  id: number;
  actividadId: number;
  nota: number;
  retroalimentacion?: string;
  fechaCalificacion?: string;
}

export interface CalificacionRequest {
  actividadId: number;
  nota: number;
  retroalimentacion?: string;
}

export interface ApiWrapper<T> {
  success: boolean;
  message: string;
  data: T;
  error?: string | null;
}
