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
package org.auraframework.ds.component.location;

import org.auraframework.adapter.ComponentLocationAdapter.Impl;
import org.auraframework.ds.serviceloader.AuraServiceProvider;

import aQute.bnd.annotation.component.Component;

/**
 * Default implementation of ComponentLocationAdapter (formerly provided by AuraComponentsConfig class)
 * Note that we had to extend Impl class in order to use its non-default constructor
 * TODO: Is this the right package location for this class?
 * 
 *
 *
 */
@Component (provide=AuraServiceProvider.class)
public class ComponentLocationAdapter_components_aura_components extends Impl {
    private static final String DEFAULT_COMPONENT_PACKAGE = "components_aura_components";
    public ComponentLocationAdapter_components_aura_components() {
        super(DEFAULT_COMPONENT_PACKAGE);
    }
}
