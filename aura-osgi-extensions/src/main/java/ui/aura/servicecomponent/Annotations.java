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
package ui.aura.servicecomponent;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.auraframework.system.Annotations.Controller;
import org.auraframework.system.Annotations.Model;
import org.auraframework.system.Annotations.Provider;

/**
 *
 * Annotations to mark Aura Service Component Controllers, Models and Providers
 *
 * These composite (or meta-) annotations include all required annotations
 * for Aura
 * 
 * TODO: In the future this needs to be moved to aura-osgi-api
 */
public interface Annotations {

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Controller
    public @interface ServiceComponentController {
    }

    /**
     * Marker annotation for "legacy" Service Component Models
     * with prototype scope
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Model
    public @interface ServiceComponentModel {
    }

    /**
     * Marker annotation for the instance (prototype scope POJO) portion of Service Component
     * Models represented by Service Component Factory and Service Component Instance.
     * {@link @ServiceComponentModelInstance} and {@link @ServiceComponentModelFactory}
     * should be always used in pairs.
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Model
    public @interface ServiceComponentModelInstance {
    }

    /**
     * Marker annotation for the factory (singleton) portion of Service Component
     * Models represented by Service Component Factory and Service Component Instance.
     * {@link @ServiceComponentModelInstance} and {@link @ServiceComponentModelFactory}
     * should be always used in pairs.
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface ServiceComponentModelFactory {
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Provider
    public @interface ServiceComponentProvider {
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    // TODO: implement Access along the lines of @Model/@Controller
    //@Access
    public @interface ServiceComponentAccess {
    }
}

