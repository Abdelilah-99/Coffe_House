import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

export interface User {
  uuid: String;
  firstName: String;
  lastName: String;
  username: String;
  email: String;
  message: String;
  status: String;
  createdAt: number;
}

export interface Report {
  uuid: String;
  postOrUserUuid: String;
  reporterUsername: String;
  reason: String;
  time: number;
}

export interface AdminStatisticsResponse {
  totalUsers: number;
  totalPosts: number;
  totalReports: number;
  message: string;
}

export interface UserPage {
  users: User[];
  lastCreatedAt: number | null;
  lastUuid: string | null;
}

export interface PostPage {
  posts: any[];
  lastTime: number | null;
  lastUuid: string | null;
}

export interface ReportPage {
  reports: Report[];
  lastTime: number | null;
  lastUuid: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class AdminPannelSefvices {
  constructor(private http: HttpClient) { }

  URL: string = `/api/admin`;

  loadUser(uuid: String) {
    return this.http.get<User>(`${this.URL}/user/${uuid}`);
  }

  deleteUser(uuid: String) {
    return this.http.post<User>(`${this.URL}/user/delete/${uuid}`, null);
  }

  banUser(uuid: String) {
    return this.http.post<User>(`${this.URL}/user/ban/${uuid}`, null);
  }

  deletePost(uuid: String) {
    return this.http.post<User>(`${this.URL}/post/delete/${uuid}`, null);
  }

  hidePost(uuid: String) {
    return this.http.post<User>(`${this.URL}/post/hide/${uuid}`, null);
  }

  getOverallStatistics() {
    return this.http.get<AdminStatisticsResponse>(`${this.URL}/statistics`);
  }

  loadUsersPaginated(lastCreatedAt: number | null, lastUuid: string | null) {
    if (!lastCreatedAt || !lastUuid) {
      return this.http.get<UserPage>(`${this.URL}/users/pages`);
    }
    const params = new HttpParams()
      .set('lastCreatedAt', lastCreatedAt.toString())
      .set('lastUuid', lastUuid);
    return this.http.get<UserPage>(`${this.URL}/users/pages`, { params });
  }

  loadPostsPaginated(lastTime: number | null, lastUuid: string | null) {
    if (!lastTime || !lastUuid) {
      return this.http.get<PostPage>(`${this.URL}/posts/pages`);
    }
    const params = new HttpParams()
      .set('lastTime', lastTime.toString())
      .set('lastUuid', lastUuid);
    return this.http.get<PostPage>(`${this.URL}/posts/pages`, { params });
  }

  loadPostsReportsPaginated(lastTime: number | null, lastUuid: string | null) {
    if (!lastTime || !lastUuid) {
      return this.http.get<ReportPage>(`${this.URL}/reports/posts/pages`);
    }
    const params = new HttpParams()
      .set('lastTime', lastTime.toString())
      .set('lastUuid', lastUuid);
    return this.http.get<ReportPage>(`${this.URL}/reports/posts/pages`, { params });
  }

  loadUsersReportsPaginated(lastTime: number | null, lastUuid: string | null) {
    if (!lastTime || !lastUuid) {
      return this.http.get<ReportPage>(`${this.URL}/reports/users/pages`);
    }
    const params = new HttpParams()
      .set('lastTime', lastTime.toString())
      .set('lastUuid', lastUuid);
    return this.http.get<ReportPage>(`${this.URL}/reports/users/pages`, { params });
  }
}
