import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const adminGuard: CanActivateFn = () => {
    const router = inject(Router);
    const role = localStorage.getItem('user_role');
    console.log("role", role);
    if (role === 'ROLE_ADMIN') return true;
    router.navigate(['/']);
    return false;
};
