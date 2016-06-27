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
    <table class="table table-compressed">
      <thead>
        <tr>
          <th>ID</th>
          <th>Annotation</th>
          <th>Action</th>
          <th>Duration</th>
          <th>From recommandations</th>
          <th>AVG Rank</th>
          <th>AVG Relevance</th>
        </tr>
      </thead>
      <tbody>
        <tr *ngFor="#log of logs">
        <td>#{{log.logid}}</td>
          <td><small><a (click)="gotoBundle(log.bundle)">{{log.bundle}}</a></small><br/>
          <span [innerHTML]="asPortPair(log.portpair).getLabel()"></span><br/>
          <em *ngFor="#ann of log.annotations">{{ann}} </em></td>
          <td>{{log.action}}</td>
          <td>{{log.duration / 1000}}s</td>
          <td>{{log.fromrec}}/{{log.count}}</td>
          <td>{{log.avgrank}}</td>
          <td>{{log.avgrel}}</td>
        </tr>
      </tbody>
    </table>
    `,
    providers: [LogsService],
    directives: []})
export class LogsComponent {
    title = 'Logs';
    logs : Array<Object> ;
    errorMessage: any;
    constructor(private _logsService: LogsService,
    private _router: Router){}
    ngOnInit(){
      this._logsService.getLogs().subscribe(
          logs => this.logs = logs,
          error => this.errorMessage = <any>error);
          console.log('logs', this.logs);
    }

    asPortPair(pp:string) : Portpair {
      return new Portpair(pp);
    }
    gotoBundle(bundle: string) {
        this._router.navigate( ['Workflow', { name: bundle }] );
    }
}
