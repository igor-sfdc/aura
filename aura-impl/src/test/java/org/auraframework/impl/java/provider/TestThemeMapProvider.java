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
package org.auraframework.impl.java.provider;

import java.util.Map;

import org.auraframework.def.ThemeMapProvider;
import org.auraframework.system.Annotations.Provider;
import org.auraframework.throwable.quickfix.QuickFixException;

import com.google.common.collect.ImmutableMap;

@Provider
public class TestThemeMapProvider implements ThemeMapProvider {
    public static final String REF = "java://org.auraframework.impl.java.provider.TestThemeMapProvider";

    public static final Map<String, String> MAP = ImmutableMap.of(
            "font", "Arial",
            "padding", "15px",
            "textColor", "red",
            "bgColor", "#434fef"
            );

    @Override
    public Map<String, String> provide() throws QuickFixException {
        return MAP;
    }
}
