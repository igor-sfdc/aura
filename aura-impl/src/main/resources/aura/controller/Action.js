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
/**
 * A base class for an Aura Action to be passed to an associated component. An Action is created in a client-side or
 * server-side controller. Invoke an Action in a controller by declaring cmp.get("c.actionName"). Call a server-side
 * Action from a client-side controller.
 *
 * @constructor
 * @class
 * @param {Object}
 *            def The definition of the Action.
 * @param {string}
 *            suffix A suffix to distinguish components.
 * @param {function}
 *            method The method for the Action. For client-side Action only. A function to serialize the Action as a
 *            String in the JSON representation.
 * @param {Object}
 *            paramDefs The parameter definitions for the Action.
 * @param {boolean}
 *            background is the action defined as a background action?
 * @param {Component}
 *            cmp The component associated with the Action.
 * @param {boolean}
 *            caboose should this action wait for the next non boxcar action?
 */
function Action(def, suffix, method, paramDefs, background, cmp, caboose) {
    this.def = def;
    this.meth = method;
    this.paramDefs = paramDefs;
    this.background = background;
    this.cmp = cmp;
    this.params = {};
    this.responseState = null;
    this.state = "NEW";
    this.callbacks = {};
    this.events = [];
    this.groups = [];
    this.components = null;
    this.actionId = Action.prototype.nextActionId++;
    this.id = this.actionId + "." + suffix;
    this.originalResponse = undefined;
    this.storable = false;
    this.caboose = caboose;
    this.allAboardCallback = undefined;

    this.pathStack = [];
    this.canCreate = true;
    // start with a body
    this.pushCreationPath("body");
    this.setCreationPathIndex(0);
}

// Static methods:

Action.getStorageKey = function(descriptor, params) {
	return descriptor + ":" + $A.util["json"].encode(params);
};

Action.getStorage = function() {
    return $A.storageService.getStorage("actions");
};

// Instance methods:

Action.prototype.nextActionId = 1;
Action.prototype.auraType = "Action";

/**
 * Gets the Action Id.
 *
 * @private
 * @returns {string}
 */
Action.prototype.getId = function() {
    return this.id;
};

/**
 * Gets the next action scoped Id.
 *
 * @private
 * @returns {string}
 */
Action.prototype.getNextGlobalId = function() {
    if (!this.nextGlobalId) {
        this.nextGlobalId = 1;
    }
    return this.nextGlobalId++;
};

/**
 *  If a component is replacing the same-named component at the same level (e.g. provider),
 *  this reactivates the path's error detection, so that is can request it's location again without
 *  reporting an error.
 *
 *  @private
 */
Action.prototype.reactivatePath = function() {
	this.canCreate = true;
};

/**
 * force the creation path to match a given value.
 *
 * This checks to see if the path matches, otherwise, it forces the path
 * to the one supplied. A warning is emitted if the path mismatches but only
 * if it is not the top level.
 *
 * @private
 * @param {string} path the path to force
 */
Action.prototype.forceCreationPath = function(path) {
    var absPath = "(empty)";
    //
    // We add the id, since our server path is bare.
    //
    var newAbsPath = this.getId()+path;
    if (this.pathStack.length > 0) {
        var top = this.pathStack[this.pathStack.length - 1];
        if (top.absPath === newAbsPath) {
            // We are ok, the creation path is actually the correct one, ignore it.
            return;
        }
        absPath = top.absPath;
    }
    if (this.topPath() !== newAbsPath && (path.length < 2 || path.indexOf("/", 1) !== -1)) {
        //
        // Only warn if this is not a top level path, to save developers from having
        // to know the internal implementation of aura (or deal with warnings)
        //
        // Top level should index the first index or /+[0] will fail as its expecting /+
        //
        $A.warning("force path of "+newAbsPath+" from "+absPath
            +" likely a use of returned component array without changing index");
    }
    var pathEntry = { relPath: "~FORCED~", absPath:newAbsPath, idx: undefined, startIdx: undefined };
    this.pathStack.push(pathEntry);
    return newAbsPath;
};

