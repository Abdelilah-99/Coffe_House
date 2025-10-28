import { Component, Inject, OnInit, PLATFORM_ID, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProfileService, ProfileRes, FollowRes, Message } from '../services/services';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { MeService, UserProfile } from '../../me/services/me.service';
import { Post, PostService } from '../../post/services/post-service';
import { AdminPannelSefvices } from '../../admin-panel/services/admin-pannel-sefvices';
import { ToastService } from '../../toast/service/toast';

@Component({
  selector: 'app-profile',
  imports: [CommonModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile implements OnInit, OnDestroy {
  @ViewChild('anchor') anchor!: ElementRef<HTMLElement>;
  observer!: IntersectionObserver;

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

  lastTime: number | null = null;
  lastUuid: string | null = null;

  showAdminBanConfirmation = false;
  toastMessage: { text: string, type: 'success' | 'error' | 'warning' } | null = null;
  constructor(private route: ActivatedRoute, private navigate: Router,
    private profileService: ProfileService,
    @Inject(PLATFORM_ID) private platformId: Object,
    private meProfile: MeService,
    private postService: PostService,
    private adminService: AdminPannelSefvices,
    private toastService: ToastService) { }
  uuid: String | null = null;
  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.uuid = params.get('id');
      if (this.uuid && isPlatformBrowser(this.platformId)) {
        this.userPosts = [];
        this.lastTime = null;
        this.lastUuid = null;
        if (this.observer) {
          this.observer.disconnect();
        }
        this.loadProfile(this.uuid);
        this.loadUserPostsByPage(this.uuid, null, null, () => {
          this.initObserver();
        });
        this.message = undefined;
      }
    });
    if (isPlatformBrowser(this.platformId)) {
      this.isAdmin = localStorage.getItem('user_role') === 'ROLE_ADMIN';
    }
  }

  ngOnDestroy(): void {
    if (this.observer) {
      this.observer.disconnect();
    }
  }

  private initObserver() {
    if (!isPlatformBrowser(this.platformId)) return;
    if (this.observer) {
      this.observer.disconnect();
    }
    this.observer = new IntersectionObserver((entries) => {
      if (entries[0].isIntersecting && !this.isLoadingPosts && this.lastTime && this.lastUuid && this.uuid) {
        this.loadUserPostsByPage(this.uuid, this.lastTime, this.lastUuid);
      }
    });
    this.observer.observe(this.anchor.nativeElement);
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
      this.toastService.show("Please provide additional details for your report", "error");
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
        this.toastService.show(String(res.message), 'success');
      },
      error: (err) => {
        console.log("error reporting ", err);
        const message = err.error?.message || 'Failed to submit report. Please try again.';
        this.toastService.show(message, this.toastService.getMessageType(message));
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
      },
      error: (err) => {
        console.log("error getting profile ", err);
      }
    })
  }

  loadUserPostsByPage(userUuid: String, lastTime: number | null, lastUuid: string | null, onFinish?: () => void) {
    this.isLoadingPosts = true;
    this.profileService.getUserPostsPaginated(userUuid, lastTime, lastUuid).subscribe({
      next: (res) => {
        this.lastTime = res.lastTime;
        if (res.lastUuid) {
          this.lastUuid = res.lastUuid.toString();
        }
        this.userPosts.push(...res.posts);
        this.isLoadingPosts = false;
        if (onFinish) onFinish();
      },
      error: (err) => {
        console.log("Err loading user posts: ", err);
        this.isLoadingPosts = false;
        if (onFinish) onFinish();
      }
    })
  }

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
          this.toastService.show('Successfully unfollowed ' + userName, 'success');
        },
        error: (err) => {
          const message = err.error?.message || 'Failed to unfollow user. Please try again.';
          this.toastService.show(message, this.toastService.getMessageType(message));
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
          this.toastService.show('Successfully followed ' + userName, 'success');
        },
        error: (err) => {
          console.log("error following ", err);
          const message = err.error?.message || 'Failed to follow user. Please try again.';
          this.toastService.show(message, this.toastService.getMessageType(message));
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
}
