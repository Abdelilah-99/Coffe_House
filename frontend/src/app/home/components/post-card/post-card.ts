import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Like, Post } from '../../../post/services/post-service';
import { UserProfile } from '../../../me/me.service';
import { CommonModule } from '@angular/common';
import { PostGallery } from './components/post-gallery/post-gallery';
import { PostLikes } from './components/post-likes/post-likes';
import { PostComments } from './components/post-comments/post-comments';

@Component({
  selector: 'app-post-card',
  imports: [CommonModule, PostGallery, PostLikes, PostComments],
  templateUrl: './post-card.html',
  styleUrls: ['./post-card.css'],
  standalone: true
})
export class PostCardComponent {
  @Input() post!: Post;
  @Input() profileData!: UserProfile | null;

  @Output() edit = new EventEmitter<Post>();
  @Output() delete = new EventEmitter<number>();
  @Output() report = new EventEmitter<number>();
  @Output() reaction = new EventEmitter<number>();
  @Output() postCard = new EventEmitter<number>();
  myPost(): boolean {
    return this.post.userId === this.profileData?.id;
  }
}