/**
 * release a creation path that was previously forced.
 *
 * This is the mirrored call to 'forceCreationPath' that releases the 'force'.
 * The path must match the call to forceCreationPath, and the path must have
 * been forced.
 * 
 * @private
 * @param {string} path the path to release.
 */
Action.prototype.releaseCreationPath = function(path) {
    var last;
    if (this.pathStack.length > 0) {
        last = this.pathStack[this.pathStack.length - 1];
    }
    if (!last || last.absPath !== path) {
        $A.assert(false, "unexpected unwinding of pathStack.  found "
            + (last ? (last.absPath + " idx " + last.idx  ) : "empty") + " expected "  + path);
    }
    if (last && last.relPath === "~FORCED~") {
        // This is the case where we forced in the path.
        this.pathStack.pop();
    }
};

/**
 * push a new part on the creation path.
 *
 * @private
 * @param {string} pathPart the new path part to insert.
 */
Action.prototype.pushCreationPath = function(pathPart) {
    var addedPath;
    this.canCreate = true;
    switch (pathPart) {
    case "body" : pathPart = "*"; break;
    case "realbody" : pathPart = "+"; break;
    case "super" : pathPart = "$"; break;
    }
    addedPath = "/"+pathPart;
    var newPath = this.topPath() + addedPath;
    var pathEntry = { relPath: addedPath, absPath:newPath, idx: undefined, startIdx: undefined };
    this.pathStack.push(pathEntry);
};

/**
 * pop off the path part that was previously pushed.
 *
 * @private
 * @param {string} pathPart the path part previously pushed.
 */
Action.prototype.popCreationPath = function(pathPart) {
    var addedPath;
    this.canCreate = false;
    switch (pathPart) {
    case "body" : pathPart = "*"; break;
    case "realbody" : pathPart = "+"; break;
    case "super" : pathPart = "$"; break;
    }
    addedPath = "/"+pathPart;
    var last = this.pathStack.pop();
    if (!last || last.relPath !== addedPath /*|| last.idx !== undefined*/) {
        $A.assert(false, "unexpected unwinding of pathStack.  found "
            + (last ? (last.relPath + " idx " + last.idx  ) : "empty") + " expected "  + addedPath);
    }
    return last;
};

/**
 * get the path for the top entry of the path stack.
 *
 * @private
 * @return {string} the top level path.
 */
Action.prototype.topPath = function() {
    if (this.pathStack.length === 0) {
        return this.getId();
    }
    var top = this.pathStack[this.pathStack.length - 1];
    return (top.absPath + (top.idx !== undefined ? ("[" + top.idx + "]") : ""  ));
};

/**
 * set the path index.
 *
 * @private
 * @param {number} the index to set.
 */
Action.prototype.setCreationPathIndex = function(idx) {
    this.canCreate = true;
    if (this.pathStack.length < 1) {
        $A.warning("Attempting to increment index on empty stack");
    }
    var top = this.pathStack[this.pathStack.length - 1];
    // establish starting index
    if (top.idx === undefined) {
        top.startIdx = idx;
        top.idx = idx;
    }
    else if (idx !== 0 && idx !== top.idx + 1) {
        // Warning if not next index and not resetting index
        $A.warning("Improper index increment. Expected: " + (top.idx + 1) + ", Actual: " + idx);
    } else {
        top.idx = idx;
    }
};

/**
 * Gets the current creatorPath from the top of the pathStack
 *
 * @private
 * @returns {String}
 */
Action.prototype.getCurrentPath = function() {
    if (!this.canCreate) {
        $A.warning("Not ready to create. path: " + this.topPath());
    }
    this.canCreate = false; // this will cause next call to getCurrentPath to fail if not popped
    return this.topPath();
};

/**
 * Gets the <code>ActionDef</code> object. Shorthand: <code>get("def")</code>
 * <p>
 * See Also: <a href="#reference?topic=api:ActionDef">ActionDef</a>
 * </p>
 *
 * @public
 * @returns {ActionDef} The action definition, including its name, origin, and descriptor.
 */
Action.prototype.getDef = function() {
    return this.def;
};

/**
 * Adds a callback group for completion tracking.
 *
 * If this action is already completed, <code>completeAction()</code> is called.
 *
 * @private
 * @param {CallbackGroup} group
 *      the group to add
 */
