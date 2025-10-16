import { Injectable } from '@angular/core';
import { Observable, of, tap } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';

export interface Post {
  postUuid: String;
  userUuid: String;
  title: string;
  content: string;
  createdAt: number;
  userName: String;
  mediaPaths: string[];
  commentCount: number;
  likeCount: number;
  profileImagePath: string;
  status: String;
}

export interface PostPage {
  posts: Post[];
  lastTime: number;
  lastUuid: String;
}

export interface UserProfile {
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  uuid: String;
}

export interface Like {
  userUuid: any;
  postUuid: any;
  likeCount: number;
}

export interface Comments {
  comments: { userUuid: String, imageProfile: String, userName: String, createdAt: number, comment: String, uuid: String }[];
  postUuid: String;
  UserUuid: String;
}

export interface Comment {
  comment: String;
}

export interface Message {
  message: String;
}

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private URL = `http://localhost:8080/api/posts`;
  constructor(private http: HttpClient) { }

  getAllPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.URL + '/all');
  }

  deletePost(uuid: String): Observable<any> {
    return this.http.post(this.URL + '/delete/' + uuid, null);
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

  doReport(uuid: String, reason: String) {
    return this.http.post<Message>(`http://localhost:8080/api/report/post/${uuid}`, { reason: reason });
  }

  loadMore(lastTime: number | null, lastUuid: string | null) {
    if (!lastTime || !lastUuid) {
      return this.http.get<PostPage>(`http://localhost:8080/api/posts/pages`);
    }
    const params = new HttpParams()
      .set('lastTime', lastTime)
      .set('lastUuid', lastUuid);
    return this.http.get<PostPage>(`http://localhost:8080/api/posts/pages`, { params });
  }
}
