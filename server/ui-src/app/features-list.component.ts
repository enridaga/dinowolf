import {Component, Input, ViewChild} from 'angular2/core';
import {FeatureDialog} from './feature-dialog.component';
import {WorkflowDetailsService} from './workflow-details.service';
import {Features, Portpair, Feature} from './app.models';
import {Http} from 'angular2/http';

@Component({
    selector: 'features-list',
    template: `
    <h5>Features</h5>
    <div *ngIf="features" class="featuresList">
      <div *ngFor="#feature of features.getFeatures(portpair.getId())">
          <strong>{{feature.l}}/{{feature.n}}</strong><br/>
          <a style="word-break: break-all;" target="_dbpedia" *ngIf="feature.v.startsWith('http:')" [href]="feature.v"
          [innerHTML]="(feature.v.indexOf('#') != -1)?feature.v.substr(feature.v.lastIndexOf('#')+1):feature.v.substr(feature.v.lastIndexOf('/')+1)"></a>
          <span style="word-break: break-all;" *ngIf="!feature.v.startsWith('http:')">{{feature.v | slice:0:100}}</span>
          <span *ngIf="feature.v.length > 100">...</span>
          <a *ngIf="feature.v.length > 100" class="btn btn-default btn-xs pull-right"
           (click)="showFeatureDialog(feature)"><i class="fa fa-expand" aria-hidden="true" aria-label="Expand"></i></a>
      </div>
    <!--  <div *ngFor="#group of features.getGroups(portpair.getId())">
          <strong>{{group}}</strong><br/>
          <ul>
            <li *ngFor="#feature of features.getGroup(portpair.getId(), group)"
            style="word-break: break-all;">{{feature.v | slice:0:100}}&nbsp;
            <span *ngIf="feature.v.length > 100">...</span>
            <a *ngIf="feature.v.length > 100" class="btn btn-warning btn-xs pull-right"
             (click)="showFeatureDialog(feature)">More</a>
            </li>
          </ul>
      </div> -->
    </div>
    <feature-dialog></feature-dialog>
    `,
    styles: [`
      .featuresList ul {
        margin: 1em;
        padding: 0em;
      }
      .featuresList ul li {
        margin:0px;
        list-style:none;
        padding-left:0;
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
