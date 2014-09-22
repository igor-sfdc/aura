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
package org.auraframework.ds.http.whiteboard.providers;

import org.auraframework.ds.http.whiteboard.proxy.HttpServiceProviderProxy;
import org.auraframework.ds.http.whiteboard.proxy.impl.FilterProxyImpl;
import org.auraframework.http.AuraRewriteFilter;

import aQute.bnd.annotation.component.Component;



@Component (provide=HttpServiceProviderProxy.class)
public class AuraRewriteFilterCmpProxy extends FilterProxyImpl<AuraRewriteFilter> {
    private static final String AURA_REWRITE_FILTER_CMP_PATTERN = "/.*[.]cmp";
    private static final int AURA_REWRITE_FILTER_CMP_RANKING = 50;

    public AuraRewriteFilterCmpProxy() {
        super(AURA_REWRITE_FILTER_CMP_PATTERN, AURA_REWRITE_FILTER_CMP_RANKING);
    }

    @Override
    protected AuraRewriteFilter newInstance() {
        return new AuraRewriteFilter();
    }
}



