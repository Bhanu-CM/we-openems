import { formatNumber } from '@angular/common';
import { ChangeDetectorRef, Directive, Input, OnChanges, OnInit } from '@angular/core';
import { ActivatedRoute, Data } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import * as Chart from 'chart.js';
import { ChartDataSets, ChartLegendLabelItem } from 'chart.js';
import { QueryHistoricTimeseriesEnergyPerPeriodResponse } from 'src/app/shared/jsonrpc/response/queryHistoricTimeseriesEnergyPerPeriodResponse';
import { DefaultTypes } from 'src/app/shared/service/defaulttypes';
import { v4 as uuidv4 } from 'uuid';
import { calculateResolution, ChannelFilter, ChartData, ChartOptions, DEFAULT_TIME_CHART_OPTIONS, DisplayValues, EMPTY_DATASET, isLabelVisible, setLabelVisible, TooltipItem, Unit, YAxisTitle } from '../../../edge/history/shared';
import { QueryHistoricTimeseriesDataRequest } from '../../jsonrpc/request/queryHistoricTimeseriesDataRequest';
import { QueryHistoricTimeseriesEnergyPerPeriodRequest } from '../../jsonrpc/request/queryHistoricTimeseriesEnergyPerPeriodRequest';
import { QueryHistoricTimeseriesEnergyRequest } from '../../jsonrpc/request/queryHistoricTimeseriesEnergyRequest';
import { QueryHistoricTimeseriesDataResponse } from '../../jsonrpc/response/queryHistoricTimeseriesDataResponse';
import { QueryHistoricTimeseriesEnergyResponse } from '../../jsonrpc/response/queryHistoricTimeseriesEnergyResponse';
import { HistoryUtils } from '../../service/utils';
import { ChannelAddress, Edge, EdgeConfig, Service, Utils } from "../../shared";

// NOTE: Auto-refresh of widgets is currently disabled to reduce server load
@Directive()
export abstract class AbstractHistoryChart implements OnInit, OnChanges {

  /** Title for Chart, diplayed above the Chart */
  @Input() public chartTitle: string = "";

  /** TODO: workaround with Observables, to not have to pass the period on Initialisation */
  @Input() public period: DefaultTypes.HistoryPeriod;
  @Input() public component: EdgeConfig.Component;
  @Input() public showPhases: boolean;
  @Input() public showTotal: boolean;

  @Input() public isOnlyChart: boolean = false;
  protected spinnerId: string = uuidv4();

  protected readonly phaseColors: string[] = ['rgb(255,127,80)', 'rgb(0,0,255)', 'rgb(128,128,0)']

  public edge: Edge | null = null;

  public loading: boolean = true;
  public labels: Date[] = [];
  public datasets: ChartDataSets[] = EMPTY_DATASET(this.translate);
  public options: ChartOptions | null = DEFAULT_TIME_CHART_OPTIONS;
  public colors: any[] = [];
  public chartObject: ChartData = null;
  public chartType: 'line' | 'bar' = 'line';
  protected isDataExisting: boolean = true;
  protected config: EdgeConfig = null;

  constructor(
    public service: Service,
    public cdRef: ChangeDetectorRef,
    protected translate: TranslateService,
    protected route: ActivatedRoute,
  ) { }

  ngOnInit() {
    // this.startSpinner();
    this.service.setCurrentComponent('', this.route).then(edge => {
      this.service.getConfig().then(config => {
        // store important variables publically
        this.edge = edge;
        this.config = config;
        this.edge = edge;

      }).then(() => {
        this.chartObject = this.getChartData()
        this.loadChart();
      })
    })
  }

  ngOnChanges() {
    this.updateChart();
  };

  protected getChartHeight(): number {
    if (this.isOnlyChart) {
      return window.innerHeight / 1.3;
    }
    return window.innerHeight / 21 * 9;
  }

  private updateChart() {
    this.startSpinner();
    this.loadChart()
  }

