import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID, inject } from '@angular/core';
import { catchError, tap, throwError } from 'rxjs';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);
  if (isPlatformBrowser(platformId)) {

    const token = localStorage.getItem('access_token');
    const role = localStorage.getItem('user_role');

    if (!token && req.url.includes('/api/') && !req.url.includes('/auth/login') && !req.url.includes('/auth/register')) {
      console.warn('No token available for authenticated request:', req.url);
      return throwError(() => new Error('No authentication token available'));
    }

    if (token) {
      let tokenRole: string;
      try {
        const decodeToken: any = jwtDecode(token);
        tokenRole = decodeToken.role;

        if (role && tokenRole && !role.includes(tokenRole)) {
          localStorage.removeItem('access_token');
          localStorage.removeItem('user_role');
          router.navigate(['/login']);
          return throwError(() => new Error('Authentication invalid'));
        }

        const isExpired = decodeToken.exp * 1000 < Date.now();
        if (isExpired) {
          localStorage.removeItem('access_token');
          localStorage.removeItem('user_role');
          router.navigate(['/login']);
          return throwError(() => new Error('Token expired'));
        }
      } catch (error) {
        localStorage.removeItem('access_token');
        localStorage.removeItem('user_role');
        router.navigate(['/login']);
        return throwError(() => new Error('Invalid token'));
      }
      const authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
      return next(authReq)
        .pipe(
          catchError((error: HttpErrorResponse) => {
            if (error.status === 401) {
              localStorage.removeItem('access_token');
              localStorage.removeItem('user_role');
              router.navigate(['/login']);
            } else if (error.status >= 500) {
              alert('⚠️ Server error occurred. Try again later.');
            }
            return throwError(() => error);
          }),
        );
    }
  }
  return next(req);
};
