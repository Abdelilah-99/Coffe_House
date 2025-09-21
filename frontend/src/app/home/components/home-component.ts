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
  like: Like = { userId: null, postId: null, likeCount: 0 };
  updatedPost: Post | null = null;
  selectedPost?: Post;
  profileData: UserProfile | null = null;
  isLoding = true;
  post = { title: '', content: '' }
  constructor(private postService: PostService,
    private router: Router,
    private profileService: ProfileService,
    @Inject(PLATFORM_ID) private platformId: Object) { }

  ngOnInit() {
    this.loadPosts();
    this.loadProfile();
  }

  loadProfile() {
    if (isPlatformBrowser(this.platformId)) {
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

  onEdit(post: Post) {
    this.selectedPost = post;
  }

  onCloseEdit() {
    this.selectedPost = undefined;
  }

  selectedFiles: File[] = [];

  onFileSelected(e: any) {
    const files: FileList = e.target.files;
    if (!files || files.length === 0) return;
    // this.selectedFiles = [];
    for (let i = 0; i < files.length; i++) {
      this.selectedFiles.push(files[i]);
    }
    console.log("file len: ", files.length);
    console.log('Selected files:', this.selectedFiles.map(f => f.name));
  }

  onReact(id: number) {
    this.postService.doReaction(id).subscribe({
      next: (like) => {
        console.log(like);
        for (let i = 0; i < this.posts.length; i++) {
          const element = this.posts[i];
          if (element.id == id) {
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


  onSave(updatedPost: any) {
    console.log(updatedPost);
    const formData = new FormData();
    formData.append("title", updatedPost.title);
    formData.append("content", updatedPost.content);
    formData.append("pathFiles", updatedPost.mediaPaths);
    console.log(formData.get("title"));
    this.selectedFiles.forEach(element => {
      formData.append("mediaFiles", element);
    });
    this.postService.editPost(updatedPost.id, formData).subscribe({
      next: (data) => {
        this.updatedPost = data;
        if (this.updatedPost) {
          for (let index = 0; index < this.posts.length; index++) {
            const element = this.posts[index];
            if (element.id === this.updatedPost.id) {
              this.posts[index] = this.updatedPost;
            }
          }
        }
      },
      error: (err) => {
        console.error("error updating post: ", err);
      }
    });
    updatedPost = undefined;
  }

  onDelete(id: number) {
    console.log("hii: ", id);
    this.postService.deletePost(id).subscribe({});
    for (let index = 0; index < this.posts.length; index++) {
      const element = this.posts[index];
      if (element.id === id) {
        this.posts.splice(index, 1);
      }
    }
    this.loadPosts();
  }

  onReport(id: number) {
    console.log("hii: ", id);
  }

  printPaths(paths: String[]) {
    paths.forEach(element => {
      console.log("paths:: ", element);
    });
  }

  getMediaType(media: String): String {
    console.log("media", media);
    const ext = media.split('.').pop()?.toLowerCase();
    if (['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(ext!)) {
      return "img";
    } else if (['mp4', 'webm', 'ogg'].includes(ext!)) {
      return "vd";
    }
    return "null";
  }

  deleteMedia(media: String, selectedMedia: any) {
    let mediaPaths = this.selectedPost?.mediaPaths;
    if (mediaPaths === undefined) return;
    for (let index = 0; index < mediaPaths.length; index++) {
      const element = mediaPaths[index];
      if (media === element) {
        this.selectedPost?.mediaPaths.splice(index, 1);
      }
    }
  }

  myPost(post: Post): boolean {
    if (isPlatformBrowser(this.platformId)) {
      const token = localStorage.getItem('access_token');
      if (!token) {
        this.router.navigate(['/login']);
      }
    }
    if (post.userId === this.profileData?.id) {
      return true;
    }
    return false;
  }
}
