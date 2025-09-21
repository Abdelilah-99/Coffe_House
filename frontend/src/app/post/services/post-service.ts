import { Injectable } from '@angular/core';
import { Observable, of, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';

export interface Post {
  id: number;
  userId: number;
  title: string;
  content: string;
  timestamp: String;
  userName: String;
  mediaPaths: string[];
  commentCount: number;
  likeCount: number;
}

export interface UserProfile {
  id: number;
  firstName: string;
  lastName: string;
  username: string;
  email: string;
}

export interface Like {
  userId: any;
  postId: any;
  likeCount: number;
}

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private postsCach: Post[] = [];
  private URL = `http://localhost:8080/api/posts/all`;
  private URLDELETE = `http://localhost:8080/api/posts/delete/`;
  private URLUPDATE = `http://localhost:8080/api/posts/edit/`;
  private URLPOSTLIKE = 'http://localhost:8080/api/posts/like';

  constructor(private http: HttpClient) { }

  getAllPosts(): Observable<Post[]> {
    if (this.postsCach.length > 0) {
      return of(this.postsCach);
    }
    return this.http.get<Post[]>(this.URL).pipe(tap(posts => this.postsCach = posts));
  }

  deletePost(id: number): Observable<any> {
    console.log(this.URLDELETE + id);
    return this.http.post(this.URLDELETE + id, null);
  }

  editPost(id: number, formData: FormData): Observable<any> {
    console.log(this.URLUPDATE + id);
    return this.http.post<Post>(this.URLUPDATE + id, formData);
  }

  doReaction(postId: number) {
    return this.http.post<Like>(`${this.URLPOSTLIKE}/${postId}`, null);
  }
}
