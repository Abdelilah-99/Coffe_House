import { HttpInterceptorFn } from '@angular/common/http';
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
      return next(authReq)
      .pipe(
        tap(value => console.log("hi ", value)),
        catchError(err => {
          console.log(err.message);
          if (err.status === 401 /* && err.message.includes("Invalid or missing JWT") */) {
            console.error("401 Unauthorized - Token invalid or expired");
            localStorage.removeItem('access_token');
            router.navigate(['/login']);
          }
          return throwError(() => err);
        })
      );
    } else {
      localStorage.removeItem('access_token');
      router.navigate(['/login']);
    }
  }
  return next(req);
};
