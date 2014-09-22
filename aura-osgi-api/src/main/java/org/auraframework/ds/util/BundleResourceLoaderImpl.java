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
package org.auraframework.ds.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.auraframework.ds.resourceloader.BundleResourceLoader;
import org.osgi.framework.BundleContext;

import aQute.bnd.annotation.component.Component;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

@Component
public class BundleResourceLoaderImpl implements BundleResourceLoader {

    @Override
    public InputStream getResourceStream(String resourceLocation, BundleContext bundleContext) throws IOException {
        return getResourceImpl(resourceLocation, bundleContext);
    }
    
    @Override
    public String getResourceAsString(String resourceLocation, BundleContext bundleContext) throws IOException {
        return inputStreamToString(getResourceImpl(resourceLocation, bundleContext));
    }

    private static InputStream getResourceImpl(String resourceLocation, BundleContext bundleContext) throws IOException {
        resourceLocation = resourceLocation.startsWith("/") ? resourceLocation.substring(1) : resourceLocation;
        
        URL resourceURL = bundleContext.getBundle().getEntry(resourceLocation);
        if (resourceURL != null) {
            return resourceURL.openStream();
        }
        return null;
    }
    
    private static String inputStreamToString(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }
        
        try {
            return CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));
        } catch (UnsupportedEncodingException e) {
            // This is extremely unlikely
            return CharStreams.toString(new InputStreamReader(inputStream));
        } finally {
            inputStream.close();
        }
    }
}
