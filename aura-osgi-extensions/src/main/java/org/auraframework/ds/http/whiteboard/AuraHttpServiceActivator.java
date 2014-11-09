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
package org.auraframework.ds.http.whiteboard;

import org.auraframework.ds.http.whiteboard.tracker.HttpServiceTrackerFactory;
import org.auraframework.ds.log.AuraDSLogService;
import org.auraframework.ds.util.BundleUtil;
import org.osgi.framework.BundleException;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.tracker.ServiceTracker;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;


@Component
public final class AuraHttpServiceActivator {
    private static final String ORG_ECLIPSE_EQUINOX_HTTP_JETTY_BUNDLE = "org.eclipse.equinox.http.jetty";

    private ServiceTracker serviceTracker;

    private HttpServiceTrackerFactory serviceTrackerFactory;    
    @Reference
    protected void setServiceTrackerFactory(HttpServiceTrackerFactory serviceTrackerFactory) {
        this.serviceTrackerFactory = serviceTrackerFactory;
    }
    
    protected void unsetServiceTrackerFactory(HttpServiceTrackerFactory serviceTrackerFactory) {
        this.serviceTrackerFactory = null;
    }
    
    private AuraDSLogService logService = null;    
    @Reference
    protected void setLogService(AuraDSLogService logServiceValue) {
        logService = logServiceValue;
    }
    protected void unsetLogService(AuraDSLogService logServiceValue) {
        logService = null;
    }

    @Activate
    protected void activateHttpServiceProviders(ComponentContext componentContext) {
        try {
            BundleUtil.startBundle(ORG_ECLIPSE_EQUINOX_HTTP_JETTY_BUNDLE, componentContext.getBundleContext());
            logService.info("Explicitly started " + ORG_ECLIPSE_EQUINOX_HTTP_JETTY_BUNDLE + " bundle.");
        } catch (BundleException e) {
            logService.error("Could not start " + ORG_ECLIPSE_EQUINOX_HTTP_JETTY_BUNDLE + " bundle.", e);
        }

        logService.info("[" + getClass().getSimpleName() + "] " + " Activation statrted");
        serviceTracker = serviceTrackerFactory.getTracker(componentContext);
        serviceTracker.open();
        logService.info("[" + getClass().getSimpleName() + "] " + " Activation completed");
    }

    @Deactivate
    protected void deactivateHttpServiceProviders(ComponentContext componentContext) {        
        serviceTracker.close();
        logService.info("[" + getClass().getSimpleName() + "] " + " Deactivation Completed");
    }
}
