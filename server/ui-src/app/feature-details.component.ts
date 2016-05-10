import {Component, Injector} from 'angular2/core';
import {RouteConfig, Router, RouteParams} from 'angular2/router';
import {Http} from 'angular2/http';

@Component({
    template: `
<h1>Feature </h1>    
<div>feature details here</div>
`
})
export class FeatureDetailsComponent {
    id : string;
    constructor(public http: Http, private _params: RouteParams, injector: Injector, private _router: Router){
       console.log("feature constructor", _params.get('id'));
   }     
    ngOnInit() {
        this.id = this._params.get('id');
    }
}