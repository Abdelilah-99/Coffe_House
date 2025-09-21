import { Routes } from '@angular/router';
import { Login } from './auth/login/login';
import { Register } from './auth/register/register';
import { Profile } from './me/me';
import { HomeComponent } from './home/components/home-component';
import { PostCard } from './post-card/post-card';
export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'login', component: Login },
    { path: 'register', component: Register },
    { path: 'me', component: Profile },
    { path: 'postCard/:id', component: PostCard }
];
