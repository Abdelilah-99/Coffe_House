import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProfileService, ProfileRes, FollowRes, Message } from '../services/services';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { MeService, UserProfile } from '../../me/services/me.service';
import { Post, PostService } from '../../post/services/post-service';
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
  message?: Message;
  ifCrrProfile = true;
  userProfile?: UserProfile;
  userPosts: Post[] = [];
  isLoadingPosts = false;
  following?: number;
  followers?: number;
  constructor(private route: ActivatedRoute, private navigate: Router,
    private profileService: ProfileService,
    @Inject(PLATFORM_ID) private platformId: Object,
    private meProfile: MeService,
    private postService: PostService) { }
  uuid: String | null = null;
  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.uuid = params.get('id');
      if (this.uuid && isPlatformBrowser(this.platformId)) {
        this.loadProfile(this.uuid);
      }
    });
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

  onSubmitReport(uuid: String, reason: String) {
    this.profileService.doReport(uuid, reason).subscribe({
      next: (res) => {
        this.message = res;
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
}
