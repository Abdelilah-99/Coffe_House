import { Component, Inject, PLATFORM_ID, signal } from '@angular/core';
import { Router, NavigationEnd, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { NotifServices, Count } from './notification/services/services';
import { UserProfile } from './me/services/me.service';
import { CommonModule, isPlatformBrowser } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App {
  protected readonly title = signal('01-Blog');
  count: Count | null = null;
  userProfile?: UserProfile;
  showNavbar = true;

  constructor(private notifService: NotifServices, private router: Router, @Inject(PLATFORM_ID) private platformId: Object) {
    if (isPlatformBrowser(platformId)) {
      this.router.events
        .pipe(filter(event => event instanceof NavigationEnd))
        .subscribe((event: NavigationEnd) => {
          const hideOn = ['/login', '/register', '/admin'];
          this.showNavbar = !hideOn.includes(event.urlAfterRedirects);
          if (this.showNavbar) {
            this.loadCountNotif();
          }
        });
    }
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
}
