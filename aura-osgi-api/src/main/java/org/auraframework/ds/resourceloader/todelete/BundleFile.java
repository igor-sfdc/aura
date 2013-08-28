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
package org.auraframework.ds.resourceloader.todelete;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Enumeration;

import org.auraframework.ds.util.BundleUtil;
import org.osgi.framework.Bundle;

import com.google.common.collect.Lists;

public class BundleFile extends File {

	private static final long serialVersionUID = 423505877461222181L;
	private static final String PATH_SEPARATOR = "/";
	private final String name;
	private final String path;
	private final Bundle bundle;
	private final String parent;

	public BundleFile(String nameOrPath, Class<?> clientClass) {
		this(nameOrPath, BundleUtil.getBundle(clientClass));
	}
	
	private BundleFile(String nameOrPath, Bundle bundle) {
		super(nameOrPath); // Need this only to satisfy Java syntax
		this.name = computeName(nameOrPath);
		this.path = nameOrPath;
		this.parent = computeParent(nameOrPath);
		// If regular file exists this must be file system
		this.bundle = (!super.exists() || name.isEmpty()) ? bundle : null;		
	}

	private static String computeName(String nameOrPath) {
		boolean removeTrailingPathSeparator = nameOrPath.endsWith(PATH_SEPARATOR);
		if (removeTrailingPathSeparator) {
			nameOrPath = nameOrPath.substring(0, nameOrPath.length() - 1);
		}

		int index = nameOrPath.lastIndexOf(PATH_SEPARATOR);
		return index == -1 ? nameOrPath : nameOrPath.substring(index + 1, nameOrPath.length());
	}
	
	private static String computeParent(String nameOrPath) {
		int index = nameOrPath.lastIndexOf(PATH_SEPARATOR);
		return index != -1 ? nameOrPath.substring(0, index + 1) : null;
	}

	private Bundle getBundle() {
		return bundle;
	}

	@Override
	public boolean canExecute() {
		if (bundle == null) return super.canExecute();
		return true;
	}

	@Override
	public boolean canRead() {
		if (bundle == null) return super.canRead();
		return true;
	}

	@Override
	public boolean canWrite() {
		if (bundle == null) return super.canWrite();
		return false;
	}

	@Override
	public int compareTo(File pathname) {
		if (bundle == null) return super.compareTo(pathname);
		return getAbsolutePath().compareTo(pathname.getAbsolutePath());
	}

