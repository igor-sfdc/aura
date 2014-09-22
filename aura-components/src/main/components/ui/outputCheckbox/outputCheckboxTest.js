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
    /* checked checkbox */
    testChecked: {
        attributes : {value : true},
        test: function(component){
        	var expectedElem = component.find("img1").getElement();
            aura.test.assertTrue($A.util.hasClass(expectedElem, "checked"), "missing class: checked");
            aura.test.assertTrue($A.util.hasClass(expectedElem, "uiOutputCheckbox"), "missing class: uiOutputCheckbox");
        }
    },

    /* unchecked checkbox */
    testUnchecked: {
        attributes : {value : false},
        test: function(component){
            var expectedElem = component.find("img1").getElement();
            aura.test.assertTrue($A.util.hasClass(expectedElem, "unchecked"), "missing class: unchecked");
            aura.test.assertTrue($A.util.hasClass(expectedElem, "uiOutputCheckbox"), "missing class: uiOutputCheckbox");
        }
    },

    /* unchecked -> checked checkbox */
    testRerenderChecked: {
        attributes : {value : false},
        test: function(component){
        	var expectedElem = component.find("img1").getElement();
            aura.test.assertTrue($A.util.hasClass(expectedElem, "unchecked"), "missing class: unchecked");
            aura.test.assertTrue($A.util.hasClass(expectedElem, "uiOutputCheckbox"), "missing class: uiOutputCheckbox");
            
            component.set("v.value",true);
            $A.renderingService.rerender(component);

            expectedElem = component.find("img1").getElement();
            aura.test.assertTrue($A.util.hasClass(expectedElem, "checked"), "missing class: checked");
            aura.test.assertTrue($A.util.hasClass(expectedElem, "uiOutputCheckbox"), "missing class: uiOutputCheckbox");
        }
    },

    /* checked -> unchecked checkbox */
    testRerenderUnchecked: {
        attributes : {value : true},
        test: function(component){
            var expectedElem = component.find("img1").getElement();
            aura.test.assertTrue($A.util.hasClass(expectedElem, "checked"), "missing class: checked");
            aura.test.assertTrue($A.util.hasClass(expectedElem, "uiOutputCheckbox"), "missing class: uiOutputCheckbox");
            
            component.set("v.value",false);
            $A.renderingService.rerender(component);

            expectedElem = component.find("img1").getElement();
            aura.test.assertTrue($A.util.hasClass(expectedElem, "unchecked"), "missing class: unchecked");
            aura.test.assertTrue($A.util.hasClass(expectedElem, "uiOutputCheckbox"), "missing class: uiOutputCheckbox");
      }
    }
})
