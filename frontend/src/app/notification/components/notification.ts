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
        console.log("notif: ", res);
      },
      error: (err) => {
        console.error(err);
      }
    })
  }

  goTo(i: number) {
    if (this.notification?.at(i)?.content.includes("post")) {
      this.notifService.markRead(this.notification?.at(i)?.uuid).subscribe({
        next: () => {
          console.info("user has read the notification");
        },
        error: (err) => {
          console.error("notificatiuoin error: ", err);
        }
      });
      this.navigate.navigate(['/postCard', this.notification?.at(i)?.postOrProfileUuid]);
    }
    // console.log("hola", );
  }
}
