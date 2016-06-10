import {Component, Injector} from 'angular2/core';
import {RouteConfig, Router, RouteParams} from 'angular2/router';
import {WorkflowDetailsService} from './workflow-details.service';
import {Feature, Workflow, Features} from './app.models';
import {Http} from 'angular2/http';

@Component({
    template: `
<h1 *ngIf="name">{{name}}</h1>
<a href="{{link}}" target="_myexperiments">{{link}}</a>
<div *ngIf="workflow">
    <strong>Workflow:</strong> 
    <span *ngIf="workflow.title">{{workflow.title}}</span>
    <span *ngIf="!workflow.title">{{workflow.name}}</span>
</div>
<div *ngIf="errorMessage" class="alert alert-danger" role="alert"><i class="fa fa-exclamation-triangle"></i> Error: {{errorMessage}}</div>
<div *ngIf="features">
    <p><strong>I/O port pairs:</strong></p>
    <ul>
    <li *ngFor="#pair of features.getPortpairs()">{{pair}}</li>
    </ul>
    <div *ngFor="#pair of features.getPortpairs()">
        <h4>{{pair}}</h4>
        <table class="table" >
            <tr *ngFor="#feature of features.getFeatures(pair)">
                <td>{{feature.l}}</td>                
                <td>{{feature.n}}</td>
                <td style="word-break: break-all;">{{feature.v}}</td>
                <td>{{feature.t}}</td>
            </tr>
        </table>
    </div>
</div>
`,
    providers: [WorkflowDetailsService]
})
export class WorkflowDetailsComponent {
    name : string;
    workflow: Workflow;
    link: string;
    features: Features;
    errorMessage: string;
    constructor(public http: Http, 
        private _params: RouteParams, 
        injector: Injector, 
        private _router: Router, 
        private _service: WorkflowDetailsService){ }     
    ngOnInit() {
        this.name = this._params.get('name');
        // workflow
        this.link = "http://www.myexperiment.org/workflows/" + this.name.slice(0, this.name.indexOf('-'));
        this._service.getWorkflow(this.name).subscribe(
            workflow => this.workflow = workflow,
            error => this.errorMessage = <any>error);
        // features
        this._service.getFeatures(this.name).subscribe(
            features => this.features = features,
            error => this.errorMessage = <any>error);
    }
}