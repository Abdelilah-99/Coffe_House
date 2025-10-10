import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Post } from '../../post/services/post-service';

export interface ProfileRes {
  email: String;
  firstName: string;
  lastName: string;
  uuid: string;
  username: string;
  follower: number;
  following: number;
  connect: boolean;
  profileImagePath: string;
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

export interface FollowUser {
  uuid: string;
  username: string;
  firstName: string;
  lastName: string;
  profileImagePath: string;
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

  getUserPosts(userUuid: String): Observable<Post[]> {
    return this.http.get<Post[]>(`http://localhost:8080/api/posts/user/${userUuid}`);
  }

  getFollowers(userUuid: String): Observable<FollowUser[]> {
    return this.http.get<FollowUser[]>(`${this.URL}/followers/${userUuid}`);
  }

  getFollowing(userUuid: String): Observable<FollowUser[]> {
    return this.http.get<FollowUser[]>(`${this.URL}/following/${userUuid}`);
  }

  getMyFollowers(): Observable<FollowUser[]> {
    return this.http.get<FollowUser[]>(`${this.URL}/me/followers`);
  }

  getMyFollowing(): Observable<FollowUser[]> {
    return this.http.get<FollowUser[]>(`${this.URL}/me/following`);
  }
}
