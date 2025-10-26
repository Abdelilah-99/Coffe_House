import { Component, Inject, OnInit } from '@angular/core';
import { MeService, UserProfile } from '../services/me.service';
import { Post, PostService } from '../../post/services/post-service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID } from '@angular/core';
import { ToastService } from '../../toast/service/toast';
@Component({
  selector: 'app-profile',
  imports: [CommonModule, FormsModule],
  templateUrl: './me.html',
  styleUrl: './me.css'
})
export class Me implements OnInit {
  userProfile: UserProfile | null = null;
  userPosts: Post[] = [];
  isLoading = false;
  isLoadingPosts = false;
  post = { title: '', content: '' };
  message?: string;
  toastMessage: { text: string, type: 'success' | 'error' | 'warning' } | null = null;
  errorMessage: string | null = null;
  constructor(private profileService: MeService,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
    private postService: PostService,
    private toast: ToastService) { }

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
          this.loadUserPosts(profile.uuid);
        },
        error: (err) => {
          console.log("Err loading profile: ", err);
          this.isLoading = !this.isLoading;
          throw new Error('Failed to load profile: ' + err.message);
        }
      })
    }
  }

  loadUserPosts(userUuid: String) {
    this.isLoadingPosts = true;
    this.profileService.getUserPosts(userUuid).subscribe({
      next: (posts) => {
        this.userPosts = posts;
        this.isLoadingPosts = false;
      },
      error: (err) => {
        console.log("Err loading user posts: ", err);
        this.isLoadingPosts = false;
      }
    })
  }
  selectedFiles: File[] = [];
  previewUrls: { url: String, type: string }[] = [];

  onFileSelected(e: any) {
    const files: FileList = e.target.files;
    if (files.length > 5 || this.selectedFiles.length > 4) {
      this.toast.show("5 file maximum", "error");
      return;
    }
    if (!files || files.length === 0) return;
    for (let i = 0; i < files.length; i++) {
      if (!files[i].type.includes("image") && !files[i].type.includes("video")) {
        this.toast.show("format image and video are only allowed", "error");
        console.log(files[i].type.split('/')[0]);
        return;
      }
      if (files[i].size > 100 * 1024 * 1024) {
        this.toast.show("Max upload size exceeded", "error");
        return;
      }
      const file = files[i];
      this.selectedFiles.push(file);
      const url = URL.createObjectURL(file);
      this.previewUrls.push({ url: url, type: file.type });
    }
    e.target.value = '';
  }

  deleteFileSelected(index: number) {
    this.selectedFiles.splice(index, 1);
    this.previewUrls.splice(index, 1);
  }

  onCreatePost() {
    const formData = new FormData();
    if (this.post.title.trim().length === 0 || this.post.content.trim().length === 0) {
      this.message = "content and title fields are required";
      this.toast.show(this.message, 'error');
      return;
    }
    if (this.post.title.length > 200) {
      this.message = "Title must not exceed 200 characters";
      this.toast.show(this.message, 'error');
      return;
    }
    if (this.post.content.length > 10000) {
      this.message = "Content must not exceed 10000 characters";
      this.toast.show(this.message, 'error');
      return;
    }
    formData.append("title", this.post.title);
    formData.append("content", this.post.content);
    this.message = "";

    this.selectedFiles.forEach(element => {
      if (!element.type.includes("image") && !element.type.includes("video")) {
        this.message = "format image and video are only allowed";
        console.log(element.type.split('/')[0]);
        return;
      }
      formData.append("mediaFiles", element);
    });
    this.profileService.createPost(formData).subscribe({
      next: () => {
        this.toast.show("Post has successfully created", 'success');
        this.loadUserPosts(this.userProfile!.uuid);
      },
      error: (err) => {
        this.toast.show(err.error.message, 'error');
        console.log("failed to create post: ", err);
      }
    })
    this.post.title = '';
    this.post.content = '';
    this.selectedFiles = [];
    this.previewUrls = [];
  }

  navigateToPost(postUuid: String) {
    this.router.navigate(['/postCard', postUuid]);
  }

  handleLike(event: Event, postUuid: String) {
    event.stopPropagation();
    this.postService.doReaction(postUuid).subscribe({
      next: (res) => {
        const post = this.userPosts.find(p => p.postUuid === postUuid);
        if (post) {
          post.likeCount = res.likeCount;
        }
      },
      error: (err) => {
        this.toast.show(err.error.message, 'error');
        console.log("Error liking post: ", err);
      }
    });
  }

  hideErrorAfterDelay() {
    setTimeout(() => {
      this.errorMessage = null;
    }, 5000);
  }

  showToast(text: string, type: 'success' | 'error' | 'warning') {
    this.toastMessage = { text, type };

    setTimeout(() => {
      this.toastMessage = null;
    }, 5000);
  }

  getMessageType(message: string): 'success' | 'error' | 'warning' {
    const lowerMessage = message.toLowerCase();
    if (lowerMessage.includes('banned') || lowerMessage.includes('deleted')) {
      return 'warning';
    }
    if (lowerMessage.includes('success') || lowerMessage.includes('successfully')) {
      return 'success';
    }
    return 'error';
  }
}
