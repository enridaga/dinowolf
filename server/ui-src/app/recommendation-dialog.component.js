System.register(['angular2/core', './annotate.service'], function(exports_1, context_1) {
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
    var core_1, annotate_service_1;
    var RecommendationDialog;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (annotate_service_1_1) {
                annotate_service_1 = annotate_service_1_1;
            }],
        execute: function() {
            RecommendationDialog = (function () {
                function RecommendationDialog(_service) {
                    this._service = _service;
                }
                RecommendationDialog.prototype.showDialog = function (rule) {
                    var _this = this;
                    var dialog = this;
                    this.rule = rule;
                    this.isVisible = false;
                    this._service.about(rule).subscribe(function (features) {
                        dialog.features = features;
                        dialog.isVisible = true;
                    }, function (error) { return _this.errorMessage = error; });
                };
                RecommendationDialog.prototype.hideDialog = function () {
                    this.isVisible = false;
                    this.rule = null;
                };
                RecommendationDialog.prototype.ngOnInit = function () {
                };
                RecommendationDialog = __decorate([
                    core_1.Component({
                        selector: 'recommendation-dialog',
                        template: "\n    <div *ngIf=\"isVisible\" class=\"modal fade show in error\" id=\"recommendationDialog\" role=\"dialog\">\n        <div class=\"modal-dialog modal-lg\">\n            <div class=\"modal-content\">\n                <div class=\"modal-header\">\n                    <button type=\"button\" class=\"btn btn-default pull-right\" (click)=\"hideDialog()\">Close</button>\n                    <h4 class=\"modal-title\">\n                    <span *ngFor=\"#h of rule.head\">{{h}} </span>\n                    </h4>\n                </div>\n                <div class=\"modal-body\" style=\"overflow-y: scroll; height:300px\">\n                <p>Support: {{rule.support}}</p>\n                <p>Confidence: {{rule.confidence}}</p>\n                <p>Relative confidence: {{rule.relativeConfidence}}</p>\n                <h5>Features:</h5>\n                <ul>\n                  <li *ngFor=\"#b of features\">{{b.n}}: {{b.v}} </li>\n                </ul>\n                </div>\n                <div class=\"modal-footer\">\n                  <button type=\"button\" class=\"btn btn-default\" (click)=\"hideDialog()\">Close</button>\n                </div>\n            </div>\n        </div>\n    </div>\n    ",
                        providers: [annotate_service_1.AnnotateService]
                    }), 
                    __metadata('design:paramtypes', [annotate_service_1.AnnotateService])
                ], RecommendationDialog);
                return RecommendationDialog;
            }());
            exports_1("RecommendationDialog", RecommendationDialog);
        }
    }
});
//# sourceMappingURL=recommendation-dialog.component.js.map