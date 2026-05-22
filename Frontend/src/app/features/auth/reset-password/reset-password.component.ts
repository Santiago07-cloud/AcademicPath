import {
  ChangeDetectionStrategy, Component, OnInit,
  inject, signal
} from '@angular/core';
import {
  AbstractControl, FormBuilder, ReactiveFormsModule,
  ValidationErrors, ValidatorFn, Validators
} from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { PasswordResetService } from '../../../core/services/password-reset.service';

/** Valida que nueva contraseña y confirmación coincidan */
const passwordMatchValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const nueva = control.get('nuevaContrasena')?.value;
  const confirmar = control.get('confirmarContrasena')?.value;
  return nueva === confirmar ? null : { passwordMismatch: true };
};

/** Al menos 8 caracteres, 1 mayúscula y 1 número */
const passwordStrengthValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const value: string = control.value ?? '';
  const hasUppercase = /[A-Z]/.test(value);
  const hasNumber    = /[0-9]/.test(value);
  const hasMinLength = value.length >= 8;
  return hasUppercase && hasNumber && hasMinLength ? null : { passwordWeak: true };
};

type PageState = 'validating' | 'valid' | 'invalid-token' | 'submitting' | 'success' | 'error';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResetPasswordComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly passwordResetSvc = inject(PasswordResetService);

  readonly state = signal<PageState>('validating');
  readonly error = signal('');
  readonly showPassword = signal(false);
  readonly showConfirm  = signal(false);

  private token = '';

  readonly form = this.fb.nonNullable.group(
    {
      nuevaContrasena:    ['', [Validators.required, passwordStrengthValidator]],
      confirmarContrasena: ['', [Validators.required]],
    },
    { validators: [passwordMatchValidator] },
  );

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token') ?? '';

    if (!this.token) {
      this.state.set('invalid-token');
      return;
    }

    // Validar token contra el backend
    this.passwordResetSvc.validarToken(this.token).subscribe({
      next: () => this.state.set('valid'),
      error: () => this.state.set('invalid-token'),
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.state.set('submitting');
    this.error.set('');

    const { nuevaContrasena } = this.form.getRawValue();

    this.passwordResetSvc.resetearContrasena(this.token, nuevaContrasena).subscribe({
      next: () => {
        this.state.set('success');
        // Redirige al login tras 3 s
        setTimeout(() => void this.router.navigate(['/login']), 3000);
      },
      error: (err: Error) => {
        this.error.set(err.message || 'No se pudo actualizar la contraseña.');
        this.state.set('error');
      },
    });
  }

  /** Calcula la fortaleza para la barra visual (0–100) */
  get passwordStrength(): number {
    const v: string = this.form.controls.nuevaContrasena.value ?? '';
    let score = 0;
    if (v.length >= 8)  score += 25;
    if (v.length >= 12) score += 15;
    if (/[A-Z]/.test(v)) score += 20;
    if (/[0-9]/.test(v)) score += 20;
    if (/[^A-Za-z0-9]/.test(v)) score += 20;
    return Math.min(score, 100);
  }

  get strengthLabel(): string {
    const s = this.passwordStrength;
    if (s < 40) return 'Débil';
    if (s < 70) return 'Media';
    return 'Fuerte';
  }

  get strengthClass(): string {
    const s = this.passwordStrength;
    if (s < 40) return 'weak';
    if (s < 70) return 'medium';
    return 'strong';
  }
}
