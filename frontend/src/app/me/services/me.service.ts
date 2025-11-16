import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Post } from '../../post/services/post-service';
import { environment } from '../../../environments/environment';

export interface UserProfile {
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  uuid: String;
  profileImagePath: string;
}

@Injectable({
  providedIn: 'root'
})
export class MeService {
  private URL = `${environment.apiUrl}/api/auth/me`;
  private URLPOST = `${environment.apiUrl}/api/posts`;

  constructor(private http: HttpClient) { }

  getProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.URL}`);
  }

  createPost(formData: FormData): Observable<any> {
    return this.http.post(`${this.URLPOST}/create`, formData);
  }

  // getUserPosts(userUuid: String): Observable<Post[]> {
  //   return this.http.get<Post[]>(`${this.URLPOST}/user/${userUuid}`);
  // }
}
