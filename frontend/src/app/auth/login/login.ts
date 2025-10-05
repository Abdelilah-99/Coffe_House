import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService, LoginResponse } from '../auth';
import { Router } from '@angular/router';
import { CommonModule, isPlatformBrowser } from '@angular/common';
@Component({
  selector: 'app-login',
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login implements OnInit {
  username: string = '';
  password: string = '';
  message: string = '';
  loginRes?: LoginResponse;

  constructor(private authService: AuthService, private router: Router, @Inject(PLATFORM_ID) private platformId: Object) { }
  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      const token = localStorage.getItem('access_token');
      if (token) {
        console.log("token: ", token);
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
        this.router.navigate(['/me']);
      },
      error: (err) => {
        this.message = "Invalid credential";
        console.log('res: ', err);
      }
    });
  }

  isFormLoginValid(): boolean {
    return this.username.trim() !== '' && this.password.trim() !== '';
  }
}
