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
  private URL = `/api/auth/me`;
  private URLPOST = `/api/posts`;

  constructor(private http: HttpClient) { }

  getProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.URL}`);
  }

  updateProfile(formData: FormData): Observable<any> {
    return this.http.put(`${this.URL}`, formData);
  }

  createPost(formData: FormData): Observable<any> {
    return this.http.post(`${this.URLPOST}/create`, formData);
  }
}
