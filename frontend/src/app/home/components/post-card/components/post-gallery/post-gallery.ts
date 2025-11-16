import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MediaDTO, Post } from '../../../../../post/services/post-service';
import { environment } from '../../../../../../environments/environment';

@Component({
  selector: 'app-post-gallery',
  imports: [CommonModule],
  templateUrl: './post-gallery.html',
  styleUrl: './post-gallery.css'
})
export class PostGallery {
  @Input() post!: Post;
  @Input() mediaPaths: MediaDTO[] = [];
  
  apiUrl = environment.apiUrl;

  isImage(type: string): boolean {
    return type.startsWith('image/');
  }

  isVideo(type: string): boolean {
    return type.startsWith('video/');
  }
}
