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
package org.auraframework.ds.http.whiteboard.proxy.impl;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.auraframework.ds.http.whiteboard.proxy.FilterProxy;
import org.auraframework.ds.log.AuraDSLog;



/**
 * VirtualProxy pattern is applied to defer real filter instantiation/initialization
 *
 *
 *
 * @param <T> real filter type
 */
public abstract class FilterProxyImpl<T extends Filter>
        extends HttpServiceProviderProxyImpl<T> implements FilterProxy<T>, Comparable<Object> {

    private FilterConfig filterConfig;
    final String patternStr;
    private final Pattern pattern;
    private final int ranking;

    protected FilterProxyImpl(String patternStr, int ranking) {
        this.patternStr = patternStr;
        pattern = Pattern.compile(patternStr);
        this.ranking = ranking;
        AuraDSLog.get().info(getClass().getSimpleName() + " Instantiated");
    }

    @Override
    public void destroy() {
        T realFilter = getRealProviderOptional();
        if (realFilter != null) {
            realFilter.destroy();
        }
        reset();
    }

    @Override
    protected void init(T realFilter) throws ServletException {
        AuraDSLog.get().info("[" + realFilter.getClass().getSimpleName() + "] " + " Istantiated for " + patternStr);
        realFilter.init(filterConfig);
        AuraDSLog.get().info("[" + realFilter.getClass().getSimpleName() + "] " + " Initialized for " + patternStr);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        if (matchesPattern(request, response, filterChain, httpRequest)) {
            T realFilter = getRealProvider();
            logFilterRequest(httpRequest, realFilter);
            realFilter.doFilter(request, response, filterChain);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private boolean matchesPattern(ServletRequest request,
            ServletResponse response, FilterChain filterChain,
            HttpServletRequest httpRequest) throws IOException, ServletException {
        String uri = httpRequest.getRequestURI();
        String pathToMatch = uri;
        String contextPath = httpRequest.getServletContext().getContextPath();
        if (contextPath != null && contextPath.length() > 1 && pathToMatch.startsWith(contextPath)) {
            pathToMatch = pathToMatch.substring(contextPath.length());
        }
        Matcher matcher = pattern.matcher(pathToMatch);
        return matcher.matches();
    }

    private void logFilterRequest(HttpServletRequest httpRequest, T realFilter) {
        String qs = httpRequest.getQueryString();
        qs = qs != null && !qs.isEmpty() ? "?" + qs : "";
        AuraDSLog.get().info("[" + realFilter.getClass().getSimpleName() + "] " + patternStr + " : " + " doFilter() for " + httpRequest.getRequestURI() + qs );
    }

    @Override
    protected abstract T newInstance();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        setInitialized();
    }

    @Override
    public String getPattern() {
        return patternStr;
    }

    @Override
    public int getRanking() {
        return ranking;
    }

    @Override
    public int compareTo(Object otherObject) {
        if (otherObject instanceof FilterProxy) {
            @SuppressWarnings("unchecked")
            FilterProxy<T> otherFilterProxy = (FilterProxy<T>)otherObject;
            int otherRanking = otherFilterProxy.getRanking();
            return ranking < otherRanking ? 1 : ranking == otherRanking ? 0 : -1;
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer
            .append("[")
            .append(getClass().getSimpleName())
            .append("] pattern: ")
            .append(patternStr)
            .append(", ranking: ")
            .append(ranking);
        return buffer.toString();
    }
}
