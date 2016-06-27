System.register(['angular2/core'], function(exports_1, context_1) {
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
    var core_1;
    var FeatureDialog;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            }],
        execute: function() {
            FeatureDialog = (function () {
                function FeatureDialog() {
                }
                FeatureDialog.prototype.showDialog = function (feature) {
                    this.feature = feature;
                    this.isVisible = true;
                };
                FeatureDialog.prototype.hideDialog = function () {
                    this.isVisible = false;
                    this.feature = null;
                };
                FeatureDialog.prototype.ngOnInit = function () {
                };
                FeatureDialog = __decorate([
                    core_1.Component({
                        selector: 'feature-dialog',
                        template: "\n    <div *ngIf=\"isVisible\" class=\"modal fade show in error\" id=\"annotationDialog\" role=\"dialog\">\n        <div class=\"modal-dialog modal-lg\">\n            <div class=\"modal-content\">\n                <div class=\"modal-header\">\n                    <h4 class=\"modal-title\">{{feature.n}}</h4>\n                </div>\n                <div class=\"modal-body\">\n                <code>{{feature.v}}</code>\n                </div>\n                <div class=\"modal-footer\">\n                  <button type=\"button\" class=\"btn btn-default\" (click)=\"hideDialog()\">Close</button>\n                </div>\n            </div>\n        </div>\n    </div>\n    ",
                        providers: []
                    }), 
                    __metadata('design:paramtypes', [])
                ], FeatureDialog);
                return FeatureDialog;
            }());
            exports_1("FeatureDialog", FeatureDialog);
        }
    }
});
//# sourceMappingURL=feature-dialog.component.js.map