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
package org.auraframework.ds.serviceloader.impl;

import java.io.IOException;
import java.util.List;

import org.auraframework.ds.log.AuraDSLog;

import com.google.common.collect.Lists;

/**
 * 
 * An example of how service loading completion can be handled:
 * auto-opens default browser with Auradocs URL upon loading completion.
 */
public class LoadingCompletionHandlerImpl implements CompletionHandler {
    enum BrowserExecutable {
        WINDOWS ("cmd", "/c", "start", "chrome"),
        LINUX("x-www-browser");
        
        private final List<String> commandELements = Lists.newArrayList();
        
        BrowserExecutable(String... elements) {
            for (String commandElement : elements) {
                this.commandELements.add(commandElement);
            }
        }
        
        List<String> toList() {
            return Lists.newArrayList(commandELements);
        }
        
        public static BrowserExecutable get() {
            String os = System.getenv("OS");
            if (os != null && os.toLowerCase().contains("win")) {
                return WINDOWS;
            } else {
                return LINUX;
            }
        }
    }

    private Process process;

    @Override
    public void handleCompletion() {
        AuraDSLog.get().info("######################################################################");
        AuraDSLog.get().info("#                                                                    #");
        AuraDSLog.get().info("#                     Aura application is ready                      #");
        AuraDSLog.get().info("#                                                                    #");
        AuraDSLog.get().info("######################################################################");
        startBrowser();
    }
    
    private void startBrowser() {
        // FIXME: Create relevant Java constants and share them with Launcher class
        String port = System.getProperty("org.osgi.service.http.port", "8080");
        port = (port == null || port.trim().isEmpty()) ? "" : ":" + port;
        String auraDocsUrl = "http://localhost" + port + "/auradocs/docs.app#";
        AuraDSLog.get().info("Listening on HTTP Port: " + port);
        String startBrowser = System.getProperty("startBrowser");
        if (startBrowser != null && "true".equals(startBrowser)) {
            List<String> commandElements = BrowserExecutable.get().toList();
            commandElements.add(auraDocsUrl);
            ProcessBuilder builder = new ProcessBuilder(commandElements);
            try {
                process = builder.start();
                AuraDSLog.get().info("Opened " + auraDocsUrl + " in your default browser");
            } catch (IOException e) {
                AuraDSLog.get().error("Failed to start default browser", e);
            }
        } else {
            AuraDSLog.get().info("Open this URL " + auraDocsUrl + " in your browser");
        }
    }
    
    /**
     * Not sure if we ever want to terminate browser process but here is a method for that...
     */
    @SuppressWarnings("unused")
    private void stopBrowser() {
        if (process != null) {
            process.destroy();
        }
    }
}
