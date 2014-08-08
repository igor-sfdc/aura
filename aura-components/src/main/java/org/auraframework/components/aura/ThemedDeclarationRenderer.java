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
package org.auraframework.components.aura;

import java.io.IOException;
import java.util.List;

import org.auraframework.def.Renderer;
import org.auraframework.def.StyleDef;
import org.auraframework.def.ThemeDef;
import org.auraframework.instance.BaseComponent;
import org.auraframework.throwable.quickfix.QuickFixException;
import org.auraframework.util.AuraTextUtil;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Used in the rendering of {@link StyleDef}s (CSS files) to output the value of referenced {@link ThemeDef} variables.
 * This represents a single declaration, for example "margin: theme('namespace.theme.margin')". In other words this has
 * to output both the property and the value.
 */
public class ThemedDeclarationRenderer implements Renderer {
    @Override
    @SuppressWarnings("unchecked")
    public void render(BaseComponent<?, ?> component, Appendable out) throws IOException, QuickFixException {
        String property = component.getAttributes().getValue("property").toString();
        // Location location = (Location) component.getAttributes().getValue("location");
        List<String> references = (List<String>) component.getAttributes().getValue("references");

        // ThemeValueProvider provide = Aura.getStyleAdapter().getThemeValueProvider(component.);

        // gather values. there can be multiple values if there were multiple theme functions in the declaration value.
        List<String> resolved = Lists.newArrayList();
        for (String reference : references) {
            String value = reference;// provider.getValue(reference, location).toString();

            // for any empty values, we don't output the declaration at all
            if (AuraTextUtil.isNullOrEmpty(value)) {
                return;
            }

            resolved.add(value);
        }

        // output property name
        out.append(property).append(":");

        // output each value
        out.append(Joiner.on(" ").join(resolved));
    }
}
