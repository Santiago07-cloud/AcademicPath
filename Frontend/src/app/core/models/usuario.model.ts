export interface Usuario {
  id: number;
  nombres: string;
  apellidos: string;
  correo: string;
  universidad?: string;
  carrera?: string;
  fechaCreacion?: string;
  fechaActualizacion?: string;
}

export interface RegisterRequest {
  nombres: string;
  apellidos: string;
  correo: string;
  contrasena: string;
  universidad?: string;
  carrera?: string;
}

export interface LoginRequest {
  correo: string;
  contrasena: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  usuario: Usuario;
}

export interface RegisterResponse {
  message: string;
  userId: number;
}