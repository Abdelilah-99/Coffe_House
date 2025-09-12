import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../auth';
@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  username: string = '';
  password: string = '';
  message: string = '';

  constructor(private authService: AuthService) { }

  onSubmit() {
    const loginData = {
      username: this.username,
      email: this.username,
      password: this.password
    };

    this.authService.login(loginData).subscribe({
      next: (res) => {
        this.message = "login successful!!";
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
