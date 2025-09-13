import { HttpInterceptorFn } from '@angular/common/http';
import { Router } from '@angular/router';
import { inject } from '@angular/core';
import { jwtDecode } from 'jwt-decode';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  if (typeof window !== 'undefined' && window.localStorage) {
    const token = localStorage.getItem('access_token');
    if (token) {
      const decodeToken: any = jwtDecode(token);
      const isExpired = decodeToken.exp * 1000 < Date.now();
      if (isExpired) {
        localStorage.removeItem('access_token');
        router.navigate(['/login']);
        return next(req);
      }
      const authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
      return next(authReq);
    }
  }
  return next(req);
};
