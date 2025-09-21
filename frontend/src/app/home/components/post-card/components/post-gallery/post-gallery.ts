import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Post } from '../../../../../post/services/post-service';

@Component({
  selector: 'app-post-gallery',
  imports: [CommonModule],
  templateUrl: './post-gallery.html',
  styleUrl: './post-gallery.css'
})
export class PostGallery {
  @Input() post!: Post;
  @Input() mediaPaths: string[] = [];
  getMediaType(media: String): String {
    const ext = media.split('.').pop()?.toLowerCase();
    if (['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(ext!)) {
      return 'img';
    } else if (['mp4', 'webm', 'ogg'].includes(ext!)) {
      return 'vd';
    }
    return 'null';
  }
}
