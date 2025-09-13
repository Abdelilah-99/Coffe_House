import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../auth';
import { Router } from '@angular/router';
@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login implements OnInit {
  username: string = '';
  password: string = '';
  message: string = '';

  constructor(private authService: AuthService, private router: Router) { }
  ngOnInit() {
    if (typeof window !== 'undefined' && window.localStorage) {
      const token = localStorage.getItem('access_token');
      if (token) {
        console.log("token: ", token);
        this.router.navigate(['/']);
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
        this.message = "login successful!!";
        localStorage.setItem('access_token', JSON.stringify(res.token));
        console.log('res: ', res);
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
