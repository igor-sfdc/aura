package org.auraframework.provider.api;


public interface ClassProvider {
	Class<?> getClazzForName(String name) throws ClassNotFoundException;
}
