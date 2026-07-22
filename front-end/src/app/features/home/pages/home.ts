import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { TokenService } from '../../../core/services/token-service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})

export class Home {
  private router = inject(Router);
  private tokenService = inject(TokenService);

  isAdmin(): boolean {
    return this.tokenService.getUserRole() === 'ROLE_ADMIN';
  }

  logout(): void {
    this.tokenService.clearToken();
    this.router.navigate(['/login']);
  }
}
