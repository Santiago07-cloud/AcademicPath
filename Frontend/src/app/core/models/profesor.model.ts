export interface Profesor {
  id: number;
  nombre: string;
  correo: string;
  fechaCreacion?: string;
}

export interface ProfesorRequest {
  nombre: string;
  correo: string;
}
