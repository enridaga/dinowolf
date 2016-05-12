import {Component} from 'angular2/core';
import {RepositoryService} from './repository.service';
import {Bundle} from './app.models';
import {Router} from 'angular2/router';
@Component({
    template: `
  <h1>{{title}}</h1>
  <ul class="items">
    <li *ngFor="#bundle of bundles">
      <a (click)="onSelect(bundle)">
      {{bundle.name}}</a>
    </li>
  </ul>
`,
    styles: [`
    ul.items li a {cursor:pointer}
`],
    providers: [RepositoryService]
})
export class RepositoryComponent {
    title = 'Workflows';
    bundles: Array<Bundle> = [];
    errorMessage = '';
    constructor(
        private _router: Router,
        private _service: RepositoryService
    ) { }
    ngOnInit() {
        this._service.getBundles().subscribe(
            bundles => this.bundles = bundles,
            error => this.errorMessage = <any>error);
    }
    onSelect(bundle: Bundle){
        this._router.navigate( ['Workflow', { name: bundle.name }] );    
    }
}