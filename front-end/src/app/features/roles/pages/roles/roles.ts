import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { AuthService } from '../../../auth/services/auth-service'; 
import { RouterLink } from '@angular/router';
import { User } from '../../models/role.model';

@Component({
  selector: 'app-roles',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './roles.html',
  styleUrl: './roles.scss',
})

export class Roles implements OnInit {

  private http = inject(HttpClient);
  private authService = inject(AuthService);
  protected users = signal<User[]>([]);
  protected isLoading = signal(false);
  

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.isLoading.set(true);
    
    this.authService.getUsers().subscribe({
      next: (data) => {
        this.users.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error when searching for users:', err);
        this.isLoading.set(false);
      }
    });
  }

  updateUserRole(userId: string | number, event: Event) {
    const selectElement = event.target as HTMLSelectElement;
    const newRole = selectElement.value;
    
    if (newRole) {
      const idAsString = String(userId);
      this.authService.updateRole(idAsString, newRole).subscribe({
        next: () => {
          this.loadUsers();
          selectElement.value = '';
        },
        error: (err) => console.error('Error updating role:', err)
      });
    }
  }
  
}
