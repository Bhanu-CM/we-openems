import { OeChartTester } from "../genericComponents/shared/tester";
import { QueryHistoricTimeseriesDataResponse } from "../jsonrpc/response/queryHistoricTimeseriesDataResponse";
import { QueryHistoricTimeseriesEnergyPerPeriodResponse } from "../jsonrpc/response/queryHistoricTimeseriesEnergyPerPeriodResponse";
import { QueryHistoricTimeseriesEnergyResponse } from "../jsonrpc/response/queryHistoricTimeseriesEnergyResponse";
import { Role } from "../type/role";
import { Edge } from "./edge";
import { EdgeConfig } from "./edgeconfig";

export namespace DummyConfig {

    const DUMMY_EDGE: Edge = new Edge("edge0", "", "", "2023.3.5", Role.ADMIN, true, new Date());
    export function from(...components: Component[]): EdgeConfig {

        return new EdgeConfig(DUMMY_EDGE, <EdgeConfig><unknown>{
            components: <unknown>components?.reduce((acc, c) => ({ ...acc, [c.id]: c }), {}),
            factories: <unknown>components?.map(c => c.factory)
        });
    };

    export function convertDummyEdgeConfigToRealEdgeConfig(edgeConfig: EdgeConfig): EdgeConfig {
        let components = Object.values(edgeConfig?.components) ?? null;

        let factories = {};
        components.forEach(obj => {
            const component = obj as unknown;
            if (factories[component['factoryId']]) {
                factories[component['factoryId']].componentIds = [...factories[component['factoryId']].componentIds, ...component['factory'].componentIds];
            } else {
                factories[component['factoryId']] = {
                    componentIds: component['factory'].componentIds,
                    description: "",
                    id: component['factoryId'],
                    name: component['factoryId'],
                    natureIds: component['factory'].natureIds,
                    properties: []
                };
            }
        });

        return new EdgeConfig(DUMMY_EDGE, <EdgeConfig>{
            components: edgeConfig.components,
            factories: factories
        });
    }

    namespace Factory {

        export const METER_SOCOMEC_THREEPHASE = {
            id: "Meter.Socomec.Threephase",
            natureIds: [
                "io.openems.edge.common.component.OpenemsComponent",
                "io.openems.edge.bridge.modbus.api.ModbusComponent",
                "io.openems.edge.common.modbusslave.ModbusSlave",
                "io.openems.edge.meter.api.ElectricityMeter",
                "io.openems.edge.meter.socomec.SocomecMeter",
                "io.openems.edge.meter.socomec.threephase.SocomecMeterThreephase"
            ]
        };

        export const METER_GOODWE_GRID = {
            id: "GoodWe.Grid-Meter",
            natureIds: [
                "io.openems.edge.goodwe.gridmeter.GoodWeGridMeter",
                "io.openems.edge.meter.api.ElectricityMeter",
                "io.openems.edge.bridge.modbus.api.ModbusComponent",
                "io.openems.edge.common.modbusslave.ModbusSlave",
                "io.openems.edge.common.component.OpenemsComponent",
                "io.openems.edge.timedata.api.TimedataProvider"
            ]
        };

        export const ESS_GENERIC_MANAGEDSYMMETRIC = {
            id: "Ess.Generic.ManagedSymmetric",
            natureIds: [
                "io.openems.edge.goodwe.common.GoodWe",
                "io.openems.edge.bridge.modbus.api.ModbusComponent",
                "io.openems.edge.common.modbusslave.ModbusSlave",
                "io.openems.edge.ess.api.SymmetricEss",
                "io.openems.edge.common.component.OpenemsComponent",
                "io.openems.edge.ess.api.HybridEss",
                "io.openems.edge.goodwe.ess.GoodWeEss",
                "io.openems.edge.ess.api.ManagedSymmetricEss",
                "io.openems.edge.timedata.api.TimedataProvider"
            ]
        };

        export const SOLAR_EDGE_PV_INVERTER = {
            id: "SolarEdge.PV-Inverter",
            natureIds: [
                "io.openems.edge.pvinverter.sunspec.SunSpecPvInverter", "io.openems.edge.meter.api.AsymmetricMeter", "io.openems.edge.meter.api.SymmetricMeter", "io.openems.edge.bridge.modbus.api.ModbusComponent", "io.openems.edge.common.modbusslave.ModbusSlave", "io.openems.edge.pvinverter.api.ManagedSymmetricPvInverter", "io.openems.edge.common.component.OpenemsComponent"
            ]
        };

        export const EVCS_HARDY_BARTH = {
            id: "Evcs.HardyBarth",
            natureIds: [
                "io.openems.edge.common.component.OpenemsComponent",
                "io.openems.edge.evcs.hardybarth.EvcsHardyBarth",
                "io.openems.edge.evcs.api.ManagedEvcs",
                "io.openems.edge.evcs.api.Evcs"
            ]
        };
    }