  private fillChart(energyPeriodResponse: QueryHistoricTimeseriesDataResponse | QueryHistoricTimeseriesEnergyPerPeriodResponse, energyResponse?: QueryHistoricTimeseriesEnergyResponse) {

    if (Utils.isDataEmpty(energyPeriodResponse)) {
      return
    }

    let result = energyPeriodResponse.result;
    let labels: Date[] = [];
    for (let timestamp of result.timestamps) {
      labels.push(new Date(timestamp));
    }

    let channelData: { name: string, data: number[] }[] = []
    this.chartObject.channel.forEach(element => {
      let channelAddress: ChannelAddress = null;
      if (this.chartType == 'bar' && element.energyChannel) {
        channelAddress = element.energyChannel
      } else {
        channelAddress = element.powerChannel
      }

      if (channelAddress?.toString() in result.data) {
        channelData.push({
          data: HistoryUtils.CONVERT_WATT_TO_KILOWATT_OR_KILOWATTHOURS(result.data[channelAddress.toString()])?.map(value => {
            if (value == null) {
              return null
            } else {

              // Custom Filters
              switch (element.filter) {
                case ChannelFilter.NOT_NULL:
                  return value;
                case ChannelFilter.NOT_NULL_OR_NEGATIVE:
                  if (value > 0) {
                    return value;
                  } else {
                    return 0;
                  }
                case ChannelFilter.NOT_NULL_OR_POSITIVE:
                  if (value < 0) {
                    return value;
                  } else {
                    return 0;
                  }
                default:
                  return value
              }
            }
          }) ?? null, name: element.name
        })
      }
    })

    let datasets: ChartDataSets[] = [];
    let colors: any[] = [];

    // Fill datasets, labels and colors
    let displayValues: DisplayValues[] = this.chartObject.displayValues(channelData);

    // TODO for each
    displayValues.forEach(element => {
      let values = element.setValue()
      let nameSuffix = null;

      // Check if energyResponse is available
      if (energyResponse) {
        nameSuffix = element.nameSuffix
          ?
          (element.nameSuffix(energyResponse) != null
            ? element.nameSuffix(energyResponse)
            : null)
          : null;
      }

      this.getLabelName(element.name, nameSuffix)      // Filter existing values
      if (values) {
        datasets.push({
          label: this.getLabelName(element.name, nameSuffix),
          data: values,
          hidden: !isLabelVisible(element.name, !(element.hiddenOnInit)),
          ...(element.stack && { stack: element.stack.toString() })
        })
        colors.push({
          backgroundColor: 'rgba(' + (this.chartType == 'bar' ? element.color.split('(').pop().split(')')[0] + ',0.4)' : element.color.split('(').pop().split(')')[0] + ',0.05)'),
          borderColor: 'rgba(' + element.color.split('(').pop().split(')')[0] + ',1)',
        })
      }
    })

    this.datasets = datasets;
    this.colors = colors;
    this.labels = labels;
  }

  private loadChart() {
    this.datasets = EMPTY_DATASET(this.translate);
    this.labels = []
    let unit = calculateResolution(this.service, this.period.from, this.period.to).resolution.unit;

    if ((unit == Unit.DAYS || unit == Unit.MONTHS)) {
      this.chartType = 'bar';
      this.queryHistoricTimeseriesEnergyPerPeriod(this.period.from, this.period.to).then(energyPerPeriodResponse => {
        this.queryHistoricTimeseriesEnergy(this.period.from, this.period.to).then((energyResponse) => {
          this.setChartLabel();
          this.fillChart(energyPerPeriodResponse, energyResponse);
          let barWidthPercentage = 0;
          let categoryGapPercentage = 0;
          switch (this.service.periodString) {
            case DefaultTypes.PeriodString.CUSTOM: {
              barWidthPercentage = 0.7;
              categoryGapPercentage = 0.4;
            }
            case DefaultTypes.PeriodString.MONTH: {
              if (this.service.isSmartphoneResolution == true) {
                barWidthPercentage = 1;
                categoryGapPercentage = 0.6;
              } else {
                barWidthPercentage = 0.9;
                categoryGapPercentage = 0.8;
              }
            }
            case DefaultTypes.PeriodString.YEAR: {
              if (this.service.isSmartphoneResolution == true) {
                barWidthPercentage = 1;
                categoryGapPercentage = 0.6;
              } else {
                barWidthPercentage = 0.8;
                categoryGapPercentage = 0.8;
              }
            }
          }
          this.datasets.forEach(element => {
            element.barPercentage = barWidthPercentage;
            element.categoryPercentage = categoryGapPercentage;
          })
        })
      })
    } else {

      // Shows Line-Chart
      this.queryHistoricTimeseriesData(this.period.from, this.period.to).then(response => {
        this.chartType = 'line'
        this.fillChart(response);
        this.setChartLabel();
      })
    }
  }

