import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormGroup, Validators } from '@angular/forms';
import { FormlyFieldConfig } from '@ngx-formly/core';
import { COUNTRY_OPTIONS, InstallationData } from '../../installation.component';

@Component({
  selector: ProtocolCustomerComponent.SELECTOR,
  templateUrl: './protocol-customer.component.html'
})
export class ProtocolCustomerComponent implements OnInit {

  private static readonly SELECTOR = "protocol-customer";

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
    this.model = this.installationData.customer ?? {
      isCorporateClient: false
    };

  }

  public onPreviousClicked() {

    this.previousViewEvent.emit();

  }

  public onNextClicked() {

    if (this.form.invalid) {
      return;
    }

    this.installationData.customer = this.model;

    this.nextViewEvent.emit(this.installationData);

  }

  public getFields(): FormlyFieldConfig[] {

    let fields: FormlyFieldConfig[] = [];

    fields.push({
      key: "isCorporateClient",
      type: "checkbox",
      templateOptions: {
        label: "Firmenkunde?",
        required: true
      }
    });

    fields.push({
      key: "companyName",
      type: "input",
      templateOptions: {
        label: "Firmenname",
        required: true
      },
      hideExpression: model => !model.isCorporateClient
    });

    fields.push({
      key: "lastName",
      type: "input",
      templateOptions: {
        label: "Nachname",
        required: true
      }
    });

    fields.push({
      key: "firstName",
      type: "input",
      templateOptions: {
        label: "Vorname",
        required: true
      }
    });

    fields.push({
      key: "street",
      type: "input",
      templateOptions: {
        label: "Straße / Hausnummer",
        required: true
      }
    });

    fields.push({
      key: "zip",
      type: "input",
      templateOptions: {
        label: "PLZ",
        required: true
      },
      validators: {
        validation: ["zip"]
      }
    });

    fields.push({
      key: "city",
      type: "input",
      templateOptions: {
        label: "Ort",
        required: true
      }
    });

    fields.push({
      key: "country",
      type: "select",
      templateOptions: {
        label: "Land",
        required: true,
        options: COUNTRY_OPTIONS
      }
    });

    fields.push({
      fieldGroup: [
        {
          key: "email",
          type: "input",
          templateOptions: {
            type: "email",
            label: "E-Mail-Adresse",
            description: "Wird zum Anlegen des persönlichen Zugangs verwendet",
            required: true
          },
          validators: {
            validation: [Validators.email]
          }
        },
        {
          key: "emailConfirm",
          type: "input",
          templateOptions: {
            type: "email",
            label: "E-Mail-Adresse",
            description: "Wiederholen",
            required: true
          }
        }
      ],
      validators: {
        validation: [
          { name: "emailMatch", options: { errorPath: "emailConfirm" } },
        ],
      }
    });

    fields.push({
      key: "phone",
      type: "input",
      templateOptions: {
        label: "Telefonnummer",
        required: true
      }
    });

    return fields;

  }

}