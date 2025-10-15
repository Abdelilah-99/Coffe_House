import { Component, Inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { Router, NavigationEnd, RouterOutlet, RouterModule } from '@angular/router';
import { filter } from 'rxjs';
import { NotifServices, Count } from './notification/services/services';
import { UserProfile, MeService } from './me/services/me.service';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Searchbar } from './searchbar/components/searchbar';
import { ProfileService, FollowUser } from './profile/services/services';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule, RouterModule, Searchbar],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App implements OnInit {
  count: Count | null = null;
  userProfile?: UserProfile;
  showNavbar = false;
  searchOpen = false;
  sidebarOpen = false;
  profileDropdownOpen = false;
  followingList: FollowUser[] = [];
  loadingFollowing = false;

  constructor(
    private notifService: NotifServices,
    private router: Router,
    private meService: MeService,
    private profileService: ProfileService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
  }

  ngOnInit(): void {
    if (isPlatformBrowser(this  .platformId)) {
      this.router.events
        .pipe(filter(event => event instanceof NavigationEnd))
        .subscribe((event: NavigationEnd) => {
          const hideOn = ['/login', '/register'];
          this.showNavbar = !hideOn.includes(event.urlAfterRedirects);
          if (this.showNavbar && localStorage.getItem('access_token')) {
            this.loadCountNotif();
            this.loadUserProfile();
          }
          this.sidebarOpen = false;
          this.profileDropdownOpen = false;
        });
    }
  }

  loadUserProfile() {
    this.meService.getProfile().subscribe({
      next: (profile) => {
        this.userProfile = profile;
        this.loadFollowingList();
      },
      error: (err) => {
        console.error("Error loading user profile:", err);
      }
    });
  }

  loadFollowingList() {
    this.loadingFollowing = true;
    this.profileService.getMyFollowing().subscribe({
      next: (following) => {
        this.followingList = following;
        this.loadingFollowing = false;
      },
      error: (err) => {
        console.error("Error loading following list:", err);
        this.loadingFollowing = false;
      }
    });
  }

  isAdmin(): boolean {
    return localStorage.getItem('user_role') === 'ROLE_ADMIN';
  }

  loadCountNotif() {
    this.notifService.getCountNotif().subscribe({
      next: (res) => {
        this.count = res;
        console.log("count: ", res);
      },
      error: (err) => {
        if (err.message !== 'Authentication invalid') {
          console.error("err count ", err);
        }
      }
    });
  }

  toggleSearch() {
    this.searchOpen = !this.searchOpen;
  }

  toggleSidebar() {
    this.sidebarOpen = !this.sidebarOpen;
    if (this.sidebarOpen) {
      this.profileDropdownOpen = false;
    }
  }

  toggleProfileDropdown() {
    this.profileDropdownOpen = !this.profileDropdownOpen;
    if (this.profileDropdownOpen) {
      this.sidebarOpen = false;
    }
  }

  closeSidebar() {
    this.sidebarOpen = false;
  }

  logout() {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('access_token');
      localStorage.removeItem('user_role');
      this.router.navigate(['/login']);
    }
  }

  navigateToProfile(uuid: string) {
    this.router.navigate(['/profile', uuid]);
    this.closeSidebar();
  }
}
