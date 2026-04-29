import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { AbstractControl, ReactiveFormsModule, ValidationErrors, ValidatorFn, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { RegisterRequest } from '../../../core/models/usuario.model';
import { environment } from '../../../../environments/environment';

const passwordMatchValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const contrasena = control.get('contrasena')?.value;
  const confirmarContrasena = control.get('confirmarContrasena')?.value;

  return contrasena === confirmarContrasena ? null : { passwordMismatch: true };
};

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegisterComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  readonly demoMode = environment.useMockAuth;

  readonly loading = signal(false);
  readonly error = signal('');
  readonly errorType = signal<'duplicate' | 'validation' | 'network' | 'generic'>('generic');
  readonly successMessage = signal('');
  showPassword = false;

  readonly highlights = [
    {
      icon: 'bi bi-person-check',
      title: 'Registro rápido',
      description: 'Solo necesitas nombre, correo y contraseña. Universidad y carrera son opcionales.',
    },
    {
      icon: 'bi bi-lock',
      title: 'Contraseña segura',
      description: 'Mínimo 8 caracteres con mayúscula, minúscula y número. Cifrada con BCrypt.',
    },
    {
      icon: 'bi bi-layout-sidebar',
      title: 'Panel listo al instante',
      description: 'Al registrarte accedes directamente a tu panel académico personalizado.',
    },
  ] as const;

  readonly form = this.fb.nonNullable.group(
    {
      nombres: ['', [Validators.required, Validators.minLength(2)]],
      apellidos: ['', [Validators.required, Validators.minLength(2)]],
      correo: ['', [Validators.required, Validators.email]],
      contrasena: ['', [Validators.required, Validators.minLength(8)]],
      confirmarContrasena: ['', [Validators.required]],
      universidad: [''],
      carrera: [''],
    },
    { validators: [passwordMatchValidator] },
  );

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.error.set('');
    this.successMessage.set('');

    const { confirmarContrasena, ...payload } = this.form.getRawValue();
    const request: RegisterRequest = payload;

    this.authService.register(request).subscribe({
      next: () => {
        this.loading.set(false);
        this.successMessage.set('¡Cuenta creada exitosamente! Redirigiendo...');
        setTimeout(() => {
          void this.router.navigate([this.demoMode ? '/dashboard' : '/login']);
        }, 1500);
      },
      error: (err: Error) => {
        this.loading.set(false);
        const msg = err.message || '';

        if (msg.includes('ya está registrado') || msg.includes('409')) {
          this.errorType.set('duplicate');
        } else if (msg.includes('conectar') || msg.includes('servidor')) {
          this.errorType.set('network');
        } else if (msg.includes('Válid') || msg.includes('válid') || msg.includes('requerido')) {
          this.errorType.set('validation');
        } else {
          this.errorType.set('generic');
        }

        this.error.set(msg || 'No se pudo registrar el usuario. Intenta de nuevo.');
      },
    });
  }
}