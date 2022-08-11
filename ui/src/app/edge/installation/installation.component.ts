import { Component, OnInit } from '@angular/core';
import { Edge, Service, Websocket } from 'src/app/shared/shared';
import { Router } from '@angular/router';
import { filter, take } from 'rxjs/operators';
import { AbstractIbn, View } from './installation-systems/abstract-ibn';
import { GeneralIbn } from './installation-systems/general-ibn';
import { HomeFeneconIbn } from './installation-systems/home/home-fenecon';
import { Commercial30AnschlussIbn } from './installation-systems/commercial/commercial-30/commercial30-anschluss';
import { Commercial30NetztrennIbn } from './installation-systems/commercial/commercial-30/commercial30-netztrenn';
import { HomeHeckertIbn } from './installation-systems/home/home-heckert';
import { Commercial50EigenverbrauchsOptimierung } from './installation-systems/commercial/commercial-50/commercial50-eigenverbrauchsoptimierung';
import { Commercial50Lastspitzenkappung } from './installation-systems/commercial/commercial-50/commercial50-lastspitzenkappung';

export const COUNTRY_OPTIONS: { value: string; label: string }[] = [
  { value: 'de', label: 'Deutschland' },
  { value: 'at', label: 'Österreich' },
  { value: 'ch', label: 'Schweiz' },
];

@Component({
  selector: InstallationComponent.SELECTOR,
  templateUrl: './installation.component.html',
})
export class InstallationComponent implements OnInit {
  private static readonly SELECTOR = 'installation';

  public ibn: AbstractIbn | null = null;
  public progressValue: number;
  public progressText: string;
  public edge: Edge = null;
  public displayedView: View;
  public readonly view = View;
  public spinnerId: string;

  constructor(
    private service: Service,
    private router: Router,
    public websocket: Websocket
  ) { }

  public ngOnInit() {
    this.service.currentPageTitle = 'Installation';
    this.spinnerId = 'installation-websocket-spinner';
    this.service.startSpinner(this.spinnerId);
    let ibn: AbstractIbn = null;
    let viewIndex: number;

    // Determine view index
    if (sessionStorage?.viewIndex) {
      // 10 is given as radix parameter.
      // 2 = binary, 8 = octal, 10 = decimal, 16 = hexadecimal.
      viewIndex = parseInt(sessionStorage.viewIndex, 10);
    } else {
      viewIndex = 0;
    }

    // Load 'Ibn' and 'edge' If it is available from session storage.
    if (sessionStorage?.edge) {
      // Ibn is added in second view.
      if (sessionStorage.ibn) {
        const ibnString = JSON.parse(sessionStorage.getItem('ibn'));
        const systemId = ibnString.id;

        // Load the specific Ibn implementation. and copy to the indivual fileds.
        // Copying the plain Json string does not recognize particular Ibn functions.
        // So we have to mention what type of implementation it is.
        // This is helpful particularly if installer does the refresh in between views.
        ibn = this.getIbnType(systemId);
        ibn.views = ibnString.views ?? [];
        ibn.customer = ibnString.customer ?? {};
        ibn.installer = ibnString.installer ?? {};
        ibn.location = ibnString.location ?? {};
        ibn.requiredControllerIds = ibnString.requiredControllerIds ?? [];
        ibn.lineSideMeterFuse = ibnString.lineSideMeterFuse ?? {};
        ibn.feedInLimitation = ibnString.feedInLimitation ?? {};
        ibn.pv = ibnString.pv ?? {};

        // Applies only for COmmercial-50.
        if (ibnString.commercial50Feature) {
          ibn.setCommercialfeature(ibnString.commercial50Feature);
        }
      }
    }

    // Load Ibn with 'General Ibn' data initially.
    if (ibn === null) {
      ibn = new GeneralIbn();
    }
    // Load it in the global Ibn from local.
    this.setIbn(ibn);

    // display view after loading edge
    // => update view needs to get removed if version is to low
    if (sessionStorage?.edge) {
      // The prototype can't be saved as JSON,
      // so it has to get instantiated here again)
      const edgeString = JSON.parse(sessionStorage.getItem('edge'));
      this.service.metadata
        .pipe(
          filter(metadata => metadata != null),
          take(1))
        .subscribe(metadata => {
          this.edge = metadata.edges[edgeString.id];
          this.displayViewAtIndex(viewIndex);
        });
    } else {
      this.displayViewAtIndex(viewIndex);
    }
  }

  /**
   * Retrieves the Ibn implementation specific to the system.
   *
   * @returns Specific Ibn object
   */
  public getIbnType(systemId: string): AbstractIbn {
    switch (systemId) {
      case 'general':
        return new GeneralIbn();
      case 'home':
        return new HomeFeneconIbn();
      case 'heckert':
        return new HomeHeckertIbn();
      case 'commercial-30-anschluss':
        return new Commercial30AnschlussIbn();
      case 'commercial-30-netztrennstelle':
        return new Commercial30NetztrennIbn();
      case 'commercial-50-eigenverbrauchsoptimierung':
        return new Commercial50EigenverbrauchsOptimierung();
      case 'commercial-50-lastspitzenkappung':
        return new Commercial50Lastspitzenkappung();
    }
  }

  /**
   * Determines the index of the current view in Ibn.
   *
   * @param view current view.
   * @returns the index of the current view.
   */
  public getViewIndex(view: View): number {
    return this.ibn.views.indexOf(view);
  }

  /**
   * Displays the view based on the index.
   *
   * @param index index of the desired view.
   */
  public displayViewAtIndex(index: number) {
    this.removeUpdateView();
    const viewCount = this.ibn.views.length;
    if (index >= 0 && index < viewCount) {
      this.displayedView = this.ibn.views[index];
      this.progressValue = viewCount === 0 ? 0 : index / (viewCount - 1);

      // Till the initial system and components are selected show only current page number.
      // The view count changes based on the components selected.
      this.progressText = this.ibn.showViewCount ? 'Schritt ' + (index + 1) +
        ' von ' + viewCount : 'Schritt ' + (index + 1);

      if (sessionStorage) {
        sessionStorage.setItem('viewIndex', index.toString());
      }

      // When clicking next on the last view
    } else if (index === viewCount) {
      // Navigate to online monitoring of the edge
      this.router.navigate(['device', this.edge.id]);

      // Clear session storage
      sessionStorage.clear();
    } else {
      console.warn('The given view index is out of bounds.');
    }
  }

  /**
   * Displays the previous view.
   */
  public displayPreviousView() {
    this.displayViewAtIndex(this.getViewIndex(this.displayedView) - 1);
  }

  /**
   * Displays the Next view.
   */
  public displayNextView(ibn?: AbstractIbn) {

    // Stores the Ibn locally
    if (ibn) {
      this.setIbn(ibn);
      if (sessionStorage) {
        sessionStorage.setItem('ibn', JSON.stringify(ibn));
      }
    }

    this.displayViewAtIndex(this.getViewIndex(this.displayedView) + 1);
  }

  private setIbn(ibn: AbstractIbn | null) {
    this.ibn = ibn;
  }

  /**
   * Removes the update view if the version is not at least '2021.19.1'.
   */
  private removeUpdateView() {
    // TODO remove when every edge starts with at least the required version
    // only show update view if the update requests are implemented 
    if (!this.edge) {
      return;
    }
    if (!this.edge.isVersionAtLeast('2021.19.1')) {
      let indexOfUpdate = this.ibn.views.indexOf(View.PreInstallationUpdate)
      if (indexOfUpdate != -1) {
        this.ibn.views.splice(indexOfUpdate, 1)
      }
    }
  }

}
