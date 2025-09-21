import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Like, Post } from '../../../../../post/services/post-service';

@Component({
  selector: 'app-post-likes',
  imports: [],
  templateUrl: './post-likes.html',
  styleUrl: './post-likes.css'
})
export class PostLikes {
  @Input() post!: Post;
  @Input() like!: Like;
  @Output() reaction = new EventEmitter<number>();
}
