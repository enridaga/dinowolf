import {Component} from 'angular2/core';
import {RouteConfig, ROUTER_DIRECTIVES, ROUTER_PROVIDERS} from 'angular2/router';
import {RepositoryComponent} from './repository.component';
import {WorkflowDetailsComponent} from './workflow-details.component';
import {AnnotateComponent} from './annotate.component';
import {LogsComponent} from './logs.component';
import {AnnotationsComponent} from './annotations.component';
import {FeatureDetailsComponent} from './feature-details.component';
import {RepositoryService} from './repository.service';
import {Home} from './home.component';

@Component({
    selector: 'app-root',
    templateUrl: 'tmpl/layout2.html',
    directives: [ROUTER_DIRECTIVES],
    providers: [ROUTER_PROVIDERS]
})
@RouteConfig([
    { path: '/home', component: Home, name: 'Home', useAsDefault: true },
    { path: '/repository', name: 'Repository', component: RepositoryComponent },
    { path: '/workflow/:name', name: 'Workflow', component: WorkflowDetailsComponent },
    { path: '/annotation/:portpair', name: 'Annotation', component: AnnotateComponent },
    { path: '/annotations', name: 'Annotations', component: AnnotationsComponent },
    { path: '/logs', name: 'Logs', component:  LogsComponent},
    { path: '/feature/:id', name: 'Feature', component: FeatureDetailsComponent },
    { path: '/**', redirectTo: ['Repository'] }
])
export class AppComponent {
    title = 'Dinowolf';
}
