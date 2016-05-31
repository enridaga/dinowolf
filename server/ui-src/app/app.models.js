System.register([], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var Feature, Bundle, Annotation, Workflow, Features;
    return {
        setters:[],
        execute: function() {
            Feature = (function () {
                function Feature(name, value) {
                    this.name = name;
                    this.value = value;
                }
                return Feature;
            }());
            exports_1("Feature", Feature);
            Bundle = (function () {
                function Bundle(id, name) {
                    this.id = id;
                    this.name = name;
                }
                return Bundle;
            }());
            exports_1("Bundle", Bundle);
            Annotation = (function () {
                function Annotation(on, id, rdf) {
                    this.on = on;
                    this.id = id;
                    this.rdf = rdf;
                }
                return Annotation;
            }());
            exports_1("Annotation", Annotation);
            Workflow = (function () {
                function Workflow(name, title, description, creator) {
                    this.name = name;
                    this.title = title;
                    this.description = description;
                    this.creator = creator;
                }
                return Workflow;
            }());
            exports_1("Workflow", Workflow);
            Features = (function () {
                function Features(_map) {
                    this._map = _map;
                }
                Features.prototype._sortByDepth = function (a, b) {
                    var _depths = ['From', 'To', 'FromToPorts', 'OtherPort', 'Activity', 'Processor', 'Workflow'];
                    return _depths.indexOf(a.l) - _depths.indexOf(b.l);
                };
                Features.prototype.getPortpairs = function () {
                    var pairs = new Array();
                    for (var p in this._map) {
                        pairs.push(p);
                    }
                    return pairs.sort();
                };
                Features.prototype.getFeatures = function (portPair) {
                    var fff = this._map[portPair];
                    return fff.sort(this._sortByDepth);
                    //       return fff;
                };
                return Features;
            }());
            exports_1("Features", Features);
        }
    }
});
//# sourceMappingURL=app.models.js.map