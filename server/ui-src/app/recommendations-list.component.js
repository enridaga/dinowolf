System.register(['angular2/core', './recommendation-dialog.component', './datanode-item.component', './workflow-details.service', './app.models'], function(exports_1, context_1) {
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
    var core_1, recommendation_dialog_component_1, datanode_item_component_1, workflow_details_service_1, app_models_1;
    var RecommendationsList;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (recommendation_dialog_component_1_1) {
                recommendation_dialog_component_1 = recommendation_dialog_component_1_1;
            },
            function (datanode_item_component_1_1) {
                datanode_item_component_1 = datanode_item_component_1_1;
            },
            function (workflow_details_service_1_1) {
                workflow_details_service_1 = workflow_details_service_1_1;
            },
            function (app_models_1_1) {
                app_models_1 = app_models_1_1;
            }],
        execute: function() {
            RecommendationsList = (function () {
                function RecommendationsList(_service) {
                    this._service = _service;
                }
                RecommendationsList.prototype.ngOnInit = function () {
                };
                RecommendationsList.prototype.showRecommendationDialog = function (rule) {
                    this.dialog.showDialog(rule);
                };
                RecommendationsList.prototype.recommended = function () {
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
                __decorate([
                    core_1.Input(), 
                    __metadata('design:type', Array)
                ], RecommendationsList.prototype, "rules", void 0);
                __decorate([
                    core_1.Input(), 
                    __metadata('design:type', Object)
                ], RecommendationsList.prototype, "annotator", void 0);
                __decorate([
                    core_1.Input(), 
                    __metadata('design:type', app_models_1.Datanode)
                ], RecommendationsList.prototype, "datanode", void 0);
                __decorate([
                    core_1.ViewChild(recommendation_dialog_component_1.RecommendationDialog), 
                    __metadata('design:type', recommendation_dialog_component_1.RecommendationDialog)
                ], RecommendationsList.prototype, "dialog", void 0);
                RecommendationsList = __decorate([
                    core_1.Component({
                        selector: 'recommendations-list',
                        template: "\n    <h6>Recommendations</h6>\n    <div *ngIf=\"rules\" #recommendationsList>\n      <div *ngFor=\"#rule of recommended()\">\n          <datanode-item *ngFor=\"#item of rule.head\" [root]=\"item\" [annotator]=\"annotator\" [datanode]=\"datanode\" [item]=\"item\"></datanode-item>\n          <small>{{rule.support.toFixed(2)}} / {{rule.confidence.toFixed(2)}} / {{rule.relativeConfidence.toFixed(2)}}</small>\n          <a class=\"btn btn-warning btn-xs pull-right\"\n           (click)=\"showRecommendationDialog(rule)\">More</a>\n          <hr/>\n      </div>\n      <div>\n        <datanode-item [datanode]=\"datanode\" [annotator]=\"annotator\" [item]=\"'relatedWith'\"></datanode-item>\n      </div>\n    </div>\n    <recommendation-dialog></recommendation-dialog>\n    ",
                        styles: ["\n      #recommendationsList {\n        background-color: #EEE;\n        border: 1px solid #DDD;\n      }\n    "],
                        providers: [workflow_details_service_1.WorkflowDetailsService],
                        directives: [datanode_item_component_1.DatanodeItem, recommendation_dialog_component_1.RecommendationDialog] }), 
                    __metadata('design:paramtypes', [workflow_details_service_1.WorkflowDetailsService])
                ], RecommendationsList);
                return RecommendationsList;
            }());
            exports_1("RecommendationsList", RecommendationsList);
        }
    }
});
//# sourceMappingURL=recommendations-list.component.js.map