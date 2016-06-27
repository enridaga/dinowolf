import { Component } from 'angular2/core';
import {Portpair, Rule, Datanode, Annotator} from './app.models';
import {DatanodeService} from './datanode.service';
import {LogsService} from './logs.service';
import {DatanodeItem} from './datanode-item.component';
import {AnnotateService} from './annotate.service';
import {Router,RouteParams} from 'angular2/router';

@Component({
    template: `
    <h1>{{title}}</h1>
    <table class="table">
      <tbody>
        <tr *ngFor="#log of annotations">
          <td><small><a (click)="gotoBundle(log.bundle)">{{log.bundle}}</a></small><br/>
          <span [innerHTML]="asPortPair(log.portpair).getLabel()"></span></td>
          <td><span *ngFor="#ann of log.annotations">{{ann}}<br/></span></td>
        </tr>
      </tbody>
    </table>
    `,
    providers: [LogsService],
    directives: []})
export class AnnotationsComponent {
    title = 'Annotations';
    annotations : Array<Object> ;
    errorMessage: any;
    constructor(private _logsService: LogsService,
    private _router: Router){}
    ngOnInit(){
      this._logsService.getAnnotations().subscribe(
          annotations => this.annotations = annotations,
          error => this.errorMessage = <any>error);
          console.log('annotations', this.annotations);
    }

    asPortPair(pp:string):Portpair{
      return new Portpair(pp);
    }

    gotoBundle(bundle: string){
        this._router.navigate( ['Workflow', { name: bundle }] );
    }
}
