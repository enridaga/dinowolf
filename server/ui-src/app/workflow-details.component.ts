import {Component, Injector, ViewChild, ElementRef, AfterViewInit} from 'angular2/core';
import {RouteConfig, Router, RouteParams} from 'angular2/router';
import {WorkflowDetailsService} from './workflow-details.service';
import {Feature, Workflow, Features, Annotations, Portpair} from './app.models';
import {Http} from 'angular2/http';
declare var jQuery:JQueryStatic;

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
<div *ngIf="annotations">
    <img src="/service/myexperiments/image/{{name}}" >
    <p><strong>I/O port pairs:</strong></p>
    <table class="table">
    <thead>
      <tr>
        <th>Workflow</th>
        <th>Processor</th>
        <th>Port mapping</th>
        <th>Annotations</th>
      </tr>
    </thead>
    <tbody>
    <tr *ngFor="#pair of annotations.getPortpairs()">
      <td>{{pair.getWorkflow()}}</td>
      <td>{{pair.getProcessor()}}</td>
      <td>{{pair.getFrom()}} <span [innerHTML]="pair.getArrow()"></span> {{pair.getTo()}}</td>
      <td>
        <button *ngIf="!annotations.annotated(pair)"
        class="btn btn-default btn-sm" (click)="annotate(pair)">Annotate</button>
        <div *ngIf="annotations.getAnnotations(pair).length > 0">
            <span *ngFor="#ann of annotations.getAnnotations(pair)">
            <span class="" >{{ann}}</span>
            &nbsp;
            </span>
        </div>
      </td>
    </tr>
    </tbody>
    </table>

</div>
`,
    providers: [WorkflowDetailsService],
    directives: []
})
export class WorkflowDetailsComponent implements AfterViewInit{
    name : string;
    workflow: Workflow;
    link: string;
    features: Features;
    annotations: Annotations;
    errorMessage: string;
    selectedValue = '';
    //@ViewChild(AnnotateDialog) annotationDialog: AnnotateDialog;
    constructor(public http: Http,
        private _params: RouteParams,
        injector: Injector,
        private _router: Router,
        private _service: WorkflowDetailsService){ }
    annotate(pair: Portpair){
      //  this.annotationDialog.showDialog(pair);
      this._router.navigate( ['Annotation', { portpair: pair.getId() }] );
    }
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
        this._service.getAnnotations(this.name).subscribe(
            annotations => this.annotations = annotations,
            error => this.errorMessage = <any>error);
    }

    ngAfterViewInit() {

    }
}
