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
import org.osgi.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;

/**
 * A temporary implementation of loging API (a placehoder to be replaced
 * with a more robust/OSGi standard implementation in the future)
 *
 *
 *
 */
@Component
public class AuraDSLogServiceImpl implements AuraDSLogService {
    private static final int STACKTRACE_OFFSET = 4;

    enum LogLevel {
        ERROR(LogService.LOG_ERROR),
        WARNING(LogService.LOG_WARNING),
        INFO(LogService.LOG_INFO),
        DEBUG(LogService.LOG_DEBUG);

        private final int level;

        LogLevel(int level) {
            this.level = level;
        }

        public static LogLevel fromLogServiceLevel(int level) {
            for (LogLevel value : LogLevel.values()) {
                if (value.level == level) {
                    return value;
                }
            }
            return null;
        }
    }

    @Override
    public void log(int level, String message) {
        handleLog(level, message, null);
    }

    @Override
    public void log(int level, String message, Throwable exception) {
        handleLog(level, message, exception);
    }

    @Override
    public void log(ServiceReference sr, int level, String message) {
    	String bundleName = sr.getBundle().getSymbolicName();
        handleLog(level, "[Bundle: " + bundleName + "] " + message, null);
    }

    @Override
    public void log(ServiceReference sr, int level, String message, Throwable exception) {
        String bundleName = sr.getBundle().getSymbolicName();
        handleLog(level, "[Bundle: " + bundleName + "] " + message, exception);
    }

    protected static void handleLog(int level, String message, Throwable throwable) {
    	StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();        
    	StackTraceElement caller = stacktrace[STACKTRACE_OFFSET];
    	Logger log = LoggerFactory.getLogger(caller.getClassName());
        switch (level) {
        case LogService.LOG_DEBUG:
            if (null == throwable) {
                log.debug(message);
            } else {
                log.debug(message, throwable);
            }
            break;
        case LogService.LOG_ERROR:
            if (null == throwable) {
                log.error(message);
            } else {
                log.error(message, throwable);
            }
            break;
        case LogService.LOG_INFO:
            if (null == throwable) {
                log.info(message);
            } else {
                log.info(message, throwable);
            }
            break;
        case LogService.LOG_WARNING:
            if (null == throwable) {
                log.warn(message);
            } else {
                log.warn(message, throwable);
            }
            break;
        default:
            if (null == throwable) {
                log.info(message);
            } else {
                log.info(message, throwable);
            }
            break;
        }
    }

    @Override
    public void info(String message) {
        log(LOG_INFO, message);
    }

    @Override
    public void debug(String message) {
    	log(LOG_DEBUG, message);
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
    public void error(String message) {
    	log(LOG_ERROR, message);
    }

    @Override
    public void error(String message, Throwable th) {
    	log(LOG_ERROR, message, th);
    }
}
