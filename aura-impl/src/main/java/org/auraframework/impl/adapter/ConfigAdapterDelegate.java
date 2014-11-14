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
package org.auraframework.impl.adapter;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.http.HttpServletRequest;

import org.auraframework.adapter.ConfigAdapter;
import org.auraframework.adapter.ContentSecurityPolicy;
import org.auraframework.ds.serviceloader.AuraServiceProvider;
import org.auraframework.system.AuraContext.Mode;
import org.auraframework.util.resource.ResourceLoader;
import org.osgi.service.component.ComponentConstants;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component (provide=AuraServiceProvider.class)
public class ConfigAdapterDelegate implements ConfigAdapter {

	private final AtomicReference<ConfigAdapter> configAdapterDelegate = new AtomicReference<>();
	
	private ConfigAdapter defaultConfigAdapter;
	@Reference(target = "("+ComponentConstants.COMPONENT_NAME+"=DefaultConfigAdapter)")
	protected void setDefaultConfigAdapter(ConfigAdapter defaultConfigAdapter) {
		this.defaultConfigAdapter = defaultConfigAdapter;
	}
	
	@Reference(target = "("+ComponentConstants.COMPONENT_NAME+"=ExternalConfigAdapter)", dynamic=true, optional=true)
	protected void setExternalConfigAdapter(ConfigAdapter externalConfigAdapter) {
		// Use external config adapter whenever available
		configAdapterDelegate.set(externalConfigAdapter);
	}
	
	protected void unsetExternalConfigAdapter(ConfigAdapter externalConfigAdapter) {
		// Fallback to default config adapter if external disappears
		configAdapterDelegate.set(defaultConfigAdapter);
	}
	
	@Activate
	protected void activate() {
		// Fallback to default config adapter if external has not been set (it may be set at a later point)
		configAdapterDelegate.compareAndSet(null, defaultConfigAdapter);
	}
	
	@Override
	public boolean isProduction() {
		return configAdapterDelegate.get().isProduction();
	}

	@Override
	public boolean isSysAdmin() {
		return configAdapterDelegate.get().isSysAdmin();
	}

	@Override
	public Set<Mode> getAvailableModes() {
		return configAdapterDelegate.get().getAvailableModes();
	}

	@Override
	public Mode getDefaultMode() {
		return configAdapterDelegate.get().getDefaultMode();
	}

	@Override
	public String getAuraJSURL() {
		return configAdapterDelegate.get().getAuraJSURL();
	}

	@Override
	public String getAuraFrameworkNonce() {
		return configAdapterDelegate.get().getAuraFrameworkNonce();
	}

	@Override
	public String getCSRFToken() {
		return configAdapterDelegate.get().getCSRFToken();
	}

	@Override
	public void validateCSRFToken(String token) {
		configAdapterDelegate.get().validateCSRFToken(token);
	}

	@Override
	public ResourceLoader getResourceLoader() {
		return configAdapterDelegate.get().getResourceLoader();
	}

	@Override
	public void regenerateAuraJS() {
		configAdapterDelegate.get().regenerateAuraJS();
	}

	@Override
	public boolean isClientAppcacheEnabled() {
		return configAdapterDelegate.get().isClientAppcacheEnabled();
	}

	@Override
	public long getAuraJSLastMod() {
		return configAdapterDelegate.get().getAuraJSLastMod();
	}

	@Override
	public long getBuildTimestamp() {
		return configAdapterDelegate.get().getBuildTimestamp();
	}

	@Override
	public String getAuraVersion() {
		return configAdapterDelegate.get().getAuraVersion();
	}

	@Override
	public boolean isAuraJSStatic() {
		return configAdapterDelegate.get().isAuraJSStatic();
	}

	@Override
	public boolean validateCss() {
		return configAdapterDelegate.get().validateCss();
	}

	@Override
	public String getHTML5ShivURL() {
		return configAdapterDelegate.get().getHTML5ShivURL();
	}

	@Override
	public String getMomentJSURL() {
		return configAdapterDelegate.get().getMomentJSURL();
	}

	@Override
	public String getFastClickJSURL() {
		return configAdapterDelegate.get().getFastClickJSURL();
	}

	@Override
	public List<String> getWalltimeJSURLs() {
		return configAdapterDelegate.get().getWalltimeJSURLs();
	}

	@Override
	public boolean isPrivilegedNamespace(String namespace) {
		return configAdapterDelegate.get().isPrivilegedNamespace(namespace);
	}

	@Override
	public String getDefaultNamespace() {
		return configAdapterDelegate.get().getDefaultNamespace();
	}

	@Override
	public boolean isUnsecuredPrefix(String prefix) {
		return configAdapterDelegate.get().isUnsecuredPrefix(prefix);
	}

	@Override
	public boolean isUnsecuredNamespace(String namespace) {
		return configAdapterDelegate.get().isUnsecuredNamespace(namespace);
	}

	@Override
	public void addPrivilegedNamespace(String namespace) {
		configAdapterDelegate.get().addPrivilegedNamespace(namespace);
	}

	@Override
	public void removePrivilegedNamespace(String namespace) {
		configAdapterDelegate.get().removePrivilegedNamespace(namespace);
	}

	@Override
	public boolean isDocumentedNamespace(String namespace) {
		return configAdapterDelegate.get().isDocumentedNamespace(namespace);
	}

	@Override
	public boolean isCacheablePrefix(String prefix) {
		return configAdapterDelegate.get().isCacheablePrefix(prefix);
	}

	@Override
	public ContentSecurityPolicy getContentSecurityPolicy(String app, HttpServletRequest request) {
		return configAdapterDelegate.get().getContentSecurityPolicy(app, request);
	}

}
