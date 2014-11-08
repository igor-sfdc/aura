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
package org.auraframework.util.javascript.directive.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.auraframework.util.javascript.JavascriptProcessingError;
import org.auraframework.util.javascript.JavascriptValidator;
import org.auraframework.util.javascript.directive.DirectiveBasedJavascriptGroup;
import org.auraframework.util.javascript.directive.JavascriptGeneratorMode;

/**
 * inserts a debugger statement, defaults to debug mode only
 */
public class DebuggerDirective extends DirectiveImpl {

    public DebuggerDirective(int offset, String line) {
        super(offset, line);
    }

    @Override
    protected EnumSet<JavascriptGeneratorMode> getDefaultModes() {
        return EnumSet.of(JavascriptGeneratorMode.DEVELOPMENT, JavascriptGeneratorMode.AUTOTESTINGDEBUG,
                JavascriptGeneratorMode.TESTINGDEBUG, JavascriptGeneratorMode.STATS);
    }

    @Override
    public void processDirective(DirectiveBasedJavascriptGroup parser) throws IOException {
    }

    @Override
    public List<JavascriptProcessingError> validate(JavascriptValidator validator) {
        return Collections.emptyList();
    }

    @Override
    public String generateOutput(JavascriptGeneratorMode mode) {
        return "debugger;";
    }

}
