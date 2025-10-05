import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const adminGuard: CanActivateFn = () => {
    const router = inject(Router);
    const role = localStorage.getItem('user_role');
    if (role === 'ADMIN') return true;
    router.navigate(['/']);
    return false;
};
