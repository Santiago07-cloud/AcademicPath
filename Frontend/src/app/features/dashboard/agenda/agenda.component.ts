import { ChangeDetectionStrategy, Component, HostListener, computed, effect, inject, signal } from '@angular/core';
import { ReactiveFormsModule, Validators, FormBuilder } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { AgendaService } from '../../../core/services/agenda.service';
import { MateriaService } from '../../../core/services/materia.service';
import { AgendaPrioridad, TareaAcademica } from '../../../core/models/agenda.model';
import { Materia } from '../../../core/models/materia.model';
import { Usuario } from '../../../core/models/usuario.model';

@Component({
  selector: 'app-agenda',
  imports: [ReactiveFormsModule],
  templateUrl: './agenda.component.html',
  styleUrl: './agenda.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AgendaComponent {
  private readonly authService = inject(AuthService);
  private readonly agendaService = inject(AgendaService);
  private readonly materiaService = inject(MateriaService);
  private readonly formBuilder = inject(FormBuilder);
  private readonly shortDateFormatter = new Intl.DateTimeFormat('es-CO', {
    day: 'numeric',
    month: 'short',
  });

  readonly currentUser = signal<Usuario | null>(this.authService.currentUser);

  readonly taskForm = this.formBuilder.nonNullable.group({
    titulo: ['', [Validators.required, Validators.maxLength(80)]],
    materia: ['', [Validators.required, Validators.maxLength(40)]],
    fechaLimite: [this.dateInputValue(this.addDays(new Date(), 2)), [Validators.required]],
    prioridad: ['media' as AgendaPrioridad, [Validators.required]],
    descripcion: ['', [Validators.maxLength(180)]],
  });

  readonly usuarioId = computed(() => this.currentUser()?.id ?? null);
  readonly materias = signal<Materia[]>([]);
  readonly materiaDropdownOpen = signal(false);
  readonly prioridadDropdownOpen = signal(false);
  readonly prioridades: AgendaPrioridad[] = ['alta', 'media', 'baja'];
  readonly calendarOpen = signal(false);
  readonly calendarMonth = signal(this.startOfMonth(new Date()));

  readonly calendarDays = computed(() => {
    const month = this.calendarMonth();
    const year = month.getFullYear();
    const monthIndex = month.getMonth();
    const firstDay = new Date(year, monthIndex, 1);
    const lastDay = new Date(year, monthIndex + 1, 0);
    const leading = firstDay.getDay();
    const totalDays = lastDay.getDate();
    const selected = this.parseDate(this.taskForm.controls.fechaLimite.value);
    const today = this.startOfDay(new Date());

    const days: Array<{
      date: Date | null;
      label: string;
      isSelected: boolean;
      isToday: boolean;
      isCurrentMonth: boolean;
    }> = [];

    for (let i = 0; i < leading; i += 1) {
      days.push({ date: null, label: '', isSelected: false, isToday: false, isCurrentMonth: false });
    }

    for (let day = 1; day <= totalDays; day += 1) {
      const date = new Date(year, monthIndex, day);
      const isSelected = this.sameDay(date, selected);
      const isToday = this.sameDay(date, today);
      days.push({
        date,
        label: String(day),
        isSelected,
        isToday,
        isCurrentMonth: true,
      });
    }

    while (days.length % 7 !== 0) {
      days.push({ date: null, label: '', isSelected: false, isToday: false, isCurrentMonth: false });
    }

    return days;
  });

  readonly tareas = computed(() => {
    const usuarioId = this.usuarioId();
    return usuarioId ? this.agendaService.listByUser(usuarioId) : [];
  });

  readonly resumen = computed(() => {
    const usuarioId = this.usuarioId();

    return usuarioId
      ? this.agendaService.getResumen(usuarioId)
      : {
          total: 0,
          pendientes: 0,
          completadas: 0,
          vencidas: 0,
        };
  });

  readonly siguienteTarea = computed(() => this.tareas().find((tarea) => tarea.estado === 'pendiente') ?? null);

  private readonly syncMaterias = effect(() => {
    const usuarioId = this.usuarioId();
    if (!usuarioId) {
      this.materias.set([]);
      return;
    }
    this.cargarMateriasUsuario(usuarioId);
  });

  submit(): void {
    const usuarioId = this.usuarioId();

    if (!usuarioId) {
      return;
    }

    if (this.taskForm.invalid) {
      this.taskForm.markAllAsTouched();
      return;
    }

    const values = this.taskForm.getRawValue();

    this.agendaService.addTask({
      usuarioId,
      titulo: values.titulo,
      materia: values.materia,
      fechaLimite: values.fechaLimite,
      prioridad: values.prioridad,
      descripcion: values.descripcion,
    });

    this.taskForm.reset({
      titulo: '',
      materia: '',
      fechaLimite: this.dateInputValue(this.addDays(new Date(), 2)),
      prioridad: 'media',
      descripcion: '',
    });
  }

  toggleMateriaDropdown(): void {
    this.prioridadDropdownOpen.set(false);
    this.calendarOpen.set(false);
    this.materiaDropdownOpen.update((open) => !open);
  }

  closeMateriaDropdown(): void {
    this.materiaDropdownOpen.set(false);
  }

  selectMateria(materia: Materia): void {
    const label = `${materia.codigo} - ${materia.nombre}`;
    this.taskForm.controls.materia.setValue(label);
    this.taskForm.controls.materia.markAsTouched();
    this.closeMateriaDropdown();
  }

  materiaSeleccionadaLabel(): string {
    return this.taskForm.controls.materia.value || 'Selecciona una materia';
  }

  togglePrioridadDropdown(): void {
    this.materiaDropdownOpen.set(false);
    this.calendarOpen.set(false);
    this.prioridadDropdownOpen.update((open) => !open);
  }

  closePrioridadDropdown(): void {
    this.prioridadDropdownOpen.set(false);
  }

  selectPrioridad(value: AgendaPrioridad): void {
    this.taskForm.controls.prioridad.setValue(value);
    this.taskForm.controls.prioridad.markAsTouched();
    this.closePrioridadDropdown();
  }

  prioridadSeleccionadaLabel(): string {
    const value = this.taskForm.controls.prioridad.value;
    const labels: Record<AgendaPrioridad, string> = {
      alta: 'Alta',
      media: 'Media',
      baja: 'Baja',
    };
    return labels[value] ?? 'Selecciona prioridad';
  }

  prioridadOptionLabel(value: AgendaPrioridad): string {
    const labels: Record<AgendaPrioridad, string> = {
      alta: 'Alta',
      media: 'Media',
      baja: 'Baja',
    };
    return labels[value] ?? value;
  }

  @HostListener('document:click')
  onDocumentClick(): void {
    this.closeMateriaDropdown();
    this.closePrioridadDropdown();
    this.closeCalendar();
  }

  toggleCalendar(): void {
    this.materiaDropdownOpen.set(false);
    this.prioridadDropdownOpen.set(false);
    this.calendarOpen.update((open) => !open);
    this.calendarMonth.set(this.startOfMonth(this.parseDate(this.taskForm.controls.fechaLimite.value)));
  }

  closeCalendar(): void {
    this.calendarOpen.set(false);
  }

  selectCalendarDate(date: Date): void {
    this.taskForm.controls.fechaLimite.setValue(this.dateInputValue(date));
    this.taskForm.controls.fechaLimite.markAsTouched();
    this.closeCalendar();
  }

  nextMonth(): void {
    const current = this.calendarMonth();
    this.calendarMonth.set(new Date(current.getFullYear(), current.getMonth() + 1, 1));
  }

  prevMonth(): void {
    const current = this.calendarMonth();
    this.calendarMonth.set(new Date(current.getFullYear(), current.getMonth() - 1, 1));
  }

  calendarLabel(): string {
    return new Intl.DateTimeFormat('es-CO', { month: 'long', year: 'numeric' })
      .format(this.calendarMonth())
      .replace(/^./, (c) => c.toUpperCase());
  }

  calendarInputLabel(): string {
    const value = this.taskForm.controls.fechaLimite.value;
    const date = this.parseDate(value);
    return new Intl.DateTimeFormat('es-CO', { day: '2-digit', month: 'short', year: 'numeric' })
      .format(date)
      .replace('.', '');
  }

  private cargarMateriasUsuario(usuarioId: number): void {
    this.materiaService.obtenerMisMateriasInscritas(usuarioId).subscribe({
      next: (inscs) => {
        const lista = Array.isArray(inscs) ? inscs : [];
        const materiaIds = new Set(lista.map((i) => Number(i.materiaId)).filter(Boolean));

        const directas = lista
          .map((i) => i.materia)
          .filter((m): m is Materia => Boolean(m));

        if (!materiaIds.size) {
          this.materias.set([]);
          return;
        }

        if (directas.length) {
          const unique = Array.from(new Map(directas.map((m) => [m.id, m])).values());
          if (unique.length >= materiaIds.size) {
            this.materias.set(unique);
            return;
          }
        }

        this.materiaService.obtenerMaterias().subscribe({
          next: (materias) => {
            const catalogo = Array.isArray(materias) ? materias : [];
            this.materias.set(catalogo.filter((m) => materiaIds.has(m.id)));
          },
          error: () => this.materias.set([]),
        });
      },
      error: () => this.materias.set([]),
    });
  }

  toggleTask(taskId: number): void {
    this.agendaService.toggleTaskCompletion(taskId);
  }

  removeTask(taskId: number): void {
    this.agendaService.removeTask(taskId);
  }

  priorityLabel(priority: AgendaPrioridad): string {
    const labels: Record<AgendaPrioridad, string> = {
      alta: 'Alta prioridad',
      media: 'Prioridad media',
      baja: 'Prioridad baja',
    };

    return labels[priority];
  }

  priorityColor(priority: AgendaPrioridad): string {
    const colors: Record<AgendaPrioridad, string> = {
      alta: '#e4f0f2',
      media: '#aabbbf',
      baja: '#77888c',
    };

    return colors[priority];
  }

  statusLabel(task: TareaAcademica): string {
    if (task.estado === 'completada') {
      return 'Completada';
    }

    if (this.isOverdue(task)) {
      return 'Vencida';
    }

    if (this.isDueToday(task)) {
      return 'Hoy';
    }

    const days = this.daysUntil(task.fechaLimite);

    if (days === 1) {
      return 'Mañana';
    }

    if (days > 1 && days < 7) {
      return `En ${days} días`;
    }

    return this.shortDateFormatter.format(this.parseDate(task.fechaLimite));
  }

  deadlineLabel(fechaLimite: string): string {
    if (this.isDueToday(fechaLimite)) {
      return 'hoy';
    }

    if (this.isDueTomorrow(fechaLimite)) {
      return 'mañana';
    }

    const days = this.daysUntil(fechaLimite);

    if (days > 1 && days < 7) {
      return `en ${days} días`;
    }

    return this.shortDateFormatter.format(this.parseDate(fechaLimite));
  }

  isOverdue(task: TareaAcademica): boolean {
    return task.estado !== 'completada' && this.toDayTime(task.fechaLimite) < this.todayStart();
  }

  isDueToday(taskOrDate: TareaAcademica | string): boolean {
    const dateValue = typeof taskOrDate === 'string' ? taskOrDate : taskOrDate.fechaLimite;
    return this.toDayTime(dateValue) === this.todayStart();
  }

  isDueTomorrow(taskOrDate: TareaAcademica | string): boolean {
    const dateValue = typeof taskOrDate === 'string' ? taskOrDate : taskOrDate.fechaLimite;
    return this.daysUntil(dateValue) === 1;
  }

  daysUntil(fechaLimite: string): number {
    return Math.round((this.toDayTime(fechaLimite) - this.todayStart()) / 86_400_000);
  }

  private parseDate(dateValue: string): Date {
    return new Date(`${dateValue}T12:00:00`);
  }

  private startOfMonth(date: Date): Date {
    return new Date(date.getFullYear(), date.getMonth(), 1);
  }

  private startOfDay(date: Date): Date {
    const result = new Date(date);
    result.setHours(0, 0, 0, 0);
    return result;
  }

  private sameDay(a: Date, b: Date): boolean {
    return a.getFullYear() === b.getFullYear()
      && a.getMonth() === b.getMonth()
      && a.getDate() === b.getDate();
  }

  private toDayTime(dateValue: string): number {
    const date = this.parseDate(dateValue);
    date.setHours(0, 0, 0, 0);
    return date.getTime();
  }

  private todayStart(): number {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return today.getTime();
  }

  private addDays(date: Date, days: number): Date {
    const nextDate = new Date(date);
    nextDate.setDate(nextDate.getDate() + days);
    return nextDate;
  }

  private dateInputValue(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }
}