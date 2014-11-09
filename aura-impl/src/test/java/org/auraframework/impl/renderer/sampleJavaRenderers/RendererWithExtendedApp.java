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
package org.auraframework.impl.renderer.sampleJavaRenderers;

import java.io.IOException;
import java.util.Map;

import org.auraframework.def.Renderer;
import org.auraframework.instance.BaseComponent;
import org.auraframework.throwable.quickfix.QuickFixException;

public class RendererWithExtendedApp extends AbstractRendererForTestingIntegrationService implements
        Renderer {

    @Override
    public void render(BaseComponent<?, ?> component, Appendable out) throws IOException, QuickFixException {
        String desc = (String) component.getAttributes().getValue("desc");
        @SuppressWarnings("unchecked")
        Map<String, Object> attr = (Map<String, Object>) component.getAttributes().getValue("attrMap");
        String placeholder = (String) component.getAttributes().getValue("placeholder");
        String localId = (String) component.getAttributes().getValue("localId");
        Boolean useAsync = (Boolean) component.getAttributes().getValue("useAsync");
        //one way to inject handler for event fired from injected component is to append it to out here -- clickHandler__t and changeHandler__t
        //another way is to put the script directly into application(aisAsyncApp.app) markup -- click2Handler__t
        out.append("<script>"
                + "function clickHandler__t(event){document._clickHandlerCalled = true; document.__clickEvent=event;}\n"
                + "function changeHandler__t(event){document._changeHandlerCalled = 'Custom JS Code'; document.__changeEvent=event;}"
                + "</script>");

        out.append(String.format("<div id='%s' style='border: 1px solid black'/>", placeholder));

        injectComponent(desc, attr, localId, placeholder, out, useAsync, "auratest:aisAsyncApp");
    }

}

