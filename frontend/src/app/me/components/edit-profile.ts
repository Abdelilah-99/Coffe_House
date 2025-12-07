import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MeService, UserProfile } from '../services/me.service';
import { ToastService } from '../../toast/service/toast';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-edit-profile',
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-profile.html',
  styleUrl: './edit-profile.css'
})
export class EditProfile implements OnInit {
  userProfile: UserProfile | null = null;
  isLoading = false;
  isSaving = false;
  apiUrl = environment.apiUrl;
  
  profileForm = {
    firstName: '',
    lastName: '',
    email: ''
  };

  selectedFile: File | null = null;
  previewUrl: string | null = null;

  constructor(
    private meService: MeService,
    private router: Router,
    private toast: ToastService
  ) { }

  ngOnInit() {
    this.loadProfile();
  }

  loadProfile() {
    this.isLoading = true;
    this.meService.getProfile().subscribe({
      next: (profile) => {
        this.userProfile = profile;
        this.profileForm.firstName = profile.firstName;
        this.profileForm.lastName = profile.lastName;
        this.profileForm.email = profile.email;
        this.isLoading = false;
      },
      error: (err) => {
        console.log("Error loading profile: ", err);
        this.toast.show("Failed to load profile", 'error');
        this.isLoading = false;
        this.router.navigate(['/me']);
      }
    });
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      this.toast.show("Only image files are allowed", "error");
      return;
    }

    if (file.size > 10 * 1024 * 1024) {
      this.toast.show("File size must be less than 10MB", "error");
      return;
    }

    this.selectedFile = file;
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.previewUrl = e.target.result;
    };
    reader.readAsDataURL(file);
    event.target.value = '';
  }

  removeImage() {
    this.selectedFile = null;
    this.previewUrl = null;
  }

  onSubmit() {
    if (this.profileForm.firstName.trim().length === 0 || 
        this.profileForm.lastName.trim().length === 0 || 
        this.profileForm.email.trim().length === 0) {
      this.toast.show("All fields are required", 'error');
      return;
    }

    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(this.profileForm.email)) {
      this.toast.show("Please enter a valid email address", 'error');
      return;
    }

    const formData = new FormData();
    const userBlob = new Blob([JSON.stringify(this.profileForm)], {
      type: 'application/json'
    });
    formData.append('user', userBlob);
    if (this.selectedFile) {
      formData.append('profileImage', this.selectedFile);
    }

    this.isSaving = true;
    this.meService.updateProfile(formData).subscribe({
      next: (response) => {
        this.toast.show(response.message, 'success');
        this.isSaving = false;
        this.router.navigate(['/me']);
      },
      error: (err) => {
        console.log("Error updating profile: ", err);
        this.toast.show(err.error?.message || "Failed to update profile", 'error');
        this.isSaving = false;
      }
    });
  }

  cancel() {
    this.router.navigate(['/me']);
  }
}

