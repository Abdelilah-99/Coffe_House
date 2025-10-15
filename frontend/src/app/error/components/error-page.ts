import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule, Location } from '@angular/common';

@Component({
  selector: 'app-error-page',
  templateUrl: './error-page.html',
  styleUrls: ['./error-page.css'],
  imports: [CommonModule, RouterModule]
})
export class ErrorPage {
  errorCode = '404';
  errorMessage = 'Page Not Found';
  errorDescription = "Oops! The page you're looking for doesn't exist.";

  constructor(
    private router: Router,
    private location: Location
  ) {}

  goHome() {
    this.router.navigate(['/']);
  }

  goBack() {
    this.location.back();
  }
}
