import { Component, OnInit } from '@angular/core';
import { ProfileService, UserProfile } from './profile.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-profile',
  imports: [CommonModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile implements OnInit {
  userProfile: UserProfile | null = null;
  isLoading = false;

  constructor(private profileService: ProfileService) { }

  ngOnInit() {
    this.loadProfile();
  }
  loadProfile() {
    this.isLoading = !this.isLoading;
    this.profileService.getProfile().subscribe({
      next: (profile) => {
        this.userProfile = profile;
        this.isLoading = !this.isLoading;
      },
      error: (err) => {
        console.error("Err loading profile: ", err);
        this.isLoading = !this.isLoading;
      }
    })
  }
}