  /**
   * Sends the Historic Timeseries Data Query and makes sure the result is not empty.
   * 
   * @param fromDate the From-Date
   * @param toDate   the To-Date
   * @param edge     the current Edge
   * @param ws       the websocket
   */
  protected queryHistoricTimeseriesData(fromDate: Date, toDate: Date): Promise<QueryHistoricTimeseriesDataResponse> {

    this.isDataExisting = true;
    let resolution = calculateResolution(this.service, fromDate, toDate).resolution;

    let result: Promise<QueryHistoricTimeseriesDataResponse> = new Promise<QueryHistoricTimeseriesDataResponse>((resolve, reject) => {
      this.service.getCurrentEdge().then(edge => {
        this.service.getConfig().then(async () => {
          let channelAddresses = (await this.getChannelAddresses()).powerChannels;
          let request = new QueryHistoricTimeseriesDataRequest(fromDate, toDate, channelAddresses, resolution);
          edge.sendRequest(this.service.websocket, request).then(response => {
            let result = (response as QueryHistoricTimeseriesDataResponse)?.result;
            if (Object.keys(result).length != 0) {
              resolve(response as QueryHistoricTimeseriesDataResponse);
            } else {
              resolve(new QueryHistoricTimeseriesDataResponse(response.id, {
                timestamps: [null], data: { null: null }
              }));
            }
          });
        })
      })
    }).then((response) => {

      // Check if channelAddresses are empty
      if (Utils.isDataEmpty(response)) {

        // load defaultchart
        this.isDataExisting = false;
        this.stopSpinner()
        this.initializeChart()
      }
      return response
    })

    return result
  }

  /**
   * Sends the Historic Timeseries Energy per Period Query and makes sure the result is not empty.
   * Symbolizes First substracted from last Datapoint for each period, only used for cumulated channel
   * 
   * @param fromDate the From-Date
   * @param toDate   the To-Date
   */
  protected queryHistoricTimeseriesEnergyPerPeriod(fromDate: Date, toDate: Date): Promise<QueryHistoricTimeseriesEnergyPerPeriodResponse> {

    this.isDataExisting = true;
    let resolution = calculateResolution(this.service, fromDate, toDate).resolution;

    let result: Promise<QueryHistoricTimeseriesEnergyPerPeriodResponse> = new Promise<QueryHistoricTimeseriesEnergyPerPeriodResponse>((resolve, reject) => {
      this.service.getCurrentEdge().then(edge => {
        this.service.getConfig().then(async () => {

          let channelAddresses = (await this.getChannelAddresses()).energyChannels.filter(element => element != null);
          if (channelAddresses.length > 0) {

            edge.sendRequest(this.service.websocket, new QueryHistoricTimeseriesEnergyPerPeriodRequest(fromDate, toDate, channelAddresses, resolution)).then(response => {
              let result = (response as QueryHistoricTimeseriesEnergyPerPeriodResponse)?.result;
              if (Object.keys(result).length != 0) {
                resolve(response as QueryHistoricTimeseriesEnergyPerPeriodResponse);
              } else {
                resolve(new QueryHistoricTimeseriesEnergyPerPeriodResponse(response.id, {
                  timestamps: [null], data: { null: null }
                }));
              }
            })
          }
        })
      });
    }).then(async (response) => {

      // Check if channelAddresses are empty
      if (Utils.isDataEmpty(response)) {

        // load defaultchart
        this.isDataExisting = false;
        this.service.stopSpinner(this.spinnerId)
        this.initializeChart()
      }
      return response
    })
    return result
  }


