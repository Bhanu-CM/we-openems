import { NgModule } from '@angular/core';
import { SharedModule } from 'src/app/shared/shared.module';
import { InstallAppComponent } from './install.component';
import { IndexComponent } from './index.component';
import { SingleAppComponent } from './single.component';
import { UpdateAppComponent } from './update.component';
import { KeyModalComponent } from './keypopup/modal.component';
import { FormControl, ValidationErrors } from '@angular/forms';
import { FormlyModule, FORMLY_CONFIG } from '@ngx-formly/core';
import { TranslateExtension } from 'src/app/shared/translate.extension';
import { TranslateService } from '@ngx-translate/core';
import { FormlySafeInputModalComponent } from './formly/safe-input/formly-safe-input-modal.component';
import { FormlySafeInputWrapperComponent } from './formly/safe-input/formly-safe-input.extended';
import { FormlyTextComponent } from './formly/formly-text';
import { FormlyInputWithUnitComponent } from './formly/input-with-unit';

export function KeyValidator(control: FormControl): ValidationErrors {
  return /^(.{4}-){3}.{4}$/.test(control.value) ? null : { 'key': true };
}

export function registerTranslateExtension(translate: TranslateService) {
  return {
    validationMessages: [
      {
        name: 'key',
        message() {
          return translate.stream('Edge.Config.App.Key.invalidPattern');
        },
      },
    ]
  };
}

@NgModule({
  imports: [
    SharedModule,
    FormlyModule.forRoot({
      wrappers: [
        { name: "formly-safe-input-wrapper", component: FormlySafeInputWrapperComponent },
        { name: "input-with-unit", component: FormlyInputWithUnitComponent },
      ],
      types: [
        { name: "text", component: FormlyTextComponent },
      ],
      validators: [
        { name: 'key', validation: KeyValidator }
      ],
      validationMessages: [
        { name: 'key', message: "The key doesnt match the pattern!" }
      ]
    })
  ],
  declarations: [
    IndexComponent,
    InstallAppComponent,
    SingleAppComponent,
    UpdateAppComponent,
    KeyModalComponent,
    FormlySafeInputModalComponent,
    FormlySafeInputWrapperComponent,
    FormlyTextComponent,
    FormlyInputWithUnitComponent,
  ],
  exports: [
    IndexComponent,
    InstallAppComponent,
    SingleAppComponent,
    UpdateAppComponent,
  ],
  providers: [
    // Use factory for formly. This allows us to use translations in validationMessages.
    { provide: FORMLY_CONFIG, multi: true, useFactory: registerTranslateExtension, deps: [TranslateService] },
  ],
})
export class AppModule { }
