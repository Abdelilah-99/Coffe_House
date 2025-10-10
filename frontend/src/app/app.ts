import { Component, Inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { Router, NavigationEnd, RouterOutlet, RouterModule } from '@angular/router';
import { filter } from 'rxjs';
import { NotifServices, Count } from './notification/services/services';
import { UserProfile } from './me/services/me.service';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Searchbar } from './home/components/searchbar/searchbar';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule, RouterModule, Searchbar],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App implements OnInit {
  protected readonly title = signal('01-Blog');
  count: Count | null = null;
  userProfile?: UserProfile;
  showNavbar = false;
  searchOpen = false;

  constructor(private notifService: NotifServices, private router: Router, @Inject(PLATFORM_ID) private platformId: Object) {
  }

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.router.events
        .pipe(filter(event => event instanceof NavigationEnd))
        .subscribe((event: NavigationEnd) => {
          const hideOn = ['/login', '/register'];
          this.showNavbar = !hideOn.includes(event.urlAfterRedirects);
          if (this.showNavbar) {
            this.loadCountNotif();
          }
        });
    }
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
        console.error("err count ", err);
      }
    });
  }

  toggleSearch() {
    this.searchOpen = !this.searchOpen;
  }
}
