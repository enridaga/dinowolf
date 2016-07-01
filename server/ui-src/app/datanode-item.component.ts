import { Component, Input } from 'angular2/core';
import { Datanode , Annotator} from './app.models';
import { DatanodeService } from './datanode.service';
import { AnnotateService } from './annotate.service';

@Component({
  selector: 'datanode-item',
  providers: [],
  directives: [ DatanodeItem ],
  template: `
    <div class="dn-item">
      <p [class.selected]="annotator.selections[item]"
      [title]="comment(item)">
        <input
          type="checkbox"
          *ngIf="annotator"
          [ngModel]="annotator.selections[item]"
          (change)="annotator.selection(item, cv.checked) ; annotator.selection(root, cv.checked)"
          #cv />
        {{label(item)}}
        <a class="btn btn-xs btn-default" *ngIf="item && (datanodeChildren().length > 0)" (click)="toggleExpanded()"><i class="fa" [ngClass]="{'fa-plus': !expanded,'fa-minus': expanded}"></i></a>
      </p>
      <ul *ngIf="expanded">
        <li *ngFor="#child of datanodeChildren()">
          <datanode-item [annotator]="annotator" [root]="root" [datanode]="datanode" *ngIf="child" [item]="child"></datanode-item>
        </li>
      </ul>
    </div>
  `,
  styles: [`
    .dn-item ul {
      margin-left: 2em;
    }
    .dn-item p {
      cursor: default;
    }
    .dn-item a {
      cursor: pointer;
      display: inline;
    }
    .nd-item ul li {

    }
  `]
})
export class DatanodeItem {

  @Input() item:string = 'realatedWith' ;
  @Input() root:string = null;
  @Input() datanode: Datanode ;
  @Input() annotator: Annotator ;
  private expanded: Boolean;
  private errorMessage;
  constructor() {}

  datanodeChildren(){
    if(!this.datanode) return [];
    if(this.item == ''){
      return ['relatedWith'];
    }
    return this.datanode.childrenOf(this.item);
  }

  datanodeItem(rel:string){
    if(!this.datanode) return ;
    return this.datanode.item(rel);
  }

  ngOnInit(){
    if(!this.item){
      this.expanded = true;
    }
  }

  toggleExpanded(){
    if(this.item){
      this.expanded = !this.expanded;
    }
  }

  comment(str){
    if(str){
      // hack I don't know why sometimes datanode is undefined!
      if(!this.datanode){
        return str;
      }
      let i = this.datanode.item(str);
      if(i.comment){
        return i.comment;
      }else{
        return this.label(str);
      }
    }
  }
  label(str){
    if(str){
      // hack I don't know why sometimes datanode is undefined!
      if(!this.datanode){
        return str;
      }
      let i = this.datanode.item(str);
      if(i.label){
        return i.label;
      }else{
        return i.id;
      }
    }
  }
}
