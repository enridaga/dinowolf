import {Component, Injector} from 'angular2/core';
import {RouteConfig, Router, RouteParams} from 'angular2/router';
import {WorkflowDetailsService} from './workflow-details.service';
import {Feature, Workflow, Features} from './app.models';
import {Http} from 'angular2/http';

@Component({
    template: `
<h1 *ngIf="workflow"><span *ngIf="workflow.title">{{workflow.title}}</span><span *ngIf="!workflow.title">{{workflow.name}}</span></h1>
<div *ngIf="errorMessage" class="alert alert-danger" role="alert"><i class="fa fa-exclamation-triangle"></i> Error: {{errorMessage}}</div>
<div *ngIf="features">
    <div *ngFor="#pair of features.getPortpairs()">
        <h4>{{pair}}</h4>
        <table class="table" >
            <tr *ngFor="#feature of features.getFeatures(pair)">
                <td>{{feature.n}}</td>
                <td>{{feature.v}}</td>
                <td>{{feature.l}}</td>
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
    features: Features;
    errorMessage: string;
    constructor(public http: Http, 
        private _params: RouteParams, 
        injector: Injector, 
        private _router: Router, 
        private _service: WorkflowDetailsService){ }     
    ngOnInit() {
        name = this._params.get('name');
        // workflow
        this._service.getWorkflow(name).subscribe(
            workflow => this.workflow = workflow,
            error => this.errorMessage = <any>error);
        // features
        this._service.getFeatures(name).subscribe(
            features => this.features = features,
            error => this.errorMessage = <any>error);
        
    }
}