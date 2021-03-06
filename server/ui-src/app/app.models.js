System.register([], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var Feature, Rule, Bundle, Annotation, Workflow, Features, Portpair, Annotations, Datanode;
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
            Rule = (function () {
                function Rule(head, body, confidence, support, relativeConfidence) {
                    this.head = head;
                    this.body = body;
                    this.confidence = confidence;
                    this.support = support;
                    this.relativeConfidence = relativeConfidence;
                    this.picked = false;
                    this.label = this.head.join(',') + ' (' + this.support + ', ' + this.confidence + ', ' + this.relativeConfidence + ')';
                }
                return Rule;
            }());
            exports_1("Rule", Rule);
            Bundle = (function () {
                function Bundle(id, name, progress) {
                    this.id = id;
                    this.name = name;
                    this.progress = progress;
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
                    var r = _depths.indexOf(a.l) - _depths.indexOf(b.l);
                    if (r == 0) {
                        if (a.v > b.v) {
                            return -1;
                        }
                        else if (a.v < b.v) {
                            return 1;
                        }
                        else {
                            return 0;
                        }
                    }
                    return r;
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
                };
                Features.prototype.getGroupedFeatures = function (portPair) {
                    var fff = this._map[portPair];
                    var groups = {};
                    for (var i in fff) {
                        if (typeof groups[fff[i].n] === 'undefined') {
                            groups[fff[i].n] = new Array();
                        }
                        groups[fff[i].n].push(fff[i]);
                    }
                    return groups;
                };
                Features.prototype.getGroups = function (portPair) {
                    var fff = this._map[portPair];
                    var groups = new Array();
                    for (var i in fff) {
                        if (groups.indexOf(fff[i].n) == -1) {
                            groups.push(fff[i].n);
                        }
                    }
                    console.log('groups', groups);
                    return groups;
                };
                Features.prototype.getGroup = function (portPair, group) {
                    var feats = new Array();
                    var fff = this._map[portPair];
                    for (var i in fff) {
                        if (fff[i].n == group) {
                            feats.push(fff[i]);
                        }
                    }
                    // console.log(group, feats);
                    return feats;
                };
                return Features;
            }());
            exports_1("Features", Features);
            Portpair = (function () {
                function Portpair(id) {
                    this.id = id;
                    var pp = id.split('/');
                    this.bundle = pp[0];
                    this.workflow = pp[1];
                    this.processor = pp[2];
                    var p = pp[3].split(':');
                    this.direction = p[0];
                    this.from = p[1];
                    this.to = p[2];
                }
                Portpair.prototype.getBundle = function () { return this.bundle; };
                Portpair.prototype.getWorkflow = function () { return this.workflow; };
                Portpair.prototype.getProcessor = function () { return this.processor; };
                Portpair.prototype.getDirection = function () { return this.direction; };
                Portpair.prototype.getArrow = function () { return "&#x2192;"; };
                Portpair.prototype.getFrom = function () { return this.from; };
                Portpair.prototype.getTo = function () { return this.to; };
                Portpair.prototype.getId = function () { return this.id; };
                Portpair.prototype.getLabel = function () {
                    return this.getFrom() + " " + this.getArrow() + " " + this.getTo();
                };
                return Portpair;
            }());
            exports_1("Portpair", Portpair);
            Annotations = (function () {
                function Annotations(_map) {
                    this._map = _map;
                }
                Annotations.prototype.getPortpairs = function () {
                    var pairs = new Array();
                    for (var p in this._map) {
                        pairs.push(new Portpair(p));
                    }
                    return pairs.sort(function (a, b) {
                        var ret;
                        ret = a.getBundle().localeCompare(b.getBundle());
                        if (ret == 0) {
                            ret = a.getWorkflow().localeCompare(b.getWorkflow());
                        }
                        if (ret == 0) {
                            ret = a.getProcessor().localeCompare(b.getProcessor());
                        }
                        if (ret == 0) {
                            ret = a.getFrom().localeCompare(b.getFrom());
                        }
                        if (ret == 0) {
                            ret = a.getTo().localeCompare(b.getTo());
                        }
                        return ret;
                    });
                };
                Annotations.prototype.getAnnotations = function (portPair) {
                    var fff = this._map[portPair.getId()];
                    return fff['annotations'];
                };
                Annotations.prototype.annotated = function (portPair) {
                    var fff = this._map[portPair.getId()];
                    return fff['annotated'] == true;
                };
                return Annotations;
            }());
            exports_1("Annotations", Annotations);
            Datanode = (function () {
                function Datanode(_o) {
                    this._o = _o;
                }
                Datanode.prototype.childrenOf = function (rel) {
                    if (rel == '') {
                        return ['relatedWith'];
                    }
                    return this.item(rel).subProperties;
                };
                Datanode.prototype.item = function (id) {
                    return this._o[id];
                };
                return Datanode;
            }());
            exports_1("Datanode", Datanode);
        }
    }
});
//@Pipe({
//  name: 'slice'
//})
//export class SlicePipe implements PipeTransform {
//  transform(val:string, params:string[]):string {
//    return val.split(params[0]);
//  }
//}
//# sourceMappingURL=app.models.js.map