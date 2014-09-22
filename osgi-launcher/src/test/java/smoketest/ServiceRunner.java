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
package smoketest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Map;

import launcher.EmptyUtil;

/**
 * Simulates starting the OSGi service from command line. 
 * Use with caution as currently there is no way to shutdown, so you may have to kill the process.
 * We will add shutdown capability later.
 * 
 * Java 7 requirement is not really critical and may be relaxed in the future.
 *
 */
public class ServiceRunner {
    static final int OSGI_LAUNCHER_LOCAL_PORT = 9090;
    static final String OSGI_LAUNCHER_LAUNCHER_HOME = "../osgi-launcher/launcherHome";
    private static final String JAVA7_HOME = System.getenv("JAVA7_HOME");
    private static final String JAVA7_HOME_DEFAULT = "/opt/java/jre1.7.0";
    private static final String JAVA7_COMMAND = !EmptyUtil.is(JAVA7_HOME) ? JAVA7_HOME + "/bin/java" : JAVA7_HOME_DEFAULT + "/bin/java";
    private static final String LAUNCHER_JAR_FILE_NAME = "osgi-launcher.jar";

    public static void main(String[] args) throws IOException {
        launchService(OSGI_LAUNCHER_LOCAL_PORT, true);
    }
    
    public static Process launchOsgiService(int httpPort) throws IOException {
        return launchService(httpPort, false);
    }
        
    public static Process launchService(int httpPort, boolean openConsole) throws IOException {
        assertFile("Is your JAVA7_HOME variable set correctly? Java executable", new File(JAVA7_COMMAND));
        
        File launcherHome = getAssertedFolder(OSGI_LAUNCHER_LAUNCHER_HOME, "Launcher Home directory");
        getAssertedFolder(launcherHome, "loadFirst", "Launcher loadFirst directory");        
        getAssertedFolder(launcherHome, "load", "Launcher load directory");
        getAssertedFolder(launcherHome, "loadApp", "Launcher loadApp directory");        

        String [] commandElements = {
                JAVA7_COMMAND,
                "-jar",
                LAUNCHER_JAR_FILE_NAME,
                "launcherHome=" + launcherHome.getAbsolutePath(),
                "httpPort=" + httpPort, 
                "openConsole=" + openConsole,
                "cleanInstallFolder=true"                
        };

        ProcessBuilder pb = new ProcessBuilder(commandElements);
         Map<String, String> env = pb.environment();
            env.put("PROXY_PORT", "" + httpPort);
        pb.directory(launcherHome);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        copyInThread(process.getInputStream(), System.out);
        copyInThread(process.getErrorStream(), System.err);
        return process;
    }

    @SuppressWarnings("unused")
    private static File getAssertedFile(File parentFolder, String fileName, String fileDescription) {
        File file = new File(parentFolder, fileName);
        assertFile(fileDescription, file);
        return file;
    }

    private static void assertFile(String fileDescription, File file) {
    }

    private static File getAssertedFolder(String folderPath, String folderDescription) {
        File folder = new File(folderPath);
        assertFolder(folderDescription, folder);
        return folder;
    }

    private static File getAssertedFolder(File parentFolder, String folderName, String folderDescription) {
        File folder = new File(parentFolder, folderName);
        assertFolder(folderDescription, folder);
        return folder;
    }

    private static void assertFolder(String folderDescription, File folder) {
    }

    @SuppressWarnings("unused")
    private static void copyFile(File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }

    private static void copyInThread(final InputStream in, final OutputStream out) {
        new Thread() {
            public void run() {
                try {
                    while (true) {
                        int x = in.read();
                        if (x < 0) {
                            return;
                        }
                        if (out != null) {
                            out.write(x);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } .start();
    }
}
