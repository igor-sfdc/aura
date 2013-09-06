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
import java.net.URL;

import org.osgi.framework.Bundle;

public interface BundleResourceAccessor {

    InputStream getResource(String resource) throws IOException;

    InputStream getResource(String resource, Class<?> clientClass) throws IOException;

    boolean exists(String resource, Class<?> clientClass) throws IOException;

    BundleIndex getBundleIndex(String packageName, Class<?> clientClass) throws IOException;

    Bundle getPackageProviderBundle(String componentSourcePackage);

    URL getResourceUrl(ClassLoader parent, String resourcePath) throws IOException;

}