import { FormGroup } from '@angular/forms';
import { FormlyFieldConfig } from '@ngx-formly/core';
import { TranslateService } from '@ngx-translate/core';
import { SetupProtocol } from 'src/app/shared/jsonrpc/request/submitSetupProtocolRequest';
import { Edge, EdgeConfig, Service, Websocket } from 'src/app/shared/shared';
import { Category } from '../shared/category';
import { Country } from '../../../shared/type/country';
import { FeedInType } from '../shared/enums';
import { ComponentData, SerialNumberFormData } from '../shared/ibndatatypes';
import { ComponentConfigurator } from '../views/configuration-execute/component-configurator';
import { EmsApp } from '../views/heckert-app-installer/heckert-app-installer.component';
import { AcPv } from '../views/protocol-additional-ac-producers/protocol-additional-ac-producers.component';

export enum View {
  Completion,
  ConfigurationEmergencyReserve,
  ConfigurationExecute,
  ConfigurationLineSideMeterFuse,
  ConfigurationSummary,
  ConfigurationSystem,
  PreInstallation,
  PreInstallationUpdate,
  ProtocolAdditionalAcProducers,
  ProtocolCustomer,
  ProtocolFeedInLimitation,
  ProtocolInstaller,
  ProtocolPv,
  ProtocolSerialNumbers,
  ProtocolSystem,
  HeckertAppInstaller,
  ConfigurationFeaturesStorageSystem,
  ConfigurationCommercialComponent,
  ConfigurationPeakShaving,
}

export type SerialNumberData = {
  formGroup: FormGroup;
  fieldSettings: FormlyFieldConfig[];
  model: any;
  header: string;
};

export type SchedulerId = {
  componentId: string,
  behaviour: SchedulerIdBehaviour
}

export enum SchedulerIdBehaviour {
  MANAGED_BY_APP_MANAGER,
  ALWAYS_INCLUDE
}

export abstract class AbstractIbn {
  // Battery type
  public readonly type: string;

  // Id
  public readonly id: string;

  // protocol-installer
  public installer?: {
    companyName: string;
    lastName: string;
    firstName: string;
    street: string;
    zip: string;
    city: string;
    country: Country;
    email: string;
    phone: string;
  };

  // protocol-customer
  public customer?: {
    isCorporateClient: boolean;
    companyName: string;
    lastName: string;
    firstName: string;
    street: string;
    zip: string;
    city: string;
    country: Country;
    email: string;
    emailConfirm: string;
    phone: string;
  };

  // protocol-system
  public location?: {
    isEqualToCustomerData: boolean;
    isCorporateClient: boolean;
    companyName: string;
    lastName: string;
    firstName: string;
    street: string;
    zip: string;
    city: string;
    country: Country;
    email?: string;
    phone?: string;
  };

  // configuration-line-side-meter-fuse
  public lineSideMeterFuse?: {
    category: Category;
    fixedValue?: number;
    otherValue?: number;
  };

  // protocol-dynamic-feed-in-limitation
  public feedInLimitation?: {
    feedInType: FeedInType,
    maximumFeedInPower?: number;
  };

  // protocol-pv
  public pv?: {
    ac?: AcPv[];
  };

  // configuration-emergency-reserve
  public emergencyReserve?: {
    isEnabled: boolean;
    isReserveSocEnabled: boolean;
    minValue: number;
    value: number;
  };

  // Protocol Serial Numbers.
  public serialNumbers: {
    modules: ComponentData[];
  };

  //Controller-Id's
  public requiredControllerIds: SchedulerId[];

  // Heckert-app-installer
  public selectedFreeApp?: EmsApp;

  // Configuration-summary
  public setupProtocol?: SetupProtocol;

  // Protocol-serial-numbers
  public setupProtocolId?: string;

  // Rundsteuerempfaenger manual in feed in limitation
  public readonly showRundSteuerManual: boolean;

  // Show view count along with Schritt number on top of page.
  public showViewCount: boolean;

  // Contains default number of battery modules per tower based on system. 
  public readonly defaultNumberOfModules: number;

  constructor(public views: View[], public translate: TranslateService) { }

  /**
   * Retrieves the fields for View Line side meter Fuse,
   * which are different for Home and Commercial systems.
   */
  public abstract getLineSideMeterFuseFields(): FormlyFieldConfig[];

