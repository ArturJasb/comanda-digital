import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NotifyService } from '../../../core/services/notify.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: '../auth.scss'
})
export class RegisterComponent {
  form = this.fb.group({
    nome: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required, Validators.minLength(6)]],
    telefone: [''],
    endereco: ['']
  });
  carregando = false;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router,
    private notify: NotifyService
  ) {}

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.carregando = true;
    this.auth.register(this.form.value as any).subscribe({
      next: () => {
        this.notify.success('Conta criada com sucesso!');
        this.router.navigate(['/']);
      },
      error: () => { this.carregando = false; }
    });
  }
}
