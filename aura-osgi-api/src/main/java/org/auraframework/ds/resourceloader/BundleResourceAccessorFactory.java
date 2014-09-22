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
package org.auraframework.ds.resourceloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.auraframework.ds.util.BundleUtil;
import org.auraframework.provider.api.ComponentLocationProvider;
import org.auraframework.provider.api.ComponentPackageProvider;
import org.auraframework.provider.api.BundleResourceProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Component
public class BundleResourceAccessorFactory implements BundleResourceAccessor {
    private Set<BundleResourceProvider> bundleResourceProviders = Sets.newHashSet();
    private BundleResourceLoader bundleResourceLoader;
    private static BundleResourceAccessor INSTANCE;
    private final Map<String, BundleIndex> bundleIndexMap = Maps.newHashMap();
    private Map<String, Bundle> packageProviders = Maps.newHashMap();
    private Set<File> componentLocationProviders = Sets.newHashSet();
    
    @Reference (multiple=true, dynamic=true, optional=true)
    protected void addLocationProvider(ComponentLocationProvider componentLocationProvider) {
    	componentLocationProviders.add(componentLocationProvider.getComponentLocation());
    }
    
    protected void removeLocationProvider(ComponentLocationProvider componentLocationProvider) {
    	componentLocationProviders.remove(componentLocationProvider.getComponentLocation());
    }
    
    @Reference (multiple=true, dynamic=true, optional=true)
    protected void addComponentPackageProvider(ComponentPackageProvider componentPackageProvider) {
        Bundle packageProviderBundle = BundleUtil.getBundle(componentPackageProvider.getClass());
        if (packageProviderBundle != null) {
            packageProviders.put(componentPackageProvider.getComponentSourcePackage(), packageProviderBundle);
        }
    }
    
    protected void removeComponentPackageProvider(ComponentPackageProvider componentPackageProvider) {
        packageProviders.remove(componentPackageProvider.getComponentSourcePackage());
    }
     
    @Reference (multiple=true, dynamic=true, optional=true)
    protected void addBundleResourceProvider(BundleResourceProvider bundleResourceProvider) {
        bundleResourceProviders.add(bundleResourceProvider);
    }
    
    protected void removeBundleResourceProvider(BundleResourceProvider bundleResourceProvider) {
        bundleResourceProviders.remove(bundleResourceProvider);
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
    public static BundleResourceAccessor get() {
        return INSTANCE;
    }
    
    @Override
    public InputStream getResource(String resource) throws IOException {
        // By default assume that we start with core bundle
        Class<?> clientClass = this.getClass();
        BundleContext bundleContext = BundleUtil.getBundleContext(clientClass );
        return getResourceStream(resource, bundleContext);
    }
    
    @Override
    public InputStream getResource(String resource, Class<?> clientClass) throws IOException {
        BundleContext bundleContext = BundleUtil.getBundleContext(clientClass);
        return getResourceStream(resource, bundleContext);
    }

    @Override
    public boolean exists(String resource, Class<?> clientClass) throws IOException {
        Bundle bundle = BundleUtil.getBundle(clientClass);
        // FIXME: osgi The second part of this condition needs to be implemented via getEntry() on provider bundles (less expensive)
        return bundle.getEntry(resource) != null || getProvidedResourceUrl(resource) != null;
    }

    @Override
    public BundleIndex getBundleIndex(String packageName, Class<?> clientClass) throws IOException {
        String bundleIndexKey = BundleIndex.createKey(packageName, clientClass);
        BundleIndex bundleIndex = null;
        synchronized (bundleIndexMap) {
            bundleIndex = bundleIndexMap.get(bundleIndexKey);
            if (bundleIndex == null) {
                Bundle packageProviderBundle = getPackageProviderBundle(packageName);
                bundleIndex = packageProviderBundle != null ? new BundleIndex(packageName, packageProviderBundle) : new BundleIndex(packageName, clientClass);
                bundleIndexMap.put(bundleIndexKey, bundleIndex);
            }
        }
        
        return bundleIndex;
    }

    @Override
    public URL getResourceUrl(ClassLoader parent, String resourcePath) throws IOException {
        // TODO: osgi The first check is using default Aura classloader method (which would only work for core resources)
        URL resourceUrl = parent.getResource(resourcePath);
        
        if (resourceUrl == null) {
            resourceUrl = getProvidedResourceUrl(resourcePath);
        }
        return resourceUrl;
    }
    
    @Override
    public Bundle getPackageProviderBundle(String componentSourcePackage) {
        return packageProviders.get(componentSourcePackage);
    }

    @Override
    public Set<File> getComponentLocations() {
    	// TODO: osgi needs to be immutable set
    	return componentLocationProviders;
    }
    
    private InputStream getResourceStream(String resource, BundleContext bundleContext) throws IOException {
        InputStream resourceStream = getLocalBundleResourceStream(resource, bundleContext);
        resourceStream = resourceStream != null ? resourceStream : getProvidedResourceStream(resource);
        return resourceStream;
    }
    
    private InputStream getLocalBundleResourceStream(String resource, BundleContext bundleContext) throws IOException {
        return bundleResourceLoader.getResourceStream(resource, bundleContext);
    }
    
    private InputStream getProvidedResourceStream(String resource) throws IOException {
        URL providedResourceUrl = getProvidedResourceUrl(resource);
        return providedResourceUrl != null ? providedResourceUrl.openStream() : null;
    }
    
    private URL getProvidedResourceUrl(String resource) throws IOException {
        // FIXME: osgi This is the most brutal way of looking for a resource by iterating over all the providers. We need provider metadata to make this efficient
        for (BundleResourceProvider bundleResourceProvider : bundleResourceProviders) {
            URL resourceUrl = bundleResourceProvider.getBundleResource(resource);
            if (resourceUrl != null) {
                return resourceUrl;
            }
        }
        return null;
    }
}
