import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { LoginRequest } from '../../../core/models/usuario.model';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  readonly demoMode = environment.useMockAuth;

  readonly loading = signal(false);
  readonly error = signal('');
  readonly errorType = signal<'credentials' | 'network' | 'server' | 'generic'>('generic');
  readonly intentosFallidos = signal(0);
  showPassword = false;

  readonly highlights = [
    {
      icon: 'bi bi-shield-lock',
      title: 'Autenticación segura',
      description: 'JWT firmado con HS384, BCrypt para contraseñas y sesión de 24 horas.',
    },
    {
      icon: 'bi bi-mortarboard',
      title: 'Gestiona tu carrera',
      description: 'Materias, notas, actividades y progreso académico en un solo panel.',
    },
    {
      icon: 'bi bi-graph-up-arrow',
      title: 'Seguimiento en tiempo real',
      description: 'Visualiza tu avance de créditos, promedio y sugerencias de materias.',
    },
  ] as const;

  readonly form = this.fb.nonNullable.group({
    correo: ['', [Validators.required, Validators.email]],
    contrasena: ['', [Validators.required]],
  });

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.error.set('');

    const payload: LoginRequest = this.form.getRawValue();

    this.authService.login(payload).subscribe({
      next: () => {
        this.loading.set(false);
        this.intentosFallidos.set(0);
        void this.router.navigate(['/dashboard']);
      },
      error: (err: Error) => {
        this.loading.set(false);
        this.intentosFallidos.update(n => n + 1);

        const msg = err.message || '';
        if (msg.includes('servidor') || msg.includes('conectar')) {
          this.errorType.set('network');
        } else if (msg.includes('incorrectos') || msg.includes('inválidas') || msg.includes('Credenciales')) {
          this.errorType.set('credentials');
          // Limpia solo la contraseña al fallar las credenciales
          this.form.controls.contrasena.reset();
        } else if (msg.includes('servidor')) {
          this.errorType.set('server');
        } else {
          this.errorType.set('generic');
        }

        this.error.set(msg || 'Credenciales inválidas. Verifica tu correo y contraseña.');
      },
    });
  }
}