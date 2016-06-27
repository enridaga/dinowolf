import {Component, Input, ViewChild} from 'angular2/core';
import {FeatureDialog} from './feature-dialog.component';
import {WorkflowDetailsService} from './workflow-details.service';
import {Features, Portpair, Feature} from './app.models';
import {Http} from 'angular2/http';

@Component({
    selector: 'features-list',
    template: `
    <h6>Features</h6>
    <div *ngIf="features" #featuresList>
      <div *ngFor="#feature of features.getFeatures(portpair.getId())">
          <strong>{{feature.l}}/{{feature.n}}</strong><br/>
          <span style="word-break: break-all;">{{feature.v | slice:0:100}}</span>
          <span *ngIf="feature.v.length > 100">...</span>
          <a *ngIf="feature.v.length > 100" class="btn btn-warning btn-xs pull-right"
           (click)="showFeatureDialog(feature)">More</a>
          <hr/>
      </div>
    </div>
    <feature-dialog></feature-dialog>
    `,
    styles: [`
      #featuresList {
        background-color: #EEE;
        border: 1px solid #DDD;
      }
    `],
    providers: [WorkflowDetailsService],
    directives: [FeatureDialog]})
export class FeaturesList {
    @Input() portpair : Portpair;
    @ViewChild(FeatureDialog) dialog : FeatureDialog;
    logs : Array<Object> ;
    errorMessage: any;
    features: Features;
    constructor(private _service: WorkflowDetailsService){}

    ngOnInit(){
      this._service.getFeatures(this.portpair.getBundle(), this.portpair.getId()).subscribe(
        features => this.features = features,
        error => this.errorMessage = <any>error);
    }

    showFeatureDialog(feature : Feature){
      this.dialog.showDialog(feature);
    }
}
