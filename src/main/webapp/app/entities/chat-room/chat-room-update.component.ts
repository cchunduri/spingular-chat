import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { IChatRoom, ChatRoom } from 'app/shared/model/chat-room.model';
import { ChatRoomService } from './chat-room.service';

@Component({
  selector: 'jhi-chat-room-update',
  templateUrl: './chat-room-update.component.html'
})
export class ChatRoomUpdateComponent implements OnInit {
  chatRoom: IChatRoom;
  isSaving: boolean;

  editForm = this.fb.group({
    id: [],
    name: [],
    desc: []
  });

  constructor(protected chatRoomService: ChatRoomService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ chatRoom }) => {
      this.updateForm(chatRoom);
      this.chatRoom = chatRoom;
    });
  }

  updateForm(chatRoom: IChatRoom) {
    this.editForm.patchValue({
      id: chatRoom.id,
      name: chatRoom.name,
      desc: chatRoom.desc
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const chatRoom = this.createFromForm();
    if (chatRoom.id !== undefined) {
      this.subscribeToSaveResponse(this.chatRoomService.update(chatRoom));
    } else {
      this.subscribeToSaveResponse(this.chatRoomService.create(chatRoom));
    }
  }

  private createFromForm(): IChatRoom {
    const entity = {
      ...new ChatRoom(),
      id: this.editForm.get(['id']).value,
      name: this.editForm.get(['name']).value,
      desc: this.editForm.get(['desc']).value
    };
    return entity;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IChatRoom>>) {
    result.subscribe((res: HttpResponse<IChatRoom>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
}
