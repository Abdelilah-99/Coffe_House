import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { ToastService, ToastData } from "../service/toast";

@Component({
    selector: 'app-toast',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './toastComponent.html',
    styleUrls: ['./toastComponent.css']
})
export class ToastComponent implements OnInit {
    toastMessage: ToastData | null = null;

    constructor(private toastService: ToastService) { }

    ngOnInit() {
        this.toastService.toast$.subscribe(toast => {
            this.toastMessage = toast;
        });
    }
}
