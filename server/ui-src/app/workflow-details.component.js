System.register(['angular2/core', 'angular2/router', './workflow-details.service', 'angular2/http'], function(exports_1, context_1) {
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
    var core_1, router_1, workflow_details_service_1, http_1;
    var WorkflowDetailsComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
            },
            function (workflow_details_service_1_1) {
                workflow_details_service_1 = workflow_details_service_1_1;
            },
            function (http_1_1) {
                http_1 = http_1_1;
            }],
        execute: function() {
            WorkflowDetailsComponent = (function () {
                function WorkflowDetailsComponent(http, _params, injector, _router, _service) {
                    this.http = http;
                    this._params = _params;
                    this._router = _router;
                    this._service = _service;
                }
                WorkflowDetailsComponent.prototype.ngOnInit = function () {
                    var _this = this;
                    name = this._params.get('name');
                    // workflow
                    this._service.getWorkflow(name).subscribe(function (workflow) { return _this.workflow = workflow; }, function (error) { return _this.errorMessage = error; });
                    // features
                    this._service.getFeatures(name).subscribe(function (features) { return _this.features = features; }, function (error) { return _this.errorMessage = error; });
                };
                WorkflowDetailsComponent = __decorate([
                    core_1.Component({
                        template: "\n<h1 *ngIf=\"workflow\"><span *ngIf=\"workflow.title\">{{workflow.title}}</span><span *ngIf=\"!workflow.title\">{{workflow.name}}</span></h1>\n<div *ngIf=\"errorMessage\" class=\"alert alert-danger\" role=\"alert\"><i class=\"fa fa-exclamation-triangle\"></i> Error: {{errorMessage}}</div>\n<div *ngIf=\"features\">\n    <div *ngFor=\"#pair of features.getPortpairs()\">\n        <h4>{{pair}}</h4>\n        <table class=\"table\" >\n            <tr *ngFor=\"#feature of features.getFeatures(pair)\">\n                <td>{{feature.n}}</td>\n                <td>{{feature.v}}</td>\n                <td>{{feature.l}}</td>\n                <td>{{feature.t}}</td>\n            </tr>\n        </table>\n    </div>\n</div>\n",
                        providers: [workflow_details_service_1.WorkflowDetailsService]
                    }), 
                    __metadata('design:paramtypes', [http_1.Http, router_1.RouteParams, core_1.Injector, router_1.Router, workflow_details_service_1.WorkflowDetailsService])
                ], WorkflowDetailsComponent);
                return WorkflowDetailsComponent;
            }());
            exports_1("WorkflowDetailsComponent", WorkflowDetailsComponent);
        }
    }
});
//# sourceMappingURL=workflow-details.component.js.map