  /**
   * Sends the Historic Timeseries Energy per Period Query and makes sure the result is not empty.
   * Symbolizes First substracted from last Datapoint for each period, only used for cumulated channel
   * 
   * @param fromDate the From-Date
   * @param toDate   the To-Date
   */
  protected queryHistoricTimeseriesEnergy(fromDate: Date, toDate: Date): Promise<QueryHistoricTimeseriesEnergyResponse> {

    this.isDataExisting = true;

    let result: Promise<QueryHistoricTimeseriesEnergyResponse> = new Promise<QueryHistoricTimeseriesEnergyResponse>((resolve, reject) => {
      this.service.getCurrentEdge().then(edge => {
        this.service.getConfig().then(async () => {

          let channelAddresses = (await this.getChannelAddresses()).energyChannels.filter(element => element != null);

          if (channelAddresses.length > 0) {
            edge.sendRequest(this.service.websocket, new QueryHistoricTimeseriesEnergyRequest(fromDate, toDate, channelAddresses)).then(response => {
              let result = (response as QueryHistoricTimeseriesEnergyResponse)?.result;
              if (Object.keys(result).length != 0) {
                resolve(response as QueryHistoricTimeseriesEnergyResponse);
              } else {
                resolve(new QueryHistoricTimeseriesEnergyResponse(response.id, {
                  data: { null: null }
                }));
              }
            })
          }
        })
      });
    })

    // load defaultchart
    this.service.stopSpinner(this.spinnerId)
    this.initializeChart()
    return result
  }

  /**
   * Generates a Tooltip Title string from a 'fromDate' and 'toDate'.
   * 
   * @param fromDate the From-Date
   * @param toDate the To-Date 
   * @param date Date from TooltipItem
   * @returns period for Tooltip Header
   */
  protected toTooltipTitle(fromDate: Date, toDate: Date, date: Date): string {
    let unit = calculateResolution(this.service, fromDate, toDate).resolution.unit;
    if (unit == Unit.MONTHS) {
      // Yearly view
      return date.toLocaleDateString('default', { month: 'long' });

    } else if (unit == Unit.DAYS) {
      // Monthly view
      return date.toLocaleDateString('default', { day: '2-digit', month: 'long' });

    } else {
      // Default
      return date.toLocaleString('default', { day: '2-digit', month: '2-digit', year: '2-digit' }) + ' ' + date.toLocaleTimeString('default', { hour12: false, hour: '2-digit', minute: '2-digit' });
    }
  }

  /**
   * Sets the Labels of the Chart
   */
  protected setChartLabel() {
    let options = <ChartOptions>Utils.deepCopy(DEFAULT_TIME_CHART_OPTIONS);
    let chartObject = this.chartObject;
    options.scales.xAxes[0].time.unit = calculateResolution(this.service, this.period.from, this.period.to).timeFormat;

    if (this.chartType == 'bar') {
      options.scales.xAxes[0].stacked = true;
      options.scales.yAxes[0].stacked = true;
      options.scales.xAxes[0].offset = true;
      options.scales.xAxes[0].ticks.maxTicksLimit = 12;
      options.scales.xAxes[0].ticks.source = 'data';
    }

    options.scales.xAxes[0].bounds = 'ticks';
    options.responsive = true;

    // Chart.pluginService.register(this.showZeroPlugin);

    // Overwrite Tooltips -Title -Label 
    options.tooltips.callbacks.title = (tooltipItems: TooltipItem[], data: Data): string => {
      let date = new Date(tooltipItems[0].xLabel);
      return this.toTooltipTitle(this.service.historyPeriod.from, this.service.historyPeriod.to, date);
    }
    options.tooltips.callbacks.label = (tooltipItem: TooltipItem, data: Data) => {
      let label = data.datasets[tooltipItem.datasetIndex].label;
      let value = tooltipItem.yLabel;

      // TODO remove
      if (this.chartType == 'bar') {
        return label
      }
      // Show floating point number for values between 0 and 1
      return label + ": " + formatNumber(value, 'de', chartObject.tooltip.formatNumber) + ' ' + this.getToolTipsLabel(chartObject.unit);
    }

    // Set Y-Axis Title
    options.scales.yAxes[0].scaleLabel.labelString = this.getYAxisTitle(chartObject.unit);

    // Save Original OnClick because calling onClick overwrites default function
    var original = Chart.defaults.global.legend.onClick;
    Chart.defaults.global.legend.onClick = function (event: MouseEvent, legendItem: ChartLegendLabelItem) {
      let chart: Chart = this.chart;
      let legendItemIndex = legendItem.datasetIndex;

      // Set @Angular SessionStorage for Labels to check if they are hidden
      setLabelVisible(legendItem.text, !chart.isDatasetVisible(legendItemIndex));
      original.call(this, event, legendItem);
    }
    this.options = options;
    this.loading = false;
    this.stopSpinner();
  }

