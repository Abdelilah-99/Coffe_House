import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface UserProfile {
  id: number;
  firstName: string;
  lastName: string;
  username: string;
  email: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private URL = 'http://localhost:8080/api/auth/me';
  constructor(private http: HttpClient) { }

  getProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.URL}`);
  }
  private URLPOST = 'http://localhost:8080/api/posts/create';
  createPost(formData: FormData): Observable<any> {
    return this.http.post(`${this.URLPOST}`, formData);
  }
}
