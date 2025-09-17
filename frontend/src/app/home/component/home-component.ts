import { Component, OnInit } from '@angular/core';
import { Post, PostService } from '../../post/services/post-service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProfileService, UserProfile } from '../../me/me.service';
import { AuthService } from '../../auth/auth';
@Component({
  selector: 'app-home',
  imports: [CommonModule],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent implements OnInit {
  posts: Post[] = [];
  profileData: UserProfile | null = null;
  isLoding = true;
  constructor(private postService: PostService, private router: Router, private profileService: ProfileService) { }

  ngOnInit(): void {
    // if ()
    this.loadPosts();
    this.loadProfile();
  }

  loadProfile() {
    this.profileService.getProfile().subscribe({
      next: (data) => {
        console.log("test: ", this.profileData);
        this.profileData = data;
      },
      error: (err) => {
        console.error("err loading profile for post check: ", err);
      }
    });
  }

  loadPosts() {
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

  getMediaType(media: String): String {
    const ext = media.split('.').pop()?.toLowerCase();
    if (['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(ext!)) {
      return "img";
    } else if (['mp4', 'webm', 'ogg'].includes(ext!)) {
      return "vd";
    }
    return "null";
  }

  myPost(post: Post): boolean {
    if (typeof window !== 'undefined' && window.localStorage) {
      const token = localStorage.getItem('access_token');
      if (!token) {
        this.router.navigate(['/login']);
      }
    }
    console.info(this.profileData);
    if (post.userId === this.profileData?.id) {
      return true;
    }
    return false;
  }
}
