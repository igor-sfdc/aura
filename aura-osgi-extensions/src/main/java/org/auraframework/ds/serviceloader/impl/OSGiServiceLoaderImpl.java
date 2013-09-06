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
package org.auraframework.ds.serviceloader.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.auraframework.ds.log.AuraDSLog;
import org.auraframework.ds.serviceloader.AuraServiceProvider;
import org.auraframework.util.ServiceLoader;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * OSGi/DS-based implementation of ServiceLoader. 
 * Replaces the old way Aura emulated DI via configuration classes
 *
 */
@Component (provide=ServiceLoader.class)
public class OSGiServiceLoaderImpl implements ServiceLoader {
    
    private static final ServiceMapHandler AURA_DS_SERVICE_HANDLER = new ServiceMapHandler();
    private final Map<Class<?>, Set<AuraServiceProvider>> serviceMap = Maps.newHashMap();
    private final Counters counters = new Counters();
    private LoadingMonitor loadingMonitor = new LoadingMonitor();
    
    public OSGiServiceLoaderImpl() {
        AuraDSLog.get().info("[" + getClass().getSimpleName() + "] " + " Instantiated");
    }

    @Reference (multiple=true, dynamic=true)
    protected void addAuraServiceProvider(AuraServiceProvider auraServiceProvider) {
        AURA_DS_SERVICE_HANDLER.set(serviceMap, auraServiceProvider, counters);
        loadingMonitor.update();
    }
    
    protected void removeAuraServiceProvider(AuraServiceProvider auraServiceProvider) {
        AURA_DS_SERVICE_HANDLER.unset(serviceMap, auraServiceProvider);
    }
    
    @Activate
    protected void activate() {
        AuraDSLog.get().info("Activated");
        loadingMonitor.startMonitoring();
    }
    
    @Deactivate
    protected void deactivateServiceImplFactory() {
        loadingMonitor.stopMonitoring();
        serviceMap.clear();
        counters.reset();
        AuraDSLog.get().info("[" + getClass().getSimpleName() + "] " + " Deactivated");
    }

    @Override
    public <T> T get(Class<T> type) {
        @SuppressWarnings("unchecked")  // This should have been verified when the class was added to the map
        Set<T> implementingServiceInstances = (Set<T>) serviceMap.get(type);
        
        if (implementingServiceInstances == null || implementingServiceInstances.isEmpty()) {
            AuraDSLog.get().warning("get(): No services found for type " + type.getName());
            return null;
        } else if (implementingServiceInstances.size() > 1) {
            AuraDSLog.get().warning("get(): Found more than one implementation for service type " + type.getSimpleName() + ". Returning the first one: ");                
        }
        
        AuraDSLog.get().info("[" + getClass().getSimpleName() + "] " + " ->->-> Returning single instance for " + type.getSimpleName());
        return implementingServiceInstances.iterator().next();
    }

    @Override
    public <T> Set<T> getAll(Class<T> type) {
        
        @SuppressWarnings("unchecked")  // This should have been verified when the class was added to the map
        Set<T> implementingServiceInstances = (Set<T>) serviceMap.get(type);
        
        if (implementingServiceInstances == null) {
            AuraDSLog.get().warning("getAll(): No services found for type " + type.getName());
            return Collections.emptySet();
        }

        AuraDSLog.get().info("[" + getClass().getSimpleName() + "] " + " =>=>= Returning " + implementingServiceInstances.size() + " instance(s) for " + type.getSimpleName());
        return implementingServiceInstances;
    }

    @Override
    public <T> T get(Class<T> type, String name) {
        // Currently unsupported
        // FIXME: osgi - When injecting service object into service map need to capture "name" annotation property
        throw new UnsupportedOperationException();
    }
    
    static class ServiceMapHandler {
        
        void set(Map<Class<?>, Set<AuraServiceProvider>> serviceMap, AuraServiceProvider value, Counters counters) {
            counters.incrementLookedAt();
            Class<? extends Object> objectClass = value.getClass();
            List<Class<?>> implementedInterfaces = ClassUtils.getAllInterfaces(objectClass);
            
            for (Class<?> implementedInterface : implementedInterfaces) {
                if (implementedInterface == AuraServiceProvider.class) {
                    // Skip the marker interface
                    continue;
                }
                addImplementation(serviceMap, implementedInterface, value, counters);
            }
            // Now add the class itself as key
            addImplementation(serviceMap, value.getClass(), value, counters);            
        }

        private void addImplementation(Map<Class<?>, Set<AuraServiceProvider>> serviceMap, Class<?> implementedInterfaceOrClass, AuraServiceProvider value, Counters counters) {
            Set<AuraServiceProvider> implementingServiceInstances = serviceMap.get(implementedInterfaceOrClass);
            if (implementingServiceInstances == null) {
                implementingServiceInstances = Sets.newHashSet();;
                serviceMap.put(implementedInterfaceOrClass, implementingServiceInstances);
            }
            implementingServiceInstances.add(value);
            counters.incrementAdded();
            AuraDSLog.get().info("Added implementation of AuraServiceProvider->" + implementedInterfaceOrClass.getSimpleName() + " by " + value + " - " + counters.added + "/" + counters.lookedAt);                
        }
        
        void unset(Map<Class<?>, Set<AuraServiceProvider>> serviceMap, AuraServiceProvider value) {
            Class<?>[] implementedInterfaces = value.getClass().getInterfaces();
            for (Class<?> implementedInterface : implementedInterfaces) {
                removeImplementation(serviceMap, implementedInterface, value);
            }
            removeImplementation(serviceMap, value.getClass(), value);
        }

        private void removeImplementation(Map<Class<?>, Set<AuraServiceProvider>> serviceMap, Class<?> implementedInterfaceOrClass, AuraServiceProvider value) {
            Set<AuraServiceProvider> implementingServiceInstances = serviceMap.get(implementedInterfaceOrClass);
            if (implementingServiceInstances != null) {
                implementingServiceInstances.remove(value);
                if (implementingServiceInstances.isEmpty()) {
                    serviceMap.remove(implementedInterfaceOrClass);
                    AuraDSLog.get().info("Removed the last implementation of AuraServiceProvider->" + implementedInterfaceOrClass.getSimpleName() + " by " + value + " - " + serviceMap.size());                
                } else {
                    AuraDSLog.get().info("Removed implementation of AuraServiceProvider->" + implementedInterfaceOrClass.getSimpleName() + " by " + value + " - " + serviceMap.size());                
                }
            }
        }
    }
    
    static class Counters {
        private int lookedAt = 0;
        private int added = 0;
        
        public void incrementLookedAt() {
            lookedAt++;
        }

        public void incrementAdded() {
            added++;
        }
        
        public int getLookedAt() {
            return lookedAt;
        }
        
        public int getAdded() {
            return added;
        }
        
        public void reset() {
            added = 0;
            lookedAt = 0;
        }
    }
}
