import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Post } from '../../post/services/post-service';

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
  profileImagePath: string;
}

@Injectable({
  providedIn: 'root'
})
export class MeService {
  private URL = 'http://localhost:8080/api/auth/me';
  private URLPOST = 'http://localhost:8080/api/posts';

  constructor(private http: HttpClient) { }

  getProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.URL}`);
  }

  createPost(formData: FormData): Observable<any> {
    return this.http.post(`${this.URLPOST}/create`, formData);
  }

  getUserPosts(userUuid: String): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.URLPOST}/user/${userUuid}`);
  }

  loadMore(lastTime: number | null, lastUuid: string | null) {
    if (!lastTime || !lastUuid) {
      return this.http.get<PostPage>(`http://localhost:8080/api/posts/mypost-page`);
    }
    const params = new HttpParams()
      .set('lastTime', lastTime)
      .set('lastUuid', lastUuid);
    return this.http.get<PostPage>(`http://localhost:8080/api/posts/mypost-page`, { params });
  }
}
