import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register implements OnInit {
  firstname: string = '';
  lastname: string = '';
  username: string = '';
  email: string = '';
  password: string = '';
  message: string = '';
  currentStep: number = 1;

  constructor(private authService: AuthService, private router: Router) { }
  ngOnInit() {
    if (typeof window !== 'undefined' && window.localStorage) {
      const token = localStorage.getItem('access_token');
      if (token) {
        console.log("token: ", token);
        this.router.navigate(['/me']);
      }
    }
  }

  selectedFile: File | null = null;
  profileImagePreview: string | null = null;

  onFileSelected(e: any) {
    const files: FileList = e.target.files;
    // console.log(files[0].type);

    if (!files || files.length === 0) {
      this.selectedFile = null;
      this.profileImagePreview = null;
      return;
    }
    this.selectedFile = files[0];
    if (!files[0].type.includes("image")) {
      this.message = "format image are only allowed";
      console.error(files[0].type.split('/')[0]);
      return;
    }
    console.log('Selected file:', this.selectedFile.name);
    const reader = new FileReader();
    reader.onload = (event: any) => {
      this.profileImagePreview = event.target.result;
    };
    reader.readAsDataURL(this.selectedFile);
  }

  removeProfileImage() {
    this.selectedFile = null;
    this.profileImagePreview = null;
    const fileInput = document.getElementById('profileImage') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = '';
    }
  }

  onRegister() {
    const registerData = {
      firstName: this.firstname,
      lastName: this.lastname,
      username: this.username,
      email: this.email,
      password: this.password
    }

    const formData = new FormData();

    formData.append(
      "user",
      new Blob([JSON.stringify(registerData)], { type: "application/json" })
    );

    if (this.selectedFile != null) {
      console.log("yes it enters");
      formData.append("profileImage", this.selectedFile);
    }

    this.authService.register(formData).subscribe({
      next: (res) => {
        this.message = "register successful!!";
        this.router.navigate(['/login']);
        console.log('res: ', res);
      },
      error: (err) => {
        console.error('err: ', err.error.message);
        this.message = err.error.message;
        this.currentStep = 1;
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

  isStep1Valid() {
    if (this.firstname.trim() === '' || this.lastname.trim() === '' ||
        this.username.trim() === '' || this.email.trim() === '') {
      return false;
    }

    if (this.firstname.length < 3 || this.firstname.length > 15) {
      this.message = 'First name must be between 3 and 15 characters';
      return false;
    }

    if (this.lastname.length < 3 || this.lastname.length > 15) {
      this.message = 'Last name must be between 3 and 15 characters';
      return false;
    }

    if (this.username.length < 3 || this.username.length > 50) {
      this.message = 'Username must be between 3 and 50 characters';
      return false;
    }

    this.message = '';
    return true;
  }

  isStep2Valid() {
    return this.password.trim() !== '' &&
      this.password.trim().length > 6;
  }

  nextStep() {
    if (this.currentStep === 1 && this.isStep1Valid()) {
      this.currentStep = 2;
    }
  }

  previousStep() {
    if (this.currentStep === 2) {
      this.currentStep = 1;
    }
  }
}
