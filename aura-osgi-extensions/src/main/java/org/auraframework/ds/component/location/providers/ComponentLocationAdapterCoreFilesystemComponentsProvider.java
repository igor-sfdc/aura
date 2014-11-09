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

import static org.auraframework.Constants.AURA_COMPONENT_DIRECTORY;

import java.io.File;

import org.auraframework.adapter.ComponentLocationAdapter.Impl;
import org.auraframework.ds.serviceloader.AuraServiceProvider;

import aQute.bnd.annotation.component.Component;

/**
 * Exposes Aura components from a local folder
 *
 */
@Component (provide=AuraServiceProvider.class)
public class ComponentLocationAdapterCoreFilesystemComponentsProvider extends Impl {
    public ComponentLocationAdapterCoreFilesystemComponentsProvider() {
        super(new File(AURA_COMPONENT_DIRECTORY), null, null);
    }
}
