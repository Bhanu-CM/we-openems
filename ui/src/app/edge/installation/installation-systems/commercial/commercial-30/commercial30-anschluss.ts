import { FormlyFieldConfig } from '@ngx-formly/core';
import { TranslateService } from '@ngx-translate/core';
import { Edge, EdgeConfig, Websocket } from 'src/app/shared/shared';
import { FeedInType } from '../../../shared/enums';
import { ComponentConfigurator, ConfigurationMode } from '../../../views/configuration-execute/component-configurator';
import { SchedulerIdBehaviour, View } from '../../abstract-ibn';
import { AbstractCommercial30Ibn } from './abstract-commercial-30';

export class Commercial30AnschlussIbn extends AbstractCommercial30Ibn {

    public readonly type: string = 'Fenecon-Commercial-30';

    public readonly id: string = 'commercial-30-anschluss';

    constructor(translate: TranslateService) {
        super([
            View.PreInstallation,
            View.PreInstallationUpdate,
            View.ConfigurationSystem,
            View.ConfigurationCommercialComponent,
            View.ProtocolInstaller,
            View.ProtocolCustomer,
            View.ProtocolSystem,
            View.ConfigurationLineSideMeterFuse,
            View.ProtocolAdditionalAcProducers,
            View.ProtocolFeedInLimitation,
            View.ConfigurationSummary,
            View.ConfigurationExecute,
            View.ProtocolSerialNumbers,
            View.Completion
        ], translate);
    }

    public setRequiredControllers() {
        this.requiredControllerIds = [];
        if (this.feedInLimitation.feedInType === FeedInType.DYNAMIC_LIMITATION) {
            this.requiredControllerIds.push({
                componentId: "ctrlGridOptimizedCharge0"
                , behaviour: SchedulerIdBehaviour.ALWAYS_INCLUDE
            })
        }
        this.requiredControllerIds.push({
            componentId: "ctrlBalancing0"
            , behaviour: SchedulerIdBehaviour.ALWAYS_INCLUDE
        });
    }

