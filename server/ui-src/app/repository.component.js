System.register(['angular2/core', './repository.service', 'angular2/router'], function(exports_1, context_1) {
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
    var core_1, repository_service_1, router_1;
    var RepositoryComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (repository_service_1_1) {
                repository_service_1 = repository_service_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
            }],
        execute: function() {
            RepositoryComponent = (function () {
                function RepositoryComponent(_router, _service) {
                    this._router = _router;
                    this._service = _service;
                    this.title = 'Workflows';
                    this.bundles = [];
                    this.errorMessage = '';
                }
                RepositoryComponent.prototype.ngOnInit = function () {
                    var _this = this;
                    this._service.getBundles().subscribe(function (bundles) { return _this.bundles = bundles; }, function (error) { return _this.errorMessage = error; });
                };
                RepositoryComponent.prototype.onSelect = function (bundle) {
                    this._router.navigate(['Workflow', { name: bundle.name }]);
                };
                RepositoryComponent = __decorate([
                    core_1.Component({
                        template: "\n  <h1>{{title}}</h1>\n  <ul class=\"items\">\n    <li *ngFor=\"#bundle of bundles\">\n      <a (click)=\"onSelect(bundle)\">\n      {{bundle.name}}</a>\n    </li>\n  </ul>\n",
                        styles: ["\n    ul.items li a {cursor:pointer}\n"],
                        providers: [repository_service_1.RepositoryService]
                    }), 
                    __metadata('design:paramtypes', [router_1.Router, repository_service_1.RepositoryService])
                ], RepositoryComponent);
                return RepositoryComponent;
            }());
            exports_1("RepositoryComponent", RepositoryComponent);
        }
    }
});
//# sourceMappingURL=repository.component.js.map