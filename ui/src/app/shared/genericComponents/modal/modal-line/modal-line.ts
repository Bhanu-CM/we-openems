import { Component, Input } from "@angular/core";
import { AbstractModalLine } from "../abstract-modal-line";

@Component({
    selector: 'oe-modal-line',
    templateUrl: './modal-line.html',
})
export class ModalLine extends AbstractModalLine {

    // Width of Left Column, Right Column is (100% - leftColumn)
    @Input()
    leftColumnWidth: number;

    /** ControlName for Form Field */
    @Input() controlName: string;

    /** ControlName for Toggle Button */
    @Input() controlType: 'TOGGLE' | 'INPUT';
}