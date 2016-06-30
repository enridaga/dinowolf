System.register(['angular2/core', './app.models', './logs.service', 'angular2/router'], function(exports_1, context_1) {
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
    var core_1, app_models_1, logs_service_1, router_1;
    var AnnotationsComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (app_models_1_1) {
                app_models_1 = app_models_1_1;
            },
            function (logs_service_1_1) {
                logs_service_1 = logs_service_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
            }],
        execute: function() {
            AnnotationsComponent = (function () {
                function AnnotationsComponent(_logsService, _router) {
                    this._logsService = _logsService;
                    this._router = _router;
                    this.title = 'Annotations';
                }
                AnnotationsComponent.prototype.ngOnInit = function () {
                    var _this = this;
                    this._logsService.getAnnotations().subscribe(function (annotations) { return _this.annotations = annotations; }, function (error) { return _this.errorMessage = error; });
                };
                AnnotationsComponent.prototype.asPortPair = function (pp) {
                    return new app_models_1.Portpair(pp);
                };
                AnnotationsComponent.prototype.gotoBundle = function (bundle) {
                    this._router.navigate(['Workflow', { name: bundle }]);
                };
                AnnotationsComponent = __decorate([
                    core_1.Component({
                        template: "\n    <h1>{{title}}</h1>\n    <table class=\"table\">\n      <tbody>\n        <tr *ngFor=\"#log of annotations\">\n          <td><small><a (click)=\"gotoBundle(log.bundle)\">{{log.bundle}}</a></small><br/>\n          <span [innerHTML]=\"asPortPair(log.portpair).getLabel()\"></span></td>\n          <td><span *ngFor=\"#ann of log.annotations\">{{ann}}<br/></span></td>\n        </tr>\n      </tbody>\n    </table>\n    ",
                        providers: [logs_service_1.LogsService],
                        directives: [] }), 
                    __metadata('design:paramtypes', [logs_service_1.LogsService, router_1.Router])
                ], AnnotationsComponent);
                return AnnotationsComponent;
            }());
            exports_1("AnnotationsComponent", AnnotationsComponent);
        }
    }
});
//# sourceMappingURL=annotations.component.js.map