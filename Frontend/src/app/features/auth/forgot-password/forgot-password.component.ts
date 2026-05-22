import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { PasswordResetService } from '../../../core/services/password-reset.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ForgotPasswordComponent {
  private readonly fb = inject(FormBuilder);
  private readonly passwordResetSvc = inject(PasswordResetService);

  readonly loading = signal(false);
  readonly enviado  = signal(false);
  readonly error    = signal('');

  readonly form = this.fb.nonNullable.group({
    correo: ['', [Validators.required, Validators.email]],
  });

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.error.set('');

    const { correo } = this.form.getRawValue();

    this.passwordResetSvc.solicitarRecuperacion(correo).subscribe({
      next: () => {
        this.loading.set(false);
        this.enviado.set(true);
      },
      error: (err: Error) => {
        this.loading.set(false);
        this.error.set(err.message || 'No se pudo enviar el correo. Intenta de nuevo.');
      },
    });
  }

  /** Permite intentar con otro correo sin recargar la página */
  intentarDeNuevo(): void {
    this.enviado.set(false);
    this.error.set('');
    this.form.reset();
  }
}