  /**
   * Initializes empty chart on error
   * @param spinnerSelector to stop spinner
   */
  protected initializeChart() {
    this.datasets = EMPTY_DATASET(this.translate);
    this.labels = [];
    this.loading = false;
    this.stopSpinner();
  }

  /**
   * Gets the ChannelAddresses that should be queried.
   */
  protected getChannelAddresses(): Promise<{ powerChannels: ChannelAddress[], energyChannels: ChannelAddress[] }> {
    return new Promise<{ powerChannels: ChannelAddress[], energyChannels: ChannelAddress[] }>(resolve => {
      if (this.chartObject?.channel) {
        resolve({
          powerChannels: this.chartObject.channel.map(element => element.powerChannel),
          energyChannels: this.chartObject.channel.map(element => element.energyChannel)
        });
      }
    })

  };

  protected getYAxisTitle(title: YAxisTitle): string {
    switch (title) {
      case YAxisTitle.PERCENTAGE:
        return this.translate.instant("General.percentage")
      case YAxisTitle.ENERGY:
        if (this.chartType == 'bar') {
          return 'kWh'
        } else {
          return 'kW'
        }
    }
  }

  protected getToolTipsLabel(title: YAxisTitle) {
    switch (title) {
      case YAxisTitle.PERCENTAGE:
        return '%'
      case YAxisTitle.ENERGY:
        if (this.chartType == 'bar') {
          return 'kWh'
        } else {
          return 'kW'
        }
    }
  }

  protected getLabelName(baseName: string, suffix?: number | string): string {
    if (suffix !== null) {
      switch (this.chartObject.unit) {
        case YAxisTitle.ENERGY:
          if (typeof suffix == 'number') {
            return baseName + ": " + formatNumber(suffix / 1000, 'de', "1.0-2") + " kWh";
          } else {
            return baseName + ": " + suffix + " kWh";
          }
        case YAxisTitle.PERCENTAGE:
          if (typeof suffix == 'number') {
            return baseName + ": " + formatNumber(suffix, 'de', "1.0-1") + " %";
          } else {
            return baseName + ": " + suffix + " %";
          }
      }
    }
    return baseName;
  }

  /**
   * Used to show a small bar on the chart if the value is 0
   *
   * @type Object
   */
  private showZeroPlugin = {
    beforeRender: function (chartInstance) {
      let datasets = chartInstance.config.data.datasets;
      for (let i = 0; i < datasets.length; i++) {
        let meta = datasets[i]._meta;
        // It counts up every time you change something on the chart so
        // this is a way to get the info on whichever index it's at
        let metaData = meta[Object.keys(meta)[0]];
        let bars = metaData.data;

        for (let j = 0; j < bars.length; j++) {
          let model = bars[j]._model;
          if (metaData.type === "horizontalBar" && model.base === model.x) {
            model.x = model.base + 2;
          }
          else if (model.base === model.y) {
            model.y = model.base - 2;
          }
        }
      }

    }
  };

  /**
   * Start NGX-Spinner
   * 
   * Spinner will appear inside html tag only
   * 
   * @example <ngx-spinner name="YOURSELECTOR"></ngx-spinner>
   * 
   * @param selector selector for specific spinner
   */
  public startSpinner() {
    this.service.startSpinner(this.spinnerId);
  }

  /**
   * Stop NGX-Spinner
   * @param selector selector for specific spinner
   */
  public stopSpinner() {
    this.service.stopSpinner(this.spinnerId);
  }
  protected abstract getChartData(): ChartData | null
}
