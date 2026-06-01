import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Perfil } from '../models/models';

/** RoleGuard (RF-43): bloqueia acesso por perfil. Use data: { roles: [...] }. */
export const roleGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const roles = (route.data?.['roles'] as Perfil[]) ?? [];

  if (!auth.isLoggedIn()) {
    router.navigate(['/auth/login']);
    return false;
  }
  if (roles.length === 0 || auth.hasRole(...roles)) {
    return true;
  }
  // sem permissao: manda pra area correta
  router.navigate([auth.isAdminArea() ? '/admin' : '/']);
  return false;
};
