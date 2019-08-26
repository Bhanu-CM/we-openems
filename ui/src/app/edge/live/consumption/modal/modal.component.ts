import { Component, Input } from '@angular/core';
<<<<<<< HEAD
import { Edge, Service, Websocket, EdgeConfig, ChannelAddress } from '../../../../shared/shared';
=======
import { Edge, Service, Websocket, EdgeConfig } from '../../../../shared/shared';
>>>>>>> develop
import { ModalController } from '@ionic/angular';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'consumption-modal',
    templateUrl: './modal.component.html'
})
export class ConsumptionModalComponent {

    private static readonly SELECTOR = "consumption-modal";

    @Input() edge: Edge;
    @Input() evcsComponents;

    public config: EdgeConfig = null;

    constructor(
        public service: Service,
        private websocket: Websocket,
        public modalCtrl: ModalController,
        private route: ActivatedRoute,
    ) { }

    ngOnInit() {
    }

    ngOnDestroy() {
        if (this.edge != null) {
            this.edge.unsubscribeChannels(this.websocket, ConsumptionModalComponent.SELECTOR);
        }
    }
}