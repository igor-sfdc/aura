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
    doCopy: function(cmp) {
      $A.log('Copy Event Fired');
      cmp.set("v.copyEventFired", true);
      cmp.find("outputStatus").set("v.value", "Copy Event Fired");
    },
    doCut: function(cmp) {
        $A.log('Cut Event Fired');
        cmp.set("v.cutEventFired", true);
        cmp.find("outputStatus").set("v.value", "Cut Event Fired");
      },
    doPaste: function(cmp) {
        $A.log('Paste Event Fired');
        cmp.set("v.pasteEventFired", true);
        cmp.find("outputStatus").set("v.value", "Paste Event Fired");
    }
})
