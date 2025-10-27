import { Component, ElementRef, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
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
export class Me implements OnInit, OnDestroy {
  @ViewChild('anchor', { static: true }) anchor!: ElementRef<HTMLElement>;
  observer!: IntersectionObserver;
  lastUuid: string | null = null;
  lastTime: number | null = null;
  isLoding = false;
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

  private initObserver() {
    if (!isPlatformBrowser(this.platformId)) return;
    this.observer = new IntersectionObserver((entries) => {
      if (entries[0].isIntersecting && !this.isLoding && this.lastTime && this.lastUuid) {
        console.log("hii");
        this.loadPostByPage(this.lastTime, this.lastUuid);
      }
    });
    console.log(this.anchor.nativeElement);
    this.observer.observe(this.anchor.nativeElement);
  }

  loadPostByPage(lastTime: number | null, lastUuid: string | null, onFinish?: () => void) {
    this.isLoding = true;
    this.postService.loadMore(lastTime, lastUuid).subscribe({
      next: (res) => {
        this.lastTime = res.lastTime;
        if (res.lastUuid) {
          this.lastUuid = res.lastUuid.toString();
        }
        this.userPosts.push(...res.posts);

        this.isLoding = false;
        console.log(res.posts);
        if (onFinish) onFinish();
      },
      error: (err) => {
        console.log("failed loading pages: ", err);
        this.isLoding = false;
        if (onFinish) onFinish();
      }
    });
  }

  ngOnInit() {
    this.loadProfile();
    this.loadPostByPage(null, null, () => {
      this.initObserver();
    });
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
    console.log(this.selectedFiles.length);

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
        // this.loadUserPosts(this.userProfile!.uuid);
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
  ngOnDestroy(): void {
    if (this.observer) this.observer.disconnect();
  }
}
