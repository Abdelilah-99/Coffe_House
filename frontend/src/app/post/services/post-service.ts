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
  private URL = `http://localhost:8080/api/posts`;
  // private URLPOSTCOMMENT = 
  constructor(private http: HttpClient) { }

  getAllPosts(): Observable<Post[]> {
    if (this.postsCach.length > 0) {
      return of(this.postsCach);
    }
    return this.http.get<Post[]>(this.URL + '/all').pipe(tap(posts => this.postsCach = posts));
  }

  deletePost(id: number): Observable<any> {
    console.log(this.URL + '/delete/' + id);
    return this.http.post(this.URL + id, null);
  }

  editPost(id: number, formData: FormData): Observable<any> {
    console.log(this.URL + id);
    return this.http.post<Post>(this.URL + '/edit/' + id, formData);
  }

  doReaction(postId: number) {
    return this.http.post<Like>(`${this.URL}/like/${postId}`, null);
  }

  doComment(postId: number, formData: FormData) {
    return this.http.post<Comment>(this.URL + '/comment/' + postId, formData);
  }
}
