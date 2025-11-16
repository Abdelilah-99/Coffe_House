import { Component, ElementRef, Inject, OnInit, PLATFORM_ID, ViewChild } from '@angular/core';
import { Like, Post, PostService, Comments, Message } from '../../services/post-service';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { MeService, UserProfile } from '../../../me/services/me.service';
import { AdminPannelSefvices } from '../../../admin-panel/services/admin-pannel-sefvices';
import { ToastService } from '../../../toast/service/toast';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-post-card',
  imports: [CommonModule],
  templateUrl: './post-card.html',
  styleUrl: './post-card.css'
})
export class PostCard implements OnInit {
  apiUrl = environment.apiUrl;
  constructor(private postService: PostService,
    private route: ActivatedRoute,
    @Inject(PLATFORM_ID) private platformId: Object,
    private navigate: Router,
    private profileService: MeService,
    private adminService: AdminPannelSefvices,
    private toast: ToastService) { }
  @ViewChild('anchor') anchor!: ElementRef<HTMLElement>;
  postUuid: String | null = null;
  post?: Post;
  like?: Like;
  comment?: Comments;
  isCommenting = false;
  profileData: UserProfile | null = null;
  reportAction = false;
  showReportConfirmation = false;
  pendingReportReason: String = '';
  message?: Message;
  isAdmin: boolean = false;
  showAdminHideConfirmation = false;
  showAdminDeleteConfirmation = false;
  errorMessage: string | null = null;
  toastMessage: { text: string, type: 'success' | 'error' | 'warning' } | null = null;

  ngOnInit(): void {
    this.loadProfile();

    this.route.paramMap.subscribe(params => {
      this.postUuid = params.get('id');
      this.loadCardData();
    });

    if (isPlatformBrowser(this.platformId)) {
      this.isAdmin = localStorage.getItem('user_role') === 'ROLE_ADMIN';
    }
  }

  loadProfile() {
    if (isPlatformBrowser(this.platformId)) {
      this.profileService.getProfile().subscribe({
        next: (data) => {
          this.profileData = data;
        },
        error: (err) => {
          console.log("err loading profile for post check: ", err);
        }
      });
    }
  }

  loadCardData() {
    if (isPlatformBrowser(this.platformId) && this.postUuid) {
      this.postService.getPost(this.postUuid).subscribe({
        next: (post) => {
          this.post = post;
        },
        error: (err) => {
          // console.log("errorl loading post ", err);
          if (err.status === 404 || err.error?.message?.includes('not available')) {
            this.toast.show("post unavailable", 'error')
            this.navigate.navigate(['']);
          }
        }
      })
    }
  }

  instantComments?: { userUuid: String, image: string, comment: String, username: String, time: number }[] = [];

  onSubmitComment(comment: String, uuid: String | null = null) {
    console.log(":jefh");

    if (!uuid || comment.trim() === "") {
      const message = 'Comment cannot be empty';
      this.toast.show(message, this.toast.getMessageType(message));
      return;
    }

    if (comment.length > 1000) {
      const message = 'Comment must not exceed 1000 characters';
      this.toast.show(message, this.toast.getMessageType(message));
      return;
    }

    this.postService.submitComment(comment, uuid).subscribe({
      next: (res) => {
        if (this.profileData) {
          this.instantComments?.push({
            userUuid: this.profileData.uuid,
            image: this.profileData.profileImagePath,
            comment: res.comment,
            username: this.profileData.username || '',
            time: Date.now()
          });
        }
        this.instantComments?.reverse();
        if (this.post) {
          this.post.commentCount += 1;
        }
        this.message = undefined;
      },
      error: (err) => {
        console.log(err.error.message);
        const message = err.error?.message || 'Failed to submit comment. Please try again.';
        this.toast.show(message, this.toast.getMessageType(message));
      }
    })
  }

  isImage(type: string): boolean {
    return type.startsWith('image/');
  }

  isVideo(type: string): boolean {
    return type.startsWith('video/');
  }

  onReact(uuid: String) {
    this.postService.doReaction(uuid).subscribe({
      next: (like) => {
        if (this.post) {
          this.post.likeCount = like.likeCount;
        }
        this.errorMessage = null;
        this.toastMessage = null;
      },
      error: (err) => {
        console.log("error loading like data ", err.error?.message);
        const message = err.error?.message || 'Failed to like post. Please try again.';
        this.toast.show(message, this.toast.getMessageType(message));
      }
    });
  }

