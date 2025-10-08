import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { Post, PostService } from '../post/services/post-service';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-edit',
  imports: [CommonModule, FormsModule],
  templateUrl: './edit.html',
  styleUrls: ['./edit.css']
})
export class Edit implements OnInit {
  constructor(private postService: PostService, private route: ActivatedRoute, @Inject(PLATFORM_ID) private platformId: Object, public navigate: Router) { }
  post: Post | null = null;
  postUuid: String | null = null;
  updatedPost: Post | null = null;

  ngOnInit(): void {
    this.postUuid = this.route.snapshot.paramMap.get('id');
    this.loadPost();
  }

  loadPost() {
    if (this.postUuid && isPlatformBrowser(this.platformId)) {
      this.postService.getPost(this.postUuid).subscribe({
        next: (post) => {
          this.post = post;
          console.log("post in edit ", this.post);
        },
        error: (err) => {
          console.error("post not found ", err);
        }
      })
    }
  }

  deleteMedia(media: String, selectedMedia: any) {
    let mediaPaths = this.post?.mediaPaths;
    if (mediaPaths === undefined) return;
    for (let index = 0; index < mediaPaths.length; index++) {
      const element = mediaPaths[index];
      if (media === element) {
        this.post?.mediaPaths.splice(index, 1);
      }
    }
  }

  selectedFiles: File[] = [];
  previewUrls: string[] = [];

  onFileSelected(e: any) {
    const files: FileList = e.target.files;
    if (!files || files.length === 0) return;

    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      this.selectedFiles.push(file);

      const url = URL.createObjectURL(file);
      console.log(url);
      this.previewUrls.push(url);
    }
  }

  deleteFileSelected(index: number) {
    this.selectedFiles.splice(index, 1);
    this.previewUrls.splice(index, 1);
  }

  onSave(updatedPost: any) {
    console.log(updatedPost);
    const formData = new FormData();
    formData.append("title", updatedPost.title);
    formData.append("content", updatedPost.content);
    formData.append("pathFiles", updatedPost.mediaPaths);
    this.selectedFiles.forEach(element => {
      formData.append("mediaFiles", element);
    });
    this.postService.editPost(updatedPost.postUuid, formData).subscribe({
      next: (data) => {
        this.updatedPost = data;
        this.navigate.navigate(['']);
        this.selectedFiles = [];
        this.previewUrls = [];
      },
      error: (err) => {
        console.error("error updating post: ", err);
      }
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
}
