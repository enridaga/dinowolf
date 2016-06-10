import {Component} from 'angular2/core';
import {RepositoryService} from './repository.service';
import {Bundle} from './app.models';
import {Router} from 'angular2/router';
@Component({
    template: `
  <h1>{{title}}</h1>
  <table class="table">
    <tr *ngFor="#bundle of bundles">
      <td><a (click)="onSelect(bundle)">
      {{bundle.name | slice:1:60}}</a></td>
        <td>
        <div class="progress">
          <div class="progress-bar" role="progressbar" aria-valuenow="bundle.progress" aria-valuemin="0" aria-valuemax="100" style="width: {{bundle.progress}}%;">
            {{bundle.progress}}%
          </div>
        </div>
        </td>
    </tr>
  </table>
`,
    styles: [`
    table.table td a {cursor:pointer;}
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