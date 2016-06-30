System.register(['angular2/core', './feature-dialog.component', './workflow-details.service', './app.models'], function(exports_1, context_1) {
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
    var core_1, feature_dialog_component_1, workflow_details_service_1, app_models_1;
    var FeaturesList;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (feature_dialog_component_1_1) {
                feature_dialog_component_1 = feature_dialog_component_1_1;
            },
            function (workflow_details_service_1_1) {
                workflow_details_service_1 = workflow_details_service_1_1;
            },
            function (app_models_1_1) {
                app_models_1 = app_models_1_1;
            }],
        execute: function() {
            FeaturesList = (function () {
                function FeaturesList(_service) {
                    this._service = _service;
                }
                FeaturesList.prototype.ngOnInit = function () {
                    var _this = this;
                    this._service.getFeatures(this.portpair.getBundle(), this.portpair.getId()).subscribe(function (features) { return _this.features = features; }, function (error) { return _this.errorMessage = error; });
                };
                FeaturesList.prototype.showFeatureDialog = function (feature) {
                    this.dialog.showDialog(feature);
                };
                __decorate([
                    core_1.Input(), 
                    __metadata('design:type', app_models_1.Portpair)
                ], FeaturesList.prototype, "portpair", void 0);
                __decorate([
                    core_1.ViewChild(feature_dialog_component_1.FeatureDialog), 
                    __metadata('design:type', feature_dialog_component_1.FeatureDialog)
                ], FeaturesList.prototype, "dialog", void 0);
                FeaturesList = __decorate([
                    core_1.Component({
                        selector: 'features-list',
                        template: "\n    <h5>Features</h5>\n    <div *ngIf=\"features\" class=\"featuresList\">\n      <div *ngFor=\"#feature of features.getFeatures(portpair.getId())\">\n          <strong>{{feature.l}}/{{feature.n}}</strong><br/>\n          <a style=\"word-break: break-all;\" target=\"_dbpedia\" *ngIf=\"feature.v.startsWith('http:')\" [href]=\"feature.v\"\n          [innerHTML]=\"(feature.v.indexOf('#') != -1)?feature.v.substr(feature.v.lastIndexOf('#')+1):feature.v.substr(feature.v.lastIndexOf('/')+1)\"></a>\n          <span style=\"word-break: break-all;\" *ngIf=\"!feature.v.startsWith('http:')\">{{feature.v | slice:0:100}}</span>\n          <span *ngIf=\"feature.v.length > 100\">...</span>\n          <a *ngIf=\"feature.v.length > 100\" class=\"btn btn-default btn-xs pull-right\"\n           (click)=\"showFeatureDialog(feature)\"><i class=\"fa fa-expand\" aria-hidden=\"true\" aria-label=\"Expand\"></i></a>\n      </div>\n    <!--  <div *ngFor=\"#group of features.getGroups(portpair.getId())\">\n          <strong>{{group}}</strong><br/>\n          <ul>\n            <li *ngFor=\"#feature of features.getGroup(portpair.getId(), group)\"\n            style=\"word-break: break-all;\">{{feature.v | slice:0:100}}&nbsp;\n            <span *ngIf=\"feature.v.length > 100\">...</span>\n            <a *ngIf=\"feature.v.length > 100\" class=\"btn btn-warning btn-xs pull-right\"\n             (click)=\"showFeatureDialog(feature)\">More</a>\n            </li>\n          </ul>\n      </div> -->\n    </div>\n    <feature-dialog></feature-dialog>\n    ",
                        styles: ["\n      .featuresList ul {\n        margin: 1em;\n        padding: 0em;\n      }\n      .featuresList ul li {\n        margin:0px;\n        list-style:none;\n        padding-left:0;\n      }\n    "],
                        providers: [workflow_details_service_1.WorkflowDetailsService],
                        directives: [feature_dialog_component_1.FeatureDialog] }), 
                    __metadata('design:paramtypes', [workflow_details_service_1.WorkflowDetailsService])
                ], FeaturesList);
                return FeaturesList;
            }());
            exports_1("FeaturesList", FeaturesList);
        }
    }
});
//# sourceMappingURL=features-list.component.js.map