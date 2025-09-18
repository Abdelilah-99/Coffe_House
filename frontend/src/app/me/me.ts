import { Component, Inject, OnInit } from '@angular/core';
import { ProfileService, UserProfile } from './me.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID } from '@angular/core';
@Component({
  selector: 'app-profile',
  imports: [CommonModule, FormsModule],
  templateUrl: './me.html',
  styleUrl: './me.css'
})
export class Profile implements OnInit {
  userProfile: UserProfile | null = null;
  isLoading = false;
  post = { title: '', content: '' };
  constructor(private profileService: ProfileService,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object) { }

  ngOnInit() {
    this.loadProfile();
  }
  loadProfile() {
    if (isPlatformBrowser(this.platformId)) {
      const token = localStorage.getItem('access_token');
      if (!token) {
        this.router.navigate(['/login']);
        return;
      }
      this.isLoading = !this.isLoading;
      this.profileService.getProfile().subscribe({
        next: (profile) => {
          this.userProfile = profile;
          this.isLoading = !this.isLoading;
        },
        error: (err) => {
          console.error("Err loading profile: ", err);
          this.isLoading = !this.isLoading;
          if (err.status == 403) {
            localStorage.removeItem('access_token');
            this.router.navigate(['/login']);
            return;
          }
        }
      })
    }
  }
  selectedFiles: File[] = [];

  onFileSelected(e: any) {
    const files: FileList = e.target.files;
    if (!files || files.length === 0) return;
    // this.selectedFiles = [];
    for (let i = 0; i < files.length; i++) {
      this.selectedFiles.push(files[i]);
    }
    console.log('Selected files:', this.selectedFiles.map(f => f.name));
  }

  onCreatePost() {
    const formData = new FormData();
    formData.append("title", this.post.title);
    formData.append("content", this.post.content);
    console.log(formData.get("title"));

    this.selectedFiles.forEach(element => {
      formData.append("mediaFiles", element);
    });
    this.profileService.createPost(formData).subscribe({
    })
  }
}
