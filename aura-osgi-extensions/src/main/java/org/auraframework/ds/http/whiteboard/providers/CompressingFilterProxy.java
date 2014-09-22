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

import aQute.bnd.annotation.component.Component;

import com.planetj.servlet.filter.compression.CompressingFilter;

@Component (provide=HttpServiceProviderProxy.class)
public class CompressingFilterProxy extends FilterProxyImpl<CompressingFilter> {
    private static final int COMPRESSING_FILTER_RANKING = 1000;
    private static final String COMPRESSING_FILTER_PATTERN = "/.*";

    public CompressingFilterProxy() {
        super(COMPRESSING_FILTER_PATTERN, COMPRESSING_FILTER_RANKING);
    }
    
    @Override
    protected CompressingFilter newInstance() {
        return new CompressingFilter();
    }
}    

