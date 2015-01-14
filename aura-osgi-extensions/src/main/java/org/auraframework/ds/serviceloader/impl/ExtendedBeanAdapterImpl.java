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

import org.auraframework.adapter.BeanAdapter;
import org.auraframework.def.JavaControllerDef;
import org.auraframework.def.JavaModelDef;
import org.auraframework.def.JavaProviderDef;
import org.auraframework.ds.log.AuraDSLogService;
import org.auraframework.ds.servicecomponent.ServiceComponentInstanceLoader;
import org.auraframework.ds.serviceloader.AuraServiceProvider;
import org.auraframework.throwable.quickfix.QuickFixException;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

/**
 * Alternative BeanAdapter to support ServiceComponent
 */
// Priority property indicates that this provider takes precedence over
// Aura default BeanAdapter implementation that has priority 0
@Component(provide = AuraServiceProvider.class, properties = {"priority:Integer=100"})
public class ExtendedBeanAdapterImpl implements BeanAdapter {

    private ServiceComponentInstanceLoader serviceComponentInstanceLoader;

    @Reference
    void setServiceComponentInstanceLoader(ServiceComponentInstanceLoader serviceComponentInstanceLoader) {
        this.serviceComponentInstanceLoader = serviceComponentInstanceLoader;
    }

    @Override
    public void validateModelBean(JavaModelDef def) throws QuickFixException {
        // TODO: service-component - need to implement
    }

    @Override
    public Object getModelBean(JavaModelDef def) {
        return serviceComponentInstanceLoader.getModelInstance(toClassName(def));
    }

    @Override
    public void validateControllerBean(JavaControllerDef def) throws QuickFixException {
        // TODO: service-component - need to implement
    }

    @Override
    public Object getControllerBean(JavaControllerDef def) {
        return serviceComponentInstanceLoader.getControllerInstance(toClassName(def));
    }

    @Override
    public void validateProviderBean(JavaProviderDef def, Class<?> clazz) throws QuickFixException {
        // TODO: service-component - need to implement
    }

    @Override
    public <T> T getProviderBean(JavaProviderDef def, Class<T> clazz) {
        // TODO: service-component - need to resolve this, cast is not acceptable
        return (T)serviceComponentInstanceLoader.getProviderInstance(toClassName(def));
    }

    private AuraDSLogService logService;

    @Reference
    protected void setLogService(AuraDSLogService logServiceValue) {
        logService = logServiceValue;
    }

    @Activate
    protected void activate() {
        logService.debug("Activated new instance of: " + this.getClass().getName() + this);
    }

    // TODO: service-component - need a common JavaDef interface with a method like getJavaType()
    private static String toClassName(JavaProviderDef def) {
        Class<?> clazz = def.getJavaType();
        return clazz.getName();
    }

    private static String toClassName(JavaControllerDef def) {
        Class<?> clazz = def.getJavaType();
        return clazz.getName();
    }

    private static String toClassName(JavaModelDef def) {
        Class<?> clazz = def.getJavaType();
        return clazz.getName();
    }
}
