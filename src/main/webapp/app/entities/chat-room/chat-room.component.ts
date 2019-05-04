import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IChatRoom } from 'app/shared/model/chat-room.model';
import { AccountService } from 'app/core';
import { ChatRoomService } from './chat-room.service';

@Component({
  selector: 'jhi-chat-room',
  templateUrl: './chat-room.component.html'
})
export class ChatRoomComponent implements OnInit, OnDestroy {
  chatRooms: IChatRoom[];
  currentAccount: any;
  eventSubscriber: Subscription;

  constructor(
    protected chatRoomService: ChatRoomService,
    protected jhiAlertService: JhiAlertService,
    protected eventManager: JhiEventManager,
    protected accountService: AccountService
  ) {}

  loadAll() {
    this.chatRoomService
      .query()
      .pipe(
        filter((res: HttpResponse<IChatRoom[]>) => res.ok),
        map((res: HttpResponse<IChatRoom[]>) => res.body)
      )
      .subscribe(
        (res: IChatRoom[]) => {
          this.chatRooms = res;
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  ngOnInit() {
    this.loadAll();
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
    this.registerChangeInChatRooms();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IChatRoom) {
    return item.id;
  }

  registerChangeInChatRooms() {
    this.eventSubscriber = this.eventManager.subscribe('chatRoomListModification', response => this.loadAll());
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }
}
