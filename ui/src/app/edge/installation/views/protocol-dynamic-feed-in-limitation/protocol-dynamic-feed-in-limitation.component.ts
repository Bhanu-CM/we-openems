import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { FormlyFieldConfig } from '@ngx-formly/core';
import { InstallationData } from '../../installation.component';

export enum FeedInSetting {
  QuEnableCurve = "QU_ENABLE_CURVE",
  PuEnableCurve = "PU_ENABLE_CURVE",
  FixedPowerFactor = "FIXED_POWER_FACTOR",
  // Lagging Power Factor
  Lagging_0_80 = "LAGGING_0_80",
  Lagging_0_81 = "LAGGING_0_81",
  Lagging_0_82 = "LAGGING_0_82",
  Lagging_0_83 = "LAGGING_0_83",
  Lagging_0_84 = "LAGGING_0_84",
  Lagging_0_85 = "LAGGING_0_85",
  Lagging_0_86 = "LAGGING_0_86",
  Lagging_0_87 = "LAGGING_0_87",
  Lagging_0_88 = "LAGGING_0_88",
  Lagging_0_89 = "LAGGING_0_89",
  Lagging_0_90 = "LAGGING_0_90",
  Lagging_0_91 = "LAGGING_0_91",
  Lagging_0_92 = "LAGGING_0_92",
  Lagging_0_93 = "LAGGING_0_93",
  Lagging_0_94 = "LAGGING_0_94",
  Lagging_0_95 = "LAGGING_0_95",
  Lagging_0_96 = "LAGGING_0_96",
  Lagging_0_97 = "LAGGING_0_97",
  Lagging_0_98 = "LAGGING_0_98",
  Lagging_0_99 = "LAGGING_0_99",
  // Leading Power Factor
  Leading_0_80 = "LEADING_0_80",
  Leading_0_81 = "LEADING_0_81",
  Leading_0_82 = "LEADING_0_82",
  Leading_0_83 = "LEADING_0_83",
  Leading_0_84 = "LEADING_0_84",
  Leading_0_85 = "LEADING_0_85",
  Leading_0_86 = "LEADING_0_86",
  Leading_0_87 = "LEADING_0_87",
  Leading_0_88 = "LEADING_0_88",
  Leading_0_89 = "LEADING_0_89",
  Leading_0_90 = "LEADING_0_90",
  Leading_0_91 = "LEADING_0_91",
  Leading_0_92 = "LEADING_0_92",
  Leading_0_93 = "LEADING_0_93",
  Leading_0_94 = "LEADING_0_94",
  Leading_0_95 = "LEADING_0_95",
  Leading_0_96 = "LEADING_0_96",
  Leading_0_97 = "LEADING_0_97",
  Leading_0_98 = "LEADING_0_98",
  Leading_0_99 = "LEADING_0_99",
  Leading_1 = "LEADING_1"
}

@Component({
  selector: ProtocolDynamicFeedInLimitation.SELECTOR,
  templateUrl: './protocol-dynamic-feed-in-limitation.component.html'
})
export class ProtocolDynamicFeedInLimitation implements OnInit {

  private static readonly SELECTOR = "protocol-dynamic-feed-in-limitation";

  @Input() public installationData: InstallationData;

  @Output() public previousViewEvent: EventEmitter<any> = new EventEmitter();
  @Output() public nextViewEvent = new EventEmitter<InstallationData>();

  public form: FormGroup;
  public fields: FormlyFieldConfig[];
  public model;

  constructor() { }

  public ngOnInit() {

    this.form = new FormGroup({});
    this.fields = this.getFields();
    this.model = this.installationData.batteryInverter.dynamicFeedInLimitation ?? {};

  }

  public onPreviousClicked() {

    this.previousViewEvent.emit();

  }

  public onNextClicked() {

    if (this.form.invalid) {
      return;
    }

    this.installationData.batteryInverter.dynamicFeedInLimitation = this.model;

    this.nextViewEvent.emit(this.installationData);

  }

