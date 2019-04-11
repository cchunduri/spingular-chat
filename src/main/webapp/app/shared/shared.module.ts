import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { SpingularChatSharedLibsModule, SpingularChatSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective } from './';

@NgModule({
  imports: [SpingularChatSharedLibsModule, SpingularChatSharedCommonModule],
  declarations: [JhiLoginModalComponent, HasAnyAuthorityDirective],
  entryComponents: [JhiLoginModalComponent],
  exports: [SpingularChatSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SpingularChatSharedModule {
  static forRoot() {
    return {
      ngModule: SpingularChatSharedModule
    };
  }
}
