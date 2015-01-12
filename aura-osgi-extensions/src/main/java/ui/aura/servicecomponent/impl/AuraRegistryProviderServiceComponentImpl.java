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
package ui.aura.servicecomponent.impl;

import java.util.Set;

import org.auraframework.def.DefDescriptor.DefType;
import org.auraframework.def.Definition;
import org.auraframework.ds.servicecomponent.ServiceComponentConstants;
import org.auraframework.ds.serviceloader.AuraServiceProvider;
import org.auraframework.impl.context.AuraRegistryProviderImpl;
import org.auraframework.impl.java.controller.JavaControllerDefFactory;
import org.auraframework.impl.java.model.JavaModelDefFactory;
import org.auraframework.impl.java.provider.JavaProviderDefFactory;
import org.auraframework.system.AuraContext.Authentication;
import org.auraframework.system.AuraContext.Mode;
import org.auraframework.system.DefFactory;
import org.auraframework.system.DefRegistry;
import org.auraframework.system.SourceLoader;

import aQute.bnd.annotation.component.Component;

/**
 * Aura ServiceComponent registry that provides support for ServiceComponentConstants.SC_PREFIX
 * Allows to treat "serviceComponent://" protocol the same way as "java://" but with useAdapter=true
 */
@Component(provide = AuraServiceProvider.class)
public class AuraRegistryProviderServiceComponentImpl extends AuraRegistryProviderImpl {

    private DefRegistry<?>[] registries;

    @Override
    public DefRegistry<?>[] getRegistries(Mode mode, Authentication access, Set<SourceLoader> loaders) {
        if (access == Authentication.UNAUTHENTICATED) { return new DefRegistry<?>[0]; }

        DefRegistry<?>[] ret = registries;

        if (mode.isTestMode() || ret == null) {
            ret = new DefRegistry<?>[] {
                    createDefRegistry(new JavaControllerDefFactory(), DefType.CONTROLLER),
                    createDefRegistry(new JavaModelDefFactory(), DefType.MODEL),
                    createDefRegistry(new JavaProviderDefFactory(), DefType.PROVIDER),
            };

            if (!mode.isTestMode()) {
                registries = ret;
            }
        }

        return ret;
    }

    private static <T extends Definition> DefRegistry<T> createDefRegistry(DefFactory<T> factory, DefType defType) {
        return createDefRegistry(factory, defType, ServiceComponentConstants.SC_PREFIX);
    }
}
