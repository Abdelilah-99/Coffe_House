import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface UserSearch {
  uuid: string;
  username: string;
  firstName: string;
  lastName: string;
  profileImagePath: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private URL = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) { }

  getUserFromSearch(prefix: string): Observable<UserSearch[]> {
    const params = new HttpParams().set('username', prefix);
    return this.http.get<UserSearch[]>(`${this.URL}/search`, { params });
  }
}
