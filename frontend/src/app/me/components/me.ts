import { Component, Inject, OnInit } from '@angular/core';
import { MeService, UserProfile } from '../services/me-service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID } from '@angular/core';
import { Post } from '../../post/services/post-service';
@Component({
  selector: 'app-profile',
  imports: [CommonModule, FormsModule],
  templateUrl: './me.html',
  styleUrl: './me.css'
})
export class Me implements OnInit {
  userProfile: UserProfile | null = null;
  myPosts?: Post[];
  isLoading = false;
  post = { title: '', content: '' };
  constructor(private profileService: MeService,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object) { }

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      this.loadProfile();
      this.loadMyPost();
    }
  }

  loadMyPost() {
    this.profileService.getMyPost().subscribe({
      next: (res) => {
        this.myPosts = res;
        console.log("myPosts: ", this.myPosts);
      },
      error: (err) => {
        console.error(err);
      }
    })
  }

  loadProfile() {
    const token = localStorage.getItem('access_token');
    if (!token) {
      this.router.navigate(['/login']);
      return;
    }
    this.isLoading = !this.isLoading;
    this.profileService.getProfile().subscribe({
      next: (res) => {
        this.userProfile = res;
        this.isLoading = !this.isLoading;
      },
      error: (err) => {
        console.error("Err loading profile: ", err);
        this.isLoading = !this.isLoading;
        throw new Error('Failed to load profile: ' + err.message);
      }
    })
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
    this.post.title = '';
    this.post.content = '';
  }
}
