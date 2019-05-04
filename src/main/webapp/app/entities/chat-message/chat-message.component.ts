import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IChatMessage } from 'app/shared/model/chat-message.model';
import { AccountService } from 'app/core';
import { ChatMessageService } from './chat-message.service';

@Component({
  selector: 'jhi-chat-message',
  templateUrl: './chat-message.component.html'
})
export class ChatMessageComponent implements OnInit, OnDestroy {
  chatMessages: IChatMessage[];
  currentAccount: any;
  eventSubscriber: Subscription;

  constructor(
    protected chatMessageService: ChatMessageService,
    protected jhiAlertService: JhiAlertService,
    protected eventManager: JhiEventManager,
    protected accountService: AccountService
  ) {}

  loadAll() {
    this.chatMessageService
      .query()
      .pipe(
        filter((res: HttpResponse<IChatMessage[]>) => res.ok),
        map((res: HttpResponse<IChatMessage[]>) => res.body)
      )
      .subscribe(
        (res: IChatMessage[]) => {
          this.chatMessages = res;
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  ngOnInit() {
    this.loadAll();
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
    this.registerChangeInChatMessages();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IChatMessage) {
    return item.id;
  }

  registerChangeInChatMessages() {
    this.eventSubscriber = this.eventManager.subscribe('chatMessageListModification', response => this.loadAll());
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }
}
