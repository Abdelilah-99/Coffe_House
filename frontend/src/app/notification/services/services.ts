import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';

export interface NotificationRes {
  postOrProfileUuid: string;
  content: string;
  time: number;
  uuid: string;
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
}
