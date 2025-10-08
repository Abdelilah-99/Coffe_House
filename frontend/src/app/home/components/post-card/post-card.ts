import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Like, Post } from '../../../post/services/post-service';
import { UserProfile } from '../../../me/services/me.service';
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

  @Output() edit = new EventEmitter<String>();
  @Output() delete = new EventEmitter<String>();
  @Output() report = new EventEmitter<String>();
  @Output() reaction = new EventEmitter<String>();
  @Output() postCard = new EventEmitter<String>();
  myPost(): boolean {
    console.log(this.post.userUuid, this.profileData?.uuid);

    return this.post.userUuid === this.profileData?.uuid;
  }
}
