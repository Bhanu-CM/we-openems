import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ModalController } from '@ionic/angular';
import { FormlyFieldConfig } from '@ngx-formly/core';
import { TranslateService } from '@ngx-translate/core';
import { ComponentJsonApiRequest } from 'src/app/shared/jsonrpc/request/componentJsonApiRequest';
import { Edge, Service, Utils, Websocket } from '../../../shared/shared';
import { AddAppInstance } from './jsonrpc/addAppInstance';
import { GetAppAssistant } from './jsonrpc/getAppAssistant';
import { KeyModalComponent, KeyValidationBehaviour } from './keypopup/modal.component';
import { hasPredefinedKey } from './permissions';

@Component({
  selector: InstallAppComponent.SELECTOR,
  templateUrl: './install.component.html'
})
export class InstallAppComponent implements OnInit {

  private static readonly SELECTOR = 'app-install';
  public readonly spinnerId: string = InstallAppComponent.SELECTOR;

  protected form: FormGroup | null = null;
  protected fields: FormlyFieldConfig[] = null;
  protected model: any | null = null;

  private key: string | null = null;
  private appId: string | null = null;
  protected appName: string | null = null;
  private edge: Edge | null = null;
  protected isInstalling: boolean = false;

  private hasPredefinedKey: boolean = false;

  public constructor(
    private route: ActivatedRoute,
    protected utils: Utils,
    private websocket: Websocket,
    private service: Service,
    private modalController: ModalController,
    private router: Router,
    private translate: TranslateService,
  ) {
  }

  public ngOnInit() {
    this.service.startSpinner(this.spinnerId);
    const state = history?.state;
    if (state && 'appKey' in state) {
      this.key = state['appKey'];
    }
    let appId = this.route.snapshot.params['appId'];
    let appName = this.route.snapshot.queryParams['name'];
    this.appId = appId;
    this.service.setCurrentComponent(appName, this.route).then(edge => {
      this.edge = edge
      this.hasPredefinedKey = hasPredefinedKey(edge);
      edge.sendRequest(this.websocket,
        new ComponentJsonApiRequest({
          componentId: '_appManager',
          payload: new GetAppAssistant.Request({ appId: appId })
        })).then(response => {
          let appAssistant = GetAppAssistant.postprocess((response as GetAppAssistant.Response).result);
          // insert alias field into appAssistent fields
          let aliasField = { key: 'ALIAS', type: 'input', templateOptions: { label: 'Alias' }, defaultValue: appAssistant.alias };
          appAssistant.fields.splice(0, 0, aliasField);

          this.fields = appAssistant.fields;
          this.appName = appAssistant.name;
          this.model = {};
          this.form = new FormGroup({});

        }).catch(reason => {
          console.error(reason.error);
          this.service.toast("Error while receiving App Assistant for [" + appId + "]: " + reason.error.message, 'danger');
        }).finally(() => {
          this.service.stopSpinner(this.spinnerId);
        });
    });
  }

  /**
   * Submit for installing a app.
   */
  protected submit() {
    this.obtainKey().then(key => {
      this.service.startSpinnerTransparentBackground(this.appId);
      // remove alias field from properties
      let alias = this.form.value['ALIAS'];
      const clonedFields = {};
      for (let item in this.form.value) {
        if (item !== 'ALIAS') {
          clonedFields[item] = this.form.value[item];
        }
      }
      this.isInstalling = true
      this.edge.sendRequest(this.websocket,
        new ComponentJsonApiRequest({
          componentId: '_appManager',
          payload: new AddAppInstance.Request({
            appId: this.appId,
            alias: alias,
            properties: clonedFields,
            ...(key && { key: key })
          })
        })).then(response => {
          let result = (response as AddAppInstance.Response).result;

          if (result.instance) {
            result.instanceId = result.instance.instanceId;
            this.model = result.instance.properties;
          }
          if (result.warnings && result.warnings.length > 0) {
            this.service.toast(result.warnings.join(';'), 'warning');
          } else {
            this.service.toast(this.translate.instant('Edge.Config.App.successInstall'), 'success')
          }

          this.form.markAsPristine();
          this.router.navigate(['device/' + (this.edge.id) + '/settings/app/']);
        }).catch(reason => {
          this.service.toast(this.translate.instant('Edge.Config.App.failInstall', { error: reason.error.message }), 'danger')
        }).finally(() => {
          this.isInstalling = false
          this.service.stopSpinner(this.appId);
        });
    }).catch(() => {
      // can not get key => dont install
    })
  }

  /**
   * Gets the key to install the current app with.
   * 
   * @returns the key or null if the predefined key gets used
   */
  private obtainKey(): Promise<string | null> {
    return new Promise<string | null>((resolve, reject) => {
      if (this.key) {
        resolve(this.key);
        return;
      }
      if (this.hasPredefinedKey) {
        resolve(null);
        return;
      }
      this.presentModal()
        .then(resolve)
        .catch(reject)
    });
  }

  // popup for key
  private async presentModal(): Promise<string> {
    const modal = await this.modalController.create({
      component: KeyModalComponent,
      componentProps: {
        edge: this.edge,
        appId: this.appId,
        behaviour: KeyValidationBehaviour.SELECT,
        appName: this.appName
      },
      cssClass: 'auto-height'
    });

    const selectKeyPromise = new Promise<string>((resolve, reject) => {
      modal.onDidDismiss().then(event => {
        if (!event.data) {
          reject();
          return; // no key selected
        }
        resolve(event.data.key["keyId"]);
      })
    });

    await modal.present();
    return selectKeyPromise;
  }


}
