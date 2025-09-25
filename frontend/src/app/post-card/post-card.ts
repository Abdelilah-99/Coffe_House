import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { Like, Post, PostService, Comments } from '../post/services/post-service';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ProfileService, UserProfile } from '../me/me.service';
import { time } from 'console';

@Component({
  selector: 'app-post-card',
  imports: [CommonModule],
  templateUrl: './post-card.html',
  styleUrl: './post-card.css'
})
export class PostCard implements OnInit {
  constructor(private postService: PostService, private route: ActivatedRoute, @Inject(PLATFORM_ID) private platformId: Object, private navigate: Router, private profileService: ProfileService) { }
  postUuid: String | null = null;
  post?: Post;
  like?: Like;
  comment?: Comments;
  isCommenting = false;
  profileData: UserProfile | null = null;
  ngOnInit(): void {
    this.loadProfile();
    this.postUuid = this.route.snapshot.paramMap.get('id');
    this.loadCardData();
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

  instantComments?: { comment: String, username: String, time: number }[] = [];

  onSubmitComment(comment: String, uuid: String | null = null) {
    if (uuid) {
      this.postService.submitComment(comment, uuid).subscribe({
        next: (res) => {
          this.instantComments?.push({
            comment: res.comment,
            username: this.profileData?.username || '',
            time: Date.now()
          });
          this.instantComments?.reverse();
          if (this.post) {
            this.post.commentCount += 1;
          }
        },
        error: (err) => {
          console.error(err);
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

  onEdit(postUuid: String) {
    console.log("this section will navigate throw the post section ", postUuid);
    this.navigate.navigate(['/edit', postUuid]);
  }
  onReport(id: String) {
    console.log("hii: ", id);
  }

  onDelete(uuid: String) {
    this.postService.deletePost(uuid).subscribe({
      next: (res) => {
        console.log("post has been deleted ", res);
        this.navigate.navigate(['']);
      },
      error: (err) => {
        console.error("error deleting post ", err);
      }
    })
  }

  myPost(userUuid: String): boolean {
    if (isPlatformBrowser(this.platformId)) {
      const token = localStorage.getItem('access_token');
      if (!token) {
        this.navigate.navigate(['/login']);
      }
    }
    console.log(userUuid + "t" + this.profileData?.uuid);

    if (userUuid === this.profileData?.uuid) {
      return true;
    }
    return false;
  }
}
