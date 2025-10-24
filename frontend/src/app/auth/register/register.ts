import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
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
  errorMessage: string | null = null;
  currentStep: number = 1;

  constructor(private authService: AuthService, private router: Router) { }
  ngOnInit() {
    if (typeof window !== 'undefined' && window.localStorage) {
      const token = localStorage.getItem('access_token');
      if (token) {
        this.router.navigate(['/me']);
      }
    }
  }

  selectedFile: File | null = null;
  profileImagePreview: string | null = null;

  onFileSelected(e: any) {
    const files: FileList = e.target.files;

    if (!files || files.length === 0) {
      this.selectedFile = null;
      this.profileImagePreview = null;
      return;
    }
    this.selectedFile = files[0];
    if (!files[0].type.includes("image")) {
      this.errorMessage = "format image are only allowed";
      console.log(files[0].type.split('/')[0]);
      return;
    }
    if (files[0].size > 10 * 1024 * 1024) {
      this.errorMessage = "Max upload size exceeded";
      return;
    }
    this.profileImagePreview = URL.createObjectURL(files[0]);
    console.log(this.profileImagePreview);
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
      formData.append("profileImage", this.selectedFile);
    }

    this.authService.register(formData).subscribe({
      next: () => {
        this.errorMessage = "register successful!!";
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 500);
      },
      error: (err) => {
        console.log('err: ', err.error.message);
        this.errorMessage = err.error.message;

        if (!err.error.message.includes('Password')) {
          this.currentStep = 1;
        }
        this.hideErrorAfterDelay();
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

  hideErrorAfterDelay() {
    setTimeout(() => {
      this.errorMessage = null;
    }, 3000);
  }
}
