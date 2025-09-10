import { CanActivateFn } from '@angular/router';

export const guestGuard: CanActivateFn = (route, state) => {
  return true;
};
