export type AgendaPrioridad = 'alta' | 'media' | 'baja';

export type AgendaEstado = 'pendiente' | 'completada';

export interface TareaAcademica {
  id: number;
  usuarioId: number;
  titulo: string;
  materia: string;
  descripcion?: string;
  fechaLimite: string;
  prioridad: AgendaPrioridad;
  estado: AgendaEstado;
  creadaEn: string;
  completadaEn?: string;
}

export interface CrearTareaAcademica {
  usuarioId: number;
  titulo: string;
  materia: string;
  descripcion?: string;
  fechaLimite: string;
  prioridad: AgendaPrioridad;
}

export interface ResumenAgenda {
  total: number;
  pendientes: number;
  completadas: number;
  vencidas: number;
}