  public getFields(): FormlyFieldConfig[] {

    let fields: FormlyFieldConfig[] = [];

    let pv = this.installationData.pv;
    let totalPvPower: number = 0;

    totalPvPower += (pv.dc1.isSelected ? pv.dc1.value : 0);
    totalPvPower += (pv.dc2.isSelected ? pv.dc2.value : 0);

    for (let ac of pv.ac) {
      totalPvPower += ac.value;
    }

    fields.push({
      key: "maximumFeedInPower",
      type: "input",
      templateOptions: {
        type: "number",
        label: "Maximale Einspeiseleistung [W]",
        description: "Diesen Wert entnehmen Sie der Anschlussbestätigung des Netzbetreibers",
        required: true
      },
      parsers: [Number],
      defaultValue: parseInt((totalPvPower * 0.7).toFixed(0))
    });

    fields.push({
      key: "feedInSetting",
      type: "radio",
      templateOptions: {
        label: "Typ",
        description: "Wirkleistungsreduzierung bei Überfrequenz",
        options: [
          { label: "Blindleistungs-Spannungskennlinie Q(U)", value: FeedInSetting.QuEnableCurve },
          { label: "Verschiebungsfaktor-/Wirkleistungskennlinie Cos φ (P)", value: FeedInSetting.PuEnableCurve },
          { label: "Fester Verschiebungsfaktor Cos φ", value: FeedInSetting.FixedPowerFactor }
        ],
        required: true
      }
    });

    fields.push({
      key: "fixedPowerFactor",
      type: "select",
      templateOptions: {
        label: "Cos φ Festwert",
        options: [
          // Lagging
          { label: "0.80 Lagging", value: FeedInSetting.Lagging_0_80 },
          { label: "0.81 Lagging", value: FeedInSetting.Lagging_0_81 },
          { label: "0.82 Lagging", value: FeedInSetting.Lagging_0_82 },
          { label: "0.83 Lagging", value: FeedInSetting.Lagging_0_83 },
          { label: "0.84 Lagging", value: FeedInSetting.Lagging_0_84 },
          { label: "0.85 Lagging", value: FeedInSetting.Lagging_0_85 },
          { label: "0.86 Lagging", value: FeedInSetting.Lagging_0_86 },
          { label: "0.87 Lagging", value: FeedInSetting.Lagging_0_87 },
          { label: "0.88 Lagging", value: FeedInSetting.Lagging_0_88 },
          { label: "0.89 Lagging", value: FeedInSetting.Lagging_0_89 },
          { label: "0.90 Lagging", value: FeedInSetting.Lagging_0_90 },
          { label: "0.91 Lagging", value: FeedInSetting.Lagging_0_91 },
          { label: "0.92 Lagging", value: FeedInSetting.Lagging_0_92 },
          { label: "0.93 Lagging", value: FeedInSetting.Lagging_0_93 },
          { label: "0.94 Lagging", value: FeedInSetting.Lagging_0_94 },
          { label: "0.95 Lagging", value: FeedInSetting.Lagging_0_95 },
          { label: "0.96 Lagging", value: FeedInSetting.Lagging_0_96 },
          { label: "0.97 Lagging", value: FeedInSetting.Lagging_0_97 },
          { label: "0.98 Lagging", value: FeedInSetting.Lagging_0_98 },
          { label: "0.99 Lagging", value: FeedInSetting.Lagging_0_99 },
          // Leading
          { label: "0.80 Leading", value: FeedInSetting.Leading_0_80 },
          { label: "0.81 Leading", value: FeedInSetting.Leading_0_81 },
          { label: "0.82 Leading", value: FeedInSetting.Leading_0_82 },
          { label: "0.83 Leading", value: FeedInSetting.Leading_0_83 },
          { label: "0.84 Leading", value: FeedInSetting.Leading_0_84 },
          { label: "0.85 Leading", value: FeedInSetting.Leading_0_85 },
          { label: "0.86 Leading", value: FeedInSetting.Leading_0_86 },
          { label: "0.87 Leading", value: FeedInSetting.Leading_0_87 },
          { label: "0.88 Leading", value: FeedInSetting.Leading_0_88 },
          { label: "0.89 Leading", value: FeedInSetting.Leading_0_89 },
          { label: "0.90 Leading", value: FeedInSetting.Leading_0_90 },
          { label: "0.91 Leading", value: FeedInSetting.Leading_0_91 },
          { label: "0.92 Leading", value: FeedInSetting.Leading_0_92 },
          { label: "0.93 Leading", value: FeedInSetting.Leading_0_93 },
          { label: "0.94 Leading", value: FeedInSetting.Leading_0_94 },
          { label: "0.95 Leading", value: FeedInSetting.Leading_0_95 },
          { label: "0.96 Leading", value: FeedInSetting.Leading_0_96 },
          { label: "0.97 Leading", value: FeedInSetting.Leading_0_97 },
          { label: "0.98 Leading", value: FeedInSetting.Leading_0_98 },
          { label: "0.99 Leading", value: FeedInSetting.Leading_0_99 },
          { label: "1 Leading", value: FeedInSetting.Leading_1 }
        ],
        required: true
      },
      hideExpression: model => model.feedInSetting !== FeedInSetting.FixedPowerFactor
    });

    return fields;

  }

}