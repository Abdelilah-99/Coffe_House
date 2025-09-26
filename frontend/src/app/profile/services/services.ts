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
}
