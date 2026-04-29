import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

const initialRedirect = 'login';

export const routes: Routes = [
	{
		path: '',
		redirectTo: initialRedirect,
		pathMatch: 'full',
	},
	{
		path: 'login',
		loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent),
	},
	{
		path: 'register',
		loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent),
	},
	{
		path: 'dashboard',
		canActivate: [authGuard],
		loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent),
	},
	{
		path: '**',
		redirectTo: initialRedirect,
	},
];
