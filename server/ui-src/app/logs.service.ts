import {Injectable} from 'angular2/core';
import {Observable} from 'rxjs/Rx';
import {} from './app.models';
import { Http, Headers, RequestOptions, Response } from 'angular2/http';

@Injectable()
export class LogsService {

  getLogs(){
    let theHeaders = new Headers();
    theHeaders.append("Accept", "application/json");
    let options = new RequestOptions({ headers: theHeaders });
    let observable = this.http.get('/service/annotations/logs', options);
    let o = observable
        .map(this.extractLogsData);
    return o
        .catch(this.handleError);
  }

  getAnnotations(){
    let theHeaders = new Headers();
    theHeaders.append("Accept", "application/json");
    let options = new RequestOptions({ headers: theHeaders });
    let observable = this.http.get('/service/annotations/list', options);
    let o = observable
        .map(this.extractLogsData);
    return o
        .catch(this.handleError);
  }

  private extractLogsData (res: Response) {
      if (res.status < 200 || res.status >= 300) {
          throw new Error('Bad response status: ' + res.status);
      }
      let body = res.json();
      let logs: Array<Object>;

      if (body) {
          logs = body;
      }
      return logs;
  }

  private handleError(error: any) {
      // console.log("Error", error);
      let errMsg = error.message || 'Server error';
      return Observable.throw(errMsg);
  }

  constructor(public http: Http) { }
}
