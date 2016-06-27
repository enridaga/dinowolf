
import { Component} from 'angular2/core';
import {Rule} from './app.models';

@Component({
    selector: 'recommendation-dialog',
    template: `
    <div *ngIf="isVisible" class="modal fade show in error" id="recommendationDialog" role="dialog">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title">
                    <span *ngFor="#h of rule.head">{{h}} </span>
                    </h4>
                </div>
                <div class="modal-body">
                <p>Support: {{rule.support}}</p>
                <p>Confidence: {{rule.confidence}}</p>
                <p>Relative confidence: {{rule.relativeConfidence}}</p>
                <h5>Features:</h5>
                <span *ngFor="#b of rule.body">{{b}} </span>
                </div>
                <div class="modal-footer">
                  <button type="button" class="btn btn-default" (click)="hideDialog()">Close</button>
                </div>
            </div>
        </div>
    </div>
    `,
    providers: []
})
export class RecommendationDialog
{
    rule: Rule;

    public isVisible: boolean;
    private errorMessage:string;

    constructor(){}

    showDialog(rule : Rule){
      this.rule = rule;
      this.isVisible = true;
    }

    hideDialog(){
      this.isVisible = false;
      this.rule = null;
    }

    ngOnInit(){
    }
}
