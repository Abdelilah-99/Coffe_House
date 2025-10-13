import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';

export interface NotificationRes {
  postOrProfileUuid: string;
  content: string;
  time: number;
  uuid: string;
  isRead: boolean;
}

export interface Count {
  count: number;
}

@Injectable({
  providedIn: 'root'
})
export class NotifServices {
  constructor(private http: HttpClient) { }

  private URL = 'http://localhost:8080/api/notif';
  getAllNotif() {
    return this.http.get<NotificationRes[]>(`${this.URL}/all`);
  }

  getCountNotif() {
    return this.http.get<Count>(`${this.URL}/count`);
  }

  markRead(uuid: string | undefined) {
    return this.http.post<void>(`${this.URL}/read`, { uuid: uuid });
  }
}
