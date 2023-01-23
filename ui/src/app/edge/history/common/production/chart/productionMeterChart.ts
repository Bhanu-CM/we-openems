import { Component } from '@angular/core';
import { AbstractHistoryChart } from 'src/app/shared/genericComponents/chart/abstracthistorychart';
import { QueryHistoricTimeseriesEnergyResponse } from 'src/app/shared/jsonrpc/response/queryHistoricTimeseriesEnergyResponse';
import { HistoryUtils } from 'src/app/shared/service/utils';

import { ChannelAddress } from '../../../../../shared/shared';
import { ChannelFilter, Channels, ChartData, DisplayValues, YAxisTitle } from '../../../shared';

@Component({
  selector: 'productionMeterchart',
  templateUrl: '../../../../../shared/genericComponents/chart/abstracthistorychart.html',
})
export class ProductionMeterChartComponent extends AbstractHistoryChart {

  protected override getChartData(): ChartData {
    let channels: Channels[] = [{
      name: 'ActivePower',
      powerChannel: ChannelAddress.fromString(this.component.id + '/ActivePower'),
      energyChannel: ChannelAddress.fromString(this.component.id + '/ActiveProductionEnergy'),
      filter: ChannelFilter.NOT_NULL,
    },
    ];

    // Phase 1 to 3
    for (let i = 1; i < 4; i++) {
      channels.push({
        name: 'ActivePowerL' + i,
        powerChannel: ChannelAddress.fromString(this.component.id + '/ActivePowerL' + i),
        energyChannel: ChannelAddress.fromString(this.component.id + '/ActiveProductionEnergyL' + i),
        filter: ChannelFilter.NOT_NULL,
      })
    }
    return {
      channel: channels,
      displayValues: (data: { [name: string]: number[] }) => {
        let datasets: DisplayValues[] = [];
        datasets.push({
          name: this.translate.instant('General.production'),
          nameSuffix: (energyPeriodResponse: QueryHistoricTimeseriesEnergyResponse) => {
            return energyPeriodResponse?.result.data[this.component.id + '/ActiveProductionEnergy'] ?? null
          },
          setValue: () => {
            return data['ActivePower']
          },
          color: 'rgb(0,152,204)'
        })
        if (this.showPhases) {

          // Phase 1 to 3
          for (let i = 1; i < 4; i++) {
            datasets.push({
              name: "Erzeugung Phase L" + i,
              setValue: () => {
                return data['ActivePowerL' + i] ?? null
              },
              color: this.phaseColors[i - 1]
            })
          }
        }
        return datasets;
      },
      tooltip: {
        formatNumber: '1.1-2'
      },
      unit: YAxisTitle.ENERGY,
    }
  }
}