Action.prototype.addCallbackGroup = function(group) {
    if (this.state === "NEW") {
        this.groups.push(group);
    } else {
        group.completeAction(this);
    }
};

/**
 * Marks this action as complete for all callback groups.
 *
 * @private
 */
Action.prototype.completeGroups = function() {
    while (this.groups.length > 0) {
        var group = this.groups.pop();
        group.completeAction(this);
    }
};

/**
 * Sets parameters for the Action.
 *
 * @public
 * @param {Object}
 *            config The key/value pairs that specify the Action. The key is an attribute on the given component.
 *             For example, <code>serverAction.setParams({ "record": id });</code> sets a parameter on <code>serverAction</code>.
 */
Action.prototype.setParams = function(config) {
    var paramDefs = this.paramDefs;
    for ( var key in paramDefs) {
        this.params[key] = config[key];
    }
};

/**
 * Sets a single parameter for the Action.
 *
 * @public
 * @param {!string}
 *            key the name of the parameter to set.
 * @param {Object}
 *            value the value to set.
 *
 */
Action.prototype.setParam = function(key, value) {
    var paramDef = this.paramDefs[key];

    if (paramDef) {
        this.params[key] = value;
    }
};

/**
 * Gets an Action parameter.
 *
 * @public
 * @param {!string}
 *            name The name of the Action.
 * @returns {Object} The parameter value
 */
Action.prototype.getParam = function(name) {
    return this.params[name];
};

/**
 * Gets the collection of parameters for this Action.
 *
 * @public
 * @returns {Object} The key/value pairs that specify the Action.
 */
Action.prototype.getParams = function() {
    return this.params;
};

/**
 * Gets the component for this Action.
 *
 * @private
 * @returns {Component} the component, if any.
 */
Action.prototype.getComponent = function() {
    return this.cmp;
};

/**
 * Sets the callback function that is executed after the server-side Action returns. Call a server-side Action from a
 * client-side controller using <code>callback</code>.
 *
 * @public
 * @param {Object}
 *            scope The scope in which the function is executed.
 * @param {function}
 *            callback The callback function to run for each controller.
 * @param {string*}
 *            name The action state for which the callback is to be associated with.
 */
Action.prototype.setCallback = function(scope, callback, name) {
    if (!$A.util.isFunction(callback)) {
        $A.error("Action callback should be a function");
        return;
    }
    // If name is undefined or specified as "ALL", then apply same callback in all cases
    if (name === undefined || name === "ALL") {
        this.callbacks["SUCCESS"] = {
            fn : callback,
            s : scope
        };
        this.callbacks["ERROR"] = {
            fn : callback,
            s : scope
        };
        this.callbacks["INCOMPLETE"] = {
            fn : callback,
            s : scope
        };
    } else {
        if (name !== "SUCCESS" && name !== "ERROR" && name !== "INCOMPLETE") {
            $A.error("Illegal name " + name);
            return;
        }
        this.callbacks[name] = {
            fn : callback,
            s : scope
        };
    }
};

/**
 * Set an 'all aboard' callback, called just before the action is sent.
 *
 * This can be used in conjunction with 'caboose' to implement a log+flush pattern.
 * Intended to be called as the 'train' leaves the 'station'. Note that setParam should
 * be used to set aditional parameters at this point.
 *
 * @public
 * @param {Object}
 *      scope The scope for the callback function.
 * @param {Function}
 *      callback the function to call.
 *
 */
Action.prototype.setAllAboardCallback = function(scope, callback) {
    if (!$A.util.isFunction(callback)) {
        $A.error("Action 'All Aboard' callback should be a function");
        return;
    }
    var that = this;
    
    /**
     * @private
     */
    this.allAboardCallback = function() { callback.call(scope, that); };
};

/**
 * Call the 'all aboard' callback.
 *
 * This should only be called internally just before an action is sent to the server.
 *
 * @private
 */
Action.prototype.callAllAboardCallback = function () {
    if (this.allAboardCallback) {
        this.allAboardCallback();
    }
};