    export namespace Component {
        export const SOCOMEC_GRID_METER = (id: string, alias?: string): Component => ({
            id: id,
            alias: alias ?? id,
            factoryId: 'Meter.Socomec.Threephase',
            factory: Factory.METER_SOCOMEC_THREEPHASE,
            properties: {
                invert: false,
                modbusUnitId: 5,
                type: "GRID"
            },
            channels: {}
        });

        export const SOCOMEC_CONSUMPTION_METER = (id: string, alias?: string): Component => ({
            id: id,
            alias: alias ?? id,
            factoryId: 'Meter.Socomec.Threephase',
            factory: Factory.METER_SOCOMEC_THREEPHASE,
            properties: {
                invert: false,
                modbusUnitId: 5,
                type: "CONSUMPTION_METERED"
            },
            channels: {}
        });

        export const SOLAR_EDGE_PV_INVERTER = (id: string, alias?: string): Component => ({
            id: id,
            alias: alias,
            factoryId: 'SolarEdge.PV-Inverter',
            factory: Factory.SOLAR_EDGE_PV_INVERTER,
            properties: {
                invert: false,
                modbusUnitId: 5,
                type: "PRODUCTION"
            },
            channels: {}
        });

        export const ESS_GENERIC_MANAGEDSYMMETRIC = (id: string, alias?: string): Component => ({
            id: id,
            alias: alias ?? id,
            factoryId: 'Ess.Generic.ManagedSymmetric',
            factory: Factory.ESS_GENERIC_MANAGEDSYMMETRIC,
            properties: {
                invert: false,
                modbusUnitId: 5
            },
            channels: {}
        });

        export const EVCS_HARDY_BARTH = (id: string, alias?: string): Component => ({
            id: id,
            alias: alias ?? id,
            factoryId: 'Evcs.HardyBarth',
            factory: Factory.EVCS_HARDY_BARTH,
            properties: {
                enabled: "true"
            },
            channels: {}
        });
    }

    export type OeChannels = {

        /** Always one value for each channel from a {@link QueryHistoricTimeseriesEnergyResponse} */
        energyChannelWithValues: QueryHistoricTimeseriesEnergyResponse,

        /** data from a {@link QueryHistoricTimeseriesEnergyPerPeriodResponse} */
        energyPerPeriodChannelWithValues?: QueryHistoricTimeseriesEnergyPerPeriodResponse,
        /** data from a {@link QueryHistoricTimeseriesDataResponse} */
        dataChannelWithValues?: QueryHistoricTimeseriesDataResponse
    }
}

export namespace ChartConfig {
    export const LINE_CHART_OPTIONS = (period: string, labelString?: string): OeChartTester.Dataset.Option => ({ type: 'option', options: { "maintainAspectRatio": false, "legend": { "labels": {}, "position": "bottom" }, "elements": { "point": { "radius": 0, "hitRadius": 0, "hoverRadius": 0 }, "line": { "borderWidth": 2, "tension": 0.1 }, "rectangle": { "borderWidth": 2 } }, "hover": { "mode": "point", "intersect": true }, "scales": { "yAxes": [{ "id": "left", "position": "left", "scaleLabel": { "display": true, "labelString": labelString ?? "kW", "padding": 5, "fontSize": 11 }, "gridLines": { "display": true }, "ticks": { "beginAtZero": false } }], "xAxes": [{ "ticks": {}, "stacked": false, "type": "time", "time": { "minUnit": "hour", "displayFormats": { "millisecond": "SSS [ms]", "second": "HH:mm:ss a", "minute": "HH:mm", "hour": "HH:[00]", "day": "DD", "week": "ll", "month": "MM", "quarter": "[Q]Q - YYYY", "year": "YYYY" }, "unit": period }, "bounds": "ticks" }] }, "tooltips": { "mode": "index", "intersect": false, "axis": "x", "callbacks": {} }, "responsive": true } });
    export const BAR_CHART_OPTIONS = (period: string, labelString?: string): OeChartTester.Dataset.Option => ({ type: 'option', options: { "maintainAspectRatio": false, "legend": { "labels": {}, "position": "bottom" }, "elements": { "point": { "radius": 0, "hitRadius": 0, "hoverRadius": 0 }, "line": { "borderWidth": 2, "tension": 0.1 }, "rectangle": { "borderWidth": 2 } }, "hover": { "mode": "point", "intersect": true }, "scales": { "yAxes": [{ "id": "left", "position": "left", "scaleLabel": { "display": true, "labelString": labelString ?? "kWh", "padding": 5, "fontSize": 11 }, "gridLines": { "display": true }, "ticks": { "beginAtZero": false }, "stacked": true }], "xAxes": [{ "ticks": { "maxTicksLimit": 12, "source": "data" }, "stacked": true, "type": "time", "time": { "minUnit": "hour", "displayFormats": { "millisecond": "SSS [ms]", "second": "HH:mm:ss a", "minute": "HH:mm", "hour": "HH:[00]", "day": "DD", "week": "ll", "month": "MM", "quarter": "[Q]Q - YYYY", "year": "YYYY" }, "unit": period }, "offset": true, "bounds": "ticks" }] }, "tooltips": { "mode": "x", "intersect": false, "axis": "x", "callbacks": {} }, "responsive": true } });
}

