/*
 * Copyright (C) 2014 salesforce.com, inc.
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
/**
 * @namespace Global Value Provider. Holds global values: $Label, $Browser, $Locale, etc
 * @param {Object} gvp an optional serialized GVP to load.
 * @param {Function} initCallback an optional callback invoked after the GVP has finished its
 *  asynchronous initialization.
 * @constructor
 */
$A.ns.GlobalValueProviders = function (gvp, initCallback) {

    this.globalValueProviders = {};
    this.valueProviders = {
        "$Label": new $A.ns.LabelValueProvider()
    };

    var that = this;
    this.loadFromStorage(function() {
        that.load(gvp);
        if (initCallback) {
            initCallback();
        }
    });
};

/**
 * Joins new GVPs with existing and saves to storage
 *
 * @param {Object} gvps
 * @param {Boolean} doNotPersist
 * @private
 */
$A.ns.GlobalValueProviders.prototype.join = function(gvps, doNotPersist) {
    if (gvps) {
        var storage;
        var storedGvps;
        if (!doNotPersist) {
            // If persistent storage is active then write through for disconnected support
            storage = this.getStorage();
            storedGvps = [];
        }

        for ( var i = 0; i < gvps.length; i++) {
            var newGvp = gvps[i];
            var t = newGvp["type"];
            var vp = this.getValueProvider(t);
            var gvp = vp.getValues();
            if (!gvp) {
                gvp = new MapValue(newGvp["values"]);
                this.globalValueProviders[t] = gvp;
            } else {
                var mergeMap = new MapValue(newGvp["values"]);
                gvp.merge(mergeMap, true);
            }

            // set values into its value provider
            vp.setValues(gvp);

            if (storage) {
                storedGvps.push({
                    "type" : t,
                    "values" : gvp.unwrap()
                });
            }
        }

        if (storage) {
            storage.put("globalValueProviders", storedGvps);
        }
    }
};

/**
 * Wrapper to get storage.
 *
 * @return {Object} storage - undefined if no storage exists
 * @private
 */
$A.ns.GlobalValueProviders.prototype.getStorage = function () {

    //$A.getContext() isn't ready at this point.
    //return $A.getContext().getStorage();

    var storage = $A.storageService.getStorage("actions");
    if (!storage) {
        return undefined;
    }

    var config = $A.storageService.getAdapterConfig(storage.getName());
    return config["persistent"] ? storage : undefined;
};

/**
 * load GVPs from storage if available
 * @param {Function} callback a callback invoked after the GVP is optionally asynchronously loaded from
 *   storage. If storage is not applicable it is invoked immediately. The first argument is a boolean
 *   specifying whether values were loaded from storage.
 * @private
 */
$A.ns.GlobalValueProviders.prototype.loadFromStorage = function(callback) {
    // If persistent storage is active then write through for disconnected support
    var storage = this.getStorage();
    var that = this;
    if (storage) {
        storage.get("globalValueProviders").then(function (item) {
            $A.run(function() {
                if (item) {
                    that.join(item, true);
                }
                callback(!!item);
            });
        });
    } else {
        // nothing loaded from persistent storage
        $A.run(function() {
            callback(false);
        });
    }
};

/**
 * Loads GVP config when from context
 *
 * @param {Object} gvp Global Value Providers
 * @private
 */
$A.ns.GlobalValueProviders.prototype.load = function(gvp) {
    if (gvp) {
        var l = gvp.length;
        for ( var i = 0; i < l; i++) {
            var g = gvp[i],
                type = g["type"],
                values = new MapValue(g["values"]);
            this.getValueProvider(type).setValues(values);
        }
    }
};

/**
 * Returns value provider or empty SimpleValueProvider
 *
 * @param {String} type - key for value provider
 * @return {Object} ValueProvider
 * @private
 */
$A.ns.GlobalValueProviders.prototype.getValueProvider = function(type) {
    if ( !this.valueProviders[type] ) {
        this.valueProviders[type] = new $A.ns.SimpleValueProvider();
    }
    return this.valueProviders[type];
};

/**
 * Creates property reference chain from string expression
 *
 * @param expression
 * @return {PropertyChain}
 * @private
 */
$A.ns.GlobalValueProviders.prototype.createPropertyRef = function(expression) {
    if ($A.util.isString(expression)) {
        expression = valueFactory.parsePropertyReference(expression);
    }
    return expression;
};

/**
 * Checks whether expression is for global values.
 * @private
 * @param {String} expression
 * @return {Boolean}
 */
$A.ns.GlobalValueProviders.prototype.isGlobalValueExp = function(expression) {
    expression = this.createPropertyRef(expression);

    if (!$A.util.isUndefinedOrNull(expression) && ("getRoot" in expression)) {
        if (expression.getRoot().charAt(0) === '$') {
            return true;
        }
    }

    return false;
};

/**
 * Calls getValue for Value Object. Unwraps and calls callback if provided.
 *
 * @param {String} expression
 * @param {Component} component
 * @return {String} The value of expression
 */
$A.ns.GlobalValueProviders.prototype.get = function(expression, component, callback) {
    return $A.unwrap(this.getValue(expression, component, function(result) {
        if($A.util.isFunction(callback)) {
            callback.call(null, $A.unwrap(result));
        }
    } ));
};

/**
 * Delegates to value provider.
 *
 * @param {String} expression
 * @param {Component} component
 * @param {Function} callback
 * @return {SimpleValue}
 */
$A.ns.GlobalValueProviders.prototype.getValue = function(expression, component, callback) {
    // in case anything other than gvps are requested
    if( !this.isGlobalValueExp(expression) ) {
        return undefined;
    }

    expression = this.createPropertyRef(expression);

    var type = expression.getRoot(),
        vp = this.getValueProvider(type);

    return vp.getValue(expression, component, callback);

};

//#include aura.provider.GlobalValueProviders_export