	@Override
	public boolean createNewFile() throws IOException {
		if (bundle == null) return super.createNewFile();
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean delete() {
		if (bundle == null) return super.delete();
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteOnExit() {
		if (bundle == null) super.deleteOnExit();
		else throw new UnsupportedOperationException();
	}

	@Override
	public boolean equals(Object obj) {
		if (bundle == null) return super.equals(obj);
		if( obj instanceof BundleFile) {
			return getAbsolutePath().equals(((BundleFile)obj).getAbsolutePath());
		} else {
			return false;
		}
	}

	@Override
	public boolean exists() {
		if (bundle == null) return super.exists();
		return bundle.getEntry(path) != null;
	}

	@Override
	public File getAbsoluteFile() {
		if (bundle == null) return super.getAbsoluteFile();
		throw new UnsupportedOperationException();
	}

	@Override
	public String getAbsolutePath() {
		if (bundle == null) return super.getAbsolutePath();
		return path;
	}

	@Override
	public File getCanonicalFile() throws IOException {
		if (bundle == null) return super.getCanonicalFile();
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCanonicalPath() throws IOException {
		if (bundle == null) return super.getCanonicalPath();
		return path;
	}

	@Override
	public long getFreeSpace() {
		if (bundle == null) return super.getFreeSpace();
		return 0;
	}

	@Override
	public String getName() {
		if (bundle == null) return super.getName();
		return name;
	}

	@Override
	public String getParent() {
		return parent;
	}

	@Override
	public File getParentFile() {
		if (bundle == null) return super.getParentFile();
		return new BundleFile(getParent(), bundle);
	}

	@Override
	public String getPath() {
		if (bundle == null) return super.getPath();
		return path;
	}

	@Override
	public long getTotalSpace() {
		if (bundle == null) return super.getTotalSpace();
		return 0;
	}

	@Override
	public long getUsableSpace() {
		if (bundle == null) return super.getUsableSpace();
		return 0;
	}

	@Override
	public int hashCode() {
		if (bundle == null) return super.hashCode();
		return path.hashCode();
	}

	@Override
	public boolean isAbsolute() {
		if (bundle == null) return super.isAbsolute();
		return false;
	}

	@Override
	public boolean isDirectory() {
		if (bundle == null) return super.isDirectory();
		return isDirectoryPath(getAbsolutePath()); 
	}

	private boolean isDirectoryPath(String path) {
		return path.endsWith(PATH_SEPARATOR);
	}

	@Override
	public boolean isFile() {
		if (bundle == null) return super.isFile();
		return !isDirectory();
	}

	@Override
	public boolean isHidden() {
		if (bundle == null) return super.isHidden();
		return false;
	}

	@Override
	public long lastModified() {
		if (bundle == null) return super.lastModified();
		return -1;
	}

	@Override
	public long length() {
		if (bundle == null) return super.length();
		return -1;
	}

	@Override
	public String[] list() {
		if (bundle == null) return super.list();
		return list(null);
	}

	@Override
	public String[] list(FilenameFilter filter) {
		if (bundle == null) return super.list(filter);
		// FIXME: Repeating parts of the implementation in the four listXXX methods below
		Enumeration<String> entries = bundle.getEntryPaths(path);
		Collection<String> paths = Lists.newArrayList();
		while(entries.hasMoreElements()) {
			String nextEntryElement = entries.nextElement();
			BundleFile nextFile = new BundleFile(nextEntryElement, bundle);
			if (filter == null || filter.accept(nextFile.getParentFile(), nextFile.getName())) {
				paths.add(nextEntryElement);
			}
		}	
		return paths.toArray(new String[0]);
	}

	@Override
	public File[] listFiles() {
		if (bundle == null) return super.listFiles();
		Enumeration<String> entries = bundle.getEntryPaths(path);
		Collection<BundleFile> files = Lists.newArrayList();
		while(entries.hasMoreElements()) {
			String nextEntryElement = entries.nextElement();
			BundleFile nextFile = new BundleFile(nextEntryElement, bundle);
			files.add(nextFile);
		}	
		return files.toArray(new BundleFile[0]);
	}

	@Override
	public File[] listFiles(FileFilter filter) {
		if (bundle == null) return super.listFiles(filter);
		Enumeration<String> entries = bundle.getEntryPaths(path);
		Collection<BundleFile> files = Lists.newArrayList();
		while(entries.hasMoreElements()) {
			String nextEntryElement = entries.nextElement();
			BundleFile nextFile = new BundleFile(nextEntryElement, bundle);
			if (filter == null || filter.accept(nextFile)) {
				files.add(nextFile);
			}
		}	
		return files.toArray(new BundleFile[0]);
	}

	@Override
	public File[] listFiles(FilenameFilter filter) {
		if (bundle == null) return super.listFiles(filter);
		Enumeration<String> entries = bundle.getEntryPaths(path);
		Collection<BundleFile> files = Lists.newArrayList();
		while(entries.hasMoreElements()) {
			String nextEntryElement = entries.nextElement();
			BundleFile nextFile = new BundleFile(nextEntryElement, bundle);
			if (filter == null || filter.accept(nextFile.getParentFile(), nextFile.getName())) {
				files.add(nextFile);
			}
		}	
		return files.toArray(new BundleFile[0]);
	}

	@Override
	public boolean mkdir() {
		if (bundle == null) return super.mkdir();
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean mkdirs() {
		if (bundle == null) return super.mkdirs();
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean renameTo(File dest) {
		if (bundle == null) return super.renameTo(dest);
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean setExecutable(boolean executable, boolean ownerOnly) {
		if (bundle == null) return super.setExecutable(executable, ownerOnly);
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean setExecutable(boolean executable) {
		if (bundle == null) return super.setExecutable(executable);
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean setLastModified(long time) {
		if (bundle == null) return super.setLastModified(time);
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean setReadOnly() {
		if (bundle == null) return super.setReadOnly();
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean setReadable(boolean readable, boolean ownerOnly) {
		if (bundle == null) return super.setReadable(readable, ownerOnly);
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean setReadable(boolean readable) {
		if (bundle == null) return super.setReadable(readable);
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean setWritable(boolean writable, boolean ownerOnly) {
		if (bundle == null) return super.setWritable(writable, ownerOnly);
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean setWritable(boolean writable) {
		if (bundle == null) return super.setWritable(writable);
		throw new UnsupportedOperationException();
	}

	@Override
	public Path toPath() {
		// TODO Auto-generated method stub
		return super.toPath();
	}

	@Override
	public String toString() {
		if (bundle == null) return super.toString();
		return path;
	}

	@Override
	public URI toURI() {
		if (bundle == null) return super.toURI();
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public URL toURL() throws MalformedURLException {
		if (bundle == null) return super.toURL();
		throw new UnsupportedOperationException();
	}

}
