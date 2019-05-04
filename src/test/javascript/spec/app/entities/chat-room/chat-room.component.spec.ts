/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { SpingularChatTestModule } from '../../../test.module';
import { ChatRoomComponent } from 'app/entities/chat-room/chat-room.component';
import { ChatRoomService } from 'app/entities/chat-room/chat-room.service';
import { ChatRoom } from 'app/shared/model/chat-room.model';

describe('Component Tests', () => {
  describe('ChatRoom Management Component', () => {
    let comp: ChatRoomComponent;
    let fixture: ComponentFixture<ChatRoomComponent>;
    let service: ChatRoomService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [SpingularChatTestModule],
        declarations: [ChatRoomComponent],
        providers: []
      })
        .overrideTemplate(ChatRoomComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ChatRoomComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ChatRoomService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new ChatRoom('123')],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.chatRooms[0]).toEqual(jasmine.objectContaining({ id: '123' }));
    });
  });
});
