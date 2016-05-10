import {Injectable} from 'angular2/core';
import {Observable} from 'rxjs/Rx';
import { Http, Headers, RequestOptions, Response } from 'angular2/http';

export class Bundle {
    constructor(public id: string, public name: string) { }
}

@Injectable()
export class RepositoryService {
    getBundles() {
        //return bundlesPromise;
        // return an observable
        let theHeaders = new Headers();
        theHeaders.append("Accept", "application/json");
        let options = new RequestOptions({ headers: theHeaders });
        let observable = this.http.get('/service/repository', options);
        //console.log(observable);
        return observable
            .map(this.extractData)
            .catch(this.handleError);
    }

    private extractData (res: Response) {
        if (res.status < 200 || res.status >= 300) {
            throw new Error('Bad response status: ' + res.status);
        }
        let body = res.json();
        let result: Array<Bundle> = [];
        if (body) {
            body.forEach((name) => {
                result.push(
                    new Bundle(name, name));
            });
        }
        return result || new Array<Bundle>();
    }
    private handleError(error: any) {
        let errMsg = error.message || 'Server error';
        return Observable.throw(errMsg);
    }
    constructor(public http: Http) {
        //console.log('Task Service created.', http);
    }
}
