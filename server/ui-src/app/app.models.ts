import {Injectable} from 'angular2/core';

export class Feature {
    constructor(public name: string, public value: string) { }
}

export class Rule {
  public label:string;
  public picked:boolean=false;
  constructor(public head: Array<string>, public body: Array<string>, public confidence: Number, public support: Number, public relativeConfidence: Number){
    this.label = this.head.join(',') + ' (' + this.support + ', ' +this.confidence+', '+this.relativeConfidence+')' ;
  }
}

export class Bundle {
    constructor(public id: string, public name: string, public progress: string) { }
}

export class Annotation {
    constructor(public on: string, public id: string, public rdf: string){ }
}

export class Workflow {
    constructor(public name: string, public title: string, public description: string, public creator: string){
    }
}

export class Features {
    constructor(private _map : Object){

    }

    _sortByDepth(a, b){
        let _depths = ['From','To','FromToPorts','OtherPort','Activity','Processor','Workflow'];
        let r = _depths.indexOf(a.l) - _depths.indexOf(b.l);
        if(r==0){
          if(a.v > b.v){
            return -1;
          }else if(a.v < b.v){
            return 1;
          }else{
            return 0;
          }
        }
        return r;
    }

    getPortpairs(){
        let pairs = new Array<string>();
        for(var p in this._map){
            pairs.push(p);
        }
        return pairs.sort();
    }

    getFeatures(portPair: string){
        let fff = this._map[portPair];
        return fff.sort(this._sortByDepth);
    }

    getGroupedFeatures(portPair:string){
      let fff = this._map[portPair];
      let groups = {};
      for(let i in fff){
        if(typeof groups[fff[i].n] === 'undefined'){
          groups[fff[i].n] = new Array<Feature>();
        }
        groups[fff[i].n].push(fff[i]);
      }
      return groups;
    }

    getGroups(portPair:string):Array<string>{
      let fff = this._map[portPair];
      let groups = new Array<string>();
      for(let i in fff){
        if( groups.indexOf(fff[i].n) == -1 ){
          groups.push(fff[i].n);
        }
      }
      console.log('groups', groups);
      return groups;
    }

    getGroup(portPair:string, group:string):Array<Feature>{
      let feats = new Array<Feature>();
      let fff = this._map[portPair];
      for(let i in fff){
          if(fff[i].n == group){
            feats.push(fff[i]);
          }
      }
      // console.log(group, feats);
      return feats;
    }
}

export class Portpair {
  private bundle:string;
  private workflow:string;
  private processor:string;
  private from:string;
  private to:string;
  private direction:string;

  constructor(private id:string){
    let pp = id.split('/');
    this.bundle = pp[0];
    this.workflow = pp[1];
    this.processor = pp[2];
    let p = pp[3].split(':');
    this.direction = p[0];
    this.from = p[1];
    this.to = p[2];
  }
  getBundle():string{return this.bundle;}
  getWorkflow():string{return this.workflow;}
  getProcessor():string{return this.processor;}
  getDirection():string{return this.direction;}
  getArrow():string{return "&#x2192;";}
  getFrom():string{return this.from;}
  getTo():string{return this.to;}
  getId():string{return this.id;}
  getLabel():string{
      return this.getFrom() + " " + this.getArrow() + " " + this.getTo();
  }
}

export class Annotations {
    constructor(private _map : Object){

    }

    getPortpairs () : Array<Portpair> {
        let pairs = new Array<Portpair>();
        for(var p in this._map){
            pairs.push(new Portpair(p));
        }
        return pairs.sort(function(a:Portpair,b:Portpair):number{
          let ret:number;
          ret=a.getBundle().localeCompare(b.getBundle());
          if(ret==0){
            ret=a.getWorkflow().localeCompare(b.getWorkflow());
          }
          if(ret==0){
            ret=a.getProcessor().localeCompare(b.getProcessor());
          }
          if(ret==0){
            ret=a.getFrom().localeCompare(b.getFrom());
          }
          if(ret==0){
            ret=a.getTo().localeCompare(b.getTo());
          }
          return ret;
        });
    }

    getAnnotations(portPair: Portpair) : Array<string>{
        let fff = this._map[portPair.getId()];
        return fff['annotations'];
    }

    annotated(portPair: Portpair) : Boolean {
      let fff = this._map[portPair.getId()];
      return fff['annotated'] == true;
    }
}

export class Datanode{
  constructor(private _o : Array<Object>){

  }

  childrenOf(rel:string){
    if(rel == ''){
      return ['relatedWith'];
    }
    return this.item(rel).subProperties;
  }

  item(id:string){
    return this._o[id];
  }
}

export interface Annotator {
  selections:any;
  toggleSelection(relation);
  selected(relation);
  unselected(relation);
  allSelected();
  select(relation);
  unselect(relation);
  selection(relation:any, state:boolean);
}

//@Pipe({
//  name: 'slice'
//})
//export class SlicePipe implements PipeTransform {
//  transform(val:string, params:string[]):string {
//    return val.split(params[0]);
//  }
//}
