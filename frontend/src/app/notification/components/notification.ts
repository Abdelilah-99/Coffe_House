import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { MeService, UserProfile } from '../../me/services/me.service';
import { NotificationRes, NotifServices } from '../services/services';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-notification',
  imports: [CommonModule],
  templateUrl: './notification.html',
  styleUrl: './notification.css'
})
export class Notification implements OnInit {
  userProfile?: UserProfile;
  notification?: NotificationRes[];
  constructor(private meServices: MeService,
    private notifService: NotifServices,
    @Inject(PLATFORM_ID) private platformId: Object,
    private navigate: Router) { }

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.loadProfile();
    }
  }

  loadProfile() {
    this.meServices.getProfile().subscribe({
      next: (res) => {
        this.userProfile = res;
        this.loadNotif(this.userProfile.uuid as string);
      },
      error: (err) => {
        console.error("error to load profile ", err);
      }
    })
  }

  loadNotif(uuid: string) {
    this.notifService.getAllNotif().subscribe({
      next: (res) => {
        this.notification = res;
      },
      error: (err) => {
        console.error(err);
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
          error: (err) => {
            console.error("notification error: ", err);
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
