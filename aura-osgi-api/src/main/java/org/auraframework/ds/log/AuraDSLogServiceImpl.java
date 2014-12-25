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
    private static final Logger LOG = LoggerFactory.getLogger(AuraDSLogServiceImpl.class);

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
        log(sr, level, message, null);
    }

    @Override
    public void log(ServiceReference sr, int level, String message, Throwable exception) {
        String bundleName = sr.getBundle().getSymbolicName();
        handleLog(level, "[Bundle: " + bundleName + "] " + message, null);
    }

    protected static void handleLog(int level, String message, Throwable throwable) {
        switch (level) {
        case LogService.LOG_DEBUG:
            if (null == throwable) {
                LOG.debug(message);
            } else {
                LOG.debug(message, throwable);
            }
            break;
        case LogService.LOG_ERROR:
            if (null == throwable) {
                LOG.error(message);
            } else {
                LOG.error(message, throwable);
            }
            break;
        case LogService.LOG_INFO:
            if (null == throwable) {
                LOG.info(message);
            } else {
                LOG.info(message, throwable);
            }
            break;
        case LogService.LOG_WARNING:
            if (null == throwable) {
                LOG.warn(message);
            } else {
                LOG.warn(message, throwable);
            }
            break;
        default:
            if (null == throwable) {
                LOG.info(message);
            } else {
                LOG.info(message, throwable);
            }
            break;
        }
    }

    private String augmentMessage(String message) {
        // Add line number
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stacktrace[STACKTRACE_OFFSET];
        StringBuilder builder = new StringBuilder();
        builder
                .append('(')
                .append(caller.getFileName())
                .append(':')
                .append(caller.getLineNumber())
                .append(')')
                .append(' ')
                .append(message);
        return builder.toString();
    }

    @Override
    public void info(String message) {
        LOG.info(augmentMessage(message));
    }

    @Override
    public void debug(String message) {
        LOG.debug(augmentMessage(message));
    }

    @Override
    public void warning(String message) {
        LOG.warn(augmentMessage(message));
    }

    @Override
    public void warning(String message, Throwable th) {
        LOG.warn(augmentMessage(message), th);
    }

    @Override
    public void error(String message) {
        LOG.error(augmentMessage(message));
    }

    @Override
    public void error(String message, Throwable th) {
        LOG.error(augmentMessage(message), th);
    }
}
