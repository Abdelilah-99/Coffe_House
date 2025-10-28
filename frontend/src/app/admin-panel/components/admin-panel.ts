import { Component, Inject, OnInit, PLATFORM_ID, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import { AdminPannelSefvices, AdminStatisticsResponse, User, Report, TopUserResponse, TopPostResponse } from '../services/admin-pannel-sefvices';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserProfile } from '../../post/services/post-service';
import { UserService } from '../../searchbar/services/services';
import { ProfileService } from '../../profile/services/services';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-panel',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-panel.html',
  styleUrl: './admin-panel.css'
})
export class AdminPanel implements OnInit, OnDestroy {
  @ViewChild('userReportsAnchor', { static: true }) userReportsAnchor!: ElementRef<HTMLElement>;
  @ViewChild('postReportsAnchor', { static: true }) postReportsAnchor!: ElementRef<HTMLElement>;
  userReportsObserver!: IntersectionObserver;
  postReportsObserver!: IntersectionObserver;

  constructor(@Inject(PLATFORM_ID) private platformId: Object, private adminePanelServices: AdminPannelSefvices, private router: Router) { }

  AdminStatisticsResponse?: AdminStatisticsResponse;
  users: User[] = [];
  posts: any[] = [];
  userReports: Report[] = [];
  postReports: Report[] = [];
  topCommenters: TopUserResponse[] = [];
  mostFollowedUsers: TopUserResponse[] = [];
  mostReportedUsers: TopUserResponse[] = [];
  mostCommentedPosts: TopPostResponse[] = [];
  mostLikedPosts: TopPostResponse[] = [];
  mostReportedPosts: TopPostResponse[] = [];

  userReportsLastTime: number | null = null;
  userReportsLastUuid: string | null = null;
  postReportsLastTime: number | null = null;
  postReportsLastUuid: string | null = null;
  isLoadingUserReports = false;
  isLoadingPostReports = false;

  showDeleteUserConfirmation = false;
  showBanUserConfirmation = false;
  showDeletePostConfirmation = false;
  showHidePostConfirmation = false;
  pendingActionUuid: string = '';
  pendingActionData: any = null;

  ngOnInit(): void {
    this.laodStatistique();
    this.loadUsers();
    this.loadPosts();
    this.loadUserReportsByPage(null, null, () => {
      this.initUserReportsObserver();
    });
    this.loadPostReportsByPage(null, null, () => {
      this.initPostReportsObserver();
    });
    this.loadAnalytics();
  }

  ngOnDestroy(): void {
    if (this.userReportsObserver) {
      this.userReportsObserver.disconnect();
    }
    if (this.postReportsObserver) {
      this.postReportsObserver.disconnect();
    }
  }

  private initUserReportsObserver() {
    if (!isPlatformBrowser(this.platformId)) return;
    this.userReportsObserver = new IntersectionObserver((entries) => {
      if (entries[0].isIntersecting && !this.isLoadingUserReports && this.userReportsLastTime && this.userReportsLastUuid) {
        this.loadUserReportsByPage(this.userReportsLastTime, this.userReportsLastUuid);
      }
    });
    this.userReportsObserver.observe(this.userReportsAnchor.nativeElement);
  }

  private initPostReportsObserver() {
    if (!isPlatformBrowser(this.platformId)) return;
    this.postReportsObserver = new IntersectionObserver((entries) => {
      if (entries[0].isIntersecting && !this.isLoadingPostReports && this.postReportsLastTime && this.postReportsLastUuid) {
        this.loadPostReportsByPage(this.postReportsLastTime, this.postReportsLastUuid);
      }
    });
    this.postReportsObserver.observe(this.postReportsAnchor.nativeElement);
  }

  laodStatistique() {
    this.adminePanelServices.getOverallStatistics().subscribe({
      next: (res) => {
        this.AdminStatisticsResponse = res;
      },
      error: (err) => {
        console.log("statistique err: ", err);
      }
    })
  }

  loadUsers() {
    this.adminePanelServices.loadUsers().subscribe({
      next: (res) => {
        this.users = res;
      },
      error: (err) => {
        console.log("Error loading users: ", err);
      }
    });
  }

  loadPosts() {
    this.adminePanelServices.loadPosts().subscribe({
      next: (res) => {
        this.posts = res;
      },
      error: (err) => {
        console.log("Error loading posts: ", err);
      }
    });
  }

  loadUserReportsByPage(lastTime: number | null, lastUuid: string | null, onFinish?: () => void) {
    this.isLoadingUserReports = true;
    this.adminePanelServices.loadUsersReportsPaginated(lastTime, lastUuid).subscribe({
      next: (res) => {
        this.userReports.push(...res.reports);
        this.userReportsLastTime = res.lastTime;
        if (res.lastUuid) {
          this.userReportsLastUuid = res.lastUuid.toString();
        }
        this.isLoadingUserReports = false;
        if (onFinish) onFinish();
      },
      error: (err) => {
        console.log("Error loading user reports: ", err);
        this.isLoadingUserReports = false;
        if (onFinish) onFinish();
      }
    });
  }

