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
package org.auraframework.ds.resourceloader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.auraframework.ds.util.BundleUtil;
import org.auraframework.provider.api.StaticResourceProvider;
import org.osgi.framework.BundleContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Component
public class StaticResourceAccessorFactory implements StaticResourceAccessor {
    private Set<StaticResourceProvider> staticResourceProviders = Sets.newHashSet();
    private BundleResourceLoader bundleResourceLoader;
    private static StaticResourceAccessor INSTANCE;
    private final Map<String, BundleIndex> bundleIndexMap = Maps.newHashMap();
    
    @Reference (multiple=true, dynamic=true, optional=true)
    protected void addComponentLocationProvider(StaticResourceProvider staticResourceProvider) {
        staticResourceProviders.add(staticResourceProvider);
    }
    
    protected void removeComponentLocationProvider(StaticResourceProvider staticResourceProvider) {
        staticResourceProviders.remove(staticResourceProvider);
    }
    
    @Reference
    protected void setBundleResourceLoader(BundleResourceLoader bundleResourceLoader) {
        this.bundleResourceLoader = bundleResourceLoader;
    }
    
    @Activate
    protected void activate() {
        INSTANCE = this;
    }
    
    // FIXME: osgi DS Antipattern
    public static StaticResourceAccessor get() {
        return INSTANCE;
    }
    
    /* (non-Javadoc)
     * @see org.auraframework.ds.resourceloader.ResourceFactory#getResource(java.lang.String, java.lang.Class)
     */
    @Override
    public InputStream getResource(String resource, Class<?> clientClass) throws IOException {
        BundleContext bundleContext = BundleUtil.getBundleContext(clientClass);
        return getResource(resource, bundleContext);
    }

    @Override
    public boolean exists(String resource, Class<?> clientClass)
            throws IOException {
        // FIXME: osgi - This needs to be re-implemented as bundle.getEntry(resource) != null (supposedly less expensive)
        return getResource(resource, clientClass) != null;
    }

    @Override
    public BundleIndex getBundleIndex(String packageName, Class<?> clientClass) throws IOException {
        String bundleIndexKey = BundleIndex.createKey(packageName, clientClass);
        BundleIndex bundleIndex = null;
        synchronized (bundleIndexMap) {
            bundleIndex = bundleIndexMap.get(bundleIndexKey);
            if (bundleIndex == null) {
                bundleIndex = new BundleIndex(packageName, clientClass);
                bundleIndexMap.put(bundleIndexKey, bundleIndex);
            }
        }
        
        return bundleIndex;
    }

    private InputStream getResource(String resource, BundleContext bundleContext) throws IOException {
        InputStream resourceStream = getLocalStaticResource(resource, bundleContext);
        resourceStream = resourceStream != null ? resourceStream : getProviderStaticResource(resource);
        return resourceStream;
    }
    
    private InputStream getLocalStaticResource(String resource, BundleContext bundleContext) throws IOException {
        return bundleResourceLoader.getResource(resource, bundleContext);
    }
    
    private InputStream getProviderStaticResource(String resource) throws IOException {
        InputStream resourceStream = null;
        // FIXME: osgi This is the most brutal way of looking for a resource by iterating over all the providers. We need provider metadata to make this efficient
        for (StaticResourceProvider staticResourceProvider : staticResourceProviders) {
            resourceStream = staticResourceProvider.getStaticResource(resource);
            if (resourceStream != null) {
                return resourceStream;
            }
        }
        return resourceStream;
    }
}
