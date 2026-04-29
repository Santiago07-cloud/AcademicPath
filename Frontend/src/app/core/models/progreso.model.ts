import { Materia } from './materia.model';

export interface ProgresoAcademico {
  id?: number;
  usuarioId: number;
  creditosTotales: number;
  creditosAprobados: number;
  promedio: number;
  fechaActualizacion?: string;
}

export interface SugerenciaMateria {
  id: number;
  usuarioId: number;
  materiaId: number;
  disponible: boolean;
  fechaGeneracion?: string;
  materia?: Materia;
}