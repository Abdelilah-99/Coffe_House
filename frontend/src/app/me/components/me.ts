import { Component, Inject, OnInit } from '@angular/core';
import { MeService, UserProfile } from '../services/me.service';
import { Post, PostService } from '../../post/services/post-service';
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
export class Me implements OnInit {
  userProfile: UserProfile | null = null;
  userPosts: Post[] = [];
  isLoading = false;
  isLoadingPosts = false;
  post = { title: '', content: '' };
  constructor(private profileService: MeService,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
    private postService: PostService) { }

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
          console.error("Err loading profile: ", err);
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
        console.error("Err loading user posts: ", err);
        this.isLoadingPosts = false;
      }
    })
  }
  selectedFiles: File[] = [];
  imagePreviews: { [key: number]: string } = {};

  onFileSelected(e: any) {
    const files: FileList = e.target.files;
    if (!files || files.length === 0) return;
    for (let i = 0; i < files.length; i++) {
      this.selectedFiles.push(files[i]);
    }
    console.log('Selected files:', this.selectedFiles.map(f => f.name));
  }

  getImagePreview(file: File): string {
    const index = this.selectedFiles.indexOf(file);
    if (!this.imagePreviews[index]) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.imagePreviews[index] = e.target.result;
      };
      reader.readAsDataURL(file);
    }
    return this.imagePreviews[index] || '';
  }

  removeImage(index: number) {
    this.selectedFiles.splice(index, 1);
    delete this.imagePreviews[index];
    // Reindex the previews
    const newPreviews: { [key: number]: string } = {};
    Object.keys(this.imagePreviews).forEach((key) => {
      const oldIndex = parseInt(key);
      if (oldIndex > index) {
        newPreviews[oldIndex - 1] = this.imagePreviews[oldIndex];
      } else if (oldIndex < index) {
        newPreviews[oldIndex] = this.imagePreviews[oldIndex];
      }
    });
    this.imagePreviews = newPreviews;
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
      next: () => {
        this.loadUserPosts(this.userProfile!.uuid);
      }
    })
    this.post.title = '';
    this.post.content = '';
    this.selectedFiles = [];
    this.imagePreviews = {};
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
        console.error("Error liking post: ", err);
      }
    });
  }
}
