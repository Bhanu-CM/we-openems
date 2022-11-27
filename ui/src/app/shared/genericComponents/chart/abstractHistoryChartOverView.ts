import { Directive, Input, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { ModalController } from "@ionic/angular";
import { Subject } from "rxjs";
import { ChannelAddress, CurrentData, Edge, EdgeConfig, Service } from "src/app/shared/shared";
import { DefaultTypes } from "../../service/defaulttypes";
import { v4 as uuidv4 } from 'uuid';

@Directive()
export abstract class AbstractHistoryChartOverView implements OnInit {

  public edge: Edge | null = null;
  public period: DefaultTypes.HistoryPeriod;
  @Input() public componentId: string;
  protected showTotal: boolean = false;
  protected showPhases: boolean = false;

  /**
   * True after this.edge, this.config and this.component are set.
   */
  public isInitialized: boolean = false;
  public config: EdgeConfig = null;
  public component: EdgeConfig.Component = null;
  public stopOnDestroy: Subject<void> = new Subject<void>();

  constructor(
    public service: Service,
    protected route: ActivatedRoute,
    public modalCtrl: ModalController,
  ) { }

  protected setShowPhases(event: boolean) {
    this.showPhases = event;
    this.updateValues();
  }
  protected setShowTotal(event: boolean) {
    this.showTotal = event;
    this.updateValues();
  }

  public ngOnInit() {
    this.service.setCurrentComponent('', this.route).then(edge => {
      this.service.getConfig().then(config => {
        // store important variables publically
        this.edge = edge;
        this.config = config;
        this.component = config.components[this.componentId];

        this.period = this.service.historyPeriod;

      }).then(() => {
        // announce initialized
        this.isInitialized = true;

        // get the channel addresses that should be subscribed and updateValues if data has changed
        this.updateValues();

      })
    });
  };

  public updateValues() {
    let channelAddresses = this.getChannelAddresses();
    this.service.queryEnergy(this.period.from, this.period.to, channelAddresses).then(response => {
      let result = response.result;
      let thisComponent = {};
      let allComponents = {};
      for (let channelAddress of channelAddresses) {
        let ca = channelAddress.toString();
        allComponents[ca] = result.data[ca]
        if (channelAddress.componentId === this.componentId) {
          thisComponent[channelAddress.channelId] = result.data[ca];
        }
      }
      this.onCurrentData({ thisComponent: thisComponent, allComponents: allComponents })
    }).catch(() => {
      // TODO Error Message
    })
  }

  public ngOnChanges() {
    this.updateValues()
  }

  public ngOnDestroy() {
    // Unsubscribe from CurrentData subject
    this.stopOnDestroy.next();
    this.stopOnDestroy.complete();
  }

  /**
   * Called on every new data.
   * 
   * @param currentData new data for the subscribed Channel-Addresses
   */
  protected onCurrentData(currentData: CurrentData): void { }

  /**
   * Gets the ChannelIds of the current Component that should be subscribed.
   */
  protected getChannelIds(): string[] {
    return [];
  }

  /**
   * Gets the ChannelAddresses that should be queried.
   * 
   * @param edge the current Edge
   * @param config the EdgeConfig
   */
  protected getChannelAddresses(): ChannelAddress[] {
    return [];
  }
}