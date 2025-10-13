import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { AdminPannelSefvices, AdminStatisticsResponse, User, Report, TopUserResponse, TopPostResponse } from '../services/admin-pannel-sefvices';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserProfile } from '../../post/services/post-service';
import { UserService } from '../../home/services/services';
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
        console.log("AdminStatisticsResponse: ", res);
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
        console.log("users: ", res);
        console.log("Users loaded successfully");
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
        console.log("Posts loaded successfully");
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
        console.log("User reports loaded successfully");
      },
      error: (err) => {
        console.error("Error loading user reports: ", err);
      }
    });

    this.adminePanelServices.loadReports('posts').subscribe({
      next: (res) => {
        this.postReports = res;
        console.log("Post reports loaded successfully");
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
        console.log("Top commenters loaded successfully");
      },
      error: (err) => {
        console.error("Error loading top commenters: ", err);
      }
    });

    this.adminePanelServices.getUsersWithMostFollowers().subscribe({
      next: (res) => {
        this.mostFollowedUsers = res;
        console.log("Most followed users loaded successfully");
      },
      error: (err) => {
        console.error("Error loading most followed users: ", err);
      }
    });

    this.adminePanelServices.getMostReportedUsers().subscribe({
      next: (res) => {
        this.mostReportedUsers = res;
        console.log("Most reported users loaded successfully");
      },
      error: (err) => {
        console.error("Error loading most reported users: ", err);
      }
    });

    this.adminePanelServices.getPostsWithMostComments().subscribe({
      next: (res) => {
        this.mostCommentedPosts = res;
        console.log("Most commented posts loaded successfully");
      },
      error: (err) => {
        console.error("Error loading most commented posts: ", err);
      }
    });

    this.adminePanelServices.getPostsWithMostLikes().subscribe({
      next: (res) => {
        this.mostLikedPosts = res;
        console.log("Most liked posts loaded successfully");
      },
      error: (err) => {
        console.error("Error loading most liked posts: ", err);
      }
    });

    this.adminePanelServices.getMostReportedPosts().subscribe({
      next: (res) => {
        this.mostReportedPosts = res;
        console.log("Most reported posts loaded successfully");
      },
      error: (err) => {
        console.error("Error loading most reported posts: ", err);
      }
    });
  }

  deleteUser(uuid: string) {
    this.adminePanelServices.deleteUser(uuid).subscribe({
      next: (res) => {
        console.log("User deleted successfully");
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
        console.log("User banned successfully");
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
        console.log("Post deleted successfully");
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
        console.log("Post hidden successfully");
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
