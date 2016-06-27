System.register(['angular2/core', './app.models', './datanode.service', './features-list.component', './recommendations-list.component', './annotate.service', 'angular2/router'], function(exports_1, context_1) {
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
    var core_1, app_models_1, datanode_service_1, features_list_component_1, recommendations_list_component_1, annotate_service_1, router_1;
    var AnnotateComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (app_models_1_1) {
                app_models_1 = app_models_1_1;
            },
            function (datanode_service_1_1) {
                datanode_service_1 = datanode_service_1_1;
            },
            function (features_list_component_1_1) {
                features_list_component_1 = features_list_component_1_1;
            },
            function (recommendations_list_component_1_1) {
                recommendations_list_component_1 = recommendations_list_component_1_1;
            },
            function (annotate_service_1_1) {
                annotate_service_1 = annotate_service_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
            }],
        execute: function() {
            AnnotateComponent = (function () {
                function AnnotateComponent(_datanodeService, _annotateService, _router, _params) {
                    var _this = this;
                    this._datanodeService = _datanodeService;
                    this._annotateService = _annotateService;
                    this._router = _router;
                    this._params = _params;
                    this.annotator = this;
                    this._datanodeService.datanode().subscribe(function (datanode) { return _this.datanode = datanode; }, function (error) { return _this.errorMessage = error; });
                }
                AnnotateComponent.prototype.recommended = function () {
                    var recommended = new Array();
                    var best = {};
                    for (var x in this.rules) {
                        var id = this.rules[x].head.join(',');
                        if (typeof best[id] === 'undefined') {
                            best[id] = this.rules[x];
                        }
                        else {
                            var cmp = best[id];
                            if (cmp.relativeConfidence < this.rules[x].relativeConfidence) {
                                best[id] = this.rules[x];
                            }
                        }
                    }
                    for (var x in best) {
                        recommended.push(best[x]);
                    }
                    recommended.sort(function (a, b) {
                        // higher values on top
                        if (a.relativeConfidence > b.relativeConfidence)
                            return -1;
                        if (a.relativeConfidence < b.relativeConfidence)
                            return 1;
                        if (a.confidence > b.confidence)
                            return -1;
                        if (a.confidence < b.confidence)
                            return 1;
                        if (a.support > b.support)
                            return -1;
                        if (a.support < b.support)
                            return 1;
                        return 0;
                    });
                    return recommended;
                };
                AnnotateComponent.prototype.unrecommended = function () {
                    return this.relations;
                };
                AnnotateComponent.prototype.cancel = function () {
                    this.gotoWorkflow();
                };
                AnnotateComponent.prototype.ngOnDestroy = function () {
                    console.log('on destroy');
                    window.clearInterval(this.intervalID);
                };
                AnnotateComponent.prototype.ngOnInit = function () {
                    var _this = this;
                    this.startTime = new Date().getTime();
                    var t = this;
                    this.intervalID = setInterval(function () { t.elapsedTimeSoFar = t.elapsedTime(); }, 1000);
                    this.portpair = new app_models_1.Portpair(this._params.get('portpair'));
                    this.bundle = this.portpair.getBundle();
                    this.selections = new Object();
                    this._annotateService.recommend(this.portpair).subscribe(function (rules) { return _this.rules = rules; }, function (error) { return _this.errorMessage = error; });
                    this._datanodeService.list().subscribe(function (relations) { return _this.relations = relations; }, function (error) { return _this.errorMessage = error; });
                };
                AnnotateComponent.prototype.toggleSelection = function (relation) {
                    if (this.selections[relation.id] == null) {
                        this.selections[relation.id] = true;
                    }
                    else {
                        this.selections[relation.id] = !this.selections[relation.id];
                    }
                };
                AnnotateComponent.prototype.selected = function (relation) {
                    if (this.selections[relation.id] == null) {
                        this.selections[relation.id] = false;
                    }
                    return this.selections[relation.id];
                };
                AnnotateComponent.prototype.unselected = function (relation) {
                    return !this.selected(relation);
                };
                AnnotateComponent.prototype.allSelected = function () {
                    var selected = new Array();
                    for (var rel in this.selections) {
                        if (this.selections[rel]) {
                            selected.push(rel);
                        }
                    }
                    return selected;
                };
                AnnotateComponent.prototype.elapsedTime = function () {
                    return new Date().getTime() - this.startTime;
                };
                AnnotateComponent.prototype.saveData = function () {
                    var _this = this;
                    var annotations = this.allSelected();
                    var o = this;
                    this._annotateService.saveAnnotations(this.portpair, annotations, this.elapsedTime()).subscribe(function () { o.gotoWorkflow(); }, function (error) { return _this.errorMessage = error; });
                };
                AnnotateComponent.prototype.saveIgnore = function () {
                    var _this = this;
                    var o = this;
                    this._annotateService.noAnnotations(this.portpair, [], this.elapsedTime()).subscribe(function () { o.gotoWorkflow(); }, function (error) { return _this.errorMessage = error; });
                };
                AnnotateComponent.prototype.saveSkipped = function () {
                    var _this = this;
                    var o = this;
                    this._annotateService.skipAnnotations(this.portpair, [], this.elapsedTime()).subscribe(function () { o.gotoWorkflow(); }, function (error) { return _this.errorMessage = error; });
                };
                AnnotateComponent.prototype.gotoWorkflow = function () {
                    this._router.navigate(['Workflow', { name: this.portpair.getBundle() }]);
                };
                AnnotateComponent.prototype.noSelected = function () {
                    return this.allSelected().length == 0;
                };
                AnnotateComponent.prototype.hasSelected = function () {
                    return !this.noSelected();
                };
                AnnotateComponent.prototype.select = function (relation) {
                    this.selection(relation, true);
                };
                AnnotateComponent.prototype.selection = function (relations, state) {
                    if (typeof relations !== 'Array') {
                        relations = [relations];
                    }
                    for (var x in relations) {
                        var relation = relations[x];
                        if (relation) {
                            if (typeof relation === 'string') {
                                this.selections[relation] = state;
                            }
                            else if (typeof relation === 'object' && relation['id']) {
                                this.selections[relation.id] = state;
                            }
                        }
                    }
                };
                AnnotateComponent.prototype.unselect = function (relation) {
                    this.selection(relation, false);
                };
                AnnotateComponent.prototype.pick = function (r) {
                    r.picked = !r.picked;
                };
                AnnotateComponent.prototype.picked = function (r) {
                    return r.picked;
                };
                AnnotateComponent.prototype.unpicked = function (r) {
                    return !r.picked;
                };
                AnnotateComponent = __decorate([
                    core_1.Component({
                        template: "\n<div class=\"annotate-view\">\n  <h1>{{portpair.getFrom()}} <span [innerHTML]=\"portpair.getArrow()\"></span> {{portpair.getTo()}}</h1>\n\n  <div class=\"row\">\n    <div class=\"col-lg-6\">\n      <span class=\"pull-left label label-default pull-left\">{{elapsedTimeSoFar/1000}}s</span>\n    </div>\n    <div class=\"col-lg-6\">\n      <div class=\"pull-right\">\n        <button type=\"button\" [disabled]=\"noSelected()\" class=\"btn btn-danger\" (click)=\"saveData()\">Annotate!</button>\n        <button type=\"button\" [disabled]=\"hasSelected()\" class=\"btn btn-warning\" (click)=\"saveIgnore()\">Ignore.</button>\n        <button type=\"button\" class=\"btn btn-default\" (click)=\"saveSkipped()\">Later.</button>\n      </div>\n    </div>\n  </div>\n<hr/>\n  <div class=\"row\">\n    <div class=\"col-lg-4\">\n      <h6>Image</h6>\n      <img class=\"img-rounded img-responsive\" (click)=\"'/service/myexperiments/image/'+bundle\" src=\"/service/myexperiments/image/{{bundle}}\">\n    </div>\n    <features-list  class=\"col-lg-4\" *ngIf=\"portpair\" [portpair]=\"portpair\"></features-list>\n    <recommendations-list class=\"col-lg-4\" *ngIf=\"rules\" [rules]=\"rules\" [annotator]=\"annotator\" [datanode]=\"datanode\"></recommendations-list>\n  </div>\n<hr/>\n    <div class=\"row\">\n      <div class=\"col-lg-6\">\n        <span class=\"label label-default pull-left\">{{elapsedTimeSoFar}}</span>\n      </div>\n      <div>\n        <div class=\"pull-right\">\n          <button type=\"button\" [disabled]=\"noSelected()\" class=\"btn btn-danger\" (click)=\"saveData()\">Annotate!</button>\n          <button type=\"button\" [disabled]=\"hasSelected()\" class=\"btn btn-warning\" (click)=\"saveIgnore()\">Ignore.</button>\n          <button type=\"button\" class=\"btn btn-default\" (click)=\"saveSkipped()\">Later.</button>\n        </div>\n      </div>\n    </div>\n</div>\n\n    ",
                        providers: [datanode_service_1.DatanodeService, annotate_service_1.AnnotateService],
                        directives: [features_list_component_1.FeaturesList, recommendations_list_component_1.RecommendationsList],
                        styles: ["\n      .annotate-view {\n        padding-bottom: 5em;\n      }\n      "]
                    }), 
                    __metadata('design:paramtypes', [datanode_service_1.DatanodeService, annotate_service_1.AnnotateService, router_1.Router, router_1.RouteParams])
                ], AnnotateComponent);
                return AnnotateComponent;
            }());
            exports_1("AnnotateComponent", AnnotateComponent);
        }
    }
});
//# sourceMappingURL=annotate.component.js.map