/**
 * Wrap the current action callbacks to ensure that they get called before a given function.
 *
 * This can be used to add additional functionality to the already existing callbacks, allowing the user to effectively
 * 'append' a function to the current one.
 *
 * @private
 * @param {Object}
 *            scope the scope in which the new function should be called.
 * @param {Function}
 *            callback the callback to call after the current callback is executed.
 */
Action.prototype.wrapCallback = function(scope, callback) {
    var nestedCallbacks = this.callbacks;
    var outerCallback = callback;
    var outerScope = scope;
    this.callbacks = {};

    this.setCallback(this, function(action, cmp) {
        var cb = nestedCallbacks[this.getState()];
        if (cb && cb.fn) {
            cb.fn.call(cb.s, this, cmp);
        }
        outerCallback.call(outerScope, this, cmp);
        this.callbacks = nestedCallbacks;
    });
};

/**
 * Deprecated. Note: This method is deprecated and should not be used. Instead, use the <code>enqueueAction</code>
 * method on the Aura type. For example, <code>$A.enqueueAction(action)</code>.
 *
 * The deprecated run method runs client-side actions. Do not use it for running server-side actions.
 *
 * If you must have synchronous execution, you can temporarily use runDeprecated.
 *
 * @deprecated
 * @public
 * @param {Event}
 *            evt The event that calls the Action.
 */
Action.prototype.run = function(evt) {
    this.runDeprecated(evt);
};

/**
 * Deprecated. Run an action immediately.
 *
 * This function should only be used for old code that requires inline execution of actions. Note that the code then
 * must know if the action is client side or server side, since server side actions cannot be executed inline.
 *
 * @deprecated
 * @public
 * @param {Event}
 *            evt The event that calls the Action.
 */
Action.prototype.runDeprecated = function(evt) {
    $A.assert(this.def && this.def.isClientAction(),
             "run() cannot be called on a server action. Use $A.enqueueAction() instead.");
    this.state = "RUNNING";
    try {
        var helper = this.cmp.getDef().getHelper();
        this.returnValue = this.meth.call(this, this.cmp, evt, helper);
        this.state = "SUCCESS";
    } catch (e) {
        this.state = "FAILURE";
        $A.warning("Action failed: " + this.cmp.getDef().getDescriptor().getQualifiedName() + " -> "
                   + this.def.getName(), e);
    }
};

/**
 * Gets the current state of the Action.
 *
 * @public
 * @returns {string} The possible action states are:
 *   "NEW": The action was created but is not in progress yet
 *   "RUNNING": The action is in progress    
 *   "SUCCESS": The action executed successfully
 *   "FAILURE": Deprecated. ERROR is returned instead. The action failed. This state is only valid for client-side actions. 
 *   "ERROR": The server returned an error
 *   "INCOMPLETE": The server didn't return a response. The server might be down or the client might be offline.
 *   "ABORTED": The action was aborted
 */
Action.prototype.getState = function() {
    return this.state;
};

/**
 * Gets the return value of the Action. A server-side Action can return any object containing serializable JSON data.<br/>
 *
 * @public
 */
Action.prototype.getReturnValue = function() {
    return this.returnValue;
};

/**
 * Returns an array of error objects only for server-side actions.
 * Each error object has a message field.
 * In any mode except PROD mode, each object also has a stack field, which is a list
 * describing the execution stack when the error occurred.
 * 
 * For example, to log any errors:
 * <pre><code>
 * var errors = a.getError();
 * if (errors)  {
 *     $A.log("Errors", errors);
 *     if (errors[0] && errors[0].message) {
 *         $A.error("Error message: " + errors[0].message);
 *     }
 * } else {
 *     $A.error("Unknown error");
 * }
 * </code></pre>
 *
 * @public
 */
Action.prototype.getError = function() {
    return this.error;
};

/**
 * Returns true if the actions should be enqueued in the background, false if it should be run in the foreground.
 *
 * @public
 */
Action.prototype.isBackground = function() {
    return this.background === true;
};

/**
 * Sets the action to run as a background action. This cannot be unset. Background actions are usually long running and
 * lower priority actions.
 *
 * @public
 */
Action.prototype.setBackground = function() {
    this.background = true;
};

