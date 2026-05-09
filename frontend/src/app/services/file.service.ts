import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface FileDeleteResponse {
  message?: string;
  error?: string;
}

export interface FileExistsResponse {
  exists: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class FileService {
  private fileUrl = '/api/files';

  constructor(private http: HttpClient) { }

  /**
   * Delete a file from the server
   * @param filePath the relative path of the file to delete (e.g., "posts/image.jpg")
   * @return Observable with deletion response
   */
  deleteFile(filePath: string): Observable<FileDeleteResponse> {
    return this.http.delete<FileDeleteResponse>(
      `${this.fileUrl}/delete?filePath=${encodeURIComponent(filePath)}`
    );
  }

  /**
   * Check if a file exists on the server
   * @param filePath the relative path of the file to check
   * @return Observable with existence status
   */
  checkFileExists(filePath: string): Observable<FileExistsResponse> {
    return this.http.get<FileExistsResponse>(
      `${this.fileUrl}/exists?filePath=${encodeURIComponent(filePath)}`
    );
  }

  /**
   * Delete multiple files
   * @param filePaths array of file paths to delete
   * @return Observable array of deletion responses
   */
  deleteFiles(filePaths: string[]): Observable<FileDeleteResponse[]> {
    const deleteRequests = filePaths.map(path => this.deleteFile(path));
    return new Observable(observer => {
      Promise.all(deleteRequests.map(req => req.toPromise()))
        .then(results => {
          observer.next(results as FileDeleteResponse[]);
          observer.complete();
        })
        .catch(error => observer.error(error));
    });
  }
}
