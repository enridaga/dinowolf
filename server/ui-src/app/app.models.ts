import {Injectable} from 'angular2/core';

export class Feature {
    constructor(public name: string, public value: string) { }
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
        return _depths.indexOf(a.l) - _depths.indexOf(b.l);    
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
 //       return fff;
    }
}

//@Pipe({
//  name: 'slice'
//})
//export class SlicePipe implements PipeTransform {
//  transform(val:string, params:string[]):string {
//    return val.split(params[0]);
//  }
//}