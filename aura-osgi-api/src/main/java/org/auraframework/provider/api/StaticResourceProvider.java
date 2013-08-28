package org.auraframework.provider.api;

import java.io.IOException;
import java.io.InputStream;


public interface StaticResourceProvider {
    InputStream getStaticResource(String resource) throws IOException;
}
