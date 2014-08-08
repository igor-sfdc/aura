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
	clearValue: function(component, event, helper) {
		component.set("v.value", "");
	},
	
	click: function(component, event, helper) {
        event.preventDefault();
        var concreteCmp = component.getConcreteComponent();
        var _helper = concreteCmp.getDef().getHelper();
        helper.displayDatePicker(concreteCmp);
    },

    doInit: function(component, event, helper) {
        // Set placeholder
        var format = component.get("v.format");
        if (!format) {
            format = $A.get("$Locale.datetimeformat");
        }
        component.set("v.placeholder", format);
    },

    openDatePicker: function(component, event, helper) {
        var concreteCmp = component.getConcreteComponent();
        var _helper = concreteCmp.getDef().getHelper();
        helper.displayDatePicker(concreteCmp);
    },

    setValue: function(component, event, helper) {
        var outputCmp = component.find("inputText");
        var elem = outputCmp ? outputCmp.getElement() : null;
        var value = elem ? elem.value : null;
        var format = component.get("v.format");
        if (!format) { // use default format
            format = $A.get("$Locale.datetimeformat");
        }
        var langLocale = component.get("v.langLocale");
        var secs = 0;
        var ms = 0;
        if (value) {
            var currDate = $A.localizationService.parseDateTimeUTC(value, format, langLocale);
            secs = currDate.getUTCSeconds();
            ms = currDate.getUTCMilliseconds();
        }

        var dateValue = event.getParam("value");
        var selectedHours = event.getParam("hours");
        var selectedMinutes = event.getParam("minutes");
        var newDate = $A.localizationService.parseDateTimeUTC(dateValue, "YYYY-MM-DD", langLocale);

        var targetTime = Date.UTC(newDate.getUTCFullYear(),
                                  newDate.getUTCMonth(),
                                  newDate.getUTCDate(),
                                  selectedHours,
                                  selectedMinutes,
                                  secs,
                                  ms);
        var d = new Date(targetTime);

        var timezone = component.get("v.timezone");
        $A.localizationService.WallTimeToUTC(d, timezone, function(utcDate) {
            component.set("v.value", $A.localizationService.toISOString(utcDate));
        });
    }
})