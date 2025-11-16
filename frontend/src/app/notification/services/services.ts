import { isPlatformBrowser } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { environment } from '../../../environments/environment';

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

export interface NotificationPage {
  notifications: NotificationRes[];
  lastTime: number | null;
  lastUuid: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class NotifServices {
  private URL = `${environment.apiUrl}/api/notif`;

  constructor(private http: HttpClient) { }
  // getAllNotif() {
  //   return this.http.get<NotificationRes[]>(`${this.URL}/all`);
  // }

  getCountNotif() {
    return this.http.get<Count>(`${this.URL}/count`);
  }

  markRead(uuid: string | undefined) {
    return this.http.post<void>(`${this.URL}/read`, { uuid: uuid });
  }

  getNotificationsPaginated(lastTime: number | null, lastUuid: string | null) {
    if (!lastTime || !lastUuid) {
      return this.http.get<NotificationPage>(`${this.URL}/pages`);
    }
    const params = new HttpParams()
      .set('lastTime', lastTime.toString())
      .set('lastUuid', lastUuid);
    return this.http.get<NotificationPage>(`${this.URL}/pages`, { params });
  }
}
