import { TranslateService } from '@ngx-translate/core';
import { format, isSameDay, subDays, getDay, endOfMonth, getYear, startOfYear, differenceInDays } from 'date-fns';
import { isFirstDayOfMonth, isLastDayOfMonth, getMonth, isSameYear, endOfYear } from 'date-fns/esm';

export module DefaultTypes {

  export type Backend = "OpenEMS Backend" | "OpenEMS Edge";

  export type ConnectionStatus = "online" | "connecting" | "waiting for authentication" | "failed";

  export interface ChannelAddresses {
    [componentId: string]: string[];
  }

  /**
   * CurrentData Summary
   * 
   * ratio is [-1,1]
   */
  export interface Summary {
    system: {
      // the balance sheet total power of all power that enters the the system (production, discharge, buy-from-grid), respectively leaves the system (consumption, charge, sell-to-grid)
      totalPower: number,
      // autarchy in percent
      autarchy: number,
      // self consumption in percent
      selfConsumption: number
    }, storage: {
      soc: number,
      activePowerL1: number,
      activePowerL2: number,
      activePowerL3: number,
      effectiveActivePowerL1: number,
      effectiveActivePowerL2: number,
      effectiveActivePowerL3: number,
      chargeActivePower: number,
      chargeActivePowerAc: number,
      chargeActivePowerDc: number,
      maxChargeActivePower?: number,
      dischargeActivePower: number,
      dischargeActivePowerAc: number,
      dischargeActivePowerDc: number,
      maxDischargeActivePower?: number,
      powerRatio: number,
      maxApparentPower: number,
      effectivePower: number,
      effectiveChargePower: number,
      effectiveDischargePower: number,
      capacity: number,
    }, production: {
      powerRatio: number,
      hasDC: boolean,
      activePower: number, // sum of activePowerAC and activePowerDC
      activePowerAc: number,
      activePowerAcL1: number,
      activePowerAcL2: number,
      activePowerAcL3: number,
      activePowerDc: number,
      maxActivePower: number
    }, grid: {
      powerRatio: number,
      activePowerL1: number,
      activePowerL2: number,
      activePowerL3: number,
      buyActivePower: number,
      maxBuyActivePower: number,
      sellActivePower: number,
      sellActivePowerL1: number,
      sellActivePowerL2: number,
      sellActivePowerL3: number,
      maxSellActivePower: number,
      gridMode: number
    }, consumption: {
      powerRatio: number,
      activePower: number,
      activePowerL1: number,
      activePowerL2: number,
      activePowerL3: number
    }
  }

  export type NotificationType = "success" | "error" | "warning" | "info";

  export interface Notification {
    type: NotificationType;
    message: string;
    code?: number,
    params?: string[]
  }

  export type PeriodString = 'day' | 'week' | 'month' | 'year' | 'custom';

  export class HistoryPeriod {

    constructor(
      public from: Date = new Date(),
      public to: Date = new Date(),
    ) { }

    public getText(translate: TranslateService): string {
      if (isSameDay(this.from, this.to) && isSameDay(this.from, new Date())) {
        return translate.instant('Edge.History.Today') + ", " + format(new Date(), translate.instant('General.DateFormat'));
      }
      else if (isSameDay(this.from, this.to) && !isSameDay(this.from, subDays(new Date(), 1))) {
        switch (getDay(this.from)) {
          case 0: {
            return translate.instant('General.Week.Sunday') + ", " + translate.instant('Edge.History.SelectedDay', {
              value: format(this.from, translate.instant('General.DateFormat'))
            })
          }
          case 1: {
            return translate.instant('General.Week.Monday') + ", " + translate.instant('Edge.History.SelectedDay', {
              value: format(this.from, translate.instant('General.DateFormat'))
            })
          }
          case 2: {
            return translate.instant('General.Week.Tuesday') + ", " + translate.instant('Edge.History.SelectedDay', {
              value: format(this.from, translate.instant('General.DateFormat'))
            })
          }
          case 3: {
            return translate.instant('General.Week.Wednesday') + ", " + translate.instant('Edge.History.SelectedDay', {
              value: format(this.from, translate.instant('General.DateFormat'))
            })
          }
          case 4: {
            return translate.instant('General.Week.Thursday') + ", " + translate.instant('Edge.History.SelectedDay', {
              value: format(this.from, translate.instant('General.DateFormat'))
            })
          }
          case 5: {
            return translate.instant('General.Week.Friday') + ", " + translate.instant('Edge.History.SelectedDay', {
              value: format(this.from, translate.instant('General.DateFormat'))
            })
          }
          case 6: {
            return translate.instant('General.Week.Saturday') + ", " + translate.instant('Edge.History.SelectedDay', {
              value: format(this.from, translate.instant('General.DateFormat'))
            })
          }
        }
      }
      else if (isSameDay(this.from, this.to) && isSameDay(this.from, subDays(new Date(), 1))) {
        return translate.instant('Edge.History.Yesterday') + ", " + format(this.from, translate.instant('General.DateFormat'));
      } else if (differenceInDays(endOfYear(this.to), startOfYear(this.from)) == differenceInDays(this.to, this.from)) {
        return getYear(this.from).toString();
      } else if (isFirstDayOfMonth(this.from) && isLastDayOfMonth(this.to)) {
        switch (getMonth(this.from)) {
          case 0: {
            return translate.instant('General.Month.January') + ' ' + getYear(this.from);
          }
          case 1: {
            return translate.instant('General.Month.February') + ' ' + getYear(this.from);
          }
          case 2: {
            return translate.instant('General.Month.March') + ' ' + getYear(this.from);
          }
          case 3: {
            return translate.instant('General.Month.April') + ' ' + getYear(this.from);
          }
          case 4: {
            return translate.instant('General.Month.May') + ' ' + getYear(this.from);
          }
          case 5: {
            return translate.instant('General.Month.June') + ' ' + getYear(this.from);
          }
          case 6: {
            return translate.instant('General.Month.July') + ' ' + getYear(this.from);
          }
          case 7: {
            return translate.instant('General.Month.August') + ' ' + getYear(this.from);
          }
          case 8: {
            return translate.instant('General.Month.September') + ' ' + getYear(this.from);
          }
          case 9: {
            return translate.instant('General.Month.October') + ' ' + getYear(this.from);
          }
          case 10: {
            return translate.instant('General.Month.November') + ' ' + getYear(this.from);
          }
          case 11: {
            return translate.instant('General.Month.Dezember') + ' ' + getYear(this.from);
          }
        }
      }
      else {
        {
          return translate.instant(
            'General.PeriodFromTo', {
            value1: format(this.from, translate.instant('General.DateFormatShort')),
            value2: format(this.to, translate.instant('General.DateFormat'))
          })
        }
      }
    }
  }
}