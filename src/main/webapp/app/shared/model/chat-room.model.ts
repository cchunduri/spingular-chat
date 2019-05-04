export interface IChatRoom {
  id?: string;
  name?: string;
  desc?: string;
}

export class ChatRoom implements IChatRoom {
  constructor(public id?: string, public name?: string, public desc?: string) {}
}
