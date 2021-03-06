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
package org.auraframework.ds.component.location.providers;

import static org.auraframework.Constants.CORE_COMPONENT_PACKAGE;

import org.auraframework.adapter.ComponentLocationAdapter.Impl;
import org.auraframework.ds.serviceloader.AuraServiceProvider;
import org.auraframework.provider.api.ComponentPackageProvider;

import aQute.bnd.annotation.component.Component;

/**
 * Exposes Aura components for core bundle
 *
 */
@Component (provide={AuraServiceProvider.class, ComponentPackageProvider.class})
public class ComponentLocationAdapterCoreBundleComponentsProvider extends Impl implements ComponentPackageProvider {
    public ComponentLocationAdapterCoreBundleComponentsProvider() {
        super(CORE_COMPONENT_PACKAGE);
    }
}
