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
    refresh: function(component, event, helper) {
    	var list = component.find("list");
    	component.set("v.refreshCallBack", false);
        list.getEvent('refresh').setParams({
            parameters: {
                callback: function() {
                	component.set("v.refreshCallBack", true);
                }
            }
        }).fire();
    },

    showMore: function(component, event, helper) {
    	var list = component.find("list");
    	component.set("v.showMoreCallback", false);
        list.getEvent('showMore').setParams({
            parameters: {
                callback: function() {
                	component.set("v.showMoreCallback", true);
                }
            }
        }).fire();
    }
})
