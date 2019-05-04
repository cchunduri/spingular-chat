import { Routes } from '@angular/router';

import { ChatComponent } from 'app/entities/chat/chat.component';
import { UserRouteAccessService } from 'app/core';

export const chatRoute: Routes = [
  {
    path: 'chat',
    component: ChatComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'chat.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
