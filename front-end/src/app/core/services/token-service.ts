import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class TokenService {
  private readonly TOKEN_KEY = 'auth-token';

  saveToken(token: string): void {
    window.localStorage.removeItem(this.TOKEN_KEY);
    window.localStorage.setItem(this.TOKEN_KEY, token);
  }

  getToken(): string | null {
    return window.localStorage.getItem(this.TOKEN_KEY);
  }

  clearToken(): void {
    window.localStorage.removeItem(this.TOKEN_KEY);
  }

  hasToken(): boolean {
    return !!this.getToken();
  }

  getUserRole(): string {
    const token = this.getToken();
    if (!token) return '';

    try {
      const payload = JSON.parse(atob(token.split('.')[1])); 
        
      if (typeof payload.role === 'string') {
        return payload.role;
      }
        
      if (Array.isArray(payload.roles) && payload.roles.length > 0) {
        return typeof payload.roles[0] === 'string' ? payload.roles[0] : payload.roles[0].role || '';
      }

      return '';
    } catch (error) {
      console.error('Error decoding token role', error);
      return '';
    }
  }
}
