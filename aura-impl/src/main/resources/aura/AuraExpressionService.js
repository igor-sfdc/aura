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
/**
 * @namespace The Aura Expression Service, accessible using $A.expressionService.  Processes Expressions.
 * @constructor
 */
var AuraExpressionService = function AuraExpressionService(){
	var propertyRefCache = {};

    var expressionService = {
        setValue : function(valueProvider, expression, value){
            if (expression.getValue) {
                expression = expression.getValue();
            }
            var lastDot = expression.lastIndexOf('.');
            aura.assert(lastDot>0, "Invalid expression for setValue");

            var parentExpression = expression.substring(0, lastDot)+"}";
            var lastPart = expression.substring(lastDot+1, expression.length-1);

            var parentValue = this.getValue(valueProvider, parentExpression);
            parentValue.getValue(lastPart).setValue(value);
        },

        /**
         * Get the wrapped value of an expression. Use <code>Component.getValue()</code> if you are retrieving the value of a component.
         * <code>$A.expressionService.get(cmp, "v.attribute")</code> is equivalent to <code>cmp.getValue("v.attribute")</code>.
         * @param {Object} valueProvider The value provider
         * @param {String} expression The expression to be evaluated
         * @param {Function} callback used to provide a way to e.g. populate dynamic labels
         * @param {Boolean} docreate used to create missing items
         * @public
         * @memberOf AuraExpressionService
         */
        getValue: function(valueProvider, expression, callback, docreate){
            if (aura.util.isString(expression)) {
            	var cached = propertyRefCache[expression];
            	if (!cached) {
	                cached = valueFactory.parsePropertyReference(expression);
	                propertyRefCache[expression] = cached;

	            	//console.debug("ExpressionService.getValue() cache property ref", [expression, propertyRefCache]);
            	}

                expression = cached;
            } else if ($A.util.instanceOf(expression, FunctionCallValue)) {
                return expression.getValue(valueProvider);
            }

            if (!$A.util.instanceOf(expression, PropertyChain)){
                return null;
            }

            // use gvp; supports existing usage of $A.get and $A.expressionService.get
            if (expression.getRoot().charAt(0) === '$'){
                var gvp = $A.getGlobalValueProviders();
                return gvp.getValue(expression, valueProvider, callback);
            }

            var propRef = expression;
            var value = valueProvider;
            while (propRef) {
                var root = propRef.getRoot();
                var lastvalue = value;
                value = value.getValue(root);
                propRef = propRef.getStem();

                if (!value || (value.isDefined && !value.isDefined())) {
                    if (!value || !docreate ||
                        !(lastvalue instanceof MapValue)) {
                        // still nothing, time to die
                        break;
                    }
                    // we're not done, and we want to create, so make a new map
                    var config = {};
                    if (propRef) {
                        // if we have more properties, we want a map
                        config[root] = {};
                    }
                    lastvalue.add(root, config);
                    value = lastvalue.getValue(root);
                }

            }

            // handle PropertyChain. get its value.
            if ($A.util.instanceOf(value, PropertyChain)) {
                value = this.getValue(valueProvider, value);
            }

            return value;
        },

        /**
         * Get the raw value referenced using property syntax. Use <code>Component.get()</code> if you are retrieving the value of a component.
         * @param {Object} valueProvider The value provider
         * @param {String} expression The expression to be evaluated
         * @param {Function} callback The method to call if a server trip is expected
         * @public
         * @memberOf AuraExpressionService
         */
        get : function(valueProvider, expression, callback){
            return $A.unwrap(this.getValue(valueProvider, expression, $A.util.isFunction(callback)?function(value){
                callback($A.unwrap(value));
            }:null));
        },

        /**
         * @deprecated JBUCH
         * @private
         */
        create : function(valueProvider, config){
            return valueFactory.create(config, null, valueProvider);
        },

        /**
         * @private
         */
        // TODO: unify with above create method
        createPassthroughValue : function(primaryProviders, cmp) {
            return new PassthroughValue(primaryProviders, cmp);
        }
    };
    //#include aura.AuraExpressionService_export

    return expressionService;
};
