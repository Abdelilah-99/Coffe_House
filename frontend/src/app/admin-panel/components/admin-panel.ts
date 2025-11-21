import { Component, Inject, OnInit, PLATFORM_ID, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import { AdminPannelSefvices, AdminStatisticsResponse, User, Report } from '../services/admin-pannel-sefvices';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastService } from '../../toast/service/toast';

@Component({
  selector: 'app-admin-panel',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-panel.html',
  styleUrl: './admin-panel.css'
})
export class AdminPanel implements OnInit, OnDestroy {
  @ViewChild('userReportsAnchor', { static: false }) userReportsAnchor!: ElementRef<HTMLElement>;
  @ViewChild('postReportsAnchor', { static: false }) postReportsAnchor!: ElementRef<HTMLElement>;
  @ViewChild('userReportsScrollContainer', { static: false }) userReportsScrollContainer!: ElementRef<HTMLElement>;
  @ViewChild('postReportsScrollContainer', { static: false }) postReportsScrollContainer!: ElementRef<HTMLElement>;
  @ViewChild('usersAnchor', { static: false }) usersAnchor!: ElementRef<HTMLElement>;
  @ViewChild('postsAnchor', { static: false }) postsAnchor!: ElementRef<HTMLElement>;
  @ViewChild('usersScrollContainer', { static: false }) usersScrollContainer!: ElementRef<HTMLElement>;
  @ViewChild('postsScrollContainer', { static: false }) postsScrollContainer!: ElementRef<HTMLElement>;
  @ViewChild('usersAnchorMobile', { static: false }) usersAnchorMobile!: ElementRef<HTMLElement>;
  @ViewChild('postsAnchorMobile', { static: false }) postsAnchorMobile!: ElementRef<HTMLElement>;
  @ViewChild('usersMobileScrollContainer', { static: false }) usersMobileScrollContainer!: ElementRef<HTMLElement>;
  @ViewChild('postsMobileScrollContainer', { static: false }) postsMobileScrollContainer!: ElementRef<HTMLElement>;
  userReportsObserver!: IntersectionObserver;
  postReportsObserver!: IntersectionObserver;
  usersObserver!: IntersectionObserver;
  postsObserver!: IntersectionObserver;
  usersMobileObserver!: IntersectionObserver;
  postsMobileObserver!: IntersectionObserver;

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private adminePanelServices: AdminPannelSefvices,
    private router: Router,
    private toastService: ToastService
  ) { }

  AdminStatisticsResponse?: AdminStatisticsResponse;
  users: User[] = [];
  posts: any[] = [];
  userReports: Report[] = [];
  postReports: Report[] = [];
  activeTab: 'users' | 'posts' | 'user-reports' | 'post-reports' = 'users';

  userReportsLastTime: number | null = null;
  userReportsLastUuid: string | null = null;
  postReportsLastTime: number | null = null;
  postReportsLastUuid: string | null = null;
  usersLastCreatedAt: number | null = null;
  usersLastUuid: string | null = null;
  postsLastTime: number | null = null;
  postsLastUuid: string | null = null;
  isLoadingUserReports = false;
  isLoadingPostReports = false;
  isLoadingUsers = false;
  isLoadingPosts = false;

  showDeleteUserConfirmation = false;
  showBanUserConfirmation = false;
  showDeletePostConfirmation = false;
  showHidePostConfirmation = false;
  pendingActionUuid: string = '';
  pendingActionData: any = null;

  ngOnInit(): void {
    this.laodStatistique();
    this.loadUsersByPage(null, null, () => {
      this.initUsersObserver();
    });
    this.loadPostsByPage(null, null, () => {
      this.initPostsObserver();
    });
    this.loadUserReportsByPage(null, null, () => {
      this.initUserReportsObserver();
    });
    this.loadPostReportsByPage(null, null, () => {
      this.initPostReportsObserver();
    });
  }

  ngOnDestroy(): void {
    if (this.userReportsObserver) {
      this.userReportsObserver.disconnect();
    }
    if (this.postReportsObserver) {
      this.postReportsObserver.disconnect();
    }
    if (this.usersObserver) {
      this.usersObserver.disconnect();
    }
    if (this.postsObserver) {
      this.postsObserver.disconnect();
    }
    if (this.usersMobileObserver) {
      this.usersMobileObserver.disconnect();
    }
    if (this.postsMobileObserver) {
      this.postsMobileObserver.disconnect();
    }
  }

  private initUserReportsObserver() {
    if (!isPlatformBrowser(this.platformId)) return;
    if (this.userReportsScrollContainer && this.userReportsAnchor) {
      this.userReportsObserver = new IntersectionObserver((entries) => {
        if (entries[0].isIntersecting && !this.isLoadingUserReports && this.userReportsLastTime && this.userReportsLastUuid) {
          this.loadUserReportsByPage(this.userReportsLastTime, this.userReportsLastUuid);
        }
      });
      this.userReportsObserver.observe(this.userReportsAnchor.nativeElement);
    }
  }

  private initPostReportsObserver() {
    if (!isPlatformBrowser(this.platformId)) return;
    if (this.postReportsScrollContainer && this.postReportsAnchor) {
      this.postReportsObserver = new IntersectionObserver((entries) => {
        if (entries[0].isIntersecting && !this.isLoadingPostReports && this.postReportsLastTime && this.postReportsLastUuid) {
          this.loadPostReportsByPage(this.postReportsLastTime, this.postReportsLastUuid);
        }
      });
      this.postReportsObserver.observe(this.postReportsAnchor.nativeElement);
    }
  }

  private initUsersObserver() {
    if (!isPlatformBrowser(this.platformId)) return;

    if (this.usersScrollContainer && this.usersAnchor) {
      this.usersObserver = new IntersectionObserver((entries) => {
        if (entries[0].isIntersecting && !this.isLoadingUsers && this.usersLastCreatedAt && this.usersLastUuid) {
          this.loadUsersByPage(this.usersLastCreatedAt, this.usersLastUuid);
        }
      });
      this.usersObserver.observe(this.usersAnchor.nativeElement);
    }

    if (this.usersMobileScrollContainer && this.usersAnchorMobile) {
      this.usersMobileObserver = new IntersectionObserver((entries) => {
        if (entries[0].isIntersecting && !this.isLoadingUsers && this.usersLastCreatedAt && this.usersLastUuid) {
          this.loadUsersByPage(this.usersLastCreatedAt, this.usersLastUuid);
        }
      });
      this.usersMobileObserver.observe(this.usersAnchorMobile.nativeElement);
    }
  }

  private initPostsObserver() {
    if (!isPlatformBrowser(this.platformId)) return;

    if (this.postsScrollContainer && this.postsAnchor) {
      this.postsObserver = new IntersectionObserver((entries) => {
        if (entries[0].isIntersecting && !this.isLoadingPosts && this.postsLastTime && this.postsLastUuid) {
          this.loadPostsByPage(this.postsLastTime, this.postsLastUuid);
        }
      });
      this.postsObserver.observe(this.postsAnchor.nativeElement);
    }

    if (this.postsMobileScrollContainer && this.postsAnchorMobile) {
      this.postsMobileObserver = new IntersectionObserver((entries) => {
        if (entries[0].isIntersecting && !this.isLoadingPosts && this.postsLastTime && this.postsLastUuid) {
          this.loadPostsByPage(this.postsLastTime, this.postsLastUuid);
        }
      });
      this.postsMobileObserver.observe(this.postsAnchorMobile.nativeElement);
    }
  }

  laodStatistique() {
    this.adminePanelServices.getOverallStatistics().subscribe({
      next: (res) => {
        this.AdminStatisticsResponse = res;
      },
      error: (err) => {
        this.toastService.show('Failed to load statistics', 'error');
      }
    })
  }

  loadUsersByPage(lastCreatedAt: number | null, lastUuid: string | null, onFinish?: () => void) {
    this.isLoadingUsers = true;
    this.adminePanelServices.loadUsersPaginated(lastCreatedAt, lastUuid).subscribe({
      next: (res) => {
        this.users.push(...res.users);
        this.usersLastCreatedAt = res.lastCreatedAt;
        this.usersLastUuid = res.lastUuid;
        this.isLoadingUsers = false;
        if (onFinish) onFinish();
      },
      error: (_err) => {
        this.toastService.show('Failed to load users', 'error');
        this.isLoadingUsers = false;
        if (onFinish) onFinish();
      }
    });
  }

  loadPostsByPage(lastTime: number | null, lastUuid: string | null, onFinish?: () => void) {
    this.isLoadingPosts = true;
    this.adminePanelServices.loadPostsPaginated(lastTime, lastUuid).subscribe({
      next: (res) => {
        this.posts.push(...res.posts);
        this.postsLastTime = res.lastTime;
        if (res.lastUuid) {
          this.postsLastUuid = res.lastUuid.toString();
        }
        this.isLoadingPosts = false;
        if (onFinish) onFinish();
      },
      error: (err) => {
        this.toastService.show('Failed to load posts', 'error');
        this.isLoadingPosts = false;
        if (onFinish) onFinish();
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
        this.toastService.show('Failed to load user reports', 'error');
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
        this.toastService.show('Failed to load post reports', 'error');
        this.isLoadingPostReports = false;
        if (onFinish) onFinish();
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
      next: () => {
        this.users = this.users.filter(u => u.uuid !== uuid);
        this.toastService.show('User deleted successfully', 'success');
      },
      error: (err) => {
        this.toastService.show('Failed to delete user', 'error');
      }
    });
  }

  banUser(uuid: string) {
    this.adminePanelServices.banUser(uuid).subscribe({
      next: () => {
        const user = this.users.find(u => u.uuid === uuid);
        if (user) {
          const wasBanned = user.status === 'ACTIVE';
          user.status = user.status === 'ACTIVE' ? 'BAN' : 'ACTIVE';
          this.toastService.show(wasBanned ? 'User banned successfully' : 'User unbanned successfully', 'success');
        }
      },
      error: (err) => {
        this.toastService.show('Failed to update user status', 'error');
      }
    });
  }

  deletePost(uuid: string) {
    this.adminePanelServices.deletePost(uuid).subscribe({
      next: () => {
        this.posts = this.posts.filter(p => p.postUuid !== uuid);
        this.toastService.show('Post deleted successfully', 'success');
      },
      error: (err) => {
        this.toastService.show('Failed to delete post', 'error');
      }
    });
  }

  hidePost(uuid: string) {
    this.adminePanelServices.hidePost(uuid).subscribe({
      next: () => {
        const post = this.posts.find(p => p.postUuid === uuid);
        if (post) {
          const wasHidden = post.status === 'EPOSED';
          post.status = post.status === 'EPOSED' ? 'HIDE' : 'EPOSED';
          this.toastService.show(wasHidden ? 'Post hidden successfully' : 'Post shown successfully', 'success');
        }
      },
      error: (err) => {
        this.toastService.show('Failed to update post status', 'error');
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

  setActiveTab(tab: 'users' | 'posts' | 'user-reports' | 'post-reports') {
    this.activeTab = tab;
  }
}
