import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { Like, Post, PostService, Comments, Message } from '../../services/post-service';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { MeService, UserProfile } from '../../../me/services/me.service';
import { AdminPannelSefvices } from '../../../admin-panel/services/admin-pannel-sefvices';

@Component({
  selector: 'app-post-card',
  imports: [CommonModule],
  templateUrl: './post-card.html',
  styleUrl: './post-card.css'
})
export class PostCard implements OnInit {
  constructor(private postService: PostService,
    private route: ActivatedRoute,
    @Inject(PLATFORM_ID) private platformId: Object,
    private navigate: Router,
    private profileService: MeService,
    private adminService: AdminPannelSefvices) { }
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
          console.log("test: ", this.profileData);
        },
        error: (err) => {
          console.error("err loading profile for post check: ", err);
        }
      });
    }
  }

  loadCardData() {
    if (isPlatformBrowser(this.platformId) && this.postUuid) {
      this.postService.getPost(this.postUuid).subscribe({
        next: (post) => {
          this.post = post;
          console.log(this.post);
        },
        error: (err) => {
          console.error("errorl loading post ", err);
          if (err.status === 404 || err.error?.message?.includes('not available')) {
            alert('This post is not available');
            this.navigate.navigate(['']);
          }
        }
      })
    }
  }

  instantComments?: { userUuid: String, image: string, comment: String, username: String, time: number }[] = [];

  onSubmitComment(comment: String, uuid: String | null = null) {
    if (!uuid || comment.trim() === "") {
      this.message = { message: 'Comment cannot be empty' };
      return;
    }

    if (comment.length > 1000) {
      this.message = { message: 'Comment must not exceed 1000 characters' };
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
        console.error(err.error.message);
        this.errorMessage = err.error?.message || 'Failed to submit comment. Please try again.';
        this.hideErrorAfterDelay();
      }
    })
  }

  getMediaType(media: String): String {
    const ext = media.split('.').pop()?.toLowerCase();
    if (['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(ext!)) {
      return 'img';
    } else if (['mp4', 'webm', 'ogg'].includes(ext!)) {
      return 'vd';
    }
    return 'null';
  }

  onReact(uuid: String) {
    this.postService.doReaction(uuid).subscribe({
      next: (like) => {
        if (this.post) {
          this.post.likeCount = like.likeCount;
        }
        this.errorMessage = null;
      },
      error: (err) => {
        console.error("error loading like data ", err);
        this.errorMessage = err.error?.message || 'Failed to like post. Please try again.';
        this.hideErrorAfterDelay();
      }
    });
  }

  onComment(uuid: String) {
    this.isCommenting = !this.isCommenting;
    console.log(this.isCommenting);
    this.instantComments = [];
    if (this.isCommenting) {
      this.postService.getComments(uuid).subscribe({
        next: (comment) => {
          this.comment = comment;
          console.log("comment.comments; ", comment.comments);
          this.errorMessage = null;
        },
        error: (err) => {
          console.error("error loading comments ", err.error.message);
          this.errorMessage = err.error?.message || 'Failed to load comments. Please try again.';
          this.hideErrorAfterDelay();
        }
      })
    }
  }

  onEdit(postUuid: String) {
    console.log("this section will navigate throw the post section ", postUuid);
    this.navigate.navigate(['/edit', postUuid]);
  }
  onReport() {
    this.reportAction = !this.reportAction;
    console.log("hii from outside");
  }

  onPrepareSubmitReport(reason: String) {
    if (!reason || reason.trim() === '') {
      alert('Please describe the issue before submitting your report.');
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
    console.log("hii from inside ", uuid, reason);
    this.postService.doReport(uuid, reason).subscribe({
      next: (res) => {
        this.message = res;
        this.reportAction = false;
      },
      error: (err) => {
        console.error(err);
      }
    });
  }

  onDelete(uuid: String) {
    this.postService.deletePost(uuid).subscribe({
      next: (res) => {
        console.log("post has been deleted ", res);
        this.navigate.navigate(['']);
      },
      error: (err) => {
        console.error("error deleting post ", err);
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
    console.log(userUuid + "t" + this.profileData?.uuid);

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
        console.log("Post hide/unhide status toggled successfully");
        this.loadCardData();
      },
      error: (err) => {
        console.error("Error hiding post: ", err);
      }
    });
  }

  adminDeletePost(postUuid: String) {
    this.adminService.deletePost(postUuid).subscribe({
      next: () => {
        console.log("Post deleted successfully");
        this.navigate.navigate(['']);
      },
      error: (err) => {
        console.error("Error deleting post: ", err);
      }
    });
  }

  moveToProfile(uuid: String | undefined) {
    console.log("====================================", uuid);

    this.navigate.navigate(['profile', uuid]);
  }

  hideErrorAfterDelay() {
    setTimeout(() => {
      this.errorMessage = null;
    }, 5000);
  }
}
