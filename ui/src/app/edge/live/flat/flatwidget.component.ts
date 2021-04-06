import { ActivatedRoute } from '@angular/router';
import { ChannelAddress, Edge, EdgeConfig, Service, Websocket } from '../../../shared/shared';
import { Component, Input, OnInit } from '@angular/core';

import { ModalController } from '@ionic/angular';
import { FixDigitalOutputModalComponent } from '../fixdigitaloutput/modal/modal.component';

@Component({
  selector: FlatWidgetComponent.SELECTOR,
  templateUrl: './flatwidget.component.html'
})
export class FlatWidgetComponent implements OnInit {

  /** SELECTOR defines, how to call this Widget */
  static SELECTOR: string = 'flat-widget';

  /** Title in Header */
  @Input() public title: string;

  /** Image in Header */
  @Input() public img: string;

  /** Icon in Header */
  @Input() public icon: Icon = null;

  /** BackgroundColor of the Header (light or dark) */
  @Input() public color: string;

  /** ChannelAdresses sends all the channels from one specific Widget to FlatWidget */
  @Input() public channelAdresses: ChannelAddress[];

  /** Selector sends the Widget's selector to FlatWidget */
  @Input() public selector: string;

  /** Title_translate specifies if there is a title to translate */
  @Input() public title_translate: string;


  public edge: Edge = null;
  public component: EdgeConfig.Component = null;

  constructor(
    public service: Service,
    public route: ActivatedRoute,
    private modalController: ModalController
  ) {

  }

  ngOnInit() {
    this.service.setCurrentComponent('', this.route).then(edge => {
      this.edge = edge;
    });
  }
  ngOnDestroy() {
    if (this.edge != null) {
    }
  }
  async presentModal() {
    const modal = await this.modalController.create({
      component: FixDigitalOutputModalComponent,
      componentProps: {
        component: this.component,
        edge: this.edge
      }
    });
    return await modal.present();
  }
}

export type Icon = {
  color: string;
  size: string;
  name: string;
}