/**
 * Factories.
 */
type Factory = {
    id: string
};

/**
 * Components
 */
type Component = {
    id: string,
    alias: string, // defaults to id
    factory: Factory,
    factoryId?: string // generated
    properties: { [property: string]: any },
    channels?: {}
};



export const DATASET = (data: OeChartTester.Dataset.Data, labels: OeChartTester.Dataset.LegendLabel, options: OeChartTester.Dataset.Option) => ({
    data: data,
    labels: labels,
    options: options
});

export const DATA = (name: string, value: number[]): OeChartTester.Dataset.Data => ({
    type: "data",
    label: name,
    value: value
});

export const LABELS = (timestamps: string[]): OeChartTester.Dataset.LegendLabel => ({
    type: "label",
    timestamps: timestamps.map(element => new Date(element))
});

export const OPTIONS = (options: OeChartTester.Dataset.Option): OeChartTester.Dataset.Option => options;


export const LINE_CHART_OPTIONS = (period: string, labelString?: string): OeChartTester.Dataset.Option => ({
    type: 'option',
    options: {
        "maintainAspectRatio": false,
        "legend": {
            "labels": {},
            "position": "bottom"
        },
        "elements": {
            "point": {
                "radius": 0,
                "hitRadius": 0,
                "hoverRadius": 0
            },
            "line": {
                "borderWidth": 2,
                "tension": 0.1
            },
            "rectangle": {
                "borderWidth": 2
            }
        },
        "hover": {
            "mode": "point",
            "intersect": true
        },
        "scales": {
            "yAxes": [
                {
                    "id": "left",
                    "position": "left",
                    "scaleLabel": {
                        "display": true,
                        "labelString": labelString ?? "kW",
                        "padding": 5,
                        "fontSize": 11
                    },
                    "gridLines": {
                        "display": true
                    },
                    "ticks": {
                        "beginAtZero": false
                    }
                }
            ],
            "xAxes": [
                {
                    "ticks": {},
                    "stacked": false,
                    "type": "time",
                    "time": {
                        "minUnit": "hour",
                        "displayFormats": {
                            "millisecond": "SSS [ms]",
                            "second": "HH:mm:ss a",
                            "minute": "HH:mm",
                            "hour": "HH:[00]",
                            "day": "DD",
                            "week": "ll",
                            "month": "MM",
                            "quarter": "[Q]Q - YYYY",
                            "year": "YYYY"
                        },
                        "unit": period
                    },
                    "bounds": "ticks"
                }
            ]
        },
        "tooltips": {
            "mode": "index",
            "intersect": false,
            "axis": "x",
            "callbacks": {}
        },
        "responsive": true
    }
});
export const BAR_CHART_OPTIONS = (period: string, labelString?: string): OeChartTester.Dataset.Option => ({
    type: 'option',
    options: {
        "maintainAspectRatio": false,
        "legend": {
            "labels": {},
            "position": "bottom"
        },
        "elements": {
            "point": {
                "radius": 0,
                "hitRadius": 0,
                "hoverRadius": 0
            },
            "line": {
                "borderWidth": 2,
                "tension": 0.1
            },
            "rectangle": {
                "borderWidth": 2
            }
        },
        "hover": {
            "mode": "point",
            "intersect": true
        },
        "scales": {
            "yAxes": [
                {
                    "id": "left",
                    "position": "left",
                    "scaleLabel": {
                        "display": true,
                        "labelString": labelString ?? "kWh",
                        "padding": 5,
                        "fontSize": 11
                    },
                    "gridLines": {
                        "display": true
                    },
                    "ticks": {
                        "beginAtZero": false
                    },
                    "stacked": true
                }
            ],
            "xAxes": [
                {
                    "ticks": {
                        "maxTicksLimit": 12,
                        "source": "data"
                    },
                    "stacked": true,
                    "type": "time",
                    "time": {
                        "minUnit": "hour",
                        "displayFormats": {
                            "millisecond": "SSS [ms]",
                            "second": "HH:mm:ss a",
                            "minute": "HH:mm",
                            "hour": "HH:[00]",
                            "day": "DD",
                            "week": "ll",
                            "month": "MM",
                            "quarter": "[Q]Q - YYYY",
                            "year": "YYYY"
                        },
                        "unit": period
                    },
                    "offset": true,
                    "bounds": "ticks"
                }
            ]
        },
        "tooltips": {
            "mode": "x",
            "intersect": false,
            "axis": "x",
            "callbacks": {}
        },
        "responsive": true
    }
});
export type OeChannels = {

    /** Always one value for each channel from a {@link QueryHistoricTimeseriesEnergyResponse} */
    energyChannelWithValues: QueryHistoricTimeseriesEnergyResponse,

    /** data from a {@link QueryHistoricTimeseriesEnergyPerPeriodResponse} */
    energyPerPeriodChannelWithValues?: QueryHistoricTimeseriesEnergyPerPeriodResponse,
    /** data from a {@link QueryHistoricTimeseriesDataResponse} */
    dataChannelWithValues?: QueryHistoricTimeseriesDataResponse
}