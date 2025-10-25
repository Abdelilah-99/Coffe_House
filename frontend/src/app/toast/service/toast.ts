import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type ToastType = 'success' | 'error' | 'warning';

export interface ToastData {
    text: string;
    type: ToastType;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
    private toastSubject = new BehaviorSubject<ToastData | null>(null);
    toast$ = this.toastSubject.asObservable();

    show(text: string, type: ToastType) {
        this.toastSubject.next({ text, type });
        setTimeout(() => this.toastSubject.next(null), 3000);
    }

    getMessageType(message: string): ToastType {
        const lowerMessage = message.toLowerCase();
        if (lowerMessage.includes('banned') || lowerMessage.includes('deleted')) return 'warning';
        if (lowerMessage.includes('success') || lowerMessage.includes('successfully')) return 'success';
        return 'error';
    }
}
