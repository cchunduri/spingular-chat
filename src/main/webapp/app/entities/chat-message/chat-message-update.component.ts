import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { IChatMessage, ChatMessage } from 'app/shared/model/chat-message.model';
import { ChatMessageService } from './chat-message.service';

@Component({
  selector: 'jhi-chat-message-update',
  templateUrl: './chat-message-update.component.html'
})
export class ChatMessageUpdateComponent implements OnInit {
  chatMessage: IChatMessage;
  isSaving: boolean;
  messageTimeDp: any;

  editForm = this.fb.group({
    id: [],
    sender: [],
    reciver: [],
    messageText: [],
    messageTime: []
  });

  constructor(protected chatMessageService: ChatMessageService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ chatMessage }) => {
      this.updateForm(chatMessage);
      this.chatMessage = chatMessage;
    });
  }

  updateForm(chatMessage: IChatMessage) {
    this.editForm.patchValue({
      id: chatMessage.id,
      sender: chatMessage.sender,
      reciver: chatMessage.reciver,
      messageText: chatMessage.messageText,
      messageTime: chatMessage.messageTime
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const chatMessage = this.createFromForm();
    if (chatMessage.id !== undefined) {
      this.subscribeToSaveResponse(this.chatMessageService.update(chatMessage));
    } else {
      this.subscribeToSaveResponse(this.chatMessageService.create(chatMessage));
    }
  }

  private createFromForm(): IChatMessage {
    const entity = {
      ...new ChatMessage(),
      id: this.editForm.get(['id']).value,
      sender: this.editForm.get(['sender']).value,
      reciver: this.editForm.get(['reciver']).value,
      messageText: this.editForm.get(['messageText']).value,
      messageTime: this.editForm.get(['messageTime']).value
    };
    return entity;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IChatMessage>>) {
    result.subscribe((res: HttpResponse<IChatMessage>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
}
