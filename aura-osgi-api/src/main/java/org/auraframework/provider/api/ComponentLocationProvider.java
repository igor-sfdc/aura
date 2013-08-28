package org.auraframework.provider.api;

import java.io.File;

public interface ComponentLocationProvider {
    File getComponentSourceDir();
    File getJavaGeneratedSourceDir();
    String getComponentSourcePackage();
}
