import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProfileService, ProfileRes, FollowRes, Message } from '../services/services';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { MeService, UserProfile } from '../../me/me.service';
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
  constructor(private route: ActivatedRoute, private navigate: Router,
    private profileService: ProfileService,
    @Inject(PLATFORM_ID) private platformId: Object,
    private meProfile: MeService) { }
  uuid: String | null = null;
  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get('id');
    if (this.uuid && isPlatformBrowser(this.platformId)) {
      this.loadProfile(this.uuid);
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
        this.myProfile(this.profileRes.uuid);
        console.log("profile has come succesfully ", res);
      },
      error: (err) => {
        console.error("error getting profile ", err);
      }
    })
  }

  followLogic(userName: String, connect: boolean) {
    if (connect && this.profileRes?.uuid) {
      this.profileService.unFollow(this.profileRes?.uuid).subscribe({
        next: (res) => {
          this.followRes = res;
          console.log("unfollow succeed");
          if (this.profileRes) {
            this.profileRes.connect = false;
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
          console.log("follow succeed");
          if (this.profileRes) {
            this.profileRes.connect = true;
          }
        },
        error: (err) => {
          console.error("error following ", err);
        }
      })
    }
  }
}
