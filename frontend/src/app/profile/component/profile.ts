import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProfileService, ProfileRes } from '../services/services';
import { CommonModule, isPlatformBrowser } from '@angular/common';
@Component({
  selector: 'app-profile',
  imports: [CommonModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile implements OnInit {
  profileRes?: ProfileRes;
  constructor(private route: ActivatedRoute, private navigate: Router,
    private profileService: ProfileService,
    @Inject(PLATFORM_ID) private platformId: Object) { }
  uuid: String | null = null;
  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get('id');
    if (this.uuid && isPlatformBrowser(this.platformId)) {
      this.loadProfile(this.uuid);
    }
  }

  loadProfile(uuid: String) {
    this.profileService.getProfile(uuid).subscribe({
      next: (res) => {
        this.profileRes = res;
        console.log("profile has come succesfully ", res);
      },
      error: (err) => {
        console.error("error getting profile ", err);
      }
    })
  }
}
