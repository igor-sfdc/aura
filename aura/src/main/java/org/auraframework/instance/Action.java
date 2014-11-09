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
package org.auraframework.instance;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.auraframework.def.ActionDef;
import org.auraframework.system.LoggingContext.KeyValueLogger;
import org.auraframework.throwable.AuraExecutionException;
import org.auraframework.util.javascript.Literal;
import org.auraframework.util.json.Json;
import org.auraframework.util.json.JsonSerializer.NoneSerializer;

/**
 * An interface for the server side implementation of an action.
 */
public interface Action extends Instance<ActionDef> {

    public enum State {
        ERROR, NEW, RUNNING, SUCCESS
    }

    /**
     * Get the ID for the action.
     */
    public String getId();

    /**
     * Set the ID for the action.
     */
    public void setId(String id);

    /**
     * run the action.
     */
    public void run() throws AuraExecutionException;

    /**
     * Add actions to run after this one.
     */
    public void add(List<Action> actions);

    /**
     * get the current list of actions run after this one.
     */
    public List<Action> getActions();

    public Object getReturnValue();

    public State getState();

    public List<Object> getErrors();

    public static final Serializer SERIALIZER = new Serializer();

    public static class Serializer extends NoneSerializer<Action> {

        @Override
        public void serialize(Json json, Action action) throws IOException {
            // This is a temporary fix to allow server-side actions to return
            // null and have the value serialized to the client.
            // Ideally I think Serializers should have a flag that determines
            // whether null values should be serialized.
            // The reason I'm not adding that now is because it would add an
            // extra call to getSerializer() in Json.java, which,
            // because JsonSerializationContext does not have caching by type,
            // could be slow. To summarize, we should:
            // 1) add Serializer caching
            // 2) add shouldSerializeNulls hook
            // 3) get rid of this hacky workaround
            // Hoo-rah.
            Object returnValue = action.getReturnValue();
            if (returnValue == null) {
                returnValue = Literal.NULL;
            }

            json.writeMapBegin();

            json.writeMapEntry("id", action.getId());
            json.writeMapEntry("state", action.getState());
            json.writeMapEntry("returnValue", returnValue);
            json.writeMapEntry("error", action.getErrors());

            if (action.isStorable()) {
                json.writeMapEntry("storable", true);

                json.writeMapEntry("action", action.getDescriptor().getQualifiedName());

                // Include params for storable server actions
                Map<String, Object> params = action.getParams();
                if (params != null && !params.isEmpty()) {
                    json.writeMapEntry("params", params);
                }
            }

            action.getInstanceStack().serializeAsPart(json);
            json.writeMapEnd();
        }
    }

    /**
     * Log any params that are useful and safe to log.
     * @param paramLogger
     */
    public void logParams(KeyValueLogger logger);

    public boolean isStorable();

    public void setStorable();

    public Map<String, Object> getParams();

    /**
     * Get the instance stack for this action.
     */
    public InstanceStack getInstanceStack();
}
