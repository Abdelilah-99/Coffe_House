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
import { EditModel } from './edit-modal/edit-model';
@Component({
  selector: 'app-home',
  imports: [CommonModule, FormsModule, Sidebar, PostCardComponent, EditModel],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent implements OnInit {
  posts: Post[] = [];
  like: Like = { userUuid: null, postUuid: null, likeCount: 0 };
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

  onPostCardSection(postUuid: String) {
    console.log("this section will navigate throw the post section ", postUuid);
    this.router.navigate(['/postCard', postUuid]);
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

  // onComment(id: number) {
  //   let commentData = new FormData();
  //   this.postService.doComment(id, commentData).subscribe({
  //   })
  // }

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
    this.postService.editPost(updatedPost.postUuid, formData).subscribe({
      next: (data) => {
        this.updatedPost = data;
        if (this.updatedPost) {
          for (let index = 0; index < this.posts.length; index++) {
            const element = this.posts[index];
            if (element.postUuid === this.updatedPost.postUuid) {
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

  onDelete(postUuid: String) {
    console.log("hii: ", postUuid);
    this.postService.deletePost(postUuid).subscribe({});
    for (let index = 0; index < this.posts.length; index++) {
      const element = this.posts[index];
      if (element.postUuid === postUuid) {
        this.posts.splice(index, 1);
      }
    }
    this.loadPosts();
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
    if (post.userUuid === this.profileData?.userUuid) {
      return true;
    }
    return false;
  }
}
