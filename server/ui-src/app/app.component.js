System.register(['angular2/core', 'angular2/router', './repository.component', './workflow-details.component', './feature-details.component', './home.component'], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1, router_1, repository_component_1, workflow_details_component_1, feature_details_component_1, home_component_1;
    var AppComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
            },
            function (repository_component_1_1) {
                repository_component_1 = repository_component_1_1;
            },
            function (workflow_details_component_1_1) {
                workflow_details_component_1 = workflow_details_component_1_1;
            },
            function (feature_details_component_1_1) {
                feature_details_component_1 = feature_details_component_1_1;
            },
            function (home_component_1_1) {
                home_component_1 = home_component_1_1;
            }],
        execute: function() {
            AppComponent = (function () {
                function AppComponent() {
                    this.title = 'Dinowolf';
                }
                AppComponent = __decorate([
                    core_1.Component({
                        selector: 'app-root',
                        templateUrl: 'tmpl/layout.html',
                        directives: [router_1.ROUTER_DIRECTIVES],
                        providers: [router_1.ROUTER_PROVIDERS]
                    }),
                    router_1.RouteConfig([
                        { path: '/home', component: home_component_1.Home, name: 'Home', useAsDefault: true },
                        { path: '/repository', name: 'Repository', component: repository_component_1.RepositoryComponent },
                        { path: '/workflow/:name', name: 'Workflow', component: workflow_details_component_1.WorkflowDetailsComponent },
                        { path: '/feature/:id', name: 'Feature', component: feature_details_component_1.FeatureDetailsComponent },
                        { path: '/**', redirectTo: ['Home'] }
                    ]), 
                    __metadata('design:paramtypes', [])
                ], AppComponent);
                return AppComponent;
            }());
            exports_1("AppComponent", AppComponent);
        }
    }
});
//# sourceMappingURL=app.component.js.map