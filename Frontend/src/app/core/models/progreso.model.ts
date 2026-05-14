export interface ProgresoAcademico {
  id?: number;
  usuarioId: number;
  creditosTotales: number;
  creditosAprobados: number;
  promedio: number;
  fechaActualizacion?: string;
}

export interface PrerrequisitoResponse {
  id: number;
  materiaId: number;
  materiaNombre: string;
  materiaPrerrequisitId: number;
  materiaPrerrequisitNombre: string;
  fechaCreacion?: string;
}

export interface PrerrequisitoRequest {
  materiaId: number;
  materiaPrerrequisitId: number;
}
