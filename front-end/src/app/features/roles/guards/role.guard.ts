import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { TokenService } from "../../../core/services/token-service"; 

export const roleGuard: CanActivateFn = (route, state) => {
    const tokenService = inject(TokenService);
    const router = inject(Router);
    
    const userRole = tokenService.getUserRole();

    if (!userRole) {
        router.navigate(['/login']);
        return false;
    }

    if (userRole === 'ROLE_ADMIN') {
        return true;
    }

    alert('Access denied: You do not have permission to access this page');
    router.navigate(['/home']);
    return false;
}