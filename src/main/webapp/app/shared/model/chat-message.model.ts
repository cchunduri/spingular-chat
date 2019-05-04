import { Moment } from 'moment';

export interface IChatMessage {
  id?: string;
  sender?: number;
  reciver?: number;
  messageText?: string;
  messageTime?: Moment;
}

export class ChatMessage implements IChatMessage {
  constructor(
    public id?: string,
    public sender?: number,
    public reciver?: number,
    public messageText?: string,
    public messageTime?: Moment
  ) {}
}
