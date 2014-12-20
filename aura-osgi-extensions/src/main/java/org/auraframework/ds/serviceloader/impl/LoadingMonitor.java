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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Monitors the injection rate and helps determine when loading is completed 
 * (i.e, when we do not get any new instance injected for awhile)
 *
 */
class LoadingMonitor extends Thread {
    
    private static final long TIMEOUT = 1000;    // Wait for 1 second after the last injection event to avoid false positives
    private final AtomicLong lastUpdate = new AtomicLong();
    private final AtomicBoolean isMonitoring = new AtomicBoolean();
    private final CompletionHandler completionHandler;
    
    public LoadingMonitor(HttpPortProvider servicePortProvider) {
        this.completionHandler = new LoadingCompletionHandlerImpl(servicePortProvider);
    }

    void update() {
        lastUpdate.set(System.currentTimeMillis());
    }
    
    private boolean finished() {
        long currentTime = System.currentTimeMillis();
        long sinceLastUpdate = currentTime - lastUpdate.get();
        return sinceLastUpdate > TIMEOUT;
    }

    @Override
    public void run() {
        while (!finished() && isMonitoring.get()) {
            try {
                Thread.sleep(TIMEOUT/10);
            } catch (InterruptedException e) {
                // Ignore
            }
        }            
        
        onComplete();
    }

    public synchronized void startMonitoring() {
        isMonitoring.set(true);
        update();
        super.start();
    }

    public synchronized void stopMonitoring() {
        isMonitoring.set(false);
        update();
    }

    protected void onComplete() {
        completionHandler.handleCompletion();
    }
}
