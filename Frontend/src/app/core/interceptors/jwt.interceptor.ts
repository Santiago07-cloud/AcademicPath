import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/** Rutas públicas que nunca necesitan token JWT */
const PUBLIC_URLS = [
  '/auth/login',
  '/auth/register',
  '/auth/forgot-password',
  '/auth/reset-password',
  '/auth/reset-password/validate',
];

const isPublicUrl = (url: string): boolean =>
  PUBLIC_URLS.some((pub) => url.includes(pub));

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  // Si no hay token o la URL es pública → pasar sin Authorization header
  if (!token || isPublicUrl(req.url)) {
    return next(req);
  }

  return next(
    req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    }),
  );
};
