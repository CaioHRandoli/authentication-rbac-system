import { Routes } from '@angular/router';
import { Login } from './features/auth/pages/login/login'; 
import { Home } from './features/home/pages/home'; 
import { roleGuard } from './features/roles/guards/role.guard'; 
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
   {
        path: '', 
        redirectTo: 'login', 
        pathMatch: 'full'
    },
    { 
        path: 'login', 
        component: Login 
    },
    { 
        path: 'home', 
        loadComponent: () => import('./features/home/pages/home').then(m => m.Home),
        canActivate: [authGuard]
    },
    { 
        path: 'roles', 
        loadComponent: () => import('./features/roles/pages/roles/roles').then(m => m.Roles),
        canActivate: [authGuard, roleGuard],
        data: { expectedRole: 'ROLE_ADMIN' }
    },
    { 
        path: '**', 
        redirectTo: 'login' 
    }
];
