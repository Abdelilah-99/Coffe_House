import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { Post, PostService } from '../post/services/post-service';
import { ActivatedRoute } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';

@Component({
  selector: 'app-post-card',
  imports: [],
  templateUrl: './post-card.html',
  styleUrl: './post-card.css'
})
export class PostCard implements OnInit {
  constructor(private postService: PostService, private route: ActivatedRoute, @Inject(PLATFORM_ID) private platformId: Object) { }
  postUuid: String | null = null;
  post?: Post;
  ngOnInit(): void {
    this.postUuid = this.route.snapshot.paramMap.get('id');
    this.loadCardData();
  }

  loadCardData() {
    if (isPlatformBrowser(this.platformId)) {
      this.postService.getPost(this.postUuid as String).subscribe({
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
}
