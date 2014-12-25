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

import java.util.concurrent.ConcurrentHashMap;

import org.auraframework.ds.servicecomponent.Access;
import org.auraframework.ds.servicecomponent.Controller;
import org.auraframework.ds.servicecomponent.ModelInstance;
import org.auraframework.ds.servicecomponent.ModelFactory;
import org.auraframework.ds.servicecomponent.ModelInitializationException;
import org.auraframework.ds.servicecomponent.Provider;
import org.auraframework.ds.servicecomponent.ServiceComponentInstanceLoader;
import org.auraframework.throwable.AuraUnhandledException;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component
public class ServiceComponentInstanceLoaderImpl implements ServiceComponentInstanceLoader {

    private final ConcurrentHashMap<String, ModelFactory<? extends ModelInstance>> modelFactoryMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Controller> controllerMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Provider> providerMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Access> accessMap = new ConcurrentHashMap<>();

    @Reference(multiple = true, dynamic = true, optional = true)
    protected void addModelFactory(ModelFactory<? extends ModelInstance> modelFactory) {
        modelFactoryMap.put(modelFactory.getClass().getName(), modelFactory);
    }

    void removeModelFactory(ModelFactory<? extends ModelInstance> modelFactory) {
        String className = modelFactory.getClass().getName();
        modelFactoryMap.remove(className, modelFactory);
    }

    @Reference(multiple = true, dynamic = true, optional = true)
    void addController(Controller controller) {
        controllerMap.put(controller.getClass().getName(), controller);
    }

    void removeController(Controller controller) {
        String className = controller.getClass().getName();
        controllerMap.remove(className, controller);
    }

    @Reference(multiple = true, dynamic = true, optional = true)
    void addProvider(Provider provider) {
        providerMap.put(provider.getClass().getName(), provider);
    }

    void removeProvider(Provider provider) {
        String className = provider.getClass().getName();
        providerMap.remove(className, provider);
    }

    @Reference(multiple = true, dynamic = true, optional = true)
    void addAccess(Access access) {
        accessMap.put(access.getClass().getName(), access);
    }

    void removeAccess(Access access) {
        String className = access.getClass().getName();
        accessMap.remove(className, access);
    }

    @Override
    public ModelInstance getModelInstance(String className) {
        ModelFactory<? extends ModelInstance> modelFactory = modelFactoryMap.get(className + "Factory");
        try {
            return modelFactory.modelInstance();
        } catch (ModelInitializationException e) {
            throw new AuraUnhandledException("Error creating model instance for '" + className + "'", e);
        }
    }

    @Override
    public Controller getControllerInstance(String className) {
        return controllerMap.get(className);
    }

    @Override
    public Provider getProviderInstance(String className) {
        return providerMap.get(className);
    }

    @Override
    public Access getAccessInstance(String className) {
        return accessMap.get(className);
    }
}
