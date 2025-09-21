import { Injectable } from '@angular/core';
import { Observable, of, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';

export interface Post {
  postUuid: number;
  userUuid: number;
  title: string;
  content: string;
  timestamp: String;
  userName: String;
  mediaPaths: string[];
  commentCount: number;
  likeCount: number;
}

export interface UserProfile {
  postUuid: number;
  firstName: string;
  lastName: string;
  username: string;
  email: string;
}

export interface Like {
  userUuid: any;
  postUuid: any;
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

  deletePost(uuid: number): Observable<any> {
    console.log(this.URL + '/delete/' + uuid);
    return this.http.post(this.URL + uuid, null);
  }

  editPost(uuid: number, formData: FormData): Observable<any> {
    console.log(this.URL + uuid);
    return this.http.post<Post>(this.URL + '/edit/' + uuid, formData);
  }

  doReaction(uuid: number) {
    return this.http.post<Like>(`${this.URL}/like/${uuid}`, null);
  }

  doComment(uuid: number, formData: FormData) {
    return this.http.post<Comment>(this.URL + '/comment/' + uuid, formData);
  }
}
