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

import org.auraframework.ds.log.AuraDSLog;
import org.auraframework.util.ServiceLoader;
import org.auraframework.util.ServiceLocator;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

/**
 * 
 * This class injects OSGi ServiceLoader into AuraFramework. Note that 
 * ideally this should have been implemented by using @Reference (and not
 * by calling a static method)
 *
 */
@Component
public class OSGiServiceLoaderInjector {
    
    @Reference
    protected void setDSServiceLoader(ServiceLoader dsServiceLoader) {
        AuraDSLog.get().info("[" + getClass().getSimpleName() + "] " + " dsServiceLoader Set");
        ServiceLocator.init(dsServiceLoader);
        AuraDSLog.get().info("[" + getClass().getSimpleName() + "] " + 
                " Injected " + dsServiceLoader.getClass().getSimpleName() + " into service locator");
    }
    
    protected void unsetDSServiceLoader(ServiceLoader dsServiceLoader) {
        // TODO: osgi Need a way to remove DS service location, i.e., need opposite to ServiceLocator.init()
        AuraDSLog.get().info("[" + getClass().getSimpleName() + "] " + " dsServiceLoader Unset");
    }
}
