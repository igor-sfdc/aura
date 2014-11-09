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
package org.auraframework;

import java.io.File;

public class Constants {
    public static final String AURA_COMPONENT_DIRECTORY_SYSTEM_PROPERTY = "aura.components.directory";
    public static final String USER_HOME_SYSTEM_PROPERTY = "user.home";
    public static final String DEFAULT_AURA_COMPONENT_DIRECTORY = System.getProperty(USER_HOME_SYSTEM_PROPERTY) + File.pathSeparator + "aura_components";
    public static final String AURA_COMPONENT_DIRECTORY = System.getProperty(AURA_COMPONENT_DIRECTORY_SYSTEM_PROPERTY, DEFAULT_AURA_COMPONENT_DIRECTORY);
    public static final String CORE_COMPONENT_PACKAGE = "components_aura_components";
}
