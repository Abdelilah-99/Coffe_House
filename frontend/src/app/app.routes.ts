import { Routes } from '@angular/router';
import { Login } from './auth/login/login';
import { Register } from './auth/register/register';
import { Profile } from './me/me';
import { HomeComponent } from './home/component/home-component';
export const routes: Routes = [
    // { path: '', redirectTo: 'login', pathMatch: 'full' },
    { path: '', component: HomeComponent },
    { path: 'login', component: Login },
    { path: 'register', component: Register },
    { path: 'me', component: Profile }
    // { path: 'editPost', component: EditPost }
];
