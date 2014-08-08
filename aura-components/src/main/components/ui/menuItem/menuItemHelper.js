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
({
    typeMap : {
        "action": "ui:actionMenuItem",
        "checkbox": "ui:checkboxMenuItem",
        "radio": "ui:radioMenuItem",
        "separator": "ui:menuItemSeparator"
        
    },
    
    addMenuItemDomEvents : function(component) {
        var events = ["click", "keydown", "mouseover"];
        for (var i=0, len=events.length; i < len; i++) {
            // We need to fire these events for status update anyway
            if (!component.hasEventHandler(events[i])) {
                this.addDomHandler(component, events[i]);
            }           
        }
    },
    
    getParentComponent: function(component) {
        var parent = component.get("v.parent");
        if (parent && parent.length > 0) {
            return parent[0];
        }
        return null;
    },
    
    /**
     * Override
     *
     */
     fireEvent : function (component, event, helper) {
        if (component.get("v.disabled") === true && event.type !== "mouseover") {
            return;
        }
        var e = component.getEvent(event.type);
        helper.setEventParams(e, event);
        e.fire();
     },
    
    fireSelectEvent: function(component, event) {
        var concrete = component.getConcreteComponent();
        var parent = concrete.get("v.parent");
        if (parent && parent.length > 0) {
            p = parent[0];
            var e = p.getEvent("menuSelect");
            if (e) {
                e.setParams({
                    selectedItem: event.getSource()
                });
                e.fire();
            }
        }
    },
    
    /**
     * Dismiss the menu and put the focus back to menu trigger.
     */
    handleEsckeydown: function(component, event) {
    	var parent = this.getParentComponent(component);
    	var concreteParentCmp = parent.getConcreteComponent();
        if (concreteParentCmp) {
            if (concreteParentCmp.get("v.visible") === true) {
                concreteParentCmp.set("v.visible", false);
                if (component.get("v.disabled") === true) {
                    // for disabled menu item, no Aura event gets fired, so we have to directly deal with DOM.
                    var devCmp = parent.find("menu");
                    if (devCmp) {
                        var elem = devCmp.getElement();
                        $A.util.removeClass(elem, "visible");
                    }
                }
            }            
        }
        // put the focus back to menu trigger
        this.setFocusToTrigger(component);
    },
    
    /**
     * Select the menu item when Space bar is pressed
     *
     */
    handleSpacekeydown: function(component, event) {
        if (component.get("v.disabled") === true) {
            return;
        }
        var e = component.getEvent("click");
        this.setEventParams(e, event);
        e.fire();
    },
    
    /**
     * Dismiss the menu when tab key is pressed.
     */
    handleTabkeydown: function(component, event) {
        var parent = this.getParentComponent(component);
		var closeOnTab = parent.get('v.closeOnTabKey');
        var concreteParentCmp = parent.getConcreteComponent();
        if (concreteParentCmp && closeOnTab) {
            if (concreteParentCmp.get("v.visible") === true) {
                concreteParentCmp.set("v.visible", false);
                if (component.get("v.disabled") === true) {
                    // for disabled menu item, no Aura event gets fired, so we have to directly deal with DOM.
                    var devCmp = parent.find("menu");
                    if (devCmp) {
                        var elem = devCmp.getElement();
                        $A.util.removeClass(elem, "visible");
                    }
                }
            }
        }
    },
    
    preEventFiring: function(component, event) {
        this.supportKeyboardInteraction(component, event);
    },
    
    setDisabled : function(component) {
    	var concreteCmp = component.getConcreteComponent();
        var linkCmp = this.getAnchorElement(component);
        var elem = linkCmp ? linkCmp.getElement() : null;
        if (elem) {
            var disabled = concreteCmp.get("v.disabled");
            if (disabled === true) {
                $A.util.removeClass(elem, "selectable");
                elem.setAttribute("aria-disabled", "true");
            } else {
                $A.util.addClass(elem, "selectable");
                elem.removeAttribute("aria-disabled");
            }
        }
    },
    
    setFocus: function(component) {
        var linkCmp = this.getAnchorElement(component);
        var elem = linkCmp ? linkCmp.getElement() : null;
        if (elem && elem.focus) {
            elem.focus();
        }
    },
    
    getAnchorElement: function(component) {
    	//Walk up the component ancestor to find the contained component by localId
    	var localId = "link", c =  component.getConcreteComponent();
    	var retCmp = null;    	
    	while (c) {    		    		
    		retCmp = c.find(localId);
    		if (retCmp) {
    			break;
    		}
    		c = c.getSuper();
    	}
    	return retCmp;
    },
    
    setFocusToNextItem: function(component) {
        var parent = this.getParentComponent(component);
        if (parent) {
            var nextIndex = 0;
            var menuItems = parent.get("v.childMenuItems");
            for (var i = 0; i < menuItems.length; i++) {
                if (component === menuItems[i]) {
                    nextIndex = ++i;
                    break;
                }
            }
            if (nextIndex >= menuItems.length) {
                nextIndex = 0;
            }
            var nextFocusCmp = menuItems[nextIndex];
            var action = nextFocusCmp.get("c.setFocus");
            action.runDeprecated();
        }
    },
    
    setFocusToPreviousItem: function(component) {
        var parent = this.getParentComponent(component);
        if (parent) {
            var previousIndex = 0;
            var menuItems = parent.get("v.childMenuItems");
            for (var i = 0; i < menuItems.length; i++) {
                if (component === menuItems[i]) {
                    previousIndex = --i;
                    break;
                }
            }
            if (previousIndex < 0) {
                previousIndex = menuItems.length - 1;
            }
            var previousFocusCmp = menuItems[previousIndex];
            var action = previousFocusCmp.get("c.setFocus");
            action.runDeprecated();
        }
    },
    
    setFocusToTrigger: function(component) {
        var parent = this.getParentComponent(component);
        if (parent) {
            var grandParent = parent.get("v.parent");
            if (grandParent && grandParent.length > 0) {
                var dropdownCmp = grandParent[0];
                var dropdownHelper = dropdownCmp.getDef().getHelper();
                var menuTriggerCmp = dropdownHelper.getTriggerComponent(dropdownCmp);
                if (menuTriggerCmp) {
                    var action =  menuTriggerCmp.get("c.focus");
                    action.runDeprecated();
                }
            }
        }
    },
    
    /**
     * Focus on the item whose starting character(s) are what the end user types.
     * Copied from Accentjs dropdown component.
     */
    setFocusToTypingChars: function(component, event) {
        var parent = this.getParentComponent(component);
        if (parent) {
            // If we were going to clear what keys were typed, don't yet.
            if (!$A.util.isUndefinedOrNull(parent._clearBufferId)) { 
                clearTimeout(parent._clearBufferId); 
            }

            // Store the letter.
            var letter = String.fromCharCode(event.keyCode);
            parent._keyBuffer = parent._keyBuffer || [];
            parent._keyBuffer.push(letter);

            // Try to select
            var matchText = parent._keyBuffer.join("").toLowerCase();
            var menuItems = parent.get("v.childMenuItems");
            for(var i = 0; i < menuItems.length; i++) {
                var c = menuItems[i];
                var text = c.get("v.label");
                if(text.toLowerCase().indexOf(matchText) === 0) {
                    var action = c.get("c.setFocus");
                    action.runDeprecated();
                    break;
                }
            }

            parent._clearBufferId = setTimeout(function() {
                parent._keyBuffer = [];
            }, 700);
        }
    },
    
    /**
     * Handle keyboard interactions
     *
     */
    supportKeyboardInteraction: function(component, event) {
        var concreteCmp = component.getConcreteComponent();
        if (event.type === "keydown") {
            if (event.keyCode === 39 || event.keyCode === 40) {  // right or down arrow key
                event.preventDefault();
                this.setFocusToNextItem(concreteCmp);
            } else if (event.keyCode === 37 || event.keyCode === 38) {  // left or up arrow key
                event.preventDefault();
                this.setFocusToPreviousItem(concreteCmp);
            } else if (event.keyCode === 27) {  // Esc key
                event.stopPropagation();
                this.handleEsckeydown(concreteCmp, event);
            } else if (event.keyCode === 9) {  // tab key: dismiss the menu
                this.handleTabkeydown(concreteCmp, event);
            } else if (event.keyCode === 32) {  // space key: select the menu item
                event.preventDefault();
                this.handleSpacekeydown(concreteCmp, event);
            } else {
                this.setFocusToTypingChars(concreteCmp, event);
            }
        }
    }
})
