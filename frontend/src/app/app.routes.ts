import { RouterModule, Routes } from '@angular/router';
import { Login } from './auth/login/login';
import { Register } from './auth/register/register';
import { Me } from './me/components/me';
import { HomeComponent } from './home/components/home-component';
import { PostCard } from './post/components/post-card/post-card';
import { Edit } from './edit/edit';
import { Profile } from './profile/component/profile';
import { Notification } from './notification/components/notification';
import { AdminPanel } from './admin-panel/components/admin-panel';
import { adminGuard } from './admin-panel/admin-panel-guard';
import { ErrorPage } from './error/components/error-page';

export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'login', component: Login },
    { path: 'register', component: Register },
    { path: 'me', component: Me },
    { path: 'postCard/:id', component: PostCard },
    { path: 'edit/:id', component: Edit },
    { path: 'profile/:id', component: Profile },
    { path: 'notification', component: Notification },
    { path: 'admin', component: AdminPanel, canActivate: [adminGuard] },
    { path: 'error', component: ErrorPage },
    { path: '**', component: ErrorPage }
];
