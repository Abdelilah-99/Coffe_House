import { Component } from '@angular/core';
import { AuthService } from '../auth';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  imports: [FormsModule],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {
  firstname: string = '';
  lastname: string = '';
  username: string = '';
  email: string = '';
  password: string = '';
  message: string = '';
  constructor(private authService: AuthService, private router: Router) { }
  onRegister() {
    const registerData = {
      firstName: this.firstname,
      lastName: this.lastname,
      username: this.username,
      email: this.email,
      password: this.password
    }

    this.authService.register(registerData).subscribe({
      next: (res) => {
        this.message = "register successful!!";
        this.router.navigate(['/login']);
        console.log('res: ', res);
      },
      error: (err) => {
        this.message = "invalid credential";
        console.log('err: ', err);
      }
    })
  }

  isFormRegisterValid() {
    return this.firstname.trim() !== '' &&
      this.lastname.trim() !== '' &&
      this.username.trim() !== '' &&
      this.email.trim() !== '' &&
      this.password.trim() !== '';
  }
}