/**
 * Deprecated. Note: This method is deprecated and should not be used. Instead, use the <code>enqueueAction</code>
 * method on the Aura type. For example, <code>$A.enqueueAction(action)</code>.
 *
 * The deprecated <code>runAfter</code> method adds a specified server-side action to the action queue. It is for
 * server-side actions only. For example, <code>this.runAfter(serverAction);</code> sends the action to the server and
 * runs the callback when the server action completes (if the action was not aborted).
 *
 * @deprecated
 * @public
 * @param {Action}
 *            action The action to run.
 */
Action.prototype.runAfter = function(action) {
    $A.assert(action.def.isServerAction(),
                    "RunAfter() cannot be called on a client action. Use run() on a client action instead.");
    $A.clientService.enqueueAction(action);
};

/**
 * Update the fields from a response.
 *
 * @private
 * @param {Object}
 *            response The response from the server.
 * @return {Boolean} Returns true if the response differs from the original response
 */
Action.prototype.updateFromResponse = function(response) {
    this.state = response["state"];
    this.responseState = response["state"];
    this.returnValue = response["returnValue"];
    this.error = response["error"];
    this.storage = response["storage"];
    this.components = response["components"];
    if (this.state === "ERROR") {
        //
        // Careful now. If we get back an event from the server as part of the error,
        // we want to fire off the event. Note that this will also remove it from the
        // list of errors, and this may leave us with an empty error list. In that case
        // we toss in a message of 'event fired' to prevent confusion from having an
        // error state, but no error.
        //
        // This code is perhaps a bit tenuous, as it attempts to reverse the mapping from
        // event descriptor to event name in the component, giving back the first one that
        // it finds (deep down in code). This almost violates encapsulation, but, well,
        // not badly enough to remove it.
        //
        var i;
        var newErrors = [];
        var fired = false;
        for (i = 0; i < response["error"].length; i++) {
            var err = response["error"][i];
            if (err["exceptionEvent"]) {
                fired = true;
                this.events.push(err["event"]);
            } else {
                newErrors.push(err);
            }
        }
        if (fired === true && newErrors.length === 0) {
            newErrors.push({
                "message" : "Event fired"
            });
        }
        this.error = newErrors;
    } else if (this.originalResponse && this.state === "SUCCESS") {
        // Compare the refresh response with the original response and return false if they are equal (no update)
        var originalValue = $A.util.json.encode(this.originalResponse["returnValue"]);
        var refreshedValue = $A.util.json.encode(response["returnValue"]);
        if (refreshedValue === originalValue) {
            var originalComponents = $A.util.json.encode(this.originalResponse["components"]);
            var refreshedComponents = $A.util.json.encode(response["components"]);
            if (refreshedComponents === originalComponents) {
                this.getStorage().log("Action.updateFromResponse(): skipping duplicate response: " + this.getId());
                return false;
            }
        }
    }
    return true;
};

/**
 * Gets a storable response from this action.
 *
 * WARNING: Use after finishAction() since getStored() modifies <code>this.components</code>.
 *
 * @private
 * @param {string}
 *            storageName the name of the storage to use.
 */
Action.prototype.getStored = function(storageName) {
    if (this.storable && this.responseState === "SUCCESS") {
        return {
            "returnValue" : this.returnValue,
            "components" : this.components,
            "state" : "SUCCESS",
            "storage" : {
                "name" : storageName,
                "created" : new Date().getTime()
            }
        };
    }
    return null;
};

/**
 * Gets the configured storage error handler callback.
 */
Action.prototype.getStorageErrorHandler = function() {
    return this.storableConfig && this.storableConfig["errorHandler"];
};

/**
 * Calls callbacks and fires events upon completion of the action.
 *
 * @private
 * @param {AuraContext} context the context for pushing and popping the current action.
 */