    public getComponentConfigurator(edge: Edge, config: EdgeConfig, websocket: Websocket) {

        const componentConfigurator: ComponentConfigurator = new ComponentConfigurator(edge, config, websocket);

        // modbus0
        componentConfigurator.add({
            factoryId: 'Bridge.Modbus.Serial',
            componentId: 'modbus0',
            alias: this.translate.instant('INSTALLATION.CONFIGURATION_EXECUTE.COMMUNICATION_WITH_BATTERY'),
            properties: [
                { name: 'enabled', value: true },
                { name: 'portName', value: '/dev/ttyAMA0' },
                { name: 'baudRate', value: 9600 },
                { name: 'databits', value: 8 },
                { name: 'stopbits', value: 'ONE' },
                { name: 'parity', value: 'NONE' },
                { name: 'logVerbosity', value: 'NONE' },
                { name: 'invalidateElementsAfterReadErrors', value: 1 }
            ],
            mode: ConfigurationMode.RemoveAndConfigure
        });

        // modbus1
        componentConfigurator.add({
            factoryId: 'Bridge.Modbus.Tcp',
            componentId: 'modbus1',
            alias: this.translate.instant('INSTALLATION.CONFIGURATION_EXECUTE.COMMUNICATION_WITH_BATTERY_INVERTER'),
            properties: [
                { name: 'enabled', value: true },
                { name: 'ip', value: '192.168.1.11' },
                { name: 'port', value: '502' },
                { name: 'logVerbosity', value: 'NONE' },
                { name: 'invalidateElementsAfterReadErrors', value: 1 }
            ],
            mode: ConfigurationMode.RemoveAndConfigure
        });

        //modbus2
        componentConfigurator.add({
            factoryId: 'Bridge.Modbus.Serial',
            componentId: 'modbus2',
            alias: this.translate.instant('INSTALLATION.CONFIGURATION_EXECUTE.COMMUNICATION_WITH_METER'),
            properties: [
                { name: 'enabled', value: true },
                { name: 'portName', value: '/dev/ttySC0' },
                { name: 'baudRate', value: 9600 },
                { name: 'databits', value: 8 },
                { name: 'stopbits', value: 'ONE' },
                { name: 'parity', value: 'NONE' },
                { name: 'logVerbosity', value: 'NONE' },
                { name: 'invalidateElementsAfterReadErrors', value: 1 }
            ],
            mode: ConfigurationMode.RemoveAndConfigure
        });

        // io0
        componentConfigurator.add({
            factoryId: 'IO.KMtronic',
            componentId: 'io0',
            alias: this.translate.instant('INSTALLATION.CONFIGURATION_EXECUTE.RELAY_BOARD'),
            properties: [
                { name: 'enabled', value: true },
                { name: 'modbus.id', value: 'modbus0' },
                { name: 'modbusUnitId', value: 6 }
            ],
            mode: ConfigurationMode.RemoveAndConfigure
        });

        // meter0
        componentConfigurator.add({
            factoryId: 'Meter.Socomec.Threephase',
            componentId: 'meter0',
            alias: this.translate.instant('INSTALLATION.CONFIGURATION_EXECUTE.GRID_METER'),
            properties: [
                { name: 'enabled', value: true },
                { name: 'modbus.id', value: 'modbus2' },
                { name: 'type', value: 'GRID' },
                { name: 'modbusUnitId', value: 5 }
            ],
            mode: ConfigurationMode.RemoveAndConfigure
        });

        // battery0
        componentConfigurator.add({
            factoryId: 'Battery.Fenecon.Commercial',
            componentId: 'battery0',
            alias: this.translate.instant('INSTALLATION.CONFIGURATION_EXECUTE.BATTERY'),
            properties: [
                { name: 'enabled', value: true },
                { name: 'startStop', value: 'AUTO' },
                { name: 'modbus.id', value: 'modbus0' },
                { name: 'batteryStartStopRelay', value: 'io0/Relay8' },
                { name: 'modbusUnitId', value: 1 }
            ],
            mode: ConfigurationMode.RemoveAndConfigure
        });

        // batteryInverter0
        componentConfigurator.add({
            factoryId: 'Battery-Inverter.Sinexcel',
            componentId: 'batteryInverter0',
            alias: this.translate.instant('INSTALLATION.CONFIGURATION_EXECUTE.BATTERY_INVERTER'),
            properties: [
                { name: 'enabled', value: true },
                { name: 'modbus.id', value: 'modbus1' },
                { name: 'countryCode', value: 'GERMANY' },
                { name: 'startStop', value: 'AUTO' },
            ],
            mode: ConfigurationMode.RemoveAndConfigure
        });

        // ess0
        componentConfigurator.add({
            factoryId: 'Ess.Generic.ManagedSymmetric',
            componentId: 'ess0',
            alias: this.translate.instant('INSTALLATION.CONFIGURATION_EXECUTE.STORAGE_SYSTEM'),
            properties: [
                { name: 'enabled', value: true },
                { name: 'startStop', value: 'START' },
                { name: 'batteryInverter.id', value: 'batteryInverter0' },
                { name: 'battery.id', value: 'battery0' }
            ],
            mode: ConfigurationMode.RemoveAndConfigure
        });

        // Optional meter2 - aditional AC PV
        const acArray = this.pv.ac;
        const isAcCreated: boolean = acArray.length >= 1;
        const acAlias = isAcCreated ? acArray[0].alias : '';
        const acModbusUnitId = isAcCreated ? acArray[0].modbusCommunicationAddress : 0;

        componentConfigurator.add({
            factoryId: 'Meter.Socomec.Threephase',
            componentId: 'meter1',
            alias: acAlias,
            properties: [
                { name: 'enabled', value: true },
                { name: 'type', value: 'PRODUCTION' },
                { name: 'modbus.id', value: 'modbus2' },
                { name: 'modbusUnitId', value: acModbusUnitId },
                { name: 'invert', value: false }
            ],
            mode: isAcCreated ? ConfigurationMode.RemoveAndConfigure : ConfigurationMode.RemoveOnly
        });

        if (this.feedInLimitation.feedInType === FeedInType.DYNAMIC_LIMITATION) {
            // ctrlGridOptimizedCharge0
            componentConfigurator.add({
                factoryId: 'Controller.Ess.GridOptimizedCharge',
                componentId: 'ctrlGridOptimizedCharge0',
                alias: this.translate.instant('INSTALLATION.CONFIGURATION_EXECUTE.GRID_OPTIMIZED_CHARGE'),
                properties: [
                    { name: 'enabled', value: true },
                    { name: 'ess.id', value: 'ess0' },
                    { name: 'meter.id', value: 'meter0' },
                    { name: 'sellToGridLimitEnabled', value: true },
                    {
                        name: 'maximumSellToGridPower',
                        value: this.feedInLimitation.maximumFeedInPower,
                    },
                    { name: 'delayChargeRiskLevel', value: 'MEDIUM' },
                    { name: 'mode', value: 'AUTOMATIC' },
                    { name: 'manualTargetTime', value: '17:00' },
                    { name: 'debugMode', value: false },
                    { name: 'sellToGridLimitRampPercentage', value: 2 },
                ],
                mode: ConfigurationMode.RemoveAndConfigure,
            });
        }

        // ctrlBalancing0
        componentConfigurator.add({ // Clearify with Productmanagement if a different App like peak shaving could be selected in IBN
            factoryId: 'Controller.Symmetric.Balancing',
            componentId: 'ctrlBalancing0',
            alias: this.translate.instant('INSTALLATION.CONFIGURATION_EXECUTE.SELF_CONSUMPTION'),
            properties: [
                { name: 'enabled', value: true },
                { name: 'ess.id', value: 'ess0' },
                { name: 'meter.id', value: 'meter0' },
                { name: 'targetGridSetpoint', value: 0 }
            ],
            mode: ConfigurationMode.RemoveAndConfigure
        });

        return componentConfigurator;
    }

    public getFields(stringNr: number, numberOfModulesPerString: number) {

        const fields: FormlyFieldConfig[] = this.getCommercial30SerialNumbersFields(stringNr, numberOfModulesPerString);

        if (stringNr === 0) {

            // Adds the ems box field only for Initial String.
            const emsbox: FormlyFieldConfig = {
                key: 'emsbox',
                type: 'input',
                templateOptions: {
                    label: 'FEMS Anschlussbox',
                    required: true,
                    prefix: 'FC',
                    placeholder: 'xxxxxxxxx'
                },
                validators: {
                    validation: ['emsBoxSerialNumber']
                },
                wrappers: ['input-serial-number']
            }

            // ems box field is added at a specific position in array, because it is always displayed at specific position in UI.
            fields.splice(1, 0, emsbox);
        }

        return fields;
    }
}
