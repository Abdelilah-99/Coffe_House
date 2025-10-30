import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { Post, PostService } from '../post/services/post-service';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MeService, UserProfile } from '../me/services/me.service';
import { ToastService } from '../toast/service/toast';

@Component({
  selector: 'app-edit',
  imports: [CommonModule, FormsModule],
  templateUrl: './edit.html',
  styleUrls: ['./edit.css']
})
export class Edit implements OnInit {
  constructor(
    private postService: PostService,
    private route: ActivatedRoute,
    @Inject(PLATFORM_ID) private platformId: Object,
    public navigate: Router,
    private meService: MeService,
    private toast: ToastService
  ) { }
  post: Post | null = null;
  postUuid: String | null = null;
  updatedPost: Post | null = null;
  message?: string;
  currentUser: UserProfile | null = null;
  isAuthorized: boolean = false;
  toastMessage: { text: string, type: 'success' | 'error' | 'warning' } | null = null;

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.postUuid = params.get('id');
      this.loadCurrentUser();
    });
  }

  loadCurrentUser() {
    if (isPlatformBrowser(this.platformId)) {
      this.meService.getProfile().subscribe({
        next: (user) => {
          this.currentUser = user;
          this.loadPost();
        },
        error: (err) => {
          console.log("Error loading user profile: ", err);
          this.toast.show('You must be logged in to edit posts', 'error');
          this.navigate.navigate(['/login']);
        }
      });
    }
  }

  loadPost() {
    if (this.postUuid && isPlatformBrowser(this.platformId)) {
      this.postService.getPost(this.postUuid).subscribe({
        next: (post) => {
          if (this.currentUser && post.userUuid !== this.currentUser.uuid) {
            this.isAuthorized = false;
            this.navigate.navigate(['']);
            return;
          }
          this.post = post;
          this.isAuthorized = true;
        },
        error: () => {
          this.toast.show('Post not found', 'error');
          this.navigate.navigate(['']);
        }
      })
    }
  }

  deleteMedia(mediaPath: String) {
    let mediaPaths = this.post?.mediaPaths;
    if (mediaPaths === undefined) return;
    for (let index = 0; index < mediaPaths.length; index++) {
      const element = mediaPaths[index];
      if (mediaPath === element.path) {
        this.post?.mediaPaths.splice(index, 1);
      }
    }
  }

  selectedFiles: File[] = [];
  previewUrls: { url: String, fileType: string }[] = [];
  oldFileLen: number = 0;

  onFileSelected(e: any) {
    const files: FileList = e.target.files;
    if (this.post) {
      this.oldFileLen = this.post.mediaPaths.length;
    }
    if (!files || files.length === 0) return;
    if (files.length + this.oldFileLen + this.selectedFiles.length > 5) {
      this.toast.show("5 file maximum", "error");
      return;
    }
    for (let i = 0; i < files.length; i++) {
      if (!files[i].type.includes("image") && !files[i].type.includes("video")) {
        this.toast.show("format image and video are only allowed", "error");
        return;
      }
      if (files[i].size > 100 * 1024 * 1024) {
        this.toast.show("Max upload size exceeded", "error");
        return;
      }
      const file = files[i];
      this.selectedFiles.push(file);
      const url = URL.createObjectURL(file);
      this.previewUrls.push({ url: url, fileType: file.type });
    }
    e.target.value = '';
  }

  deleteFileSelected(index: number) {
    this.selectedFiles.splice(index, 1);
    this.previewUrls.splice(index, 1);
  }

  onSave(updatedPost: any) {
    if (!updatedPost.title || updatedPost.title.trim().length === 0) {
      this.toast.show("Title is required", 'error');
      return;
    }
    if (updatedPost.title.length > 200) {
      this.toast.show("Title must not exceed 200 characters", 'error');
      return;
    }

    if (!updatedPost.content || updatedPost.content.trim().length === 0) {
      this.toast.show("Content is required", 'error');
      return;
    }
    if (updatedPost.content.length > 10000) {
      this.toast.show("Content must not exceed 10000 characters", 'error');
      return;
    }

    const formData = new FormData();
    formData.append("title", updatedPost.title);
    formData.append("content", updatedPost.content);
    const pathsOnly = updatedPost.mediaPaths.map((media: any) => media.path).join(',');
    formData.append("pathFiles", pathsOnly);
    this.selectedFiles.forEach(element => {
      formData.append("mediaFiles", element);
    });
    this.postService.editPost(updatedPost.postUuid, formData).subscribe({
      next: (data) => {
        this.updatedPost = data;
        this.toast.show("Post updated successfully", 'success');
        this.navigate.navigate(['']);
        this.selectedFiles = [];
        this.previewUrls = [];
      },
      error: (err) => {
        console.log("error updating post: ", err);
        const message = err.error?.message || 'Error updating post. Please try again.';
        this.toast.show(message, this.getMessageType(message));
      }
    });
  }

  isImage(type: string): boolean {
    return type.startsWith('image/');
  }

  isVideo(type: string): boolean {
    return type.startsWith('video/');
  }

  showToast(text: string, type: 'success' | 'error' | 'warning') {
    this.toastMessage = { text, type };
    setTimeout(() => {
      this.toastMessage = null;
    }, 2000);
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
