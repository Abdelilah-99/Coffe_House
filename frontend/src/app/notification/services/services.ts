import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';

export interface NotificationRes {
  postOrProfileUuid: string;
  content: string;
  time: number;
  uuid: string;
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
  getAllNotif(uuid: string) {
    return this.http.post<NotificationRes[]>(`${this.URL}/all`, { uuid: uuid });
  }

  getCountNotif() {
    return this.http.get<Count>(`${this.URL}/count`);
  }

  markRead(uuid: string | undefined) {
    return this.http.post<void>(`${this.URL}/read`, { uuid: uuid });
  }
}
