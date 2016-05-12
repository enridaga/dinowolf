import {Injectable} from 'angular2/core';

export class Feature {
    constructor(public name: string, public value: string) { }
}

export class Bundle {
    constructor(public id: string, public name: string) { }
}

export class Annotation {
    constructor(public on: string, public id: string, public rdf: string){ }    
}

export class Workflow {
    constructor(public name: string, public title: string, public description: string, public creator: string){ }    
}

export class Features {
    constructor(private _map : Object){ }
    
    getPortpairs(){
        let pairs = new Array<string>();
        for(var p in this._map){
            pairs.push(p);    
        }
        return pairs;
    }
    
    getFeatures(portPair: string){
        return this._map[portPair];    
    }
}