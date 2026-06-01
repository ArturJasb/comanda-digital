import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NotifyService } from '../../../core/services/notify.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: '../auth.scss'
})
export class LoginComponent {
  form = this.fb.group({
    email: ['admin@email.com', [Validators.required, Validators.email]],
    senha: ['senha123', [Validators.required]]
  });
  carregando = false;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private notify: NotifyService
  ) {}

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.carregando = true;
    const { email, senha } = this.form.value;
    this.auth.login(email!, senha!).subscribe({
      next: () => {
        this.notify.success('Bem-vindo de volta!');
        const redirect = this.route.snapshot.queryParamMap.get('redirect');
        if (redirect) {
          this.router.navigateByUrl(redirect);
        } else {
          this.router.navigate([this.auth.isAdminArea() ? '/admin' : '/']);
        }
      },
      error: () => { this.carregando = false; }
    });
  }
}
