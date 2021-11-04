import { NgModule } from '@angular/core';
import { SharedModule } from './../../shared/shared.module';
import { AutoinstallerComponent } from './autoinstaller/autoinstaller.component';
import { EvcsInstallerComponent } from './autoinstaller/evcs/evcs.component';
import { HeatingElementRtuInstallerComponent } from './autoinstaller/heatingelementrtu/heatingelementrtu.component';
import { HeatingElementTcpInstallerComponent } from './autoinstaller/heatingelementtcp/heatingelementtcp.component';
import { HeatingpumpTcpInstallerComponent } from './autoinstaller/heatingpumptcp/heatingpumptcp.component';
import { ReadWriteModbusTCPInstallerComponent } from './autoinstaller/readwritemodbustcp/readwritemodbustcp.component';
import { ChannelsComponent } from './channels/channels.component';
import { IndexComponent as ComponentInstallIndexComponent } from './component/install/index.component';
import { ComponentInstallComponent } from './component/install/install.component';
import { IndexComponent as ComponentUpdateIndexComponent } from './component/update/index.component';
import { ComponentUpdateComponent } from './component/update/update.component';
import { NetworkComponent } from './network/network.component';
import { AliasUpdateComponent } from './profile/aliasupdate.component';
import { ProfileComponent } from './profile/profile.component';
import { ServiceAssistantModule } from './serviceassistant/serviceassistant.module';
import { SettingsComponent } from './settings.component';
import { SystemExecuteComponent } from './systemexecute/systemexecute.component';
import { SystemUpdateComponent } from './systemupdate/systemupdate.component';
import { SystemUpdateOldComponent } from './systemupdate.old/systemupdate.old.component';

@NgModule({
  imports: [
    SharedModule,
    ServiceAssistantModule
  ],
  declarations: [
    AliasUpdateComponent,
    AutoinstallerComponent,
    ChannelsComponent,
    ComponentInstallComponent,
    ComponentInstallIndexComponent,
    ComponentUpdateComponent,
    ComponentUpdateIndexComponent,
    EvcsInstallerComponent,
    HeatingElementRtuInstallerComponent,
    HeatingElementTcpInstallerComponent,
    HeatingpumpTcpInstallerComponent,
    NetworkComponent,
    ProfileComponent,
    ReadWriteModbusTCPInstallerComponent,
    SettingsComponent,
    SystemExecuteComponent,
    SystemUpdateComponent,
    SystemUpdateOldComponent,
  ],
  entryComponents: [
    EvcsInstallerComponent,
    HeatingElementRtuInstallerComponent,
    HeatingElementTcpInstallerComponent,
    HeatingpumpTcpInstallerComponent,
    ReadWriteModbusTCPInstallerComponent,
  ]
})
export class SettingsModule { }
