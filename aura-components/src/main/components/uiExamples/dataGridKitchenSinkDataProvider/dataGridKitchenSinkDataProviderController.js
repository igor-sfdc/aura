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
    init: function (cmp) {
        cmp.set('v.totalItems', 1000);
    },

    handleProvide: function (cmp, evt, hlp) {
        var currentPage = cmp.get('v.currentPage'),
            pageSize = cmp.get('v.pageSize'),
            sortBy = cmp.get('v.sortBy'),
            tasks = cmp._tasks || hlp.createTasks(cmp),
            column = sortBy, 
            descending = false,
        	requestedTasks;
        
        if (column && column.indexOf('-') === 0) {
            column = sortBy.slice(1);
            descending = true;
        }

        if (column) {
            hlp.sort(tasks, column, descending);
        }
        
        // Paginate
        requestedTasks = hlp.applyPagination(tasks, currentPage, pageSize);

        hlp.fireDataChangeEvent(cmp, requestedTasks);
    }
})
