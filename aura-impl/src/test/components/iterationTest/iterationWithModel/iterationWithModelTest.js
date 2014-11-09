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
    /**
     * Verify adding rows to an iteration does not lose the model data.
     */
    testAddNewRowsWithModelData: {
        test: function(cmp) {
            var cmps = cmp.find("innerCmp");
            $A.test.assertStartsWith("one : readonly", $A.util.getText(cmps[0].getElement()));
            $A.test.assertStartsWith("two : readonly", $A.util.getText(cmps[1].getElement()));
            $A.test.assertStartsWith("three : readonly", $A.util.getText(cmps[2].getElement()));

            $A.run(function(){
                cmp.get("addRow").get("e.press").fire();
                cmp.get("addRow").get("e.press").fire();
                cmp.get("addRow").get("e.press").fire();
            });

            // Wait for 5 elements- 3 original plus 2 added
            $A.test.addWaitFor(6, function() {
                return cmp.find("innerCmp").length;
            }, function() {
                cmps = cmp.find("innerCmp");
                $A.test.assertStartsWith("one : readonly", $A.util.getText(cmps[0].getElement()));
                $A.test.assertStartsWith("two : readonly", $A.util.getText(cmps[1].getElement()));
                $A.test.assertStartsWith("three : readonly", $A.util.getText(cmps[2].getElement()));
                $A.test.assertStartsWith("new! : readonly", $A.util.getText(cmps[3].getElement()));
                $A.test.assertStartsWith("new! : readonly", $A.util.getText(cmps[4].getElement()));
                $A.test.assertStartsWith("new! : readonly", $A.util.getText(cmps[5].getElement()));
            });
            
        }
    },

    /**
     * Verify removing rows from an iteration does not lose the model data.
     */
    testRemoveRowWithModelData: {
        test: function(cmp) {
            var cmps = cmp.find("innerCmp");
            $A.test.assertStartsWith("one : readonly", $A.util.getText(cmps[0].getElement()));
            $A.test.assertStartsWith("two : readonly", $A.util.getText(cmps[1].getElement()));
            $A.test.assertStartsWith("three : readonly", $A.util.getText(cmps[2].getElement()));

            $A.run(function(){
                cmp.get("removeRow").get("e.press").fire();
            });
            // Wait for 2 elements- 3 original minus 1 deleted
            $A.test.addWaitFor(2, function() {
                return cmp.find("innerCmp").length;
            }, function() {
                cmps = cmp.find("innerCmp");
                $A.test.assertStartsWith("two : readonly", $A.util.getText(cmps[0].getElement()));
                $A.test.assertStartsWith("three : readonly", $A.util.getText(cmps[1].getElement()));
            });
        }
    },

    /**
     * Verify that we load the components inside the iteration the expected number of times. Once during the initial
     * load and once per inner component for a change to the iteration items.
     * 
     * Note that if the cmp or initial list to iterate over is changed this test may need to be changed accordingly.
     */
    testRenderCount : {
        test : function(cmp) {
            // 6 total renders, 3 for each iteration
            $A.test.assertEquals(6, window.__testRenderCount, "Each inner component should be rendered once on load.");

            $A.run(function(){
                cmp.get("addRow").get("e.press").fire();
            });
            
            $A.test.addWaitFor(4, function() {
                return cmp.find("innerCmp").length;
            }, function() {
                // 8 total renders, 6 for initial load, 1 additional for each iteration
                $A.test.assertEquals(8, window.__testRenderCount, "Unexpected number of total items loaded after adding to list.");
            });
        }
    },

    /**
     * Verify wrapping a component which has server side dependencies with a component that does not still goes to the
     * server. 
     * 
     * TODO(W-1787477): Until fixed, we can use the forceServer flag to manually force going to the server while
     * creating the components. Once the bug is fixed we won't need to set the flag and the test should be modified.
     */
    testWrapInnerCmpWithNoServerDeps: {
        test: function(cmp) {
            var cmps = cmp.find("innerCmpWrapper");
            $A.test.assertStartsWith("one : readonly", $A.util.getText(cmps[0].getElement()));
            $A.test.assertStartsWith("two : readonly", $A.util.getText(cmps[1].getElement()));
            $A.test.assertStartsWith("three : readonly", $A.util.getText(cmps[2].getElement()));

            $A.run(function(){
                cmp.get("addRow").get("e.press").fire();
            });
            
            // Wait for 4 elements- 3 original plus 1 added
            $A.test.addWaitFor(4, function() {
                return cmp.find("innerCmpWrapper").length;
            }, function() {
                cmps = cmp.find("innerCmpWrapper");
                $A.test.assertStartsWith("one : readonly", $A.util.getText(cmps[0].getElement()));
                $A.test.assertStartsWith("two : readonly", $A.util.getText(cmps[1].getElement()));
                $A.test.assertStartsWith("three : readonly", $A.util.getText(cmps[2].getElement()));
                $A.test.assertStartsWith("new! : readonly", $A.util.getText(cmps[3].getElement()));
            });
        }
    }
})
