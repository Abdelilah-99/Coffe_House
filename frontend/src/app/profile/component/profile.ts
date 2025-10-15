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

  // Admin action confirmation state
  showAdminBanConfirmation = false;
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
        console.error("error fetching user profile ", err);
      }
    })
  }

  onReport() {
    this.reportAction = !this.reportAction;
  }

  onPrepareSubmitReport(uuid: String, reason: String) {
    if (!reason || reason.trim() === '') {
      alert('Please provide additional details for your report.');
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
      },
      error: (err) => {
        console.error("error reporting ", err);
      }
    })
  }

  loadProfile(uuid: String) {
    this.profileService.getProfile(uuid).subscribe({
      next: (res) => {
        this.profileRes = res;
        this.followers = this.profileRes?.follower;
        this.myProfile(this.profileRes.uuid);
        this.loadUserPosts(uuid);
        console.log("profile has come succesfully ", this.profileRes.username);
      },
      error: (err) => {
        console.error("error getting profile ", err);
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
        console.error("Err loading user posts: ", err);
        this.isLoadingPosts = false;
      }
    })
  }

  block?: boolean;

  followLogic(userName: String, connect: boolean) {
    if (connect && this.profileRes?.uuid) {
      this.profileService.unFollow(this.profileRes?.uuid).subscribe({
        next: (res) => {
          this.followRes = res;
          this.followers = this.profileRes?.follower;
          console.log("unfollow succeed");
          if (this.profileRes) {
            this.profileRes.connect = false;
            if (this.followers && (this.block === undefined || this.block === true)) {
              this.followers--;
              this.block = true;
            }
          }
        },
        error: (err) => {
          console.error("error unfollowing ", err);
        }
      })
    }
    if (!connect && this.profileRes?.uuid) {
      this.profileService.follow(this.profileRes?.uuid).subscribe({
        next: (res) => {
          this.followRes = res;
          this.followers = this.profileRes?.follower;
          console.error("followers: ", this.followers);
          console.log("follow succeed");
          if (this.profileRes) {
            this.profileRes.connect = true;
            if (this.followers != null && (this.block === undefined || this.block === false)) {
              this.followers++;
              this.block = false;
            }
          }
        },
        error: (err) => {
          console.error("error following ", err);
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
        console.error("Error liking post: ", err);
      }
    });
  }

  // Admin actions - prepare
  onPrepareAdminBanUser() {
    this.showAdminBanConfirmation = true;
  }

  // Admin actions - confirm
  onConfirmAdminBanUser() {
    if (this.profileRes) {
      this.adminBanUser(this.profileRes.uuid);
    }
    this.showAdminBanConfirmation = false;
  }

  // Admin actions - cancel
  onCancelAdminBanUser() {
    this.showAdminBanConfirmation = false;
  }

  // Actual admin API call
  adminBanUser(uuid: String) {
    this.adminService.banUser(uuid).subscribe({
      next: (res) => {
        console.log("User ban status toggled successfully");
        if (this.uuid) {
          this.loadProfile(this.uuid);
        }
      },
      error: (err) => {
        console.error("Error toggling user ban status: ", err);
      }
    });
  }
}
