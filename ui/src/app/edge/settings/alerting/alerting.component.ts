import { User } from 'src/app/shared/jsonrpc/shared';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { SetUserAlertingConfigsRequest, UserSettingRequest } from 'src/app/shared/jsonrpc/request/setUserAlertingConfigsRequest';
import { GetUserAlertingConfigsRequest } from 'src/app/shared/jsonrpc/request/getUserAlertingConfigsRequest';
import { GetUserAlertingConfigsResponse, AlertingSettingResponse } from 'src/app/shared/jsonrpc/response/getUserAlertingConfigsResponse';
import { Edge, Service, Utils, Websocket } from 'src/app/shared/shared';

export enum AlertingType {
  offline = 0,
  fault = 1,
  warning = 2
};

type DefaultValues = { [K in AlertingType]: Delay[]; };
type Delay = { value: number, label: string };
type AlertingSetting = AlertingSettingResponse;

@Component({
  selector: AlertingComponent.SELECTOR,
  templateUrl: './alerting.component.html',
})
export class AlertingComponent implements OnInit {
  protected AlertingType = AlertingType;

  protected static readonly SELECTOR = "alerting";
  public readonly spinnerId: string = AlertingComponent.SELECTOR;

  protected readonly defaultValues: DefaultValues;

  protected edge: Edge;
  protected user: User;
  protected error: Error;

  protected currentUserInformation: AlertingSetting;
  protected currentUserForm: FormGroup;

  protected otherUserInformation: AlertingSetting[];
  protected otherUserForm: FormGroup;

  public constructor(
    private route: ActivatedRoute,
    protected utils: Utils,
    private websocket: Websocket,
    private service: Service,
    private translate: TranslateService,
    public formBuilder: FormBuilder,
  ) {
    this.defaultValues = {
      [AlertingType.offline]: this.asDelayOptions([15, 60, 1440]),
      [AlertingType.fault]: this.asDelayOptions([15, 60, 1440]),
      [AlertingType.warning]: this.asDelayOptions([15, 60, 1440]),
    };
  }

  public ngOnInit(): void {
    this.service.setCurrentComponent({ languageKey: 'Edge.Config.Index.alerting' }, this.route).then(edge => {
      this.edge = edge;

      this.service.metadata.subscribe(metadata => {
        this.user = metadata.user;
      });

      let request = new GetUserAlertingConfigsRequest({ edgeId: this.edge.id });

      this.sendRequest(request).then(response => {
        const result = response.result;

        this.currentUserInformation = result.currentUserSettings;
        this.currentUserForm = this.formBuilder.group({
          offlineEdgeDelay: new FormControl(this.currentUserInformation.offlineEdgeDelay),
          faultEdgeDelay: new FormControl(this.currentUserInformation.faultEdgeDelay),
          warningEdgeDelay: new FormControl(this.currentUserInformation.warningEdgeDelay),
        });

        if (result.otherUsersSettings) {
          [this.otherUserInformation, this.otherUserForm] = this.generateSettings(result.otherUsersSettings);
        }
      }).catch(error => {
        this.error = error.error;
      });
    });
  }

  private generateSettings(response: AlertingSettingResponse[]): [AlertingSetting[], FormGroup] {
    var settings: AlertingSetting[] = [];
    var form: FormGroup = new FormGroup({});

    var sorted = this.sortedAlphabetically(response);

    sorted.forEach((r) => {
      var setting: AlertingSetting = {
        userLogin: r.userLogin,
        offlineEdgeDelay: r.offlineEdgeDelay,
        faultEdgeDelay: r.faultEdgeDelay,
        warningEdgeDelay: r.warningEdgeDelay,
      };

      settings.push(setting);

      form.addControl(setting.userLogin, //
        this.formBuilder.group({
          offlineEdgeDelay: new FormControl(setting.offlineEdgeDelay),
          faultEdgeDelay: new FormControl(setting.faultEdgeDelay),
          warningEdgeDelay: new FormControl(setting.warningEdgeDelay),
        }));
    });

    return [settings, form];
  }

  private sortedAlphabetically(userSettings: AlertingSettingResponse[]): AlertingSettingResponse[] {
    return userSettings.sort((userA, userB) => {
      return userA.userLogin.localeCompare(userB.userLogin, undefined, { sensitivity: 'accent' });
    });
  }

  private asDelayOptions(settings: number[]): Delay[] {
    return settings.map(v => this.asDelayOption(v));
  }

  private asDelayOption(setting: number): Delay {
    return { value: setting, label: this.getLabelToDelay(setting) };
  }

  /**
   * get if given delay is valid
   */
  protected isInvalidDelay(type: AlertingType, delay: number): boolean {
    if (delay <= 0) {
      return false;
    }
    return this.defaultValues[type].findIndex(e => e.value === delay) === -1;
  }