  onComment(uuid: String) {
    this.isCommenting = !this.isCommenting;
    this.instantComments = [];
    if (this.isCommenting) {
      this.postService.getComments(uuid).subscribe({
        next: (comment) => {
          this.comment = comment;
          this.errorMessage = null;
          this.toastMessage = null;
        },
        error: (err) => {
          console.log("error loading comments ", err.error.message);
          const message = err.error?.message || 'Failed to load comments. Please try again.';
          this.toast.show(message, this.toast.getMessageType(message));
        }
      })
    }
  }

  onEdit(postUuid: String) {
    this.navigate.navigate(['/edit', postUuid]);
  }

  onReport() {
    this.reportAction = !this.reportAction;
  }

  onPrepareSubmitReport(reason: String) {
    if (!reason || reason.trim() === '') {
      this.toast.show('Please describe the issue before submitting your report.', 'error');
      return;
    }
    if (reason.length > 200) {
      this.toast.show('report must be under 200 charchter', 'error');
      return;
    }
    this.pendingReportReason = reason;
    this.showReportConfirmation = true;
  }

  onConfirmReport() {
    if (this.post && this.pendingReportReason) {
      this.submitReport(this.post.postUuid, this.pendingReportReason);
    }
    this.showReportConfirmation = false;
    this.pendingReportReason = '';
  }

  onCancelReport() {
    this.showReportConfirmation = false;
    this.pendingReportReason = '';
  }

  submitReport(uuid: String, reason: String) {
    this.postService.doReport(uuid, reason).subscribe({
      next: (res) => {
        this.message = res;
        this.reportAction = false;
        this.toast.show(String(res.message), 'success');
      },
      error: (err) => {
        console.log(err);
        const message = err.error?.message || 'Failed to submit report. Please try again.';
        this.toast.show(message, this.toast.getMessageType(message));
        this.reportAction = false;
      }
    });
  }

  onDelete(uuid: String) {
    this.postService.deletePost(uuid).subscribe({
      next: (res) => {
        this.toast.show("post has deleted", 'success');
        this.navigate.navigate(['']);
      },
      error: (err) => {
        console.log("error deleting post ", err);
        this.toast.show("error deleting post", 'error');
      }
    })
  }

  myPost(userUuid: String): boolean {
    if (isPlatformBrowser(this.platformId)) {
      const token = localStorage.getItem('access_token');
      if (!token) {
        this.navigate.navigate(['/login']);
      }
    }

    if (userUuid === this.profileData?.uuid) {
      return true;
    }
    return false;
  }

  onPrepareAdminHidePost() {
    this.showAdminHideConfirmation = true;
  }

  onPrepareAdminDeletePost() {
    this.showAdminDeleteConfirmation = true;
  }

  onConfirmAdminHidePost() {
    if (this.post) {
      this.adminHidePost(this.post.postUuid);
    }
    this.showAdminHideConfirmation = false;
  }

  onConfirmAdminDeletePost() {
    if (this.post) {
      this.adminDeletePost(this.post.postUuid);
    }
    this.showAdminDeleteConfirmation = false;
  }

  onCancelAdminHidePost() {
    this.showAdminHideConfirmation = false;
  }

  onCancelAdminDeletePost() {
    this.showAdminDeleteConfirmation = false;
  }

  adminHidePost(postUuid: String) {
    this.adminService.hidePost(postUuid).subscribe({
      next: () => {
        this.loadCardData();
      },
      error: (err) => {
        console.log("Error hiding post: ", err);
      }
    });
  }

  adminDeletePost(postUuid: String) {
    this.adminService.deletePost(postUuid).subscribe({
      next: () => {
        this.navigate.navigate(['']);
      },
      error: (err) => {
        console.log("Error deleting post: ", err);
      }
    });
  }

  moveToProfile(uuid: String | undefined) {
    this.navigate.navigate(['profile', uuid]);
  }

  onDeleteComment(commentUuid: String) {
    this.postService.deleteComment(commentUuid).subscribe({
      next: (res) => {
        this.toast.show(String(res.message), 'success');
        if (this.post && this.comment) {
          this.post.commentCount = Math.max(0, this.post.commentCount - 1);
          this.comment.comments = this.comment.comments.filter(c => c.uuid !== commentUuid);
        }
      },
      error: (err) => {
        console.log('Error deleting comment:', err);
        const message = err.error?.message || 'Failed to delete comment. Please try again.';
        this.toast.show(message, this.toast.getMessageType(message));
      }
    });
  }

  myComment(commentUserUuid: String): boolean {
    return commentUserUuid === this.profileData?.uuid;
  }
}
