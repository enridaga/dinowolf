System.register(['angular2/core', './app.models', './datanode.service', './annotate.service'], function(exports_1, context_1) {
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
    var core_1, app_models_1, datanode_service_1, annotate_service_1;
    var AnnotateDialog;
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
            function (annotate_service_1_1) {
                annotate_service_1 = annotate_service_1_1;
            }],
        execute: function() {
            AnnotateDialog = (function () {
                function AnnotateDialog(_datanodeService, _annotateService) {
                    this._datanodeService = _datanodeService;
                    this._annotateService = _annotateService;
                    this.datanode = this._datanodeService.tree();
                }
                AnnotateDialog.prototype.showDialog = function (portpair) {
                    var _this = this;
                    this.portpair = portpair;
                    this.selections = new Object();
                    this.isVisible = true;
                    this._annotateService.recommend(portpair).subscribe(function (rules) { return _this.rules = rules; }, function (error) { return _this.errorMessage = error; });
                };
                AnnotateDialog.prototype.recommended = function () {
                    var recommended = new Array();
                    for (var x in this.rules) {
                        console.log("", this.rules[x]);
                        recommended.push(this.rules[x]);
                    }
                    return recommended;
                };
                AnnotateDialog.prototype.unrecommended = function () {
                    var unrec = new Array();
                    var rec = this.recommended();
                    for (var x in this.relations) {
                        if (rec.indexOf(this.relations[x]) === -1) {
                            unrec.push(this.relations[x]);
                        }
                    }
                    return unrec;
                };
                AnnotateDialog.prototype.hideDialog = function () {
                    this.isVisible = false;
                };
                AnnotateDialog.prototype.ngOnInit = function () {
                    var _this = this;
                    this._datanodeService.list().subscribe(function (relations) { return _this.relations = relations; }, function (error) { return _this.errorMessage = error; });
                };
                AnnotateDialog.prototype.toggleSelection = function (relation) {
                    if (this.selections[relation.id] == null) {
                        this.selections[relation.id] = true;
                    }
                    else {
                        this.selections[relation.id] = !this.selections[relation.id];
                    }
                };
                AnnotateDialog.prototype.selected = function (relation) {
                    if (relation instanceof app_models_1.Rule) {
                    }
                    if (this.selections[relation.id] == null) {
                        this.selections[relation.id] = false;
                    }
                    return this.selections[relation.id];
                };
                AnnotateDialog.prototype.unselected = function (relation) {
                    return !this.selected(relation);
                };
                AnnotateDialog.prototype.allSelected = function () {
                    var selected = new Array();
                    for (var rel in this.selections) {
                        if (this.selections[rel]) {
                            selected.push(rel);
                        }
                    }
                    return selected;
                };
                AnnotateDialog.prototype.saveData = function () {
                    var _this = this;
                    console.log("save button hit");
                    var annotations = this.allSelected();
                    this._annotateService.saveAnnotations(this.portpair, annotations, this.elapsedTime()).subscribe(function (res) { }, function (error) { return _this.errorMessage = error; });
                    this.hideDialog();
                };
                AnnotateDialog.prototype.noSelected = function () {
                    return this.allSelected().length == 0;
                };
                AnnotateDialog.prototype.hasSelected = function () {
                    return !this.noSelected();
                };
                AnnotateDialog = __decorate([
                    core_1.Component({
                        selector: 'annotate-dialog',
                        template: "\n    <div *ngIf=\"isVisible\" class=\"modal fade show in error\" id=\"annotationDialog\" role=\"dialog\">\n        <div class=\"modal-dialog modal-lg\">\n            <div class=\"modal-content\">\n                <div class=\"modal-header\">\n                    <button type=\"button\" class=\"close\" data-dismiss=\"modal\" (click)=\"hideDialog()\">&times;</button>\n                    <h4 class=\"modal-title\">Annotate {{portpair.getFrom()}} <span [innerHTML]=\"portpair.getArrow()\"></span> {{portpair.getTo()}}</h4>\n                </div>\n                <div class=\"modal-body text-center\">\n                <a class=\"btn btn-xs\"\n                  *ngFor=\"#rule of recommended()\"\n                  (click)=\"toggleSelection(rule)\"\n                  [ngClass]=\"{'btn-primary': unselected(rule), 'btn-success': selected(rule)}\"\n                >{{rule.label}}</a>\n                </div>\n                <div class=\"modal-body text-center\">\n                <a class=\"btn btn-default btn-xs\"\n                  *ngFor=\"#relation of unrecommended()\"\n                  (click)=\"toggleSelection(relation)\"\n                  data-toggle=\"tooltip\"\n                  data-placement=\"left\"\n                  [ngClass]=\"{'btn-default': unselected(relation), 'btn-success': selected(relation)}\"\n                  [title]=\"relation.comment\"\n                ><span *ngIf=\"relation.label\">{{relation.label}}</span><span *ngIf=\"!relation.label\">{{relation.id}}</span></a>\n                </div>\n                <div class=\"modal-footer\">\n                  <span class=\"label label-primary pull-left\">Recommended</span>\n                  <span class=\"label label-success pull-left\">Selected</span>\n                  <button type=\"button\" [disabled]=\"noSelected()\" class=\"btn btn-danger\" (click)=\"saveData()\">Annotate!</button>\n                  <button type=\"button\" [disabled]=\"hasSelected()\" class=\"btn btn-warning\" (click)=\"saveIgnore()\">Ignore.</button>\n                  <button type=\"button\" class=\"btn btn-default\" (click)=\"hideDialog()\">Later.</button>\n                </div>\n            </div>\n        </div>\n    </div>\n    ",
                        providers: [datanode_service_1.DatanodeService, annotate_service_1.AnnotateService]
                    }), 
                    __metadata('design:paramtypes', [datanode_service_1.DatanodeService, annotate_service_1.AnnotateService])
                ], AnnotateDialog);
                return AnnotateDialog;
            }());
            exports_1("AnnotateDialog", AnnotateDialog);
        }
    }
});
//# sourceMappingURL=value-dialog.component.js.map