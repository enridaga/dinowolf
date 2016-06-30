import {Component, Input, ViewChild} from 'angular2/core';
import {RecommendationDialog} from './recommendation-dialog.component';
import {DatanodeItem} from './datanode-item.component';
import {WorkflowDetailsService} from './workflow-details.service';
import {Rule,Datanode, Annotator} from './app.models';

@Component({
    selector: 'recommendations-list',
    template: `
    <h5>Annotations</h5>
    <div *ngIf="rules" #recommendationsList>
      <div *ngFor="#rule of recommended()">
          <datanode-item *ngFor="#item of rule.head" [root]="item" [annotator]="annotator" [datanode]="datanode" [item]="item"></datanode-item>
          <small>{{rule.support.toFixed(2)}} / {{rule.confidence.toFixed(2)}} / {{rule.relativeConfidence.toFixed(2)}}</small>
          <a class="btn btn-warning btn-xs pull-right"
           (click)="showRecommendationDialog(rule)">More</a>
          <hr/>
      </div>
      <div>
        <datanode-item [datanode]="datanode" [annotator]="annotator" [item]="'relatedWith'"></datanode-item>
      </div>
    </div>
    <recommendation-dialog></recommendation-dialog>
    `,
    styles: [`
      #recommendationsList {
        background-color: #EEE;
        border: 1px solid #DDD;
      }
    `],
    providers: [WorkflowDetailsService],
    directives: [DatanodeItem,RecommendationDialog]})
export class RecommendationsList {
    @Input() rules: Array<Rule>;
    @Input() annotator: Annotator;
    @Input() datanode: Datanode;
    @ViewChild(RecommendationDialog) dialog : RecommendationDialog;
    errorMessage: any;

    constructor(private _service: WorkflowDetailsService){}

    ngOnInit() {

    }

    showRecommendationDialog(rule : Rule){
      this.dialog.showDialog(rule);
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
}
