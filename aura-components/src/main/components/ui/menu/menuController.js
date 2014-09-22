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
({
    doInit: function(component, event, helper) {
        var body = component.get("v.body");
        for (var i = 0; i < body.length; i++) {
            var c = body[i];
            if (c.isInstanceOf("ui:menuTrigger")) {
                c.addHandler("menuTriggerPress", component, "c.trigger");
            }
            if (c.isInstanceOf("ui:menuList")) {
                var focusActionHandler = $A.expressionService.create(component, component.get('c.focusTrigger'));
                c.set("v.focusTrigger", focusActionHandler);
            }
        }
    },
    
    click : function(cmp, event){
        if ($A.util.getBooleanValue(cmp.get("v.stopClickPropagation"))) {
            if (event.stopPropagation) { // IE9 & Other Browsers
              event.stopPropagation();
            } else { // IE8 and Lower
              event.cancelBubble = true;
            }
        }
    },
    
    focusTrigger: function(component, event, helper) {
        var trigger = helper.getTriggerComponent(component);
        if (trigger) {
            var action = trigger.get("c.focus");
            action.runDeprecated();
        };
    },

    onMenuListVisible: function(component, event, helper){
        helper.handleVisible(component);
    },
    
    trigger: function(component, event, helper) {
        var index = event.getParam("focusItemIndex");
        var concreteCmp = component.getConcreteComponent();
        var _helper = concreteCmp.getDef().getHelper();
        _helper.toggleMenuVisible(concreteCmp, index, event);
    },
    
    refresh: function(component, event, helper) {
        var menuList = helper.getMenuComponent(component);
        if (menuList) {
            menuList.get("e.refresh").fire();
        }
    }
})
