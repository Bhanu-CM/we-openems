import { FormlyFieldConfig } from '@ngx-formly/core';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';
import { JsonrpcResponseSuccess } from 'src/app/shared/jsonrpc/base';
import { SetupProtocol, SubmitSetupProtocolRequest } from 'src/app/shared/jsonrpc/request/submitSetupProtocolRequest';
import { ChannelAddress, Edge, Websocket } from 'src/app/shared/shared';
import { environment } from 'src/environments';
import { Category } from '../../shared/category';
import { FeedInType, WebLinks } from '../../shared/enums';
import { ComponentData } from '../../shared/ibndatatypes';
import { Meter } from '../../shared/meter';
import { AbstractIbn } from '../abstract-ibn';

export abstract class AbstractCommercialIbn extends AbstractIbn {

    public readonly showRundSteuerManual: boolean = false;

    public showViewCount: boolean = false;

    // configuration-emergency-reserve
    public emergencyReserve?= {
        isEnabled: true,
        minValue: 15,
        value: 20,
        isReserveSocEnabled: false,
    };

    // protocol-dynamic-feed-in-limitation
    public feedInLimitation?= {
        feedInType: FeedInType.EXTERNAL_LIMITATION,
        maximumFeedInPower: 0
    };

    // Protocol line side meter fuse
    public lineSideMeterFuse?: {
        category: Category;
        fixedValue?: number;
        otherValue?: number;
        meterType: Meter;
    } = {
            category: Category.LINE_SIDE_METER_FUSE_COMMERCIAL,
            meterType: Meter.SOCOMEC
        }

    public numberOfModulesPerTower: number;

    public setFeedInLimitsFields(model: any) {
        this.feedInLimitation.feedInType = model.feedInType;
        return this.feedInLimitation;
    }

    //Configuration Summary
    public gtcAndWarrantyLinks: {
        gtcLink: WebLinks;
        warrantyLink: WebLinks;
    } = {
            gtcLink: WebLinks.GTC_LINK,
            warrantyLink: WebLinks.WARRANTY_LINK_COMMERCIAL,
        };

    public getLineSideMeterFuseFields() {
        const fields: FormlyFieldConfig[] = [];

        fields.push({
            key: "otherValue",
            type: "input",
            templateOptions: {
                label: this.translate.instant('INSTALLATION.CONFIGURATION_LINE_SIDE_METER_FUSE.VALUE'),
                type: 'number',
                description: this.translate.instant('INSTALLATION.CONFIGURATION_LINE_SIDE_METER_FUSE.FIXED_VALUE_DESCRIPTION'),
                min: 0,
                required: true
            },
            parsers: [Number],
            validators: {
                validation: ["onlyPositiveInteger"]
            }
        });

        fields.push({
            key: "meterType",
            type: "select",
            templateOptions: {
                label: this.translate.instant('INSTALLATION.CONFIGURATION_LINE_SIDE_METER_FUSE.METER_LABEL'),
                required: true,
                options: [
                    { label: "SOCOMEC", value: Meter.SOCOMEC },
                    { label: "KDK", value: Meter.KDK }
                ]
            },
            defaultValue: Meter.SOCOMEC
        });
        return fields;
    }

    public getFeedInLimitFields() {

        const fields: FormlyFieldConfig[] = [];
        const pv = this.pv;
        let totalPvPower = 0;

        for (const ac of pv.ac) {
            totalPvPower += ac.value ?? 0;
        }

        // Update the feedInlimitation field
        this.feedInLimitation.maximumFeedInPower = parseInt((totalPvPower * 0.7).toFixed(0), 10);

        fields.push({
            key: "feedInType",
            type: "select",
            className: "white-space-initial",
            templateOptions: {
                label: "Typ",
                placeholder: "Select Option",
                options: [
                    { label: this.translate.instant('INSTALLATION.PROTOCOL_FEED_IN_MANAGEMENT.DYNAMIC_LIMITATION'), value: FeedInType.DYNAMIC_LIMITATION },
                    { label: this.translate.instant('INSTALLATION.PROTOCOL_FEED_IN_MANAGEMENT.EXTERNAL_LIMITATION'), value: FeedInType.EXTERNAL_LIMITATION }
                ],
                required: true,
            }
        })

        fields.push({
            key: 'maximumFeedInPower',
            type: 'input',
            templateOptions: {
                type: 'number',
                label: this.translate.instant('INSTALLATION.PROTOCOL_FEED_IN_MANAGEMENT.MAXIMUM_FEED_IN_VALUE'),
                description: this.translate.instant('INSTALLATION.PROTOCOL_FEED_IN_MANAGEMENT.MAXIMUM_VALUE_DESCRIPTION'),
                required: true
            },
            parsers: [Number],
            // 10 is given as radix parameter.
            // 2 = binary, 8 = octal, 10 = decimal, 16 = hexadecimal.
            defaultValue: totalPvPower,
            hideExpression: model => model.feedInType != FeedInType.DYNAMIC_LIMITATION
        });
        return fields;
    }

