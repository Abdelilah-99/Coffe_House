import { isPlatformBrowser } from '@angular/common';
import { inject, PLATFORM_ID } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { ToastService } from '../toast/service/toast';

export const authGuard: CanActivateFn = () => {
    const router = inject(Router);
    const platformId = inject(PLATFORM_ID);
    const toast = inject(ToastService);
    if (isPlatformBrowser(platformId)) {
        const token = localStorage.getItem('access_token');
        if (token) {
            toast.show("already logged in", 'warning');
            router.navigate(['']);
            return false;
        }
    }
    return true;
};
