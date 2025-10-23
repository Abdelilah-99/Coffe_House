import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { UserService } from '../services/services';
import { debounceTime, distinctUntilChanged, switchMap, tap } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
@Component({
  selector: 'app-searchbar',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './searchbar.html',
  styleUrl: './searchbar.css'
})
export class Searchbar implements OnInit {
  searchControl = new FormControl('');
  searchData: any[] = [];

  constructor(private userService: UserService, private route: Router) { }
  ngOnInit(): void {

    this.searchControl.valueChanges
      .pipe(
        debounceTime(400),
        distinctUntilChanged(),
        switchMap(value => this.userService.getUserFromSearch(value as string))
      )
      .subscribe({
        next: (res) => {
          this.searchData = res;
        },
        error: (err) => {
          console.error("data didn't come ", err);
        }
      });
  }

  goToProfile(uuid: String) {
    this.route.navigate(['profile', uuid]);
    this.searchControl.setValue('');
    this.searchData = [];
  }
}
