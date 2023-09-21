import { Component } from '@angular/core';
import { AbstractHistoryChartOverview } from 'src/app/shared/genericComponents/chart/abstractHistoryChartOverview';
import { ChannelAddress, EdgeConfig } from 'src/app/shared/shared';

@Component({
  templateUrl: './overview.html'
})
export class OverviewComponent extends AbstractHistoryChartOverview {
  protected chargerComponents: EdgeConfig.Component[] = [];
  protected productionMeterComponents: EdgeConfig.Component[] = [];

  protected override getChannelAddresses(): ChannelAddress[] {
    //  Get Chargers
    this.chargerComponents =
      this.config.getComponentsImplementingNature("io.openems.edge.ess.dccharger.api.EssDcCharger")
        .filter(component => component.isEnabled);

    // Get productionMeters
    this.productionMeterComponents =
      this.config.getComponentsImplementingNature("io.openems.edge.meter.api.SymmetricMeter")
        .filter(component => component.isEnabled && this.config.isProducer(component));
    return [];
  }
}  