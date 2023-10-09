import { TranslateService } from "@ngx-translate/core";
import { Category } from "../../shared/category";
import { FeedInSetting, FeedInType, View } from "../../shared/enums";
import { DcPv } from "../../shared/ibndatatypes";
import { Meter } from "../../shared/meter";
import { SystemId, SystemType } from "../../shared/system";
import { SafetyCountry } from "../../views/configuration-execute/safety-country";
import { AbstractHomeIbn } from "./abstract-home";

type Home20App = {
    SAFETY_COUNTRY: SafetyCountry,
    FEED_IN_TYPE: FeedInType,
    FEED_IN_SETTING: string,
    MAX_FEED_IN_POWER?: number,
    HAS_AC_METER: boolean,
    AC_METER_TYPE?: string,
    HAS_PV_1: boolean,
    ALIAS_PV_1?: string,
    HAS_PV_2: boolean,
    ALIAS_PV_2?: string,
    HAS_PV_3: boolean,
    ALIAS_PV_3?: string,
    HAS_PV_4: boolean,
    ALIAS_PV_4?: string,
    HAS_EMERGENCY_RESERVE: boolean,
    EMERGENCY_RESERVE_ENABLED?: boolean,
    EMERGENCY_RESERVE_SOC?: number,
    SHADOW_MANAGEMENT_DISABLED?: boolean
}

export class Home20FeneconIbn extends AbstractHomeIbn {

    public override readonly type: SystemType = SystemType.FENECON_HOME_20;
    public override readonly id: SystemId = SystemId.FENECON_HOME_20;
    public override readonly emsBoxLabel = Category.EMS_BOX_LABEL_HOME;
    public override readonly maxNumberOfPvStrings: number = 4;
    public override readonly maxFeedInLimit: number = 20000;
    public override readonly homeAppId: string = 'App.FENECON.Home.20';
    public override readonly homeAppAlias: string = 'FENECON Home 20';

    public override mppt: {
        connectionCheck: boolean,
        mppt1pv1: boolean,
        mppt1pv2: boolean,
        mppt2pv3: boolean,
        mppt2pv4: boolean
    } = {
            connectionCheck: false,
            mppt1pv1: false,
            mppt1pv2: false,
            mppt2pv3: false,
            mppt2pv4: false
        };

    constructor(public override translate: TranslateService) {
        super([
            View.PreInstallation,
            View.PreInstallationUpdate,
            View.ConfigurationSystem,
            View.ProtocolInstaller,
            View.ProtocolCustomer,
            View.ProtocolSystem,
            View.ConfigurationEmergencyReserve,
            View.ConfigurationLineSideMeterFuse,
            View.ConfigurationMpptSelection,
            View.ProtocolPv,
            View.ProtocolAdditionalAcProducers,
            View.ProtocolFeedInLimitation,
            View.ConfigurationSummary,
            View.ConfigurationExecute,
            View.ProtocolSerialNumbers,
            View.Completion
        ], translate);
    }

    public override getHomeAppProperties(safetyCountry: SafetyCountry, feedInSetting: FeedInSetting): Home20App {

        // meter1
        const acArray = this.pv.ac;
        const isAcCreated: boolean = acArray.length >= 1;
        const acMeterType: Meter = isAcCreated ? acArray[0].meterType : Meter.SOCOMEC;

        const dc1: DcPv = this.pv.dc[0];
        const dc2: DcPv = this.pv.dc[1];
        const dc3: DcPv = this.pv.dc[2];
        const dc4: DcPv = this.pv.dc[3];

        const Home20AppProperties: Home20App = {
            SAFETY_COUNTRY: safetyCountry,
            FEED_IN_TYPE: this.feedInLimitation.feedInType,
            ...(this.feedInLimitation.feedInType === FeedInType.DYNAMIC_LIMITATION && { MAX_FEED_IN_POWER: this.feedInLimitation.maximumFeedInPower }),
            FEED_IN_SETTING: feedInSetting,
            HAS_AC_METER: isAcCreated,
            ...(isAcCreated && { AC_METER_TYPE: Meter.toAppAcMeterType(acMeterType) }),
            HAS_PV_1: dc1.isSelected,
            ...(dc1.isSelected && { ALIAS_PV_1: dc1.alias }),
            HAS_PV_2: dc2.isSelected,
            ...(dc2.isSelected && { ALIAS_PV_2: dc2.alias }),
            HAS_PV_3: dc3.isSelected,
            ...(dc3.isSelected && { ALIAS_PV_3: dc3.alias }),
            HAS_PV_4: dc4.isSelected,
            ...(dc4.isSelected && { ALIAS_PV_4: dc4.alias }),
            HAS_EMERGENCY_RESERVE: this.emergencyReserve.isEnabled,
            ...(this.emergencyReserve.isEnabled && { EMERGENCY_RESERVE_ENABLED: this.emergencyReserve.isReserveSocEnabled }),
            ...(this.emergencyReserve.isReserveSocEnabled && { EMERGENCY_RESERVE_SOC: this.emergencyReserve.value }),
            ...(this.batteryInverter?.shadowManagementDisabled && { SHADOW_MANAGEMENT_DISABLED: true })
        };

        return Home20AppProperties;
    }
}