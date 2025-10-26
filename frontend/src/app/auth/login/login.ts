import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService, LoginResponse } from '../auth';
import { Router, RouterModule } from '@angular/router';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ToastService } from '../../toast/service/toast';
@Component({
  selector: 'app-login',
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login implements OnInit {
  username: string = '';
  password: string = '';
  message: string = '';
  loginRes?: LoginResponse;

  constructor(private authService: AuthService,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
    private toast: ToastService) { }
  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      const token = localStorage.getItem('access_token');
      if (token) {
        this.toast.show("Already logged in", 'warning');
        this.router.navigate(['/me']);
      }
    }
  }

  onSubmit() {
    const loginData = {
      username: this.username,
      email: this.username,
      password: this.password
    };

    this.authService.login(loginData).subscribe({
      next: (res) => {
        this.loginRes = res;
        localStorage.setItem('access_token', this.loginRes.token.toString());
        localStorage.setItem("user_role", this.loginRes.userRole.toString());
        this.toast.show("Loggin succefful", 'success');
        this.router.navigate(['/me']);
      },
      error: (err) => {
        this.message = err.error.message;
        this.toast.show(err.error.message, 'error');
      }
    });
  }

  isFormLoginValid(): boolean {
    return this.username.trim() !== '' && this.password.trim() !== '';
  }
}
