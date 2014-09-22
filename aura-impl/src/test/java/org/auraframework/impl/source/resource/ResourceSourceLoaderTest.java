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
package org.auraframework.impl.source.resource;

import org.auraframework.impl.AuraImplTestCase;

public class ResourceSourceLoaderTest extends AuraImplTestCase {
    public ResourceSourceLoaderTest(String name){
        super(name);
    }
    
    /**
     * All namespaces loaded by ResourceSourceLoader are privileged, verify that ResourceSourceLoader says so.
     */
    public void testIsPrivilegedNamespace(){
        ResourceSourceLoader rs = new ResourceSourceLoader(null);
        assertTrue("All namespaces loaded by ResourceSourceLoader are to be privileged",
                rs.isPrivilegedNamespace(null));
        assertTrue("All namespaces loaded by ResourceSourceLoader are to be privileged," +
                "Regardless of the namespace.", rs.isPrivilegedNamespace("fooBared"));
    }
}
