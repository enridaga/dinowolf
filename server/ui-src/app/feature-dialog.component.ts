
import { Component} from 'angular2/core';
import {Feature} from './app.models';

@Component({
    selector: 'feature-dialog',
    template: `
    <div *ngIf="isVisible" class="modal fade show in error" id="annotationDialog" role="dialog">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title">{{feature.n}}</h4>
                </div>
                <div class="modal-body">
                <code>{{feature.v}}</code>
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
export class FeatureDialog
{
    feature: Feature;

    public isVisible: boolean;
    private errorMessage:string;

    constructor(){}

    showDialog(feature : Feature){
      this.feature = feature;
      this.isVisible = true;
    }

    hideDialog(){
      this.isVisible = false;
      this.feature = null;
    }

    ngOnInit(){
    }
}
