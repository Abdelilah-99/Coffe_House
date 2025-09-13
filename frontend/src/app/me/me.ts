import { Component, OnInit } from '@angular/core';
import { ProfileService, UserProfile } from './me.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile',
  imports: [CommonModule],
  templateUrl: './me.html',
  styleUrl: './me.css'
})
export class Profile implements OnInit {
  userProfile: UserProfile | null = null;
  isLoading = false;

  constructor(private profileService: ProfileService, private router: Router) { }

  ngOnInit() {
    this.loadProfile();
  }
  loadProfile() {
    if (typeof window !== 'undefined' && window.localStorage) {
      const token = localStorage.getItem('access_token');
      if (!token) {
        this.router.navigate(['/login']);
        return;
      }
    }
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
