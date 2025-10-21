import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface LoginResponse {
  message: String;
  userRole: String;
  userName: String;
  token: String;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private URL = 'http://localhost:8080/api/auth';

  constructor(private protocolHttp: HttpClient) { }

  login(cre: { username: string; email: string; password: string }) {
    return this.protocolHttp.post<LoginResponse>(`${this.URL}/login`, cre);
  }

  register(formData: FormData): Observable<any> {
    return this.protocolHttp.post(`${this.URL}/register`, formData);
  }

  logout(): void {
    localStorage.removeItem('access_token');
    localStorage.removeItem('user_role');
    localStorage.removeItem('username');
  }
}
