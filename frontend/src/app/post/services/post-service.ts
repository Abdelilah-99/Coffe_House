import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

export interface Post {
  title: string;
  content: string;
  timestamp: String;
  userName: String;
  mediaPaths: String[];
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
