import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IChatRoom } from 'app/shared/model/chat-room.model';

@Component({
  selector: 'jhi-chat-room-detail',
  templateUrl: './chat-room-detail.component.html'
})
export class ChatRoomDetailComponent implements OnInit {
  chatRoom: IChatRoom;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ chatRoom }) => {
      this.chatRoom = chatRoom;
    });
  }

  previousState() {
    window.history.back();
  }
}
