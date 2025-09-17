import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

export interface Post {
  id: number;
  userId: number;
  title: string;
  content: string;
  timestamp: String;
  userName: String;
  mediaPaths: String[];
}

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
export class PostService {

  private URL = `http://localhost:8080/api/posts/all`;
  constructor(private http: HttpClient) { }

  getAllPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.URL);
  }
}
