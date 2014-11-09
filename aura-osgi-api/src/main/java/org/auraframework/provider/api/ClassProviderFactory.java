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
package org.auraframework.provider.api;

import java.util.Set;

import org.auraframework.ds.log.AuraDSLog;

import com.google.common.collect.Sets;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

/**
 * A factory to expose classes from provider bundles
 * 
 *
 *
 */
@Component
public class ClassProviderFactory {
    private static Set<ClassProvider> classProviders = Sets.newHashSet();

    @Reference (multiple=true, dynamic=true, optional=true) 
    protected void addClassProvider(ClassProvider classProvider) {
        classProviders.add(classProvider);
    }
    
    protected void removeClassProvider(ClassProvider classProvider) {
        classProviders.remove(classProvider);
    }
    
    public static Class<?> getClazzForName(String name) {
        Class<?> clazz = getLocalClassForName(name);
        clazz = clazz != null ? clazz : getProviderClassForName(name);
        if (clazz == null) {
            AuraDSLog.get().error("Coud not find local class " + name);
        }
        return clazz;
    }
    
    private static Class<?> getLocalClassForName(String name) {
        Class<?> clazz;
        try {
                clazz = Class.forName(name);
                AuraDSLog.get().info("Loaded LOCAL class " + name);
                return clazz;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
    private static Class<?> getProviderClassForName(String name) {
        Class<?> clazz;
        // TODO: osgi - fixme This is the most brutal way of looking for a class by iterating over all the providers. We need provider metadata to make this efficient
        for (ClassProvider classProvider : classProviders) {
            try {
                clazz = classProvider.getClazzForName(name);
                AuraDSLog.get().info("Loaded PROVIDER class " + name);
                return clazz;
            } catch (ClassNotFoundException e) {
                // Ignore
            }
        }
        return null;
     }
}
