import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SpingularChatModule } from 'app/entities/chat/chat.module';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'chat-room',
        loadChildren: './chat-room/chat-room.module#SpingularChatChatRoomModule'
      },
      {
        path: 'chat-message',
        loadChildren: './chat-message/chat-message.module#SpingularChatChatMessageModule'
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
    SpingularChatModule
  ],
  declarations: [],
  entryComponents: [],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SpingularChatEntityModule {}
