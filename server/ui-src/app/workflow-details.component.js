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
                //@ViewChild(AnnotateDialog) annotationDialog: AnnotateDialog;
                function WorkflowDetailsComponent(http, _params, injector, _router, _service) {
                    this.http = http;
                    this._params = _params;
                    this._router = _router;
                    this._service = _service;
                    this.selectedValue = '';
                }
                WorkflowDetailsComponent.prototype.annotate = function (pair) {
                    //  this.annotationDialog.showDialog(pair);
                    this._router.navigate(['Annotation', { portpair: pair.getId() }]);
                };
                WorkflowDetailsComponent.prototype.ngOnInit = function () {
                    var _this = this;
                    this.name = this._params.get('name');
                    // workflow
                    this.link = "http://www.myexperiment.org/workflows/" + this.name.slice(0, this.name.indexOf('-'));
                    this._service.getWorkflow(this.name).subscribe(function (workflow) { return _this.workflow = workflow; }, function (error) { return _this.errorMessage = error; });
                    // features
                    this._service.getFeatures(this.name).subscribe(function (features) { return _this.features = features; }, function (error) { return _this.errorMessage = error; });
                    this._service.getAnnotations(this.name).subscribe(function (annotations) { return _this.annotations = annotations; }, function (error) { return _this.errorMessage = error; });
                };
                WorkflowDetailsComponent.prototype.ngAfterViewInit = function () {
                };
                WorkflowDetailsComponent = __decorate([
                    core_1.Component({
                        template: "\n<h1 *ngIf=\"name\">{{name}}</h1>\n<a href=\"{{link}}\" target=\"_myexperiments\">{{link}}</a>\n<div *ngIf=\"workflow\">\n    <strong>Workflow:</strong>\n    <span *ngIf=\"workflow.title\">{{workflow.title}}</span>\n    <span *ngIf=\"!workflow.title\">{{workflow.name}}</span>\n</div>\n<div *ngIf=\"errorMessage\" class=\"alert alert-danger\" role=\"alert\"><i class=\"fa fa-exclamation-triangle\"></i> Error: {{errorMessage}}</div>\n<div *ngIf=\"annotations\">\n    <img src=\"/service/myexperiments/image/{{name}}\" >\n    <p><strong>I/O port pairs:</strong></p>\n    <table class=\"table\">\n    <thead>\n      <tr>\n        <th>Workflow</th>\n        <th>Processor</th>\n        <th>Port mapping</th>\n        <th>Annotations</th>\n      </tr>\n    </thead>\n    <tbody>\n    <tr *ngFor=\"#pair of annotations.getPortpairs()\">\n      <td>{{pair.getWorkflow()}}</td>\n      <td>{{pair.getProcessor()}}</td>\n      <td>{{pair.getFrom()}} <span [innerHTML]=\"pair.getArrow()\"></span> {{pair.getTo()}}</td>\n      <td>\n        <button *ngIf=\"!annotations.annotated(pair)\"\n        class=\"btn btn-default btn-sm\" (click)=\"annotate(pair)\">Annotate</button>\n        <div *ngIf=\"annotations.getAnnotations(pair).length > 0\">\n            <span *ngFor=\"#ann of annotations.getAnnotations(pair)\">\n            <span class=\"\" >{{ann}}</span>\n            &nbsp;\n            </span>\n        </div>\n      </td>\n    </tr>\n    </tbody>\n    </table>\n\n</div>\n",
                        providers: [workflow_details_service_1.WorkflowDetailsService],
                        directives: []
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