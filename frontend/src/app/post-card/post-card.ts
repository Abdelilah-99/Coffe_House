import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { Like, Post, PostService, Comment } from '../post/services/post-service';
import { ActivatedRoute } from '@angular/router';
import { CommonModule, isPlatformBrowser } from '@angular/common';

@Component({
  selector: 'app-post-card',
  imports: [CommonModule],
  templateUrl: './post-card.html',
  styleUrl: './post-card.css'
})
export class PostCard implements OnInit {
  constructor(private postService: PostService, private route: ActivatedRoute, @Inject(PLATFORM_ID) private platformId: Object) { }
  postUuid: String | null = null;
  post?: Post;
  like?: Like;
  comment?: Comment;
  isCommenting = false;
  ngOnInit(): void {
    this.postUuid = this.route.snapshot.paramMap.get('id');
    this.loadCardData();
  }

  loadCardData() {
    if (isPlatformBrowser(this.platformId) && this.postUuid) {
      this.postService.getPost(this.postUuid).subscribe({
        next: (post) => {
          this.post = post;
          console.log(this.post);
        },
        error: (err) => {
          console.error("errorl loading post ", err);
        }
      })
    }
  }

  getMediaType(media: String): String {
    const ext = media.split('.').pop()?.toLowerCase();
    if (['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(ext!)) {
      return 'img';
    } else if (['mp4', 'webm', 'ogg'].includes(ext!)) {
      return 'vd';
    }
    return 'null';
  }

  onReact(uuid: String) {
    this.postService.doReaction(uuid).subscribe({
      next: (like) => {
        if (this.post) {
          this.post.likeCount = like.likeCount;
        }
      },
      error: (err) => {
        console.error("error loading like data ", err);
      }
    });
  }

  onComment(uuid: String) {
    this.isCommenting = !this.isCommenting;
    console.log(this.isCommenting);
    
    if (this.isCommenting) {
      this.postService.getComments(uuid).subscribe({
        next: (comment) => {
            this.comment = comment;
            console.log(comment);
        },
        error: (err) => {
          console.error("error loading comments ", err);
        }
      })
    }
  }
}
