import { Component, Input } from '@angular/core';
import { Post } from '../../../../../post/services/post-service';

@Component({
  selector: 'app-post-comments',
  imports: [],
  templateUrl: './post-comments.html',
  styleUrl: './post-comments.css'
})
export class PostComments {
  @Input() post!: Post;
}
