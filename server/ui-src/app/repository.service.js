System.register(['angular2/core', 'rxjs/Rx', './app.models', 'angular2/http'], function(exports_1, context_1) {
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
    var core_1, Rx_1, app_models_1, http_1;
    var RepositoryService;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (Rx_1_1) {
                Rx_1 = Rx_1_1;
            },
            function (app_models_1_1) {
                app_models_1 = app_models_1_1;
            },
            function (http_1_1) {
                http_1 = http_1_1;
            }],
        execute: function() {
            RepositoryService = (function () {
                function RepositoryService(http) {
                    this.http = http;
                    //console.log('Task Service created.', http);
                }
                RepositoryService.prototype.getBundles = function () {
                    var theHeaders = new http_1.Headers();
                    theHeaders.append("Accept", "application/json");
                    var options = new http_1.RequestOptions({ headers: theHeaders });
                    var observable = this.http.get('/service/annotations/progress', options);
                    return observable
                        .map(this.extractData)
                        .catch(this.handleError);
                };
                RepositoryService.prototype.extractData = function (res) {
                    if (res.status < 200 || res.status >= 300) {
                        throw new Error('Bad response status: ' + res.status);
                    }
                    var body = res.json();
                    var result = [];
                    if (body) {
                        body.forEach(function (obj) {
                            result.push(new app_models_1.Bundle(obj.bundle, obj.bundle, obj.progress));
                        });
                    }
                    return result || new Array();
                };
                RepositoryService.prototype.handleError = function (error) {
                    var errMsg = error.message || 'Server error';
                    return Rx_1.Observable.throw(errMsg);
                };
                RepositoryService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [http_1.Http])
                ], RepositoryService);
                return RepositoryService;
            }());
            exports_1("RepositoryService", RepositoryService);
        }
    }
});
//# sourceMappingURL=repository.service.js.map