Action.prototype.finishAction = function(context) {
    var previous = context.setCurrentAction(this);
    var clearComponents = false;
    var id = this.getId(context);
    try {
        if (this.cmp === undefined || this.cmp.isValid()) {
            // Add in any Action scoped components /or partial configs
            if (this.components) {
                context.joinComponentConfigs(this.components, id);
                clearComponents = true;
            }

            if (this.events.length > 0) {
                for (var x = 0; x < this.events.length; x++) {
                    this.parseAndFireEvent(this.events[x]);
                }
            }

            // If there is a callback for the action's current state, invoke that too
            var cb = this.callbacks[this.getState()];

            if (cb) {
                cb.fn.call(cb.s, this, this.cmp);
            }
            if (this.components && (cb || !this.storable || !this.getStorage())) {
                context.finishComponentConfigs(id);
                clearComponents = false;
            }
        } else {
            this.abort();
        }
    } finally {
        context.setCurrentAction(previous);
        this.completeGroups();
        if (clearComponents) {
            context.clearComponentConfigs(id);
        }
    }
};

/**
 * Mark this action as aborted.
 *
 * @private
 */
Action.prototype.abort = function() {
    this.state = "ABORTED";
    this.completeGroups();
};

/**
 * Marks the Action as abortable. For server-side Actions only.
 *
 * @public
 */
Action.prototype.setAbortable = function() {
    this.abortable = true;
};

/**
 * Checks if this action is a refresh.
 *
 * @private
 */
Action.prototype.isRefreshAction = function() {
    return this.originalResponse !== undefined;
};

/**
 * Checks if the function is abortable. For server-side Actions only.
 *
 * @public
 * @returns {Boolean} The function is abortable (true), or false otherwise.
 */
Action.prototype.isAbortable = function() {
    return this.abortable || false;
};

/**
 * An exclusive Action is processed on an XMLHttpRequest of its own. <code>a.setExclusive(true)</code> and
 * <code>a.setExclusive()</code> are the same. For server-side Actions only.
 *
 * @public
 * @param {Object}
 *            val
 * @returns {Boolean} Set to true if the Action should be exclusive, or false otherwise.
 */
Action.prototype.setExclusive = function(val) {
    this.exclusive = val === undefined ? true : val;
};

/**
 * Returns true if a given function is exclusive, or false otherwise.
 *
 * @public
 * @returns {Boolean}
 */
Action.prototype.isExclusive = function() {
    return this.exclusive || false;
};

/**
 * Marks the Action as storable and abortable. For server-side Actions only.
 *
 * @public
 * @param {Object}
 *            config Optional. A set of key/value pairs that specify the storage options to set. You can set the
 *            following options: <code>ignoreExisting</code> and <code>refresh</code>.
 */
Action.prototype.setStorable = function(config) {
    $A.assert(this.def && this.def.isServerAction(),
              "setStorable() cannot be called on a client action.");
    this.storable = true;
    this.storableConfig = config;

    //
    // Storable actions must also be abortable (idempotent, replayable and non-mutating)
    // Careful with this, as it will cause side effects if there are other abortable actions
    //
    this.setAbortable();
};

/**
 * Returns true if the function is storable, or false otherwise. For server-side Actions only.
 *
 * @public
 * @returns {Boolean}
 */
Action.prototype.isStorable = function() {
    var ignoreExisting = this.storableConfig && this.storableConfig["ignoreExisting"];
    return this._isStorable() && !ignoreExisting;
};

/**
 * Sets this action as a 'caboose'.
 *
 * This is only relevant for server side actions, and will cause the action to never initiate
 * an XHR request. This action will not be sent to the server until there is some other action
 * that would cause a server round-trip. This can be a little dangerous, as the this will queue
 * forever if nothing goes to the server.
 *
 * @public
 */
Action.prototype.setCaboose = function() {
    this.caboose = true;
};


/**
 * Returns true if the function should not create an XHR request.
 *
 * @public
 * @returns {boolean}
 */
Action.prototype.isCaboose = function() {
    return this.caboose;
};

/**
 * @private
 */
Action.prototype._isStorable = function() {
    return this.storable || false;
};

/**
 * Gets the storage key in name-value pairs.
 *
 * @private
 */
Action.prototype.getStorageKey = function() {
    return Action.getStorageKey(
        this.def ? this.def.getDescriptor().toString() : "",
        this.params
    );
};

/**
 * Returns true if a given function is from the current storage, or false otherwise.
 *
 * @public
 * @returns {Boolean}
 */
Action.prototype.isFromStorage = function() {
    return !$A.util.isUndefinedOrNull(this.storage);
};

