import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface UserProfile {
  id: string;
  firstName: string;
  lastName: string;
  username: string;
  email: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private URL = 'http://localhost:8080/api/users/profile';
  constructor(private http: HttpClient) { }

  getProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.URL}`)
  }
}
