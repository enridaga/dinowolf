import {Component, Injector} from 'angular2/core';
import {RouteConfig, Router, RouteParams} from 'angular2/router';
import {Http} from 'angular2/http';

@Component({
    template: `
<h1>{{title}}</h1>    
<div>workflow details here</div>
`
})
export class WorkflowDetailsComponent {
    title = 'Workflow';
    name : string;
    constructor(public http: Http, private _params: RouteParams, injector: Injector, private _router: Router){
       console.log("workflow constructor", _params.get('name'));
   }     
    ngOnInit() {
        name = this._params.get('name');
    }
}