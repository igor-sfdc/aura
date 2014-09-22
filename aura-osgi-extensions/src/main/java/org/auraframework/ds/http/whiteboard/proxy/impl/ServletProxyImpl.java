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

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.auraframework.ds.http.whiteboard.proxy.ServletProxy;
import org.auraframework.ds.log.AuraDSLog;



/**
 * VirtualProxy pattern is applied to defer real servlet instantiation/initialization
 * 
 *
 *
 * @param <T> real servlet type
 */
public abstract class ServletProxyImpl<T extends Servlet> 
        extends HttpServiceProviderProxyImpl<T> implements ServletProxy<T>, Comparable<Object> {

    private ServletConfig servletConfig;
    private final String alias;

    protected ServletProxyImpl(String alias) {
        this.alias = alias;
        AuraDSLog.get().info(getClass().getSimpleName() + " Instantiated");
    }

    @Override
    public void destroy() {
        T realServlet = getRealProviderOptional();
        if (realServlet != null) {
            realServlet.destroy();
        }
        reset();
    }

    @Override
    public ServletConfig getServletConfig() {
        return getRealProvider().getServletConfig();
    }

    @Override
    public String getServletInfo() {
        return getRealProvider().getServletInfo();
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        this.servletConfig = servletConfig;
        setInitialized();
    }
    
    @Override
    protected void init(T realServlet) throws ServletException {
        AuraDSLog.get().info("[" + realServlet.getClass().getSimpleName() + "] " + " Istantiated for " + alias);
        realServlet.init(servletConfig);
        AuraDSLog.get().info("[" + realServlet.getClass().getSimpleName() + "] " + " Initialized for " + alias);
    }

    @Override
    public void service(ServletRequest request, ServletResponse response)
            throws ServletException, IOException {
        T realServlet = getRealProvider();
        logServiceRequest(request, realServlet);
        realServlet.service(request, response);
    }

    private void logServiceRequest(ServletRequest request, T realServlet) {
        HttpServletRequest httRequest = (HttpServletRequest)request;
        String uri = httRequest.getRequestURI();
        String qs = httRequest.getQueryString();
        qs = qs != null && !qs.isEmpty() ? "?" + qs : "";
        AuraDSLog.get().info("[" + realServlet.getClass().getSimpleName() + "] service request for " + uri + qs);
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public int compareTo(Object otherObject) {
        return 0;
    }
    
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer
            .append("[")
            .append(getClass().getSimpleName())
            .append("] alias: ")
            .append(alias);
        return buffer.toString();
    }

    abstract protected T newInstance();
}
