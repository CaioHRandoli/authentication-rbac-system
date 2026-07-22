import { inject, Injectable } from '@angular/core';
import { TokenService } from '../../../core/services/token-service'; 
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginRequestDto, LoginResponseDto } from '../models/auth.model';
import { User } from '../../roles/models/role.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api/auth';
  private readonly USERS_API_URL = 'http://localhost:8080/api/users';
  private http = inject(HttpClient);
  private tokenService = inject(TokenService);

  login(credentials: LoginRequestDto): Observable<LoginResponseDto> {
    return this.http.post<LoginResponseDto>(`${this.API_URL}/login`, credentials).pipe(
      tap(response => {
        if (response && response.token) {
          this.tokenService.saveToken(response.token);
        }
      })
    );
  }

  logout(): void {
    this.tokenService.clearToken();
  }

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.USERS_API_URL);
  }

  updateRole(userId: string, roleName: string): Observable<void> {
    return this.http.put<void>(`${this.USERS_API_URL}/${userId}/role`, { role: roleName });
  }
}
