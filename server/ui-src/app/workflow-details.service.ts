import {Injectable} from 'angular2/core';
import {Observable} from 'rxjs/Rx';
import {Feature, Features, Workflow, Bundle, Annotations, Portpair} from './app.models';
import { Http, Headers, RequestOptions, Response } from 'angular2/http';

@Injectable()
export class WorkflowDetailsService {

    getWorkflow(name: string){
        let theHeaders = new Headers();
        theHeaders.append("Accept", "application/json");
        let options = new RequestOptions({ headers: theHeaders });
        let observable = this.http.get('/service/workflow/' + name, options);
        let o = observable
            .map(this.extractWorkflowData);

        return o
            .catch(this.handleError);
    }

    getFeatures(name: string, portpair: string = undefined){
       let theHeaders = new Headers();
        theHeaders.append("Accept", "application/json");
        let options = new RequestOptions({ headers: theHeaders });
        let url = '/service/workflow/' + name + "/features";
        if(portpair){
          url += '/' + portpair;
        }
        console.log('get features: ', url);
        let observable = this.http.get(url, options);
        let o = observable
            .map(this.extractFeaturesData);
        return o
            .catch(this.handleError);
    }

    getAnnotations(bundle: string){
        let theHeaders = new Headers();
        theHeaders.append("Accept", "application/json");
        let options = new RequestOptions({ headers: theHeaders });
        let observable = this.http.get('/service/annotations/bundle/' + bundle, options);
        let o = observable
            .map(this.extractAnnotationsData);
        return o
            .catch(this.handleError);
    }

    private handleError(error: any) {
        // console.log("Error", error);
        let errMsg = error.message || 'Server error';
        return Observable.throw(errMsg);
    }

    private extractAnnotationsData (res: Response) {
        if (res.status < 200 || res.status >= 300) {
            throw new Error('Bad response status: ' + res.status);
        }
        let body = res.json();
        let annotations: Annotations;

        if (body) {
            annotations = new Annotations(body);
        }
        return annotations;
    }

    private extractWorkflowData (res: Response) {
        if (res.status < 200 || res.status >= 300) {
            throw new Error('Bad response status: ' + res.status);
        }
        let body = res.json();
        let workflow: Workflow;

        if (body) {
            workflow = new Workflow(body.name, body.title, body.description, body.creator);
        }
        return workflow;
    }

    private extractFeaturesData (res: Response) {
        if (res.status < 200 || res.status >= 300) {
            throw new Error('Bad response status: ' + res.status);
        }
        let body = res.json();
        let features: Features;

        if (body) {
            features = new Features(body);
        }
//        console.log("body",body);
//        console.log("WF",features);
        return features;
    }
    constructor(public http: Http) { }
}
