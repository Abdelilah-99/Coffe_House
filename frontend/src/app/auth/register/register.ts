import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { AuthService } from '../service/auth';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ToastService } from '../../toast/service/toast';

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
  currentStep: number = 1;
  show: boolean = false;

  constructor(private authService: AuthService,
    private router: Router,
    private toast: ToastService,
    @Inject(PLATFORM_ID) private platforId: Object) { }
  ngOnInit() {
    if (isPlatformBrowser(this.platforId)) {
      const token = localStorage.getItem('access_token');
      if (token) {
        this.toast.show("Already logged in", 'warning');
        this.router.navigate(['/me']);
        this.show = false;
      } else {
        this.show = true;
      }
    }
  }

  selectedFile: File | null = null;
  profileImagePreview: string = '';

  onFileSelected(e: any) {
    const files: FileList = e.target.files;

    if (!files || files.length === 0) {
      this.selectedFile = null;
      this.profileImagePreview = '';
      return;
    }
    this.selectedFile = files[0];
    if (!files[0].type.includes("image")) {
      this.toast.show("format image are only allowed!!", 'error');
      return;
    }
    if (files[0].size > 10 * 1024 * 1024) {
      this.toast.show("Max upload size exceeded!!", 'error');
      return;
    }
    this.profileImagePreview = URL.createObjectURL(files[0]);
    console.log(this.profileImagePreview);
    
  }

  removeProfileImage() {
    this.selectedFile = null;
    this.profileImagePreview = '';
    const fileInput = document.getElementById('profileImage') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = '';
    }
    URL.revokeObjectURL(this.profileImagePreview);
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
        this.toast.show("register successful!!", 'success');
        URL.revokeObjectURL(this.profileImagePreview)
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.log('err: ', err.error.message);
        this.toast.show(err.error.message, 'error');
        if (!err.error.message.includes('Password')) {
          this.currentStep = 1;
        }
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
}
