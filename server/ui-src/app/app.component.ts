import {Component} from 'angular2/core';
import {RouteConfig, ROUTER_DIRECTIVES, ROUTER_PROVIDERS} from 'angular2/router';
import {RepositoryComponent} from './repository.component';
import {WorkflowDetailsComponent} from './workflow-details.component';
import {FeatureDetailsComponent} from './feature-details.component';
import {RepositoryService} from './repository.service';
import {Home} from './home.component';

@Component({
    selector: 'app-root',
    templateUrl: 'tmpl/layout.html',
    directives: [ROUTER_DIRECTIVES],
    providers: [ROUTER_PROVIDERS]
})
@RouteConfig([
    { path: '/home', component: Home, name: 'Home', useAsDefault: true },
    { path: '/repository', name: 'Repository', component: RepositoryComponent },
    { path: '/workflow/:name', name: 'Workflow', component: WorkflowDetailsComponent },
    { path: '/feature/:id', name: 'Feature', component: FeatureDetailsComponent },
    { path: '/**', redirectTo: ['Home'] }
])
export class AppComponent {
    title = 'Dinowolf';
}
