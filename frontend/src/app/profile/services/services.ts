import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface ProfileRes {
  email: String;
  firstName: string;
  lastName: string;
  uuid: string;
  userName: string;
  follower: number;
  following: number;
  connect: boolean;
}

export interface FollowRes {
  follower: number;
  following: number;
  followerUuid: String;
  followingUuid: String;
  message: String;
}

export interface Message {
  message: String;
}

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private URL = 'http://localhost:8080/api/users';
  constructor(private http: HttpClient) { }

  getProfile(uuid: String): Observable<ProfileRes> {
    return this.http.get<ProfileRes>(`${this.URL}/profile/${uuid}`);
  }

  follow(uuid: String): Observable<any> {
    return this.http.post<FollowRes>(`${this.URL}/follow/${uuid}`, null);
  }

  unFollow(uuid: String): Observable<any> {
    return this.http.post<FollowRes>(`${this.URL}/unfollow/${uuid}`, null);
  }

  doReport(uuid: String, reason: String) {
    return this.http.post<Message>(`http://localhost:8080/api/report/profile/${uuid}`, { reason: reason });
  }
}
