System.register(['angular2/core', 'angular2/router', 'angular2/http'], function(exports_1, context_1) {
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
    var core_1, router_1, http_1;
    var FeatureDetailsComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
            },
            function (http_1_1) {
                http_1 = http_1_1;
            }],
        execute: function() {
            FeatureDetailsComponent = (function () {
                function FeatureDetailsComponent(http, _params, injector, _router) {
                    this.http = http;
                    this._params = _params;
                    this._router = _router;
                    console.log("feature constructor", _params.get('id'));
                }
                FeatureDetailsComponent.prototype.ngOnInit = function () {
                    this.id = this._params.get('id');
                };
                FeatureDetailsComponent = __decorate([
                    core_1.Component({
                        template: "\n<h1>Feature </h1>    \n<div>feature details here</div>\n"
                    }), 
                    __metadata('design:paramtypes', [http_1.Http, router_1.RouteParams, core_1.Injector, router_1.Router])
                ], FeatureDetailsComponent);
                return FeatureDetailsComponent;
            }());
            exports_1("FeatureDetailsComponent", FeatureDetailsComponent);
        }
    }
});
//# sourceMappingURL=feature-details.component.js.map