    public addCustomBatteryInverterData(batteryInverterData: ComponentData[]) {

        const feedInLimitation = this.feedInLimitation;
        feedInLimitation.feedInType === FeedInType.DYNAMIC_LIMITATION
            ? batteryInverterData.push(
                {
                    label: this.translate.instant('INSTALLATION.PROTOCOL_FEED_IN_MANAGEMENT.MAXIMUM_FEED_IN_VALUE'),
                    value: feedInLimitation.maximumFeedInPower,
                }
            )
            : batteryInverterData.push(
                {
                    label: this.translate.instant('INSTALLATION.PROTOCOL_FEED_IN_MANAGEMENT.EXTERNAL_CONTROLLER_RECIEVER'),
                    value: this.translate.instant('General.yes')
                })

        return batteryInverterData;
    }

    public getSettings(edge: Edge, websocket: Websocket):
        Promise<{
            numberOfTowers: number;
            numberOfModulesPerTower: number;
        }> {
        return new Promise((resolve) => {
            let isResolved = false;

            // Edge-subscribe
            edge.subscribeChannels(websocket, 'commercial', [
                new ChannelAddress('battery0', 'NumberOfTowers'),
                new ChannelAddress('battery0', 'NumberOfModulesPerTower')
            ]);

            // Subject to stop the subscription to currentData
            const stopOnRequest: Subject<void> = new Subject<void>();

            // Read tower and module numbers
            edge.currentData
                .pipe(
                    takeUntil(stopOnRequest),
                    filter(currentData => currentData != null))
                .subscribe((currentData) => {
                    const numberOfTowers = currentData.channel['battery0/NumberOfTowers'];
                    const numberOfModulesPerTower = currentData.channel['battery0/NumberOfModulesPerTower'];

                    // If values are available, resolve the promise with them
                    if (numberOfTowers && numberOfModulesPerTower) {
                        isResolved = true;
                        resolve({
                            // 10 is given as radix parameter.
                            // 2 = binary, 8 = octal, 10 = decimal, 16 = hexadecimal.
                            numberOfTowers: parseInt(numberOfTowers, 10),
                            numberOfModulesPerTower: parseInt(numberOfModulesPerTower, 10)
                        });
                    }
                });

            setTimeout(() => {
                // If data isn't available after the timeout, the
                // promise gets resolved with default values
                if (!isResolved) {
                    resolve({ numberOfTowers: 1, numberOfModulesPerTower: this.defaultNumberOfModules });
                }

                // Unsubscribe to currentData and channels after timeout
                stopOnRequest.next();
                stopOnRequest.complete();
                edge.unsubscribeChannels(websocket, 'commercial');
            }, 5000);
        });
    }