  /**
   * Returns the number of towers and modules per tower.
   *
   * @param edge the current edge.
   * @param websocket the Websocket connection.
   */
  public abstract getSettings(edge: Edge, websocket: Websocket): Promise<{ numberOfTowers: number; numberOfModulesPerTower: number }>;

  /**
   * Returns the component fields for serial numbers based on system being installed.
   *
   * @param towerNr number of towers.
   * @param numberOfModulesPerTower number of modules per tower.
   */
  public abstract getFields(towerNr: number, numberOfModulesPerTower: number): FormlyFieldConfig[];

  /**
   * Returns the fields to enter number of towers and modules, manually.
   *
   * @param numberOfModulesPerTower number of modules per tower.
   * @param numberOfTowers number of towers.
   */
  public abstract getSettingsFields(numberOfModulesPerTower: number, numberOfTowers: number): FormlyFieldConfig[];

  /**
   * Fills the entire fields.
   *
   * @param numberOfTowers number of towers.
   * @param numberOfModulesPerTower number of modules per tower.
   * @param models form specific data.
   * @param forms Array of form data to display.
   */
  public abstract fillForms(
    numberOfTowers: number,
    numberOfModulesPerTower: number,
    models: any,
    forms: SerialNumberFormData[]): SerialNumberFormData[];

  /**
   * Retrieves the Serial numbers of the battery modules and components.
   *
   * @param towerNr number of towers.
   * @param edge the current edge.
   * @param websocket the Websocket connection.
   * @param numberOfModulesPerTower number of modules per tower.
   */
  public abstract getSerialNumbers(towerNr: number, edge: Edge, websocket: Websocket, numberOfModulesPerTower?: number): Promise<Object>;

  /**
   * View Configuration Dynamic Feed-In limitation.
   * Returns the fields for the views based on the system.
   */
  public abstract getFeedInLimitFields(): FormlyFieldConfig[];

  /**
   * Returns the updated ibn after filling Dynamic-Feed-In-Limit fields from the model.
   * 
   * @param model the model containing the user input for the Dynamic-Feed-In-Limit fields.
   */
  public abstract setFeedInLimitsFields(model: any);

  /**
   * View Configuration-execute.
   * Returns the required configuration object with components specific to the system.
   *
   * @param edge the current edge.
   * @param config the EdgeConfig.
   * @param websocket the Websocket connection.
   * @param service the Service.
   */
  public abstract getComponentConfigurator(
    edge: Edge,
    config: EdgeConfig,
    websocket: Websocket,
    service?: Service
  ): ComponentConfigurator;

  /**
   * View Protocol-serial-numbers.
   * Returns the Protocol information of the system.
   *
   * @param edge the current edge.
   * @param websocket the Websocket connection.
   */
  public abstract getProtocol(edge: Edge, websocket: Websocket): Promise<string>;

  /**
   * Returns the set of controller for updateScheduler in component configurator.
   */
  public abstract setRequiredControllers();

  /**
   * View Emergency-reserve
   * Adds and returns the specific battery information based on Ibn to view in summary.
   *
   * @param batteryData the battery data.
   */
  public addCustomBatteryData(batteryData: ComponentData[]): ComponentData[] {
    return batteryData;
  };

  /**
   * View Dynamic limitation
   * Adds and returns the specific Battery-Inverter information based on Ibn to view in summary.
   *
   * @param batteryInverterData the battery inverter data.
   */
  public addCustomBatteryInverterData(batteryInverterData: ComponentData[]): ComponentData[] {
    return batteryInverterData;
  };

  /**
   * View Protocol pv Data
   * Adds and returns the PV information based on Ibn to view in summary.
   *
   * @param pvData the photovoltaic data.
   */
  public addCustomPvData(pvData: ComponentData[]): ComponentData[] {
    return pvData;
  };

  /**
   * View configuration peak shaving.
   * Adds the peak shaving data specific to commercial-50 systems.
   * 
   * @param peakShavingData the peak shaving data.
   */
  public addPeakShavingData(peakShavingData: ComponentData[]): ComponentData[] {
    return peakShavingData;
  }

  /**
   * Specific to commercial-50 and sets the Beladung Unter and Entladung Über information based on user's requirement.
   * 
   * @param model model information from the peak shaving view.
   */
  public setCommercialfeature(model: any) { }

  /**
   * Specific to commercial-50. Retrieves title for peakshaving view based on feature selected. 
   */
  public getPeakShavingHeader(): Category {
    return null;
  };

}
