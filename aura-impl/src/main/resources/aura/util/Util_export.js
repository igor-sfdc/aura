/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*jslint sub: true */
var p = $A.ns.Util.prototype;
exp(p,
    "isIE", p.isIE,
    "isIOSWebView", p.isIOSWebView,
    "isArray", p.isArray,
    "isBoolean", p.isBoolean,
    "isObject", p.isObject,
    "isError", p.isError,
    "isFunction", p.isFunction,
    "isNumber", p.isNumber,
    "isFiniteNumber", p.isFiniteNumber,
    "isString", p.isString,
    "isUndefined", p.isUndefined,
    "isUndefinedOrNull", p.isUndefinedOrNull,
    "isEmpty", p.isEmpty,
    "getElement", p.getElement,
    "getBooleanValue", p.getBooleanValue,
    "hasClass", p.hasClass,
    "addClass", p.addClass,
    "removeClass", p.removeClass,
    "toggleClass", p.toggleClass,
    "swapClass", p.swapClass,
    "insertFirst", p.insertFirst,
    "insertBefore", p.insertBefore,
    "insertAfter", p.insertAfter,
    "removeElement", p.removeElement,
    "attachToDocumentBody", p.attachToDocumentBody,
    "stringEndsWith",p.stringEndsWith,
    "urlDecode", p.urlDecode,
    "trim", p.trim,
    "truncate", p.truncate,
    "on", p.on,
    "removeOn", p.removeOn,
    "formToMap", p.formToMap,
    "getSelectValue", p.getSelectValue,
    "addValueToMap", p.addValueToMap,
    "addMapValueToMap", p.addMapValueToMap,
    "isSubDef", p.isSubDef,
    "getElementAttributeValue", p.getElementAttributeValue,
    "getDataAttribute", p.getDataAttribute,
    "setDataAttribute", p.setDataAttribute,
    "hasDataAttribute", p.hasDataAttribute,
    "createTimeoutCallback", p.createTimeoutCallback,
    "arrayIndexOf", p.arrayIndexOf,
    "contains", p.contains,
    "squash", p.squash,
    "stripTags", p.stripTags,
    "getWindowSize", p.getWindowSize,
    "isComponent", p.isComponent,
    "isValue", p.isValue,
    "instanceOf", p.instanceOf,
    "supportsTouchEvents", p.supportsTouchEvents,
    "estimateSize", p.estimateSize,
    "createPromise", p.createPromise,
    "when", p.when,
    "lookup", p.lookup,
    "map", p.map,
    "reduce", p.reduce,
    "forEach", p.forEach,
    "merge", p.merge,
    "every", p.every,
    "some", p.some,
    "filter", p.filter,
    "keys", p.keys,
    "bind", p.bind,
    "includeScript", p.includeScript,
    "equalBySource", p.equalBySource
    
    //#if {"excludeModes" : ["PRODUCTION", "PRODUCTIONDEBUG"]}
	    ,
	    "getDebugToolComponent", p.getDebugToolComponent,
	    "setDebugToolWindow", p.setDebugToolWindow,
	    "getDebugToolsAuraInstance",  p.getDebugToolsAuraInstance,
	    "getUrl", p.getUrl,
	    "getText", p.getText,
	    "errorBasedOnMode", p.errorBasedOnMode
    //#end
);

