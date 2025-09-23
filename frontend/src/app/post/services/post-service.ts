import { Injectable } from '@angular/core';
import { Observable, of, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';

export interface Post {
  postUuid: String;
  userUuid: String;
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

export interface Comments {
  comments: { userName: String, timesTamp: String, comment: String, uuid: String }[];
  postUuid: String;
  UserUuid: String;
}

export interface Comment {
  comment: String;
}

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private postsCach: Post[] = [];
  private commentCach: Comment[] = [];
  private URL = `http://localhost:8080/api/posts`;
  // private URLPOSTCOMMENT = 
  constructor(private http: HttpClient) { }

  getAllPosts(): Observable<Post[]> {
    if (this.postsCach.length > 0) {
      return of(this.postsCach);
    }
    return this.http.get<Post[]>(this.URL + '/all').pipe(tap(posts => this.postsCach = posts));
  }

  deletePost(uuid: String): Observable<any> {
    console.log(this.URL + '/delete/' + uuid);
    return this.http.post(this.URL + uuid, null);
  }

  editPost(uuid: String, formData: FormData): Observable<any> {
    console.log(this.URL + uuid);
    return this.http.post<Post>(this.URL + '/edit/' + uuid, formData);
  }

  doReaction(uuid: String) {
    return this.http.post<Like>(`${this.URL}/like/${uuid}`, null);
  }

  doComment(uuid: String, formData: FormData) {
    return this.http.post<Comment>(this.URL + '/comment/' + uuid, formData);
  }

  getPost(uuid: String) {
    return this.http.get<Post>(this.URL + '/postCard/' + uuid);
  }

  getComments(uuid: String) {
    return this.http.get<Comments>(this.URL + '/comment/' + uuid);
  }

  submitComment(comment: String, uuid: String) {
    return this.http.post<Comment>(this.URL + '/comment/create/' + uuid, { comment: comment })
  }
}
