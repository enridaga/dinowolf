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
    var WorkflowDetailsService;
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
            WorkflowDetailsService = (function () {
                function WorkflowDetailsService(http) {
                    this.http = http;
                }
                WorkflowDetailsService.prototype.getWorkflow = function (name) {
                    var theHeaders = new http_1.Headers();
                    theHeaders.append("Accept", "application/json");
                    var options = new http_1.RequestOptions({ headers: theHeaders });
                    var observable = this.http.get('/service/workflow/' + name, options);
                    var o = observable
                        .map(this.extractWorkflowData);
                    return o
                        .catch(this.handleError);
                };
                WorkflowDetailsService.prototype.getFeatures = function (name) {
                    var theHeaders = new http_1.Headers();
                    theHeaders.append("Accept", "application/json");
                    var options = new http_1.RequestOptions({ headers: theHeaders });
                    var observable = this.http.get('/service/workflow/' + name + "/features", options);
                    var o = observable
                        .map(this.extractFeaturesData);
                    return o
                        .catch(this.handleError);
                };
                WorkflowDetailsService.prototype.handleError = function (error) {
                    console.log("Error", error);
                    var errMsg = error.message || 'Server error';
                    return Rx_1.Observable.throw(errMsg);
                };
                WorkflowDetailsService.prototype.extractWorkflowData = function (res) {
                    if (res.status < 200 || res.status >= 300) {
                        throw new Error('Bad response status: ' + res.status);
                    }
                    var body = res.json();
                    var workflow;
                    if (body) {
                        workflow = new app_models_1.Workflow(body.name, body.title, body.description, body.creator);
                    }
                    return workflow;
                };
                WorkflowDetailsService.prototype.extractFeaturesData = function (res) {
                    if (res.status < 200 || res.status >= 300) {
                        throw new Error('Bad response status: ' + res.status);
                    }
                    var body = res.json();
                    var features;
                    if (body) {
                        features = new app_models_1.Features(body);
                    }
                    //        console.log("body",body);
                    //        console.log("WF",features);
                    return features;
                };
                WorkflowDetailsService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [http_1.Http])
                ], WorkflowDetailsService);
                return WorkflowDetailsService;
            }());
            exports_1("WorkflowDetailsService", WorkflowDetailsService);
        }
    }
});
//# sourceMappingURL=workflow-details.service.js.map