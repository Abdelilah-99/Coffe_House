import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { UserService } from '../../services/services';
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
  @Output() closeSearch = new EventEmitter<void>();

  constructor(private userService: UserService, private route: Router) { }
  ngOnInit(): void {

    this.searchControl.valueChanges
      .pipe(
        debounceTime(400),
        distinctUntilChanged(),
        // tap(value => console.log('🔎 User typed:', value)),
        switchMap(value => this.userService.getUserFromSearch(value as string))
        // tap(res => console.log('✅ API response:', res))
      )
      .subscribe({
        next: (res) => {
          this.searchData = res;
          console.log("data has come");
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
    this.closeSearch.emit();
  }
}