/**
 * Chains a function to run after the current Action. For server-side Actions only.
 *
 * @public
 */
Action.prototype.setChained = function() {
    this.chained = true;
    $A.enqueueAction(this);
};

/**
 * Returns true if a given function is chained, or false otherwise. For server-side Actions only.
 *
 * @private
 * @returns {Boolean}
 */
Action.prototype.isChained = function() {
    return this.chained || false;
};

/**
 * Returns the key/value pairs of the Action id, descriptor, and parameters in JSON format.
 *
 * @public
 */
Action.prototype.toJSON = function() {
    return {
        "id" : this.getId(),
        "descriptor" : (this.def?this.def.getDescriptor():"UNKNOWN"),
        "params" : this.params
    };
};

/**
 * Mark the current action as incomplete.
 *
 * @private
 */
Action.prototype.incomplete = function(context) {
    this.state = "INCOMPLETE";
    if (!this.error || !(this.error instanceof Array)) {
        this.error = [ { message : "Disconnected or Canceled" } ];
    }
    this.finishAction(context);
};

/**
 * Refreshes the Action. Used with storage.
 *
 * @private
 */
Action.prototype.getRefreshAction = function(originalResponse) {
    var storage = originalResponse["storage"];
    var storageService = this.getStorage();
    var autoRefreshInterval =
            (this.storableConfig && !$A.util.isUndefined(this.storableConfig["refresh"])
             && $A.util.isNumber(this.storableConfig["refresh"]))
                    ? this.storableConfig["refresh"] * 1000
                    : storageService.getDefaultAutoRefreshInterval();

    // Only auto refresh if the data we have is more than
    // v.autoRefreshInterval seconds old
    var now = new Date().getTime();
    if ((now - storage["created"]) >= autoRefreshInterval && this.def) {
        var refreshAction = this.def.newInstance(this.cmp);

        storageService.log("Action.refresh(): auto refresh begin: " + this.getId() + " to " + refreshAction.getId());

        var executeCallbackIfUpdated = (this.storableConfig && !$A.util.isUndefined(this.storableConfig["executeCallbackIfUpdated"]))
                ? this.storableConfig["executeCallbackIfUpdated"] : true;
        if (executeCallbackIfUpdated !== false) {
            refreshAction.callbacks = this.callbacks;
        }

        refreshAction.setParams(this.params);
        refreshAction.setStorable({
            "ignoreExisting" : true,
            "errorHandler": this.getStorageErrorHandler()
        });

        refreshAction.abortable = this.abortable;
        refreshAction.originalResponse = originalResponse;

        return refreshAction;
    }

    return null;
};

/**
 * Gets the Action storage.
 *
 * @private
 * @returns {Storage}
 */
Action.prototype.getStorage = function() {
    return Action.getStorage();
};

/**
 * Uses the event object in the action's response and fires the event.
 *
 * @private
 */
Action.prototype.parseAndFireEvent = function(evtObj) {
    var descriptor = evtObj["descriptor"];

    // If the current component has registered to fire the event,
    // then create the event object and associate it with this component(make it the source)
    var evt = null;
    var comp = this.getComponent();
    if (comp) {
        evt = comp.getEventByDescriptor(descriptor);
    }
    if (evt !== null) {
        if (evtObj["attributes"]) {
            evt.setParams(evtObj["attributes"]["values"]);
        }
        evt.fire();
    } else {
        // Else create the event using ClientService and fire it. Usually the case for APPLICATION events.
        // If the event is a COMPONENT event, it is fired anyway but has no effect because its an orphan(without source)
        $A.clientService.parseAndFireEvent(evtObj);
    }
};

/**
 * Fire off a refresh event if there is a valid component listener.
 *
 * @private
 */
Action.prototype.fireRefreshEvent = function(event) {
    // storageService.log("Action.refresh(): auto refresh: "+event+" for "+this.actionId);
    if (this.cmp && this.cmp.isValid()) {
        var isRefreshObserver = this.cmp.isInstanceOf("auraStorage:refreshObserver");
        if (isRefreshObserver) {
            this.cmp.getEvent(event).setParams({
                    "action" : this
            }).fire();
        }
    }
};
// #include aura.controller.Action_export
