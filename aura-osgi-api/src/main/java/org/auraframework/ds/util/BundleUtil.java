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
package org.auraframework.ds.util;

import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;

import com.google.common.collect.Maps;

public class BundleUtil {
    /**
     * Computes bundle context for the given bundle class. 
     * Use with caution and only if componentContext is not available
     * 
     * @param bundleClass bundle resource
     * @return bundle context or null if passed object reference is null
     */
    public static BundleContext getBundleContext(Class<?> bundleClass) {
        Bundle bundle = getBundle(bundleClass);
        return bundle != null ? bundle.getBundleContext() : null;
    }

    public static Bundle getBundle(Class<?> bundleClass) {
        return bundleClass != null ? FrameworkUtil.getBundle(bundleClass) : null;
    }
    
    /**
     * Provides for supposedly "missing" OSGi API. I guess the reason it is missing is the fact
     * that this logic can be quite expensive, so it should not be used more than once, or
     * otherwise we should provide a singled with the bundle map being cached and dynamically maintained
     * 
     * @param bundleSymbolicName symbolic bundle name
     * @param bundleContext bundle context
     * @return bundle bundle for symbolic name
     * @throws BundleException if bundle with requested symbolic name does not exist
     */
    public static Bundle getBundleBySymbolicName(String bundleSymbolicName, BundleContext bundleContext) throws BundleException {
        Bundle[] bundles = bundleContext.getBundles();
        
        Map<String, Bundle> bundleMap = Maps.newHashMap();
        for (Bundle bundle : bundles) {
            bundleMap.put(bundle.getSymbolicName(), bundle);
        }
        
        Bundle bundle = bundleMap.get(bundleSymbolicName);
        if (bundle == null) {
            throw new BundleException("Bundle for symbolic name '" + bundleSymbolicName + "' not found");
        }
        return bundle;
    }
    
    /**
     * A convenience method to start a bundle
     * 
     * @param bundleSymbolicName
     * @param bundleContext
     * @throws BundleException
     */
    public static void startBundle(String bundleSymbolicName, BundleContext bundleContext) throws BundleException {
        Bundle bundle = getBundleBySymbolicName(bundleSymbolicName, bundleContext);
        bundle.start();
    }
    
    /**
     * A convenience method to stop a bundle
     * 
     * @param bundleSymbolicName
     * @param bundleContext
     * @throws BundleException
     */
    public static void stopBundle(String bundleSymbolicName, BundleContext bundleContext) throws BundleException {
        Bundle bundle = getBundleBySymbolicName(bundleSymbolicName, bundleContext);
        bundle.stop();
    }
}
