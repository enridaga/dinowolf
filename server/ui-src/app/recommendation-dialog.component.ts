import { Component} from 'angular2/core';
import {Rule} from './app.models';
import {AnnotateService} from './annotate.service';

@Component({
    selector: 'recommendation-dialog',
    template: `
    <div *ngIf="isVisible" class="modal fade show in error" id="recommendationDialog" role="dialog">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="btn btn-default pull-right" (click)="hideDialog()">Close</button>
                    <h4 class="modal-title">
                    <span *ngFor="#h of rule.head">{{h}} </span>
                    </h4>
                </div>
                <div class="modal-body" style="overflow-y: scroll; height:300px">
                <p>Support: {{rule.support}}</p>
                <p>Confidence: {{rule.confidence}}</p>
                <p>Relative confidence: {{rule.relativeConfidence}}</p>
                <h5>Features:</h5>
                <ul>
                  <li *ngFor="#b of features">{{b.n}}: {{b.v}} </li>
                </ul>
                </div>
                <div class="modal-footer">
                  <button type="button" class="btn btn-default" (click)="hideDialog()">Close</button>
                </div>
            </div>
        </div>
    </div>
    `,
    providers: [AnnotateService]
})
export class RecommendationDialog
{
    rule: Rule;

    public isVisible: boolean;
    private errorMessage:string;
    private features:any;
    constructor(private _service: AnnotateService){}

    showDialog(rule : Rule){
      let dialog = this;
      this.rule = rule;
      this.isVisible = false;
      this._service.about(rule).subscribe(
          function(features){
            dialog.features = features;
            dialog.isVisible = true;
          },
          error => this.errorMessage = <any>error);
    }

    hideDialog(){
      this.isVisible = false;
      this.rule = null;
    }

    ngOnInit() {

    }
}