    public getProtocol(edge: Edge, websocket: Websocket): Promise<string> {

        const installer = this.installer;
        const customer = this.customer;
        const pv = this.pv;
        const lineSideMeterFuse = this.lineSideMeterFuse;
        const ac = pv.ac;

        const installerObj: any = {
            firstname: installer.firstName,
            lastname: installer.lastName
        };

        const customerObj: any = {
            firstname: customer.firstName,
            lastname: customer.lastName,
            email: customer.email,
            phone: customer.phone,
            address: {
                street: customer.street,
                city: customer.city,
                zip: customer.zip,
                country: customer.country
            }
        };

        if (customer.isCorporateClient) {
            customerObj.company = {
                name: customer.companyName
            };
        }

        let protocol: SetupProtocol = {
            fems: {
                id: edge.id
            },
            installer: installerObj,
            customer: customerObj,
            oem: environment.theme
        };

        // If location data is different to customer data, the location
        // data gets sent too
        if (!this.location.isEqualToCustomerData) {
            const location = this.location;

            protocol.location = {
                firstname: location.firstName,
                lastname: location.lastName,
                email: location.email,
                phone: location.phone,
                address: {
                    street: location.street,
                    city: location.city,
                    zip: location.zip,
                    country: location.country
                },
                company: {
                    name: location.companyName
                }
            };
        }

        protocol.items = [];

        let lineSideMeterFuseValue: number;
        lineSideMeterFuseValue = lineSideMeterFuse.otherValue;

        protocol.items.push({
            category: this.lineSideMeterFuse.category,
            name: this.translate.instant('INSTALLATION.CONFIGURATION_LINE_SIDE_METER_FUSE.VALUE'),
            value: lineSideMeterFuseValue ? lineSideMeterFuseValue.toString() : '',
        });

        const feedInLimitation = this.feedInLimitation;
        protocol.items.push(
            {
                category: Category.FEED_IN_MANAGEMENT,
                name: this.translate.instant('INSTALLATION.PROTOCOL_FEED_IN_MANAGEMENT.EXTERNAL_CONTROLLER_RECIEVER'),
                value: feedInLimitation.feedInType == FeedInType.EXTERNAL_LIMITATION
                    ? this.translate.instant('General.yes')
                    : this.translate.instant('General.no')
            },
            {
                category: Category.FEED_IN_MANAGEMENT,
                name: this.translate.instant('INSTALLATION.PROTOCOL_FEED_IN_MANAGEMENT.DYNAMIC_LIMITATION'),
                value: feedInLimitation.feedInType == FeedInType.DYNAMIC_LIMITATION
                    ? this.translate.instant('General.yes')
                    : this.translate.instant('General.no')
            });

        if (feedInLimitation.feedInType == FeedInType.DYNAMIC_LIMITATION) {
            protocol.items.push({
                category: Category.FEED_IN_MANAGEMENT,
                name: this.translate.instant('INSTALLATION.PROTOCOL_FEED_IN_MANAGEMENT.MAXIMUM_FEED_IN_VALUE'),
                value: feedInLimitation.maximumFeedInPower
                    ? feedInLimitation.maximumFeedInPower.toString()
                    : (0).toString(),
            });
        }

        for (let index = 0; index < ac.length; index++) {
            const element = ac[index];
            const label = 'AC';
            const acNr = (index + 1);

            protocol.items.push(
                {
                    category: Category.ADDITIONAL_AC_PRODUCERS,
                    name: this.translate.instant('INSTALLATION.ALIAS_WITH_LABEL', { label: label, number: acNr }),
                    value: element.alias,
                },
                {
                    category: Category.ADDITIONAL_AC_PRODUCERS,
                    name: this.translate.instant('INSTALLATION.VALUE_WITH_LABEL', { label: label, number: acNr, symbol: '[Wp]' }),
                    value: element.value ? element.value.toString() : '',
                });

            element.orientation && protocol.items.push({
                category: Category.ADDITIONAL_AC_PRODUCERS,
                name: this.translate.instant('INSTALLATION.PROTOCOL_PV_AND_ADDITIONAL_AC.ORIENTATION_WITH_LABEL', { label: label, number: acNr }),
                value: element.orientation,
            });

            element.moduleType && protocol.items.push({
                category: Category.ADDITIONAL_AC_PRODUCERS,
                name: this.translate.instant('INSTALLATION.PROTOCOL_PV_AND_ADDITIONAL_AC.MODULE_TYPE_WITH_LABEL', { label: label, number: acNr }),
                value: element.moduleType,
            });

            element.modulesPerString && protocol.items.push({
                category: Category.ADDITIONAL_AC_PRODUCERS,
                name: this.translate.instant('INSTALLATION.PROTOCOL_PV_AND_ADDITIONAL_AC.NUMBER_OF_MODULES_WITH_LABEL', { label: label, number: acNr }),
                value: element.modulesPerString
                    ? element.modulesPerString.toString()
                    : '',
            });

            element.meterType && protocol.items.push({
                category: Category.ADDITIONAL_AC_PRODUCERS,
                name: this.translate.instant('INSTALLATION.PROTOCOL_PV_AND_ADDITIONAL_AC.METER_TYPE_WITH_LABEL', { label: label, number: acNr }),
                value: Meter.toLabelString(element.meterType),
            });

            element.modbusCommunicationAddress && protocol.items.push({
                category: Category.ADDITIONAL_AC_PRODUCERS,
                name: this.translate.instant('INSTALLATION.PROTOCOL_PV_AND_ADDITIONAL_AC.MODBUS_WITH_LABEL', { label: label, number: acNr }),
                value: element.modbusCommunicationAddress
                    ? element.modbusCommunicationAddress.toString()
                    : '',
            });
        }

        protocol.items.push({
            category: Category.EMS_DETAILS,
            name: this.translate.instant('INSTALLATION.CONFIGURATION_SUMMARY.EDGE_NUMBER', { edgeShortName: environment.edgeShortName }),
            value: edge.id
        });

        protocol = this.getProtocolSerialNumbers(protocol);

        return new Promise((resolve, reject) => {
            websocket.sendRequest(SubmitSetupProtocolRequest.translateFrom(protocol, this.translate)).then((response: JsonrpcResponseSuccess) => {
                resolve(response.result['setupProtocolId']);
            }).catch((reason) => {
                reject(reason);
            });
        });
    }

