import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

export interface User {
  uuid: String;
  firstName: String;
  lastName: String;
  username: String;
  email: String;
  message: String;
  status: String;
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
  totalComments: number;
  totalReports: number;
  totalFollows: number;
  message: string;
}

export interface TopUserResponse {
  uuid: string;
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  count: number;
}

export interface TopPostResponse {
  uuid: string;
  title: string;
  content: string;
  authorUsername: string;
  count: number;
}

@Injectable({
  providedIn: 'root'
})
export class AdminPannelSefvices {
  constructor(private http: HttpClient) { }

  URL: string = `http://localhost:8080/api/admin`;

  loadUsers() {
    return this.http.get<User[]>(`${this.URL}/users`);
  }

  loadPosts() {
    return this.http.get<any[]>(`${this.URL}/posts`);
  }

  loadReports(type: String) {
    return this.http.get<Report[]>(`${this.URL}/reports/${type}`);
  }

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

  getUsersWithMostComments() {
    return this.http.get<TopUserResponse[]>(`${this.URL}/analytics/users/top-commenters`);
  }

  getUsersWithMostFollowers() {
    return this.http.get<TopUserResponse[]>(`${this.URL}/analytics/users/most-followed`);
  }

  getMostReportedUsers() {
    return this.http.get<TopUserResponse[]>(`${this.URL}/analytics/users/most-reported`);
  }

  getPostsWithMostComments() {
    return this.http.get<TopPostResponse[]>(`${this.URL}/analytics/posts/most-commented`);
  }

  getPostsWithMostLikes() {
    return this.http.get<TopPostResponse[]>(`${this.URL}/analytics/posts/most-liked`);
  }

  getMostReportedPosts() {
    return this.http.get<TopPostResponse[]>(`${this.URL}/analytics/posts/most-reported`);
  }

  getOverallStatistics() {
    return this.http.get<AdminStatisticsResponse>(`${this.URL}/statistics`);
  }
}
