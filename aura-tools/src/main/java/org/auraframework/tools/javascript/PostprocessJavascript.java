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
package org.auraframework.tools.javascript;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.logging.Logger;

import org.auraframework.impl.util.AuraImplFiles;
import org.auraframework.util.IOUtil;

public class PostprocessJavascript {
    private static final String BRACKETED_COLUMN_SEARCH_PATTERN = "[:]";
	private static final String OPTIONAL_TRAILING_SLASH_SEARCH_PATTERN = "([/]|[\\\\])?";
	private static final String RAW_BACKSLASH_SEARCH_PATTERN = "\\\\";
	private static final String BRACKETED_ESCAPED_BACKSLASH_SEARCH_PATTERN = "[\\\\\\\\]";
	private static final String SYMBOL_SET_JSON_FILE = "symbolSet.json";
	private static final String JSDOC_DIRECTORY = "jsdoc";
	private static final String AURA_JAVASCRIPT_LOCATION_IN_BUNDLE = "/aura/javascript/";

	public static void main(String[] args) throws IOException {
        Logger logger = Logger.getLogger(PostprocessJavascript.class.getName());
        File javascriptDestDirectory = AuraImplFiles.AuraResourceJavascriptDirectory.asFile();
        if (!javascriptDestDirectory.isDirectory()) {
            throw new IOException(javascriptDestDirectory.getPath() + " is supposed to be a directory");
        }
        
        File resourcesDirectory = javascriptDestDirectory.getParentFile().getParentFile();
        File jsdocDirectory = new File(resourcesDirectory, JSDOC_DIRECTORY);
        if (!jsdocDirectory.exists() || !jsdocDirectory.isDirectory()) {
            // throw new IOException(jsdocDirectory.getPath() + " should exist and is supposed to be a directory");
            logger.warning("Directory " + jsdocDirectory.getPath() + " does not exist. Exiting....");
            return;
        }
        
        File symbolFile = new File(jsdocDirectory, SYMBOL_SET_JSON_FILE);
        if (!symbolFile.exists() || !symbolFile.isFile()) {
            // throw new IOException(symbolFile.getPath() + " should exist and is supposed to be a file");
            logger.warning("File " + symbolFile.getPath() + " does not exist. Exiting....");
            return;
        }
        
        replaceFileSystemPathWithBundleLocation(symbolFile, javascriptDestDirectory.getCanonicalPath(), AURA_JAVASCRIPT_LOCATION_IN_BUNDLE, logger);
        
        logger.info("Finished post-processing framework javascript");
    }

	private static void replaceFileSystemPathWithBundleLocation(File symbolFile, String searchPattern, String replacePattern, Logger logger) throws IOException {
		String content = IOUtil.readTextFile(symbolFile);
		// Escape/substitute special characters in the search pattern
		searchPattern = searchPattern.replaceFirst(BRACKETED_COLUMN_SEARCH_PATTERN, BRACKETED_COLUMN_SEARCH_PATTERN).replaceAll(RAW_BACKSLASH_SEARCH_PATTERN, BRACKETED_ESCAPED_BACKSLASH_SEARCH_PATTERN);
		// Add optional terminating slash or backslash
		searchPattern += OPTIONAL_TRAILING_SLASH_SEARCH_PATTERN;
		logger.info("Search Pattern: " + searchPattern);
		content = content.replaceAll(searchPattern, replacePattern);		
		logger.info("Sample of postprocessed content: " + content.substring(0, 2000));		
		Reader reader = new StringReader(content);
		Writer writer = new FileWriter(symbolFile);
		IOUtil.copyStream(reader, writer);
		logger.info("Finished updating symbol file: " + symbolFile.getPath());
	}
}
