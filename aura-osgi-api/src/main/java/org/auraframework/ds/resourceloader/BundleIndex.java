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
import java.net.URL;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.auraframework.ds.util.BundleUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
/**
 * A temporary solution to build package index on the fly (currently Maven build 
 * fails to create a proper .index, which would otherwise make package unusable)  
 * 
 *
 *
 */
public class BundleIndex {
    private static final String BUNDLE_PATH_SEPARATOR = "/";
    private static final int NAMESPACE_GROUP = 1;
    private static final int ENTRY_NAME_GROUP = 2;
    private static final String NAMESPACE_SEPARATOR = ":";
    private static final String ENTRIES_SEPARATOR = ",";
    private final StringBuilder indexBuffer = new StringBuilder();
    private static final Pattern indexEntryPattern = Pattern.compile(".*/([^/]*)/[^/]*/([^/]+[.][^/]+)");
            
    protected BundleIndex(String basePackage, Class<?> clientClass) throws IOException {
        this(basePackage, BundleUtil.getBundleContext(clientClass));
    }

    protected BundleIndex(String basePackage, BundleContext bundleContext) throws IOException {
        if (bundleContext != null) {
            Bundle bundle = bundleContext.getBundle();
            locateEntries(bundle, indexBuffer, basePackage);
        }
    }
    
    static String createKey(String packageName, Class<?> clientClass) {
        BundleContext bundleContext = BundleUtil.getBundleContext(clientClass);
        if (bundleContext == null) {
            return packageName;
        }

        return packageName + bundleContext.getBundle().getBundleId();
    }

    private static void locateEntries(Bundle bundle, StringBuilder indexBuffer, String parent) throws IOException {
        Enumeration<URL> entries = bundle.findEntries(parent, "*", true);
        while (entries != null && entries.hasMoreElements()) {
            URL entryURL = entries.nextElement();
            String entry = entryURL.toString();
            if (!entry.endsWith(BUNDLE_PATH_SEPARATOR)) {
                addToIndex(indexBuffer, entry);
            }
        }
    }
    
    private static void addToIndex(StringBuilder indexBuffer, String entry) throws IOException {
        Matcher matcher = indexEntryPattern.matcher(entry);
        if (matcher.matches()) {
            if (indexBuffer.length() > 0) {
                indexBuffer.append(ENTRIES_SEPARATOR);
            }
            indexBuffer.append(matcher.group(NAMESPACE_GROUP));
            indexBuffer.append(NAMESPACE_SEPARATOR);
            indexBuffer.append(matcher.group(ENTRY_NAME_GROUP));
        }
    }

    @Override
    public String toString() {
        return indexBuffer.toString();
    }
}
