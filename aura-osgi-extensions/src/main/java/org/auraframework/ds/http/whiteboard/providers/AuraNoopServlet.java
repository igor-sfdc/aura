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
package org.auraframework.ds.http.whiteboard.providers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.auraframework.ds.resourceloader.BundleResourceAccessorFactory;

import com.google.common.io.ByteStreams;

public class AuraNoopServlet extends HttpServlet {
    private static final long serialVersionUID = 8641565454202087224L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String resource = req.getRequestURI();
        InputStream resourceStream = BundleResourceAccessorFactory.get().getResource(resource);
        long numBytesCopied = 0;

        if (resourceStream != null) {
            try {
                numBytesCopied = ByteStreams.copy(resourceStream, resp.getOutputStream());
            } finally {
                resourceStream.close();
            }
        }

        if (numBytesCopied == 0 && !"/favicon.ico".equals(resource)) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("<script>");
            buffer.append("window.location = '/auradocs/docs.app';");
            buffer.append("</script>");
            ByteStreams.copy(new ByteArrayInputStream(buffer.toString().getBytes("UTF-8")), resp.getOutputStream());
        }
    }
}
