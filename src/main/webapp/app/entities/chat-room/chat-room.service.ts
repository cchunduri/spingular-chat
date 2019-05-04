import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IChatRoom } from 'app/shared/model/chat-room.model';

type EntityResponseType = HttpResponse<IChatRoom>;
type EntityArrayResponseType = HttpResponse<IChatRoom[]>;

@Injectable({ providedIn: 'root' })
export class ChatRoomService {
  public resourceUrl = SERVER_API_URL + 'api/chat-rooms';

  constructor(protected http: HttpClient) {}

  create(chatRoom: IChatRoom): Observable<EntityResponseType> {
    return this.http.post<IChatRoom>(this.resourceUrl, chatRoom, { observe: 'response' });
  }

  update(chatRoom: IChatRoom): Observable<EntityResponseType> {
    return this.http.put<IChatRoom>(this.resourceUrl, chatRoom, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IChatRoom>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IChatRoom[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
