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
package org.auraframework.ds.log;

import org.osgi.framework.ServiceReference;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

/**
 * A global access point to LogService 
 * 
 * This is a temporary solution that simplifies debugging by making code lines 
 * accessible from log output (to be replaced with a standard OSGi logging implementation)
 * 
 *
 *
 */
@Component
public class AuraDSLog {
    private static AuraDSLogService INSTANCE = new NullLogService();

    @Reference
    protected void setLogService(AuraDSLogService logServiceValue) {
        INSTANCE = logServiceValue;
        INSTANCE.info("AuraTestLogService started");
    }

    protected void unsetLogService(AuraDSLogService logServiceValue) {
        INSTANCE.info("AuraTestLogService terminated");
        INSTANCE = new NullLogService();
    }
    
    // FIXME: osgi DS Antipattern
    public static AuraDSLogService get() {
        return INSTANCE;
    }
    
    /**
     * Provides "no-op" functionality until real implementation is loaded
     *
     *
     */
    static class NullLogService implements AuraDSLogService {
        @Override
        public void log(int level, String message) {
            AuraDSLogServiceImpl.handleLog(level, "[" + getClass().getSimpleName() + "] " + message, null);
        }

        @Override
        public void log(int level, String message, Throwable exception) {
            AuraDSLogServiceImpl.handleLog(level, "[" + getClass().getSimpleName() + "] " + message, exception);
        }

        @SuppressWarnings("rawtypes")
        @Override
        public void log(ServiceReference sr, int level, String message) {
            AuraDSLogServiceImpl.handleLog(level, "[" + getClass().getSimpleName() + "] " + message, null);
        }

        @SuppressWarnings("rawtypes")
        @Override
        public void log(ServiceReference sr, int level, String message, Throwable exception) {
            AuraDSLogServiceImpl.handleLog(level, "[" + getClass().getSimpleName() + "] " + message, exception);
        }

        @Override
        public void info(String message) {
            log(LOG_INFO, message);
        }

        @Override
        public void warning(String message) {
            log(LOG_WARNING, message);
        }

        @Override
        public void warning(String message, Throwable th) {
            log(LOG_WARNING, message, th);
        }

        @Override
        public void debug(String message) {
            log(LOG_DEBUG, message);
        }

        @Override
        public void error(String message) {
            log(LOG_ERROR, message);
        }

        @Override
        public void error(String message, Throwable th) {
            log(LOG_ERROR, message, th);
        }
    }
}
