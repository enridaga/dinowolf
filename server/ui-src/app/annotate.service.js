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
    var AnnotateService;
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
            AnnotateService = (function () {
                function AnnotateService(http) {
                    this.http = http;
                }
                AnnotateService.prototype.saveAnnotations = function (portPair, selected, elapsedTime) {
                    var theHeaders = new http_1.Headers();
                    theHeaders.append("Content-Type", "application/json");
                    var options = new http_1.RequestOptions({ headers: theHeaders });
                    var data = { annotations: selected, elapsedTime: elapsedTime };
                    var body = JSON.stringify(data);
                    var observable = this.http.post('/service/annotation/' + portPair.getId(), body, options);
                    var o = observable
                        .map(this.receiveSaveSuccess);
                    return o
                        .catch(this.handleError);
                };
                AnnotateService.prototype.noAnnotations = function (portPair, selected, elapsedTime) {
                    var theHeaders = new http_1.Headers();
                    theHeaders.append("Content-Type", "application/x-www-form-urlencoded");
                    var options = new http_1.RequestOptions({ headers: theHeaders });
                    var body = "action=" + encodeURIComponent("NONE") + "&elapsedTime=" + encodeURIComponent('' + elapsedTime);
                    var observable = this.http.post('/service/annotation/' + portPair.getId(), body, options);
                    var o = observable
                        .map(this.receiveSaveSuccess);
                    return o
                        .catch(this.handleError);
                };
                AnnotateService.prototype.skipAnnotations = function (portPair, selected, elapsedTime) {
                    var theHeaders = new http_1.Headers();
                    theHeaders.append("Content-Type", "application/x-www-form-urlencoded");
                    var options = new http_1.RequestOptions({ headers: theHeaders });
                    var body = "action=" + encodeURIComponent("SKIPPED") + "&elapsedTime=" + encodeURIComponent('' + elapsedTime);
                    var observable = this.http.post('/service/annotation/' + portPair.getId(), body, options);
                    var o = observable
                        .map(this.receiveSaveSuccess);
                    return o
                        .catch(this.handleError);
                };
                AnnotateService.prototype.recommend = function (portPair) {
                    var theHeaders = new http_1.Headers();
                    theHeaders.append("Accept", "application/json");
                    var options = new http_1.RequestOptions({ headers: theHeaders });
                    var observable = this.http.get('/service/recommend/' + portPair.getId(), options);
                    var o = observable
                        .map(this.extractRecommendData);
                    return o
                        .catch(this.handleError);
                };
                AnnotateService.prototype.extractRecommendData = function (res) {
                    if (res.status < 200 || res.status >= 300) {
                        throw new Error('Bad response status: ' + res.status);
                    }
                    var body = res.json();
                    var recommendations = new Array();
                    if (body) {
                        for (var x in body) {
                            recommendations.push(new app_models_1.Rule(body[x]['head'], body[x]['body'], body[x]['confidence'], body[x]['support'], body[x]['relativeConfidence']));
                        }
                    }
                    return recommendations;
                };
                AnnotateService.prototype.about = function (rule) {
                    var theHeaders = new http_1.Headers();
                    theHeaders.append("Accept", "application/json");
                    var options = new http_1.RequestOptions({ headers: theHeaders });
                    var ids = "";
                    for (var z in rule.body) {
                        var f = rule.body[z];
                        if (ids) {
                            ids += ',' + f.substr(2);
                        }
                        else {
                            ids = f.substr(2);
                        }
                    }
                    var observable = this.http.get('/service/features/' + ids, options);
                    var o = observable
                        .map(this.extractFeaturesData);
                    return o
                        .catch(this.handleError);
                };
                AnnotateService.prototype.receiveSaveSuccess = function (res) {
                    if (res.status < 200 || res.status >= 300) {
                        throw new Error('Save failed: ' + res.status);
                    }
                    return true;
                };
                AnnotateService.prototype.extractFeaturesData = function (res) {
                    if (res.status < 200 || res.status >= 300) {
                        throw new Error('Bad response status: ' + res.status);
                    }
                    var body = res.json();
                    if (body) {
                        return body;
                    }
                    return false;
                };
                AnnotateService.prototype.handleError = function (error) {
                    var errMsg = error.message || 'Server error';
                    return Rx_1.Observable.throw(errMsg);
                };
                AnnotateService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [http_1.Http])
                ], AnnotateService);
                return AnnotateService;
            }());
            exports_1("AnnotateService", AnnotateService);
        }
    }
});
//# sourceMappingURL=annotate.service.js.map