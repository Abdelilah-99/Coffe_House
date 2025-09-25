import { Component, OnInit } from '@angular/core';
import { Like, Post, PostService } from '../../post/services/post-service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProfileService, UserProfile } from '../../me/me.service';
import { isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Sidebar } from './sidebar/sidebar';
import { PostCardComponent } from './post-card/post-card';
@Component({
  selector: 'app-home',
  imports: [CommonModule, FormsModule, Sidebar, PostCardComponent],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent implements OnInit {
  posts: Post[] = [];
  like: Like = { userUuid: null, postUuid: null, likeCount: 0 };
  selectedPost?: Post;
  profileData: UserProfile | null = null;
  isLoding = true;
  post = { title: '', content: '' }
  constructor(private postService: PostService,
    private router: Router,
    private profileService: ProfileService,
    @Inject(PLATFORM_ID) private platformId: Object) { }

  ngOnInit() {
    this.loadProfile();
    this.loadPosts();
  }

  loadProfile() {
    if (isPlatformBrowser(this.platformId)) {
      this.profileService.getProfile().subscribe({
        next: (data) => {
          this.profileData = data;
          console.log("test: ", this.profileData);
        },
        error: (err) => {
          console.error("err loading profile for post check: ", err);
        }
      });
    }
  }

  loadPosts() {
    if (isPlatformBrowser(this.platformId)) {
      const token = localStorage.getItem('access_token');
      if (!token) {
        this.router.navigate(['/login']);
      }
      this.postService.getAllPosts().subscribe({
        next: (posts) => {
          this.posts = posts;
          console.log(posts);
          this.isLoding = false;
        },
        error: (err) => {
          console.error("err loading posts: ", err);
          this.isLoding = false;
        }
      });
    }
  }

  onEdit(postUuid: String) {
    console.log("this section will navigate throw the post section ", postUuid);
    this.router.navigate(['/edit', postUuid]);
  }

  onPostCardSection(postUuid: String) {
    console.log("this section will navigate throw the post section ", postUuid);
    this.router.navigate(['/postCard', postUuid]);
  }

  onReact(uuid: String) {
    this.postService.doReaction(uuid).subscribe({
      next: (like) => {
        console.log(like);
        for (let i = 0; i < this.posts.length; i++) {
          const element = this.posts[i];
          if (element.postUuid == uuid) {
            console.log(this.posts[i].likeCount, like.likeCount);
            element.likeCount = like.likeCount;
            this.posts[i] = element;
          }
        }
      },
      error: (err) => {
        console.error("error like ", err);
      }
    })
  }

  onReport(id: String) {
    console.log("hii: ", id);
  }

  printPaths(paths: String[]) {
    paths.forEach(element => {
      console.log("paths:: ", element);
    });
  }

  myPost(post: Post): boolean {
    if (isPlatformBrowser(this.platformId)) {
      const token = localStorage.getItem('access_token');
      if (!token) {
        this.router.navigate(['/login']);
      }
    }
    if (post.userUuid === this.profileData?.uuid) {
      return true;
    }
    return false;
  }
}
