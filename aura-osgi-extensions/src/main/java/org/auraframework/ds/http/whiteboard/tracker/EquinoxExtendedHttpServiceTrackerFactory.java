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
package org.auraframework.ds.http.whiteboard.tracker;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.auraframework.ds.http.whiteboard.proxy.FilterProxy;
import org.auraframework.ds.http.whiteboard.proxy.HttpServiceProviderProxy;
import org.auraframework.ds.http.whiteboard.proxy.ServletProxy;
import org.auraframework.ds.log.AuraDSLogService;
import org.eclipse.equinox.http.servlet.ExtendedHttpService;
import org.osgi.service.http.NamespaceException;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;


@Component (provide=HttpServiceTrackerFactory.class)
public class EquinoxExtendedHttpServiceTrackerFactory extends AbstractHttpServiceTrackerFactory<ExtendedHttpService> {

    private static final String HTTP_SERVICE_NAME = ExtendedHttpService.class.getName();

    @Reference
    @Override
    protected void setLogService(AuraDSLogService logServiceValue) {
        logService = logServiceValue;
    }

    @Override
    protected void unsetLogService(AuraDSLogService logServiceValue) {
        logService = null;
    }

    @Reference (multiple=true, dynamic=true)
    protected void addHttpServiceProvider(HttpServiceProviderProxy httpServiceProvider) {
        handleHttpServiceProviderAdded(httpServiceProvider);
    }

    protected void removeHttpServiceProvider(HttpServiceProviderProxy httpServiceProvider) {
        handleHttpServiceProviderRemoved(httpServiceProvider);
    }

    public EquinoxExtendedHttpServiceTrackerFactory() {
        super(HTTP_SERVICE_NAME);
    }

    @Activate
    protected void activate() {
        logService.info(getClass().getSimpleName() + " Activated");
    }

    protected void deactivate() {
        logService.info(getClass().getSimpleName() + " Deactivated");
    }

    @Override
    protected void register(ExtendedHttpService service,
            ServletProxy<? extends Servlet> servlet) throws ServletException, NamespaceException {
        try {
            service.registerServlet(servlet.getAlias(), servlet, null, null);
        } catch (NamespaceException e) {
            if (e.toString().contains("The alias '/' is already in use")) {
                // Ignore: a conflict with the newer version of Whiteboard
            } else {
                throw e;
            }
        }
    }

    @Override
    protected void register(ExtendedHttpService service, FilterProxy<? extends Filter> filter)
            throws ServletException, NamespaceException {
        // To abstract the differences b/w Equinox and Felix implementations filters are initially
        // bound to "/" and then FilterProxy implementation handles the actual pattern matching
        service.registerFilter("/", filter, null, null);
    }

    @Override
    protected void unregister(ExtendedHttpService service, FilterProxy<? extends Filter> filter) {
        service.unregisterFilter(filter);
        filter.destroy();
    }

    @Override
    protected void unregister(ExtendedHttpService service, ServletProxy<? extends Servlet> servlet) {
        service.unregister(servlet.getAlias());
        servlet.destroy();
    }
}
