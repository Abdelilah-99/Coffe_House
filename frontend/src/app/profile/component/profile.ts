import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProfileService, ProfileRes, FollowRes, Message } from '../services/services';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { MeService, UserProfile } from '../../me/services/me.service';
import { Post, PostService } from '../../post/services/post-service';
import { AdminPannelSefvices } from '../../admin-panel/services/admin-pannel-sefvices';

@Component({
  selector: 'app-profile',
  imports: [CommonModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile implements OnInit {
  profileRes?: ProfileRes;
  followRes?: FollowRes;
  reportAction = false;
  showReportConfirmation = false;
  pendingReportReason: String = '';
  message?: Message;
  ifCrrProfile = true;
  userProfile?: UserProfile;
  userPosts: Post[] = [];
  isLoadingPosts = false;
  following?: number;
  followers?: number;
  isAdmin: boolean = false;

  showAdminBanConfirmation = false;
  toastMessage: { text: string, type: 'success' | 'error' | 'warning' } | null = null;
  constructor(private route: ActivatedRoute, private navigate: Router,
    private profileService: ProfileService,
    @Inject(PLATFORM_ID) private platformId: Object,
    private meProfile: MeService,
    private postService: PostService,
    private adminService: AdminPannelSefvices) { }
  uuid: String | null = null;
  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.uuid = params.get('id');
      if (this.uuid && isPlatformBrowser(this.platformId)) {
        this.loadProfile(this.uuid);
        this.message = undefined;
      }
    });
    if (isPlatformBrowser(this.platformId)) {
      this.isAdmin = localStorage.getItem('user_role') === 'ROLE_ADMIN';
    }
  }

  myProfile(uuid: String): void {
    this.meProfile.getProfile().subscribe({
      next: (res) => {
        this.userProfile = res;
        if (this.userProfile.uuid == uuid) {
          this.ifCrrProfile = true;
        } else {
          this.ifCrrProfile = false;
        }
      },
      error: (err) => {
        console.log("error fetching user profile ", err);
      }
    })
  }

  onReport() {
    this.reportAction = !this.reportAction;
  }

  onPrepareSubmitReport(uuid: String, reason: String) {
    if (!reason || reason.trim() === '') {
      this.showToast("Please provide additional details for your report", "error");
      return;
    }
    this.pendingReportReason = reason;
    this.showReportConfirmation = true;
  }

  onConfirmReport() {
    if (this.profileRes && this.pendingReportReason) {
      this.onSubmitReport(this.profileRes.uuid, this.pendingReportReason);
    }
    this.showReportConfirmation = false;
    this.pendingReportReason = '';
  }

  onCancelReport() {
    this.showReportConfirmation = false;
    this.pendingReportReason = '';
  }

  onSubmitReport(uuid: String, reason: String) {
    this.profileService.doReport(uuid, reason).subscribe({
      next: (res) => {
        this.message = res;
        this.reportAction = false;
        this.showToast(String(res.message), 'success');
      },
      error: (err) => {
        console.log("error reporting ", err);
        const message = err.error?.message || 'Failed to submit report. Please try again.';
        this.showToast(message, this.getMessageType(message));
        this.reportAction = false;
      }
    })
  }

  loadProfile(uuid: String) {
    this.profileService.getProfile(uuid).subscribe({
      next: (res) => {
        this.profileRes = res;
        this.followers = this.profileRes.follower;
        this.following = this.profileRes.following;
        this.myProfile(this.profileRes.uuid);
        this.loadUserPosts(uuid);
      },
      error: (err) => {
        console.log("error getting profile ", err);
      }
    })
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

  block: boolean = false;

  followLogic(userName: String, connect: boolean) {
    if (connect && this.profileRes?.uuid) {
      this.profileService.unFollow(this.profileRes?.uuid).subscribe({
        next: (res) => {
          console.log(res);
          this.followers = res.follower;
          this.following = res.following;
          if (this.profileRes) {
            this.profileRes.connect = false;
          }
          this.showToast('Successfully unfollowed ' + userName, 'success');
        },
        error: (err) => {
          const message = err.error?.message || 'Failed to unfollow user. Please try again.';
          this.showToast(message, this.getMessageType(message));
        }
      })
    }
    if (!connect && this.profileRes?.uuid) {
      this.profileService.follow(this.profileRes?.uuid).subscribe({
        next: (res) => {
          this.followers = res.follower;
          this.following = res.following;
          if (this.profileRes) {
            this.profileRes.connect = true;
          }
          this.showToast('Successfully followed ' + userName, 'success');
        },
        error: (err) => {
          console.log("error following ", err);
          const message = err.error?.message || 'Failed to follow user. Please try again.';
          this.showToast(message, this.getMessageType(message));
        }
      })
    }
  }

  navigateToPost(postUuid: String) {
    this.navigate.navigate(['/postCard', postUuid]);
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
        console.log("Error liking post: ", err);
      }
    });
  }

  onPrepareAdminBanUser() {
    this.showAdminBanConfirmation = true;
  }

  onConfirmAdminBanUser() {
    if (this.profileRes) {
      this.adminBanUser(this.profileRes.uuid);
    }
    this.showAdminBanConfirmation = false;
  }

  onCancelAdminBanUser() {
    this.showAdminBanConfirmation = false;
  }

  adminBanUser(uuid: String) {
    this.adminService.banUser(uuid).subscribe({
      next: () => {
        if (this.uuid) {
          this.loadProfile(this.uuid);
        }
      },
      error: (err) => {
        console.log("Error toggling user ban status: ", err);
      }
    });
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
