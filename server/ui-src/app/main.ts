import {bootstrap}    from 'angular2/platform/browser';
import {AppComponent} from './app.component';
import {RouteConfig, ROUTER_DIRECTIVES, ROUTER_PROVIDERS} from 'angular2/router';
import {Http, Response, RequestOptions, Headers, Request, RequestMethod, HTTP_PROVIDERS} from 'angular2/http';
import 'rxjs/Rx';

bootstrap(AppComponent,[HTTP_PROVIDERS,ROUTER_PROVIDERS]);
