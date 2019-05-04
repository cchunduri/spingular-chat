/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { SpingularChatTestModule } from '../../../test.module';
import { ChatMessageComponent } from 'app/entities/chat-message/chat-message.component';
import { ChatMessageService } from 'app/entities/chat-message/chat-message.service';
import { ChatMessage } from 'app/shared/model/chat-message.model';

describe('Component Tests', () => {
  describe('ChatMessage Management Component', () => {
    let comp: ChatMessageComponent;
    let fixture: ComponentFixture<ChatMessageComponent>;
    let service: ChatMessageService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [SpingularChatTestModule],
        declarations: [ChatMessageComponent],
        providers: []
      })
        .overrideTemplate(ChatMessageComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ChatMessageComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ChatMessageService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new ChatMessage('123')],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.chatMessages[0]).toEqual(jasmine.objectContaining({ id: '123' }));
    });
  });
});
