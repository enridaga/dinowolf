import { Component } from 'angular2/core';
import {Portpair, Rule, Datanode, Annotator} from './app.models';
import {DatanodeService} from './datanode.service';
import {FeaturesList} from './features-list.component';
import {RecommendationsList} from './recommendations-list.component';
import {AnnotateService} from './annotate.service';
import {Router,RouteParams} from 'angular2/router';

@Component({
    template: `
<div class="annotate-view">
  <h1>{{portpair.getFrom()}} <span [innerHTML]="portpair.getArrow()"></span> {{portpair.getTo()}}</h1>

  <div class="row">
    <div class="col-lg-6">
      <span class="pull-left label label-default pull-left">{{elapsedTimeSoFar/1000}}s</span>
    </div>
    <div class="col-lg-6">
      <div class="pull-right">
        <button type="button" [disabled]="noSelected()" class="btn btn-danger" (click)="saveData()">Annotate!</button>
        <button type="button" [disabled]="hasSelected()" class="btn btn-warning" (click)="saveIgnore()">Ignore.</button>
        <button type="button" class="btn btn-default" (click)="saveSkipped()">Later.</button>
      </div>
    </div>
  </div>
<hr/>
  <div class="row">
    <div class="col-lg-4">
      <h6>Image</h6>
      <img class="img-rounded img-responsive" (click)="'/service/myexperiments/image/'+bundle" src="/service/myexperiments/image/{{bundle}}">
    </div>
    <features-list  class="col-lg-4" *ngIf="portpair" [portpair]="portpair"></features-list>
    <recommendations-list class="col-lg-4" *ngIf="rules" [rules]="rules" [annotator]="annotator" [datanode]="datanode"></recommendations-list>
  </div>
<hr/>
    <div class="row">
      <div class="col-lg-6">
        <span class="label label-default pull-left">{{elapsedTimeSoFar}}</span>
      </div>
      <div>
        <div class="pull-right">
          <button type="button" [disabled]="noSelected()" class="btn btn-danger" (click)="saveData()">Annotate!</button>
          <button type="button" [disabled]="hasSelected()" class="btn btn-warning" (click)="saveIgnore()">Ignore.</button>
          <button type="button" class="btn btn-default" (click)="saveSkipped()">Later.</button>
        </div>
      </div>
    </div>
</div>

    `,
    providers: [DatanodeService, AnnotateService],
    directives: [FeaturesList,RecommendationsList],
    styles: [`
      .annotate-view {
        padding-bottom: 5em;
      }
      `]
})
export class AnnotateComponent implements Annotator
{
    private startTime: number;
    private elapsedTimeSoFar: number;
    private intervalID:number;
    private portpair: Portpair;
    public isVisible: boolean;
    private errorMessage:string;
    annotator : Annotator = this;
    bundle:string;
    relations: Array<string>;
    rules: Array<Rule>;
    public selections: Object;
    datanode: Datanode;

    constructor(
      private _datanodeService : DatanodeService,
      private _annotateService: AnnotateService,
      private _router: Router,
      private _params: RouteParams){
        this._datanodeService.datanode().subscribe(
          datanode => this.datanode = datanode,
          error => this.errorMessage = <any>error);
    }

    recommended():Array<Rule>{
      let recommended = new Array<Rule>();
      let best = {};
      for(let x in this.rules){
        let id = this.rules[x].head.join(',');
        if(typeof best[id] === 'undefined'){
          best[id] = this.rules[x];
        }else{
          let cmp = best[id];
          if(cmp.relativeConfidence < this.rules[x].relativeConfidence){
            best[id] = this.rules[x];
          }
        }
      }
      for(let x in best){
        recommended.push(best[x]);
      }
      recommended.sort(function(a,b){
        // higher values on top
        if(a.relativeConfidence > b.relativeConfidence)
          return -1;
        if(a.relativeConfidence < b.relativeConfidence)
          return 1;
        if(a.confidence > b.confidence)
          return -1;
        if(a.confidence < b.confidence)
          return 1;
        if(a.support > b.support)
          return -1;
        if(a.support < b.support)
          return 1;
        return 0;
      });
      return recommended;
    }

    unrecommended():Array<string>{
      return this.relations;
    }

    cancel(){
      this.gotoWorkflow();
    }

    ngOnDestroy(){
      console.log('on destroy');
      window.clearInterval(this.intervalID);
    }

    ngOnInit(){
      this.startTime = new Date().getTime();
      let t = this;
      this.intervalID = setInterval(function(){t.elapsedTimeSoFar = t.elapsedTime(); }, 1000);
      this.portpair = new Portpair(this._params.get('portpair'));
      this.bundle = this.portpair.getBundle();
      this.selections = new Object();
      this._annotateService.recommend(this.portpair).subscribe(
        rules => this.rules = rules,
        error => this.errorMessage = <any>error
      );
      this._datanodeService.list().subscribe(
          relations => this.relations = relations,
          error => this.errorMessage = <any>error);
    }

    toggleSelection(relation){
      if(this.selections[relation.id] == null){
        this.selections[relation.id] = true;
      }else{
        this.selections[relation.id] = !this.selections[relation.id];
      }
    }

    selected(relation){
      if(this.selections[relation.id] == null){
        this.selections[relation.id] = false;
      }
      return this.selections[relation.id] ;
    }

    unselected(relation){
      return !this.selected(relation);
    }

    allSelected(){
      let selected = new Array<string>();
      for(let rel in this.selections){
        if(this.selections[rel]){
          selected.push(rel);
        }
      }
      return selected;
    }

    elapsedTime():number{
      return new Date().getTime() - this.startTime ;
    }

    saveData(){
      let annotations = this.allSelected();
      let o = this;
      this._annotateService.saveAnnotations(this.portpair, annotations, this.elapsedTime()).subscribe(
          function(){o.gotoWorkflow();},
          error => this.errorMessage = <any>error);
    }

    saveIgnore(){
      let o = this;
      this._annotateService.noAnnotations(this.portpair, [], this.elapsedTime()).subscribe(
          function(){o.gotoWorkflow();},
          error => this.errorMessage = <any>error);
    }

    saveSkipped(){
      let o = this;
      this._annotateService.skipAnnotations(this.portpair, [], this.elapsedTime()).subscribe(
          function(){o.gotoWorkflow();},
          error => this.errorMessage = <any>error);
    }

    gotoWorkflow(){
      this._router.navigate( ['Workflow', { name: this.portpair.getBundle() }]);
    }

    noSelected(){
      return this.allSelected().length == 0;
    }

    hasSelected(){
      return !this.noSelected();
    }

    select(relation){
      this.selection(relation, true);
    }

    selection(relations:any, state:boolean){
      if(typeof relations !== 'Array'){
        relations = [relations];
      }
      for(let x in relations){
        let relation = relations[x];
        if(relation){
          if(typeof relation === 'string'){
            this.selections[relation] = state;
          }else if(typeof relation === 'object' && relation['id']){
            this.selections[relation.id] = state;
          }
        }
      }
    }

    unselect(relation){
      this.selection(relation, false);
    }

    pick(r:Rule){
      r.picked = !r.picked;
    }

    picked(r:Rule){
      return r.picked;
    }

    unpicked(r:Rule){
      return !r.picked;
    }
}
