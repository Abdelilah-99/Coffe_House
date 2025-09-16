import { Component, OnInit } from '@angular/core';
import { Post, PostService } from '../../post/services/post-service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  imports: [CommonModule],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent implements OnInit {
  posts: Post[] = [];
  isLoding = true;
  constructor(private postService: PostService, private router: Router) { }

  ngOnInit(): void {
    this.loadPosts();
  }

  loadPosts() {
    if (typeof window !== 'undefined' && window.localStorage) {
      const token = localStorage.getItem('access_token');
      if (!token) {
        this.router.navigate(['/login']);
        return;
      }
    }
    this.postService.getAllPosts().subscribe({
      next: (posts) => {
        this.posts = posts;
        console.log(posts);
        this.isLoding = false;
      },
      error: (err) => {
        console.error("err loading posts");
        this.isLoding = false;
      }
    });
  }

  getMediaType(media: String): String {
    const ext = media.split('.').pop()?.toLowerCase();
    if (['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(ext!)) {
      return "img";
    } else if (['mp4', 'webm', 'ogg'].includes(ext!)) {
      return "vd";
    }
    return "null";
  }
}