  loadPostReportsByPage(lastTime: number | null, lastUuid: string | null, onFinish?: () => void) {
    this.isLoadingPostReports = true;
    this.adminePanelServices.loadPostsReportsPaginated(lastTime, lastUuid).subscribe({
      next: (res) => {
        this.postReports.push(...res.reports);
        this.postReportsLastTime = res.lastTime;
        if (res.lastUuid) {
          this.postReportsLastUuid = res.lastUuid.toString();
        }
        this.isLoadingPostReports = false;
        if (onFinish) onFinish();
      },
      error: (err) => {
        console.log("Error loading post reports: ", err);
        this.isLoadingPostReports = false;
        if (onFinish) onFinish();
      }
    });
  }

  loadAnalytics() {
    this.adminePanelServices.getUsersWithMostComments().subscribe({
      next: (res) => {
        this.topCommenters = res;
      },
      error: (err) => {
        console.log("Error loading top commenters: ", err);
      }
    });

    this.adminePanelServices.getUsersWithMostFollowers().subscribe({
      next: (res) => {
        this.mostFollowedUsers = res;
      },
      error: (err) => {
        console.log("Error loading most followed users: ", err);
      }
    });

    this.adminePanelServices.getMostReportedUsers().subscribe({
      next: (res) => {
        this.mostReportedUsers = res;
      },
      error: (err) => {
        console.log("Error loading most reported users: ", err);
      }
    });

    this.adminePanelServices.getPostsWithMostComments().subscribe({
      next: (res) => {
        this.mostCommentedPosts = res;
      },
      error: (err) => {
        console.log("Error loading most commented posts: ", err);
      }
    });

    this.adminePanelServices.getPostsWithMostLikes().subscribe({
      next: (res) => {
        this.mostLikedPosts = res;
      },
      error: (err) => {
        console.log("Error loading most liked posts: ", err);
      }
    });

    this.adminePanelServices.getMostReportedPosts().subscribe({
      next: (res) => {
        this.mostReportedPosts = res;
      },
      error: (err) => {
        console.log("Error loading most reported posts: ", err);
      }
    });
  }

  onPrepareDeleteUser(uuid: string, userData: any) {
    this.pendingActionUuid = uuid;
    this.pendingActionData = userData;
    this.showDeleteUserConfirmation = true;
  }

  onPrepareBanUser(uuid: string, userData: any) {
    this.pendingActionUuid = uuid;
    this.pendingActionData = userData;
    this.showBanUserConfirmation = true;
  }

  onPrepareDeletePost(uuid: string, postData: any) {
    this.pendingActionUuid = uuid;
    this.pendingActionData = postData;
    this.showDeletePostConfirmation = true;
  }

  onPrepareHidePost(uuid: string, postData: any) {
    this.pendingActionUuid = uuid;
    this.pendingActionData = postData;
    this.showHidePostConfirmation = true;
  }

  onConfirmDeleteUser() {
    if (this.pendingActionUuid) {
      this.deleteUser(this.pendingActionUuid);
    }
    this.showDeleteUserConfirmation = false;
    this.pendingActionUuid = '';
    this.pendingActionData = null;
  }

  onConfirmBanUser() {
    if (this.pendingActionUuid) {
      this.banUser(this.pendingActionUuid);
    }
    this.showBanUserConfirmation = false;
    this.pendingActionUuid = '';
    this.pendingActionData = null;
  }

  onConfirmDeletePost() {
    if (this.pendingActionUuid) {
      this.deletePost(this.pendingActionUuid);
    }
    this.showDeletePostConfirmation = false;
    this.pendingActionUuid = '';
    this.pendingActionData = null;
  }

  onConfirmHidePost() {
    if (this.pendingActionUuid) {
      this.hidePost(this.pendingActionUuid);
    }
    this.showHidePostConfirmation = false;
    this.pendingActionUuid = '';
    this.pendingActionData = null;
  }

  onCancelDeleteUser() {
    this.showDeleteUserConfirmation = false;
    this.pendingActionUuid = '';
    this.pendingActionData = null;
  }

  onCancelBanUser() {
    this.showBanUserConfirmation = false;
    this.pendingActionUuid = '';
    this.pendingActionData = null;
  }

  onCancelDeletePost() {
    this.showDeletePostConfirmation = false;
    this.pendingActionUuid = '';
    this.pendingActionData = null;
  }

  onCancelHidePost() {
    this.showHidePostConfirmation = false;
    this.pendingActionUuid = '';
    this.pendingActionData = null;
  }

  deleteUser(uuid: string) {
    this.adminePanelServices.deleteUser(uuid).subscribe({
      next: (res) => {
        this.loadUsers();
      },
      error: (err) => {
        console.log("Error deleting user: ", err);
      }
    });
  }

  banUser(uuid: string) {
    this.adminePanelServices.banUser(uuid).subscribe({
      next: () => {
        this.loadUsers();
      },
      error: (err) => {
        console.log("Error banning user: ", err);
      }
    });
  }

  deletePost(uuid: string) {
    this.adminePanelServices.deletePost(uuid).subscribe({
      next: () => {
        this.loadPosts();
      },
      error: (err) => {
        console.log("Error deleting post: ", err);
      }
    });
  }

  hidePost(uuid: string) {
    this.adminePanelServices.hidePost(uuid).subscribe({
      next: () => {
        this.loadPosts();
      },
      error: (err) => {
        console.log("Error hiding post: ", err);
      }
    });
  }

  goTo(destination: String, uuid: String) {
    if (destination === "profile") {
      this.router.navigate(['profile', uuid]);
    } else if (destination === "post") {
      this.router.navigate(['postCard', uuid]);
    }
  }
}
