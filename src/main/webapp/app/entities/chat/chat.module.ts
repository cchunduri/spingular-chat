import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ChatComponent } from 'app/entities/chat/chat.component';
import { chatRoute } from 'app/entities/chat/chat.route';

@NgModule({
  declarations: [ChatComponent],
  imports: [RouterModule.forChild(chatRoute)]
})
export class SpingularChatModule {}
