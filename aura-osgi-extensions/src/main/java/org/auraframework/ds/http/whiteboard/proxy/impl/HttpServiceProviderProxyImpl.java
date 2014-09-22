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
package org.auraframework.ds.http.whiteboard.proxy.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.ServletException;

import org.auraframework.ds.http.whiteboard.proxy.HttpServiceProviderProxy;
import org.auraframework.ds.log.AuraDSLog;


public abstract class HttpServiceProviderProxyImpl<T> implements HttpServiceProviderProxy {
    
    private boolean isRegistered;
    private AtomicBoolean isInitialized = new AtomicBoolean();
    private AtomicReference<T> realProviderReference = new AtomicReference<T>();

    @Override
    public boolean isRegistered() {
        return isRegistered;        
    }
    
    @Override
    public void setRegistered(boolean registered) {
        this.isRegistered = registered;
    }

    /**
     * Do not force to instantiate
     * 
     * @return real provider or null if has not been instantiated
     */
    protected T getRealProviderOptional() {
        return realProviderReference.get();
    }

    protected T getRealProvider() {
        T realProvider = getRealProviderOptional();
        if (realProvider == null) {
            if (!isInitialized.get()) {
                throw new IllegalStateException("Cannot access real provider unless proxy has beend initialized");
            }
            
            synchronized (this) {
                realProvider = realProviderReference.get();
                if (realProvider == null) {
                    try {
                        realProvider = newInstance();
                        init(realProvider);
                        realProviderReference.set(realProvider);
                    } catch (Throwable th) {
                        Class<?> clazz = realProvider !=null ? realProvider.getClass() : null;
                        String className = clazz != null ? clazz.getSimpleName() : "Unknown";
                        AuraDSLog.get().error("[" + className + "] " + " Failed to register " + realProvider, th);
                    }
                }
            }
        }
        return realProvider;
    }
    
    protected void setInitialized() {
        isInitialized.set(true);
    }
    
    protected void reset() {
        isInitialized.set(false);
        realProviderReference.set(null);
    }

    protected abstract T newInstance();

    protected abstract void init(T realProvider) throws ServletException;
}
