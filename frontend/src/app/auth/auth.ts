import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private URL = 'http://localhost:8080/api/auth';

  constructor(private protocolHttp: HttpClient) { }

  login(cre: { username: string; email: string; password: string }): Observable<any> {
    return this.protocolHttp.post(`${this.URL}/login`, cre);
  }

  register(formData: FormData): Observable<any> {
    console.log(formData.get('profileImage'));
    return this.protocolHttp.post(`${this.URL}/register`, formData);
  }
}
