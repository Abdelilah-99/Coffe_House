import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { AdminPannelSefvices, AdminStatisticsResponse, User, Report, TopUserResponse, TopPostResponse } from '../services/admin-pannel-sefvices';
import { CommonModule } from '@angular/common';
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
export class AdminPanel implements OnInit {

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
    this.loadReports();
    this.loadAnalytics();
  }

  laodStatistique() {
    this.adminePanelServices.getOverallStatistics().subscribe({
      next: (res) => {
        this.AdminStatisticsResponse = res;
      },
      error: (err) => {
        console.error("statistique err: ", err);
      }
    })
  }

  loadUsers() {
    this.adminePanelServices.loadUsers().subscribe({
      next: (res) => {
        this.users = res;
      },
      error: (err) => {
        console.error("Error loading users: ", err);
      }
    });
  }

  loadPosts() {
    this.adminePanelServices.loadPosts().subscribe({
      next: (res) => {
        this.posts = res;
      },
      error: (err) => {
        console.error("Error loading posts: ", err);
      }
    });
  }

  loadReports() {
    this.adminePanelServices.loadReports('users').subscribe({
      next: (res) => {
        this.userReports = res;
      },
      error: (err) => {
        console.error("Error loading user reports: ", err);
      }
    });

    this.adminePanelServices.loadReports('posts').subscribe({
      next: (res) => {
        this.postReports = res;
      },
      error: (err) => {
        console.error("Error loading post reports: ", err);
      }
    });
  }

  loadAnalytics() {
    this.adminePanelServices.getUsersWithMostComments().subscribe({
      next: (res) => {
        this.topCommenters = res;
      },
      error: (err) => {
        console.error("Error loading top commenters: ", err);
      }
    });

    this.adminePanelServices.getUsersWithMostFollowers().subscribe({
      next: (res) => {
        this.mostFollowedUsers = res;
      },
      error: (err) => {
        console.error("Error loading most followed users: ", err);
      }
    });

    this.adminePanelServices.getMostReportedUsers().subscribe({
      next: (res) => {
        this.mostReportedUsers = res;
      },
      error: (err) => {
        console.error("Error loading most reported users: ", err);
      }
    });

    this.adminePanelServices.getPostsWithMostComments().subscribe({
      next: (res) => {
        this.mostCommentedPosts = res;
      },
      error: (err) => {
        console.error("Error loading most commented posts: ", err);
      }
    });

    this.adminePanelServices.getPostsWithMostLikes().subscribe({
      next: (res) => {
        this.mostLikedPosts = res;
      },
      error: (err) => {
        console.error("Error loading most liked posts: ", err);
      }
    });

    this.adminePanelServices.getMostReportedPosts().subscribe({
      next: (res) => {
        this.mostReportedPosts = res;
      },
      error: (err) => {
        console.error("Error loading most reported posts: ", err);
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
        console.error("Error deleting user: ", err);
      }
    });
  }

  banUser(uuid: string) {
    this.adminePanelServices.banUser(uuid).subscribe({
      next: () => {
        this.loadUsers();
      },
      error: (err) => {
        console.error("Error banning user: ", err);
      }
    });
  }

  deletePost(uuid: string) {
    this.adminePanelServices.deletePost(uuid).subscribe({
      next: () => {
        this.loadPosts();
      },
      error: (err) => {
        console.error("Error deleting post: ", err);
      }
    });
  }

  hidePost(uuid: string) {
    this.adminePanelServices.hidePost(uuid).subscribe({
      next: () => {
        this.loadPosts();
      },
      error: (err) => {
        console.error("Error hiding post: ", err);
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
