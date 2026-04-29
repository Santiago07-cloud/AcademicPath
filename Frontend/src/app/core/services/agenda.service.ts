import { Injectable, signal } from '@angular/core';
import { CrearTareaAcademica, ResumenAgenda, TareaAcademica } from '../models/agenda.model';

@Injectable({ providedIn: 'root' })
export class AgendaService {
  private readonly storageKey = 'academic_agenda_tasks';

  readonly tareas = signal<TareaAcademica[]>(this.loadTasks());

  listByUser(usuarioId: number): TareaAcademica[] {
    return this.tareas()
      .filter((tarea) => tarea.usuarioId === usuarioId)
      .slice()
      .sort((a, b) => this.compareTasks(a, b));
  }

  getResumen(usuarioId: number): ResumenAgenda {
    const tareas = this.listByUser(usuarioId);
    const today = this.startOfDay(new Date()).getTime();

    return {
      total: tareas.length,
      pendientes: tareas.filter((tarea) => tarea.estado === 'pendiente').length,
      completadas: tareas.filter((tarea) => tarea.estado === 'completada').length,
      vencidas: tareas.filter((tarea) => tarea.estado === 'pendiente' && this.toDayTime(tarea.fechaLimite) < today).length,
    };
  }

  addTask(data: CrearTareaAcademica): TareaAcademica {
    const tarea: TareaAcademica = {
      id: Date.now(),
      usuarioId: data.usuarioId,
      titulo: data.titulo.trim(),
      materia: data.materia.trim(),
      descripcion: data.descripcion?.trim() || undefined,
      fechaLimite: data.fechaLimite,
      prioridad: data.prioridad,
      estado: 'pendiente',
      creadaEn: new Date().toISOString(),
    };

    this.tareas.update((tasks) => [...tasks, tarea]);
    this.persistTasks();

    return tarea;
  }

  clearTasksForUser(usuarioId: number): void {
    this.tareas.update((tasks) => tasks.filter((task) => task.usuarioId !== usuarioId));
    this.persistTasks();
  }

  toggleTaskCompletion(taskId: number): void {
    this.tareas.update((tasks) =>
      tasks.map((task) =>
        task.id === taskId
          ? {
              ...task,
              estado: task.estado === 'completada' ? 'pendiente' : 'completada',
              completadaEn: task.estado === 'completada' ? undefined : new Date().toISOString(),
            }
          : task,
      ),
    );

    this.persistTasks();
  }

  removeTask(taskId: number): void {
    this.tareas.update((tasks) => tasks.filter((task) => task.id !== taskId));
    this.persistTasks();
  }

  private compareTasks(a: TareaAcademica, b: TareaAcademica): number {
    if (a.estado !== b.estado) {
      return a.estado === 'completada' ? 1 : -1;
    }

    const diff = this.toDayTime(a.fechaLimite) - this.toDayTime(b.fechaLimite);

    if (diff !== 0) {
      return diff;
    }

    return b.id - a.id;
  }

  private loadTasks(): TareaAcademica[] {
    const rawTasks = localStorage.getItem(this.storageKey);

    if (!rawTasks) {
      return [];
    }

    try {
      const tasks = JSON.parse(rawTasks) as TareaAcademica[];
      return Array.isArray(tasks) ? tasks : [];
    } catch {
      return [];
    }
  }

  private persistTasks(): void {
    localStorage.setItem(this.storageKey, JSON.stringify(this.tareas()));
  }

  private toDayTime(dateValue: string): number {
    return this.startOfDay(new Date(`${dateValue}T12:00:00`)).getTime();
  }

  private startOfDay(date: Date): Date {
    const result = new Date(date);
    result.setHours(0, 0, 0, 0);
    return result;
  }
}
