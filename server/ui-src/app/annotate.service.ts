import {Injectable} from 'angular2/core';
import {Observable} from 'rxjs/Rx';
import {Portpair,Rule} from './app.models';
import { Http, Headers, RequestOptions, Response } from 'angular2/http';

@Injectable()
export class AnnotateService {

  constructor(public http: Http) { }

  public saveAnnotations(portPair:Portpair, selected:Array<String>, elapsedTime: Number){
    let theHeaders = new Headers();
    theHeaders.append("Content-Type", "application/json");
    let options = new RequestOptions({ headers: theHeaders });
    let data = {annotations: selected, elapsedTime: elapsedTime};
    let body = JSON.stringify(data);
    let observable = this.http.post('/service/annotation/' + portPair.getId(), body, options);
    let o = observable
        .map(this.receiveSaveSuccess);
    return o
        .catch(this.handleError);
  }

  public noAnnotations(portPair:Portpair, selected:Array<String>, elapsedTime: Number){
    let theHeaders = new Headers();
    theHeaders.append("Content-Type", "application/x-www-form-urlencoded");
    let options = new RequestOptions({ headers: theHeaders });
    let body = "action=" + encodeURIComponent("NONE") +"&elapsedTime="+encodeURIComponent(''+elapsedTime) ;
    let observable = this.http.post('/service/annotation/' + portPair.getId(), body, options);
    let o = observable
        .map(this.receiveSaveSuccess);
    return o
        .catch(this.handleError);
  }
  
  public skipAnnotations(portPair:Portpair, selected:Array<String>, elapsedTime: Number){
    let theHeaders = new Headers();
    theHeaders.append("Content-Type", "application/x-www-form-urlencoded");
    let options = new RequestOptions({ headers: theHeaders });
    let body = "action=" + encodeURIComponent("SKIPPED") +"&elapsedTime="+encodeURIComponent(''+elapsedTime) ;
    let observable = this.http.post('/service/annotation/' + portPair.getId(), body, options);
    let o = observable
        .map(this.receiveSaveSuccess);
    return o
        .catch(this.handleError);
  }

  public recommend(portPair: Portpair) : Observable<Array<Rule>>{
    let theHeaders = new Headers();
    theHeaders.append("Accept", "application/json");
    let options = new RequestOptions({ headers: theHeaders });
    let observable = this.http.get('/service/recommend/' + portPair.getId(), options);
    let o = observable
        .map(this.extractRecommendData);
    return o
        .catch(this.handleError);
  }

  private extractRecommendData (res: Response) {
      if (res.status < 200 || res.status >= 300) {
          throw new Error('Bad response status: ' + res.status);
      }
      let body = res.json();
      let recommendations = new Array<Rule>();

      if (body) {
        for(let x in body){
          recommendations.push(new Rule(
            body[x]['head'],
            body[x]['body'],
            body[x]['confidence'],
            body[x]['support'],
            body[x]['relativeConfidence']));
        }
      }
      return recommendations;
  }

  private receiveSaveSuccess (res: Response) {
      if (res.status < 200 || res.status >= 300) {
          throw new Error('Save failed: ' + res.status);
      }

      return true;
  }

  private handleError(error: any) {
      let errMsg = error.message || 'Server error';
      return Observable.throw(errMsg);
  }
}
