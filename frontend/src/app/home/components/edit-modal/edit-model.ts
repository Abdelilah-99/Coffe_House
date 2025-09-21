import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Post } from '../../../post/services/post-service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-edit-model',
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-model.html',
  styleUrl: './edit-model.css'
})
export class EditModel {
  @Input() post!: Post;
  @Input() selectedPost!: Post;
  @Output() save = new EventEmitter<Post>();
  @Output() close = new EventEmitter<void>();
  @Output() file = new EventEmitter<any>();
  @Output() mediaDel = new EventEmitter<[string, any]>();
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
