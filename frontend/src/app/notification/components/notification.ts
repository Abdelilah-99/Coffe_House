import { Component, Inject, OnInit, PLATFORM_ID, ViewChild, ElementRef, AfterViewInit, OnDestroy } from '@angular/core';
import { MeService, UserProfile } from '../../me/services/me.service';
import { NotificationRes, NotifServices } from '../services/services';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { Token } from '@angular/compiler';
import { routes } from '../../app.routes';
import { ToastService } from '../../toast/service/toast';

@Component({
  selector: 'app-notification',
  imports: [CommonModule],
  templateUrl: './notification.html',
  styleUrl: './notification.css'
})
export class Notification implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('anchor') anchor!: ElementRef<HTMLElement>;
  observer!: IntersectionObserver;

  userProfile?: UserProfile;
  notification: NotificationRes[] = [];
  show: boolean = false;
  isLoading: boolean = false;

  lastTime: number | null = null;
  lastUuid: string | null = null;

  constructor(private meServices: MeService,
    private notifService: NotifServices,
    @Inject(PLATFORM_ID) private platformId: Object,
    private navigate: Router,
    private toast: ToastService) { }

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      const token = localStorage.getItem('access_token');
      if (token) {
        this.loadProfile();
        this.show = true;
      } else {
        this.show = false;
        this.navigate.navigate(['/login']);
      }
    }
  }

  ngAfterViewInit(): void {
    if (isPlatformBrowser(this.platformId) && this.show === true) {
      this.initObserver();
    }
  }

  ngOnDestroy(): void {
    if (this.observer) {
      this.observer.disconnect();
    }
  }

  private initObserver() {
    if (!isPlatformBrowser(this.platformId)) return;
    this.observer = new IntersectionObserver((entries) => {
      if (entries[0].isIntersecting && !this.isLoading && this.lastTime && this.lastUuid) {
        this.loadNotifByPage(this.lastTime, this.lastUuid);
      }
    });
    this.observer.observe(this.anchor.nativeElement);
  }

  loadProfile() {
    this.meServices.getProfile().subscribe({
      next: (res) => {
        this.userProfile = res;
        this.loadNotif();
      },
      error: (err) => {
        console.log("error to load profile ", err);
      }
    })
  }

  loadNotif() {
    this.loadNotifByPage(null, null);
  }

  loadNotifByPage(lastTime: number | null, lastUuid: string | null) {
    this.isLoading = true;
    this.notifService.getNotificationsPaginated(lastTime, lastUuid).subscribe({
      next: (res) => {
        this.notification.push(...res.notifications);
        this.lastTime = res.lastTime;
        this.lastUuid = res.lastUuid;
        this.isLoading = false;
      },
      error: (err) => {
        console.log(err);
        this.isLoading = false;
      }
    })
  }

  goTo(i: number) {
    if (this.notification?.at(i)?.content.includes("post")) {
      const notif = this.notification?.at(i);

      if (notif && !notif.isRead) {
        notif.isRead = true;

        this.notifService.markRead(notif.uuid).subscribe({
          next: () => {
            console.info("user has read the notification");
          },
          error: () => {
            this.toast.show('notification error', 'error');
            if (notif) {
              notif.isRead = false;
            }
          }
        });
      }
      this.navigate.navigate(['/postCard', notif?.postOrProfileUuid]);
    }
  }
}
