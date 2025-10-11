import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { Like, Post, PostService, Comments, Message } from '../../services/post-service';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { MeService, UserProfile } from '../../../me/services/me.service';

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
    private profileService: MeService) { }
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

  ngOnInit(): void {
    this.loadProfile();

    this.route.paramMap.subscribe(params => {
      this.postUuid = params.get('id');
      this.loadCardData();
    });
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
        }
      })
    }
  }

  instantComments?: { comment: String, username: String, time: number }[] = [];

  onSubmitComment(comment: String, uuid: String | null = null) {
    if (uuid) {
      this.postService.submitComment(comment, uuid).subscribe({
        next: (res) => {
          this.instantComments?.push({
            comment: res.comment,
            username: this.profileData?.username || '',
            time: Date.now()
          });
          this.instantComments?.reverse();
          if (this.post) {
            this.post.commentCount += 1;
          }
        },
        error: (err) => {
          console.error(err);
        }
      })
    }
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
      },
      error: (err) => {
        console.error("error loading like data ", err);
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
          console.log(comment);
        },
        error: (err) => {
          console.error("error loading comments ", err);
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
}