  /**
   * get the label matching the given delay, with translated timeunits and
   * attention to writing differences and singular and plural.
   *
   * @param delay to generate label for
   * @returns label as string
   */
  protected getLabelToDelay(delay: number): string {
    if (delay <= 0) {
      return this.translate.instant("Edge.Config.Alerting.deactivated");
    }
    if (delay >= 1440) {
      delay = delay / 1440;
      return delay + ' ' + (delay == 1
        ? this.translate.instant("Edge.Config.Alerting.interval.day")
        : this.translate.instant("Edge.Config.Alerting.interval.days"));
    } else if (delay >= 60) {
      delay = delay / 60;
      return delay + ' ' + (delay == 1
        ? this.translate.instant("Edge.Config.Alerting.interval.hour")
        : this.translate.instant("Edge.Config.Alerting.interval.hours"));
    } else {
      return delay + ' ' + (delay == 1
        ? this.translate.instant("Edge.Config.Alerting.interval.minute")
        : this.translate.instant("Edge.Config.Alerting.interval.minutes"));
    }
  }

  protected setUsersAlertingConfig() {
    let edgeId: string = this.edge.id;

    let dirtyformGroups: FormGroup<any>[] = [];
    let changedUserSettings: UserSettingRequest[] = [];

    if (this.currentUserForm.dirty) {
      var formGroup = this.currentUserForm;
      dirtyformGroups.push(formGroup);

      let offlineEdgeDelay = formGroup.controls['offlineEdgeDelay']?.value ?? 0;
      let faultEdgeDelay = formGroup.controls['faultEdgeDelay']?.value ?? 0;
      let warningEdgeDelay = formGroup.controls['warningEdgeDelay']?.value ?? 0;

      changedUserSettings.push({
        userLogin: this.currentUserInformation.userLogin,
        offlineEdgeDelay: offlineEdgeDelay,
        warningEdgeDelay: warningEdgeDelay,
        faultEdgeDelay: faultEdgeDelay,
      });
    }

    let userOptions: AlertingSetting[] = [];
    if (this.otherUserInformation) {
      if (this.otherUserForm.dirty) {
        dirtyformGroups.push(this.otherUserForm);

        for (let user of this.otherUserInformation) {
          let control = this.otherUserForm.controls[user.userLogin];
          if (control.dirty) {
            let offlineEdgeDelay = control.value['offlineEdgeDelay'];
            let faultEdgeDelay = control.value['faultEdgeDelay'];
            let warningEdgeDelay = control.value['warningEdgeDelay'];
            //let isActivated = control.value['isActivated'];
            changedUserSettings.push({
              userLogin: user.userLogin,
              offlineEdgeDelay: offlineEdgeDelay,
              warningEdgeDelay: warningEdgeDelay,
              faultEdgeDelay: faultEdgeDelay,
            });
            userOptions.push(user);
          }
        }
      }
    }

    console.log(changedUserSettings);

    let request = new SetUserAlertingConfigsRequest({ edgeId: edgeId, userSettings: changedUserSettings });
    this.sendRequestAndUpdate(request, dirtyformGroups);

    /* reset options for users with a non-default option.
    var defaultSettingsCount = this.Delays.size;
    userOptions.forEach(user => {
      if (user.options.size > defaultSettingsCount) {
        user.options = this.getDelayOptions(user.offlineEdgeDelay);
      }
    });*/
  }

  /**
   * send requests, show events using toasts and reset given formGroup if successful.
   * @param request   stucture containing neccesary parameters
   * @param formGroup   formGroup to update
   * @returns @GetUserAlertingConfigsResponse containing logged in users data, as well as data other users, if user is admin
   */
  private sendRequestAndUpdate(request: GetUserAlertingConfigsRequest | SetUserAlertingConfigsRequest, formGroup: FormGroup<any>[]) {
    this.sendRequest(request)
      .then(() => {
        this.service.toast(this.translate.instant('General.changeAccepted'), 'success');
        for (let group of formGroup.values()) {
          group.markAsPristine();
        }
      })
      .catch((response) => {
        let error = response.error;
        this.errorToast(this.translate.instant('General.changeFailed'), error.message);
      });
  }

  /**
   * send requests and show events using toasts.
   * @param request   stucture containing neccesary parameters
   * @returns @GetUserAlertingConfigsResponse containing logged in users data, as well as data other users, if user is admin
   */
  private sendRequest(request: GetUserAlertingConfigsRequest | SetUserAlertingConfigsRequest): Promise<GetUserAlertingConfigsResponse> {
    return new Promise((resolve, reject) => {
      this.service.startSpinner(this.spinnerId);
      this.websocket.sendRequest(request).then(response => {
        resolve(response as GetUserAlertingConfigsResponse);
      }).catch(reason => {
        let error = reason.error;
        console.error(error);
        this.errorToast(this.translate.instant('Edge.Config.Alerting.toast.error'), error.message);
        reject(reason);
      }).finally(() => {
        this.service.stopSpinner(this.spinnerId);
      });
    });
  }

  private errorToast(errorType: string, errorMsg: string) {
    this.service.toast('[ ' + errorType + ' ]<br/>' + errorMsg, 'danger');
  }

  /**
   * get if any userSettings has changed/is dirty.
   * @returns true if any settings are changed, else false
   */
  protected isDirty(): boolean {
    if (this.error || !this.currentUserForm) {
      return false;
    }
    return this.currentUserForm?.dirty || this.otherUserForm?.dirty;
  }
}
