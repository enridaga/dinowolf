import {Injectable} from 'angular2/core';
import {Observable} from 'rxjs/Rx';
import {Datanode} from './app.models';
import { Http, Headers, RequestOptions, Response } from 'angular2/http';

@Injectable()
export class DatanodeService {

  list(){
      let theHeaders = new Headers();
      theHeaders.append("Accept", "application/json");
      let options = new RequestOptions({ headers: theHeaders });
      let observable = this.http.get('/service/datanode/list', options);
      let o = observable
          .map(this.extractData);
      return o
          .catch(this.handleError);
  }

  tree(){
    let theHeaders = new Headers();
    theHeaders.append("Accept", "application/json");
    let options = new RequestOptions({ headers: theHeaders });
    let observable = this.http.get('/service/datanode/tree', options);
    let o = observable
        .map(this.extractData);
    return o
        .catch(this.handleError);
  }


    datanode(){
      let theHeaders = new Headers();
      theHeaders.append("Accept", "application/json");
      let options = new RequestOptions({ headers: theHeaders });
      let observable = this.http.get('/service/datanode/tree', options);
      let o = observable
          .map(this.extractDatanode);
      return o
          .catch(this.handleError);
    }

  private handleError(error: any) {
      console.log("Error", error);
      let errMsg = error.message || 'Server error';
      return Observable.throw(errMsg);
  }

  private extractData (res: Response) {
      if (res.status < 200 || res.status >= 300) {
          throw new Error('Bad response status: ' + res.status);
      }
      let body = res.json();
      let o: Array<string>;

      if (body) {
          o = body;
      }
      return o;
  }

  private extractDatanode (res: Response) {
      if (res.status < 200 || res.status >= 300) {
          throw new Error('Bad response status: ' + res.status);
      }
      let body = res.json();
      let o: Datanode;

      if (body) {
          o = new Datanode(body);
      }
      return o;
  }
  constructor(public http: Http) { }
}
