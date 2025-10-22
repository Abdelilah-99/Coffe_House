import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Like, Post, PostPage, PostService } from '../../post/services/post-service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PostCardComponent } from './post-card/post-card';
@Component({
  selector: 'app-home',
  imports: [CommonModule, FormsModule, PostCardComponent],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent implements OnInit, OnDestroy {
  @ViewChild('anchor', { static: true }) anchor!: ElementRef<HTMLElement>;
  observer!: IntersectionObserver;
  postsPage?: PostPage;
  posts: Post[] = [];
  like: Like = { userUuid: null, postUuid: null, likeCount: 0 };
  selectedPost?: Post;
  lastUuid: string | null = null;
  lastTime: number | null = null;
  isLoding = false;
  post = { title: '', content: '' }
  constructor(private postService: PostService,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object) { }

  private initObserver() {
    if (!isPlatformBrowser(this.platformId)) return;
    this.observer = new IntersectionObserver((entries) => {
      if (entries[0].isIntersecting && !this.isLoding && this.lastTime && this.lastUuid) {
        this.loadPostByPage(this.lastTime, this.lastUuid);
      }
    });
    this.observer.observe(this.anchor.nativeElement);
  }


  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      const token = localStorage.getItem('access_token');
      if (!token) {
        this.router.navigate(['/login']);
        return;
      }
      this.loadPostByPage(null, null, () => {
        this.initObserver();
      });
    }
  }

  loadPostByPage(lastTime: number | null, lastUuid: string | null, onFinish?: () => void) {
    this.isLoding = true;
    this.postService.loadMore(lastTime, lastUuid).subscribe({
      next: (res) => {
        this.postsPage = res;
        this.lastTime = res.lastTime;
        if (res.lastUuid) {
          this.lastUuid = res.lastUuid.toString();
        }
        this.posts.push(...res.posts);
        this.isLoding = false;
        if (onFinish) onFinish();
      },
      error: (err) => {
        console.error("failed loading pages: ", err);
        this.isLoding = false;
        if (onFinish) onFinish();
      }
    });
  }


  ngOnDestroy(): void {
    if (this.observer) this.observer.disconnect();
  }

  loadPosts() {
    this.postService.getAllPosts().subscribe({
      next: (posts) => {
        this.posts = posts;
        this.isLoding = false;
      },
      error: (err) => {
        console.error("err loading posts: ", err);
        this.isLoding = false;
      }
    });
  }

  onPostCardSection(postUuid: String) {
    this.router.navigate(['/postCard', postUuid]);
  }

  onReact(uuid: String) {
    this.postService.doReaction(uuid).subscribe({
      next: (like) => {
        for (let i = 0; i < this.posts.length; i++) {
          const element = this.posts[i];
          if (element.postUuid == uuid) {
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
}
