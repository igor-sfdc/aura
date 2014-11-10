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
package org.auraframework.impl.context;

import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

import org.auraframework.adapter.LocalizationAdapter;
import org.auraframework.ds.serviceloader.AuraServiceProvider;
import org.auraframework.util.AuraLocale;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

/**
 */
@Component (provide=AuraServiceProvider.class)
public class LocalizationAdapterDelegate implements LocalizationAdapter {
	
	private final AtomicReference<LocalizationAdapter> localizationAdapterDelegate = new AtomicReference<>();
	@Reference (name="ExternalAuraLocalizationAdapter", optional=true, dynamic=true)
	protected void setExternalLocalizationAdapter(LocalizationAdapter externalLocalizationAdapter) {
		localizationAdapterDelegate.set(externalLocalizationAdapter);
	}
	protected void unsetExternalLocalizationAdapter(LocalizationAdapter externalLocalizationAdapter) {
		localizationAdapterDelegate.set(new LocalizationAdapterImpl());
	}
	
	@Activate
	protected void activate() {
		localizationAdapterDelegate.compareAndSet(null, new LocalizationAdapterImpl());
	}
	
	@Override
	public String getLabel(String section, String name, Object... params) {
		return localizationAdapterDelegate.get().getLabel(section, name, params);
	}

	@Override
	public boolean labelExists(String section, String name) {
		return localizationAdapterDelegate.get().labelExists(section, name);
	}

	@Override
	public AuraLocale getAuraLocale() {
		return localizationAdapterDelegate.get().getAuraLocale();
	}

	@Override
	public AuraLocale getAuraLocale(Locale defaultLocale) {
		return localizationAdapterDelegate.get().getAuraLocale(defaultLocale);
	}

	@Override
	public AuraLocale getAuraLocale(Locale defaultLocale, TimeZone timeZone) {
		return localizationAdapterDelegate.get().getAuraLocale(defaultLocale, timeZone);
	}

	@Override
	public AuraLocale getAuraLocale(Locale defaultLocale,
			Locale currencyLocale, Locale dateLocale, Locale languageLocale,
			Locale numberLocale, Locale systemLocale, TimeZone timeZone) {
		return localizationAdapterDelegate.get().getAuraLocale(defaultLocale, currencyLocale, dateLocale, 
				languageLocale, numberLocale, systemLocale, timeZone);
	}
}