    public getProtocolSerialNumbers(protocol: SetupProtocol): SetupProtocol {

        protocol.lots = [];

        const serialNumbers = this.serialNumbers;

        // Initial tower has 3 static components other than modules such as Welcherischter, BMS and EMS box.
        const initialStaticTowerComponents = 3;

        // Subsequent towers will have only 2 static components. Paralell box and BMS box.
        const subsequentStaticTowerComponents = 1;

        // Total number of components each tower contains, so that easier to categorize the serial numbers based on towers.
        const numberOfComponentsTower1 = this.numberOfModulesPerTower + initialStaticTowerComponents;
        const numberOfComponentsTower2 = numberOfComponentsTower1 + this.numberOfModulesPerTower + subsequentStaticTowerComponents;
        const numberOfComponentsTower3 = numberOfComponentsTower2 + this.numberOfModulesPerTower + subsequentStaticTowerComponents;
        const numberOfComponentsTower4 = numberOfComponentsTower3 + this.numberOfModulesPerTower + subsequentStaticTowerComponents;

        for (let componentCount = 0; componentCount < serialNumbers.modules.length; componentCount++) {
            if (serialNumbers.modules[componentCount].value !== null && serialNumbers.modules[componentCount].value !== '') {
                var name: string = this.translate.instant('INSTALLATION.PROTOCOL_SERIAL_NUMBERS.SINGLE_SERIAL_NUMBER', { label: serialNumbers.modules[componentCount].label });
                var serialNumber: string = serialNumbers.modules[componentCount].value;

                // String 1
                if (componentCount < numberOfComponentsTower1) {
                    protocol.lots.push({
                        category: this.translate.instant('INSTALLATION.PROTOCOL_SERIAL_NUMBERS.BESS_COMPONENTS'),
                        name: name,
                        serialNumber: serialNumber,
                    });
                }
                // String 2
                else if (componentCount < numberOfComponentsTower2) {
                    protocol.lots.push({
                        category: this.translate.instant('INSTALLATION.PROTOCOL_SERIAL_NUMBERS.BATTERY_STRING', { stringNumber: 2 }),
                        name: name,
                        serialNumber: serialNumber,
                    });
                }
                // String 3
                else if (componentCount < numberOfComponentsTower3) {
                    protocol.lots.push({
                        category: this.translate.instant('INSTALLATION.PROTOCOL_SERIAL_NUMBERS.BATTERY_STRING', { stringNumber: 3 }),
                        name: name,
                        serialNumber: serialNumber,
                    });
                }
                // String 4
                else if (componentCount < numberOfComponentsTower4) {
                    protocol.lots.push({
                        category: this.translate.instant('INSTALLATION.PROTOCOL_SERIAL_NUMBERS.BATTERY_STRING', { stringNumber: 4 }),
                        name: name,
                        serialNumber: serialNumber,
                    });
                }
            }
        }
        return protocol;
    }
}
