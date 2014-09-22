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
package org.auraframework.ds.http.whiteboard.tracker;

import java.util.Arrays;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.auraframework.ds.http.whiteboard.proxy.FilterProxy;
import org.auraframework.ds.http.whiteboard.proxy.HttpServiceProviderProxy;
import org.auraframework.ds.http.whiteboard.proxy.ServletProxy;
import org.auraframework.ds.log.AuraDSLogService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;

import com.google.common.collect.Sets;


public abstract class AbstractHttpServiceTrackerFactory<T> implements HttpServiceTrackerFactory {

    protected AuraDSLogService logService;
    private final String serviceClassName;
    private final Set<HttpServiceProviderProxy> httpServiceProviders = Sets.newHashSet();
    private T service;

    public AbstractHttpServiceTrackerFactory(String serviceClassName) {
        this.serviceClassName = serviceClassName;
    }

    @Override
    public ServiceTracker getTracker(ComponentContext componentContext) {
        logService.info("[" + getClass().getSimpleName() + "] " + " ServiceTracker creation Started");
        BundleContext context = componentContext.getBundleContext();
        ServiceTracker tracker = new ServiceTracker(context, serviceClassName, null) {
            @Override
            public Object addingService(ServiceReference ref) {
                Object service =  super.addingService(ref);
                serviceAdded((T)service);
                return service;
            }

            @Override
            public void removedService(ServiceReference ref, Object service) {
                serviceRemoved((T)service);
                super.removedService(ref, service);
            }
        };
        logService.info("[" + getClass().getSimpleName() + "] " + " ServiceTracker creation Finished");
        return tracker;
    }

    protected void serviceAdded(T service) {
        handleHttpServiceAdded(service);
    }

    protected void serviceRemoved(T service) {
        handleHttpServiceRemoved(service);
    }
    
    private void handleHttpServiceAdded(T service) {
        synchronized (this) {
            this.service = service;
            registerAll();
        }        
    }

    private void registerAll() {
        HttpServiceProviderProxy[] httpServiceProvidersArray = httpServiceProviders.toArray(new HttpServiceProviderProxy[0]);
        Arrays.sort(httpServiceProvidersArray);
        for (HttpServiceProviderProxy httpServiceProvider : httpServiceProvidersArray) {
            registerSilent(httpServiceProvider);
        }
    }

    private void unregisterAll() {
        for (HttpServiceProviderProxy httpServiceProvider : httpServiceProviders) {
            unregisterSilent(httpServiceProvider);
        }
    }
    
    private void handleHttpServiceRemoved(T service) {
        synchronized (this) {
            unregisterAll();
            this.service = null;
        }        
    }

    protected void handleHttpServiceProviderAdded(HttpServiceProviderProxy httpServiceProvider) {
        synchronized (this) {
            httpServiceProviders.add(httpServiceProvider);
            if (service != null) {
                registerSilent(httpServiceProvider);
            }
        }
    }

    protected void handleHttpServiceProviderRemoved(HttpServiceProviderProxy httpServiceProvider) {
        synchronized (this) {
            httpServiceProviders.remove(httpServiceProvider);
            if (service != null) {
                unregisterSilent(httpServiceProvider);
            }
        }
    }

    private void registerSilent(HttpServiceProviderProxy httpServiceProvider) {
        try {
            logService.info("Registering " + httpServiceProvider);
            register(httpServiceProvider);
        } catch (Throwable th) {
            logService.error("Problem registering " + httpServiceProvider, th);
        }
    }

    private void unregisterSilent(HttpServiceProviderProxy httpServiceProvider) {
        try {
            logService.info("Unregistering " + httpServiceProvider);
            unregister(httpServiceProvider);
        } catch (Throwable th) {
            logService.error("Problem unregistering " + httpServiceProvider, th);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void register(HttpServiceProviderProxy httpServiceProvider) throws ServletException, NamespaceException {
        if (httpServiceProvider.isRegistered()) {
            throw new IllegalStateException(httpServiceProvider + " must not be already registered");
        }
        
        if (httpServiceProvider instanceof ServletProxy) {
            register(service, (ServletProxy<? extends Servlet>) httpServiceProvider);
        } else if (httpServiceProvider instanceof FilterProxy) {
            register(service, (FilterProxy<? extends Filter>) httpServiceProvider);
        } else {
            throw new IllegalStateException("httpServiceProvider is of unknown type: " + httpServiceProvider.getClass().getName());
        }
        
        httpServiceProvider.setRegistered(true);
    }
    
    @SuppressWarnings("unchecked")
    private void unregister(HttpServiceProviderProxy httpServiceProvider) throws ServletException, NamespaceException {
        if (!httpServiceProvider.isRegistered()) {
// TODO: osgi-temporary
//            throw new IllegalStateException(httpServiceProvider + " must be already registered before unrestering");
        }
        
        if (httpServiceProvider instanceof ServletProxy) {
            unregister(service, (ServletProxy<? extends Servlet>) httpServiceProvider);
        } else if (httpServiceProvider instanceof FilterProxy) {
            unregister(service, (FilterProxy<? extends Filter>) httpServiceProvider);
        } else {
            throw new IllegalStateException("httpServiceProvider is of unknown type: " + httpServiceProvider.getClass().getName());
        }
        
        httpServiceProvider.setRegistered(false);
    }

    abstract protected void register(T service, ServletProxy<? extends Servlet> servlet) 
            throws ServletException, NamespaceException;
    
    abstract protected void unregister(T service, ServletProxy<? extends Servlet> servlet);
    
    abstract protected void register(T service, FilterProxy<? extends Filter> filter) 
            throws ServletException, NamespaceException;
    
    abstract protected void unregister(T service, FilterProxy<? extends Filter> filter);
    
    protected abstract void setLogService(AuraDSLogService logService);
    
    protected abstract void unsetLogService(AuraDSLogService logService);
    
}
