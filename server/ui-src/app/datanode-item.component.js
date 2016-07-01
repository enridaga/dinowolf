System.register(['angular2/core', './app.models'], function(exports_1, context_1) {
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
    var core_1, app_models_1;
    var DatanodeItem;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (app_models_1_1) {
                app_models_1 = app_models_1_1;
            }],
        execute: function() {
            DatanodeItem = (function () {
                function DatanodeItem() {
                    this.item = 'realatedWith';
                    this.root = null;
                }
                DatanodeItem.prototype.datanodeChildren = function () {
                    if (!this.datanode)
                        return [];
                    if (this.item == '') {
                        return ['relatedWith'];
                    }
                    return this.datanode.childrenOf(this.item);
                };
                DatanodeItem.prototype.datanodeItem = function (rel) {
                    if (!this.datanode)
                        return;
                    return this.datanode.item(rel);
                };
                DatanodeItem.prototype.ngOnInit = function () {
                    if (!this.item) {
                        this.expanded = true;
                    }
                };
                DatanodeItem.prototype.toggleExpanded = function () {
                    if (this.item) {
                        this.expanded = !this.expanded;
                    }
                };
                DatanodeItem.prototype.comment = function (str) {
                    if (str) {
                        // hack I don't know why sometimes datanode is undefined!
                        if (!this.datanode) {
                            return str;
                        }
                        var i = this.datanode.item(str);
                        if (i.comment) {
                            return i.comment;
                        }
                        else {
                            return this.label(str);
                        }
                    }
                };
                DatanodeItem.prototype.label = function (str) {
                    if (str) {
                        // hack I don't know why sometimes datanode is undefined!
                        if (!this.datanode) {
                            return str;
                        }
                        var i = this.datanode.item(str);
                        if (i.label) {
                            return i.label;
                        }
                        else {
                            return i.id;
                        }
                    }
                };
                __decorate([
                    core_1.Input(), 
                    __metadata('design:type', String)
                ], DatanodeItem.prototype, "item", void 0);
                __decorate([
                    core_1.Input(), 
                    __metadata('design:type', String)
                ], DatanodeItem.prototype, "root", void 0);
                __decorate([
                    core_1.Input(), 
                    __metadata('design:type', app_models_1.Datanode)
                ], DatanodeItem.prototype, "datanode", void 0);
                __decorate([
                    core_1.Input(), 
                    __metadata('design:type', Object)
                ], DatanodeItem.prototype, "annotator", void 0);
                DatanodeItem = __decorate([
                    core_1.Component({
                        selector: 'datanode-item',
                        providers: [],
                        directives: [DatanodeItem],
                        template: "\n    <div class=\"dn-item\">\n      <p [class.selected]=\"annotator.selections[item]\"\n      [title]=\"comment(item)\">\n        <input\n          type=\"checkbox\"\n          *ngIf=\"annotator\"\n          [ngModel]=\"annotator.selections[item]\"\n          (change)=\"annotator.selection(item, cv.checked) ; annotator.selection(root, cv.checked)\"\n          #cv />\n        {{label(item)}}\n        <a class=\"btn btn-xs btn-default\" *ngIf=\"item && (datanodeChildren().length > 0)\" (click)=\"toggleExpanded()\"><i class=\"fa\" [ngClass]=\"{'fa-plus': !expanded,'fa-minus': expanded}\"></i></a>\n      </p>\n      <ul *ngIf=\"expanded\">\n        <li *ngFor=\"#child of datanodeChildren()\">\n          <datanode-item [annotator]=\"annotator\" [root]=\"root\" [datanode]=\"datanode\" *ngIf=\"child\" [item]=\"child\"></datanode-item>\n        </li>\n      </ul>\n    </div>\n  ",
                        styles: ["\n    .dn-item ul {\n      margin-left: 2em;\n    }\n    .dn-item p {\n      cursor: default;\n    }\n    .dn-item a {\n      cursor: pointer;\n      display: inline;\n    }\n    .nd-item ul li {\n\n    }\n  "]
                    }), 
                    __metadata('design:paramtypes', [])
                ], DatanodeItem);
                return DatanodeItem;
            }());
            exports_1("DatanodeItem", DatanodeItem);
        }
    }
});
//# sourceMappingURL=datanode-item.component.js.map