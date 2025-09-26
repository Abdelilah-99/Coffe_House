import { Component } from '@angular/core';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-searchbar',
  imports: [],
  templateUrl: './searchbar.html',
  styleUrl: './searchbar.css'
})
export class Searchbar {
    formControl = new FormControl('');
    
}
