import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { UsuarioService } from '../../../core/services/usuario.service';
import { UpdateUsuarioRequest } from '../../../core/models/usuario.model';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './perfil.component.html',
  styleUrl: './perfil.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PerfilComponent implements OnInit {
  private readonly auth   = inject(AuthService);
  private readonly usuSvc = inject(UsuarioService);
  private readonly fb     = inject(FormBuilder);

  readonly guardando = signal(false);
  readonly exito     = signal('');
  readonly error     = signal('');

  readonly form = this.fb.nonNullable.group({
    nombres:     ['', [Validators.required, Validators.minLength(2)]],
    apellidos:   ['', [Validators.required, Validators.minLength(2)]],
    universidad: [''],
    carrera:     [''],
  });

  readonly usuario = this.auth.currentUser;

  ngOnInit(): void {
    if (this.usuario) {
      this.form.patchValue({
        nombres:     this.usuario.nombres,
        apellidos:   this.usuario.apellidos,
        universidad: this.usuario.universidad ?? '',
        carrera:     this.usuario.carrera ?? '',
      });
    }
  }

  guardar(): void {
    if (this.form.invalid || !this.usuario) return;
    this.guardando.set(true);
    this.error.set('');
    this.exito.set('');

    const payload: UpdateUsuarioRequest = this.form.getRawValue();

    this.usuSvc.actualizar(this.usuario.id, payload).subscribe({
      next: actualizado => {
        this.auth.actualizarUsuarioLocal(actualizado);
        this.exito.set('Perfil actualizado correctamente.');
        this.guardando.set(false);
        setTimeout(() => this.exito.set(''), 3500);
      },
      error: (e: Error) => {
        this.error.set(e.message ?? 'No se pudo actualizar el perfil.');
        this.guardando.set(false);
      },
    });
  }
}
