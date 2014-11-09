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
	handleClick: function (cmp) {
		alert("ROW CLICK");
	}

	/* API not exposed yet.
	handleOpen: function (cmp, evt, hlp) {
		evt.getParam('domEvent').preventDefault();
		cmp.getEvent('open').setParams(evt.getParams()).fire();
	},

	handleClose: function (cmp, evt, hlp) {
		evt.getParam('domEvent').preventDefault();
		cmp.getEvent('close').setParams(evt.getParams()).fire();
	}
	*/
})
