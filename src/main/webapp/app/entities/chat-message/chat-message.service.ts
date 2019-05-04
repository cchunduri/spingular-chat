import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IChatMessage } from 'app/shared/model/chat-message.model';

type EntityResponseType = HttpResponse<IChatMessage>;
type EntityArrayResponseType = HttpResponse<IChatMessage[]>;

@Injectable({ providedIn: 'root' })
export class ChatMessageService {
  public resourceUrl = SERVER_API_URL + 'api/chat-messages';

  constructor(protected http: HttpClient) {}

  create(chatMessage: IChatMessage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(chatMessage);
    return this.http
      .post<IChatMessage>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(chatMessage: IChatMessage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(chatMessage);
    return this.http
      .put<IChatMessage>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<IChatMessage>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IChatMessage[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(chatMessage: IChatMessage): IChatMessage {
    const copy: IChatMessage = Object.assign({}, chatMessage, {
      messageTime: chatMessage.messageTime != null && chatMessage.messageTime.isValid() ? chatMessage.messageTime.format(DATE_FORMAT) : null
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.messageTime = res.body.messageTime != null ? moment(res.body.messageTime) : null;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((chatMessage: IChatMessage) => {
        chatMessage.messageTime = chatMessage.messageTime != null ? moment(chatMessage.messageTime) : null;
      });
    }
    return res;
  }
}
