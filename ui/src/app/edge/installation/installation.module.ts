import { NgModule } from "@angular/core";
import { FormControl, ValidationErrors } from "@angular/forms";
import { FormlyModule } from "@ngx-formly/core";
import { SharedModule } from "src/app/shared/shared.module";
import { SettingsModule } from "../settings/settings.module";
import { InstallationViewComponent } from "./installation-view/installation-view.component";
import { InstallationComponent } from "./installation.component";
import { KeyMaskDirective } from "./keymask";
import { CompletionComponent } from "./views/completion/completion.component";
import { ConfigurationCommercialComponent } from "./views/configuration-commercial-component/configuration-commercial.component";
import { ConfigurationEmergencyReserveComponent } from "./views/configuration-emergency-reserve/configuration-emergency-reserve.component";
import { ConfigurationExecuteComponent } from "./views/configuration-execute/configuration-execute.component";
import { ConfigurationFeaturesStorageSystemComponent } from "./views/configuration-features-storage-system/configuration-features-storage-system.component";
import { ConfigurationLineSideMeterFuseComponent } from "./views/configuration-line-side-meter-fuse/configuration-line-side-meter-fuse.component";
import { ConfigurationPeakShavingComponent } from "./views/configuration-peak-shaving/configuration-peak-shaving.component";
import { ConfigurationSummaryComponent } from "./views/configuration-summary/configuration-summary.component";
import { ConfigurationSystemComponent } from "./views/configuration-system/configuration-system.component";
import { HeckertAppInstallerComponent } from "./views/heckert-app-installer/heckert-app-installer.component";
import { PreInstallationUpdateComponent } from "./views/pre-installation-update/pre-installation-update.component";
import { PreInstallationComponent } from "./views/pre-installation/pre-installation.component";
import { ProtocolAdditionalAcProducersComponent } from "./views/protocol-additional-ac-producers/protocol-additional-ac-producers.component";
import { ProtocolCustomerComponent } from "./views/protocol-customer/protocol-customer.component";
import { ProtocolFeedInLimitationComponent } from "./views/protocol-feed-in-limitation/protocol-feed-in-limitation.component";
import { ProtocolInstallerComponent } from "./views/protocol-installer/protocol-installer.component";
import { ProtocolPvComponent } from "./views/protocol-pv/protocol-pv.component";
import { ProtocolSerialNumbersComponent } from "./views/protocol-serial-numbers/protocol-serial-numbers.component";
import { ProtocolSystemComponent } from "./views/protocol-system/protocol-system.component";

//#region Validators
export function EmailMatchValidator(control: FormControl): ValidationErrors {

  const { email, emailConfirm } = control.value;
  if (email === emailConfirm) {
    return null;
  }

  return { "emailMatch": true };
}

export function OnlyPositiveIntegerValidator(control: FormControl): ValidationErrors {
  return /^[0-9]+$/.test(control.value) ? null : { "onlyPositiveInteger": true }
}

export function BatteryInverterSerialNumberValidator(control: FormControl): ValidationErrors {
  // This validator checks the length of the value
  return /^.{16}$/.test(control.value) ? null : { "batteryInverterSerialNumber": true };
}

export function EmsBoxSerialNumberValidator(control: FormControl): ValidationErrors {
  // This validator only checks the value after the prefix
  return /^[FS]\d{9}$/.test(control.value) ? null : { "emsBoxSerialNumber": true };
}

export function EmsBoxNetztrennstelleSerialNumberValidator(control: FormControl): ValidationErrors {
  return /^\d{4}$/.test(control.value) ? null : { "emsBoxNetztrennstelleSerialNumber": true };
}

export function BoxSerialNumberValidator(control: FormControl): ValidationErrors {
  // This validator only checks the value after the prefix
  return /^\d{9}$/.test(control.value) ? null : { "boxSerialNumber": true };
}

export function BmsBoxSerialNumberValidator(control: FormControl): ValidationErrors {
  return /^\d{24}$/.test(control.value) ? null : { "batterySerialNumber": true };
}

export function BatterySerialNumberValidator(control: FormControl): ValidationErrors {
  // This validator only checks the value after the prefix
  return /^\d{12}$/.test(control.value) ? null : { "batterySerialNumber": true };
}

export function CommercialBmsBoxSerialNumberValidator(control: FormControl): ValidationErrors {
  return /^\d{10}$/.test(control.value) ? null : { "commercialBmsBoxSerialNumber": true };
}

export function CommercialBatteryModuleSerialNumberValidator(control: FormControl): ValidationErrors {
  // This validator only checks the value after the prefix
  return /^\d{10}$/.test(control.value) ? null : { "commercialBatteryModuleSerialNumber": true };
}

export function Commercial30BatteryInverterSerialNumberValidator(control: FormControl): ValidationErrors {
  // This validator checks the length of the value
  return /^.{10}$/.test(control.value) ? null : { "commercialBatteryInverterSerialNumber": true };
}

export function Commercial50BatteryInverterSerialNumberValidator(control: FormControl): ValidationErrors {
  // This validator checks the length of the value
  return /^.{6}$/.test(control.value) ? null : { "commercialBatteryInverterSerialNumber": true };
}

@NgModule({
  imports: [
    FormlyModule.forRoot({
      validators: [
        { name: "emailMatch", validation: EmailMatchValidator },
        { name: "batteryInverterSerialNumber", validation: BatteryInverterSerialNumberValidator },
        { name: "emsBoxSerialNumber", validation: EmsBoxSerialNumberValidator },
        { name: "emsBoxNetztrennstelleSerialNumber", validation: EmsBoxNetztrennstelleSerialNumberValidator },
        { name: "boxSerialNumber", validation: BoxSerialNumberValidator },
        { name: "bmsBoxSerialNumber", validation: BmsBoxSerialNumberValidator },
        { name: "batterySerialNumber", validation: BatterySerialNumberValidator },
        { name: "onlyPositiveInteger", validation: OnlyPositiveIntegerValidator },
        { name: "commercialBmsBoxSerialNumber", validation: CommercialBmsBoxSerialNumberValidator },
        { name: "commercialBatteryModuleSerialNumber", validation: CommercialBatteryModuleSerialNumberValidator },
        { name: "commercial30BatteryInverterSerialNumber", validation: Commercial30BatteryInverterSerialNumberValidator },
        { name: "commercial50BatteryInverterSerialNumber", validation: Commercial50BatteryInverterSerialNumberValidator },
      ],
    }),
    SharedModule,
    SettingsModule
  ],
  declarations: [
    CompletionComponent,
    ConfigurationEmergencyReserveComponent,
    ConfigurationExecuteComponent,
    ConfigurationLineSideMeterFuseComponent,
    KeyMaskDirective,
    ProtocolCustomerComponent,
    ProtocolFeedInLimitationComponent,
    ProtocolInstallerComponent,
    ProtocolSystemComponent,
    InstallationComponent,
    InstallationViewComponent,
    PreInstallationComponent,
    PreInstallationUpdateComponent,
    ConfigurationSystemComponent,
    ProtocolPvComponent,
    ProtocolAdditionalAcProducersComponent,
    ConfigurationSummaryComponent,
    ProtocolSerialNumbersComponent,
    HeckertAppInstallerComponent,
    ConfigurationCommercialComponent,
    ConfigurationFeaturesStorageSystemComponent,
    ConfigurationPeakShavingComponent,
  ]
})
export class InstallationModule { }

// TODO rename to Setup or SetupAssistant to be in line with SetupProtocol on Backend side