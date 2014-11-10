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
package launcher;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

/**
 * Custom OSGi App launcher
 */
public class Launcher {
    private static final String OSGI_SERVICE_HTTP_PORT = "org.osgi.service.http.port";
    private static final int HTTP_PORT_NOT_SET = -1;
    private static final String TRUE_STRING_VALUE = "true";
    private static final String CONFIGURATION_SUBFOLDER = "configuration";
    private static final String FRAGMENTS_SUBFOLDER = "fragments";
    // Folder for core OSGi bundles that need to be loaded first
    private static final String BUNDLE_LOAD_FIRST_SUBFOLDER = "loadFirst";
    // Folder for other OSGi bundles
    private static final String BUNDLE_LOAD_SUBFOLDER = "load";
    // Folder for application OSGi
    private static final String BUNDLE_LOAD_APP_SUBFOLDER = "loadApp";
    private static final String LAUNCHER_HOME_PARAM = "launcherHome=";
    private static final String HTTP_PORT_PARAM = "httpPort=";
    private static final String CLEAN_INSTALL_FOLDER_PARAM = "cleanInstallFolder=";
    private static final String OPEN_CONSOLE_PARAM = "openConsole=";
    private static final String FILE_PROTOCOL = "file:";
    private static final String DEFAULT_LAUNCHER_HOME = "./launcherHome";
    private static final int DEFAULT_HTTP_PORT_8080 = 8080;
    private static final String SYSTEM_PROPERTY_PREFIX = "-D";

    public static void main(String[] args) throws BundleException {        
        String launcherHome = null;
        int httpPort = HTTP_PORT_NOT_SET; // Port is not set
        boolean openConsole = true; // Default value
        boolean cleanInstallFolder = true; // Default value

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith(LAUNCHER_HOME_PARAM)) {
                launcherHome = arg.substring(LAUNCHER_HOME_PARAM.length());
            } else if (arg.startsWith(HTTP_PORT_PARAM)) {
                String httpPortStr = arg.substring(HTTP_PORT_PARAM.length());
                httpPort = Integer.valueOf(httpPortStr);
            } else if (arg.startsWith(CLEAN_INSTALL_FOLDER_PARAM)) {
                String cleanInstallFolderStr = arg.substring(CLEAN_INSTALL_FOLDER_PARAM.length());
                cleanInstallFolder = Boolean.valueOf(cleanInstallFolderStr);
            } else if (arg.startsWith(OPEN_CONSOLE_PARAM)) {
                String openConsoleStr = arg.substring(OPEN_CONSOLE_PARAM.length());
                openConsole = Boolean.valueOf(openConsoleStr);
            } else if (arg.startsWith(OPEN_CONSOLE_PARAM)) {
                String openConsoleStr = arg.substring(OPEN_CONSOLE_PARAM.length());
                openConsole = Boolean.valueOf(openConsoleStr);
            } else if (arg.startsWith(SYSTEM_PROPERTY_PREFIX)) {
                // Apply as system property
                arg = arg.substring(SYSTEM_PROPERTY_PREFIX.length(), arg.length());
                String[] keyValue = arg.split("=");
                if (keyValue.length == 2) {
                    if (!"null".equals(keyValue[1])) {
                        System.out.println("System property provided: " + keyValue[0] + "=" + keyValue[1]);
                        System.setProperty(keyValue[0], keyValue[1]);
                    }
                } else if (keyValue.length == 1) {
                    System.out.println("System property provided: " + keyValue[0] + "=");
                    System.setProperty(keyValue[0], "");
                } else {
                    System.out.println("Error interpreting system property '" + arg + "'");
                }
            } else {
                String message = "Invalid argument: '"  + arg + "'";
                showUsage(message);
                return;
            }
        }
        
        if (httpPort == HTTP_PORT_NOT_SET) {
            httpPort = Integer.valueOf(System.getProperty(OSGI_SERVICE_HTTP_PORT, "" + DEFAULT_HTTP_PORT_8080));
        }

        System.setProperty(OSGI_SERVICE_HTTP_PORT, "" + httpPort);
        
        if (isEmpty(launcherHome)) {
            launcherHome = DEFAULT_LAUNCHER_HOME;
            String message = "Using default value for: " + LAUNCHER_HOME_PARAM + "'" +  DEFAULT_LAUNCHER_HOME + "'";
            System.out.println(message);
        }

        new Launcher().launchService(launcherHome, httpPort, openConsole, cleanInstallFolder);
    }

    private Framework framework;

    public void launchService(String launcherHome, int httpPort, boolean openConsole, boolean cleanInstallFolder)
            throws BundleException {
        
        validateFolder(launcherHome);
        String launcherHomeAbsolutePath = new File(launcherHome).getAbsolutePath();

        if (cleanInstallFolder) {
            File cofigurationFolder = new File(launcherHome, CONFIGURATION_SUBFOLDER);
            if (cofigurationFolder.exists()) {
                recursiveFolderDelete(cofigurationFolder);
                if (cofigurationFolder.exists()) {
                    System.out.println("Warning: configuration directory could not be deleted. It is set to be deleted on exit");
                }
            }
        }

        // Folders validated later    
        File loadFirstFolder = new File(launcherHome, BUNDLE_LOAD_FIRST_SUBFOLDER);
        File loadFolder = new File(launcherHome, BUNDLE_LOAD_SUBFOLDER);
        File loadAppFolder = new File(launcherHome, BUNDLE_LOAD_APP_SUBFOLDER);
        File fragmentsFolder = new File(launcherHome, FRAGMENTS_SUBFOLDER);

        ServiceLoader<FrameworkFactory> serviceLoader = ServiceLoader.load(FrameworkFactory.class);

        Iterator<FrameworkFactory> serviceLoaderIterator = serviceLoader.iterator();
        int nuberOfIterations = 0;
        while (serviceLoaderIterator.hasNext()) {
            if (++nuberOfIterations > 1) {
                throw new IllegalStateException("Expected number of iterations must not exceed 1");
            }

            FrameworkFactory frameworkFactory = serviceLoaderIterator.next();
            Map<String, String> config = new HashMap<String, String>();
            // More on env settings here: http://www.artificialworlds.net/blog/2012/11/27/launching-an-osgi-app-on-the-command-line/
            config.put("org.eclipse.equinox.http.jetty.http.port", "" + httpPort);
            
            if (openConsole) {
                config.put("osgi.console", "");
            }
            config.put("eclipse.ignoreApp", TRUE_STRING_VALUE);
            config.put("osgi.noShutdown", TRUE_STRING_VALUE);
            config.put("osgi.install.area", launcherHomeAbsolutePath);
            config.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, "sun.reflect, sun.misc");
            // Enable when troubleshooting is needed
//            config.put("osgi.debug", "true");
//            config.put("eclipse.consoleLog", "true");
//            config.put("org.eclipse.equinox.http.jetty.log.stderr.threshold", "debug");
//            config.put("equinox.ds.debug", "true");
//            config.put("equinox.ds.print", "true");
            
            framework = frameworkFactory.newFramework(config);
            framework.start();

            final String frameworkBundleSymbolicName = framework.getSymbolicName();
            System.out.println("Started " + frameworkBundleSymbolicName + " " + framework.getClass().getName() + " framework at " + launcherHomeAbsolutePath);

            BundleContext context = framework.getBundleContext();

            FileFilter jarFilter = new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    String fileName = pathname.getName().toLowerCase();
                    return fileName.endsWith(".jar");
                }
            };

            loadBundles(context, loadFirstFolder, jarFilter, true);
            if (fragmentsFolder.exists()) {
                loadBundles(context, fragmentsFolder, jarFilter, false);
            }
            loadBundles(context, loadFolder, jarFilter, true);
            loadBundles(context, loadAppFolder, jarFilter, true);
        }
    }

    /**
     * Stops the service. To be used for testing only
     * 
     * @param framework framework value provided by launchService() method
     * @throws BundleException
     */
    public void shutDownService() throws BundleException {
        if (framework == null) {
            throw new IllegalStateException("Service was not properly launched");
        }
        BundleContext bundleContext = framework.getBundleContext();
        Bundle coreBundle = bundleContext.getBundle(0);
        coreBundle.stop(Bundle.STOP_TRANSIENT);
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // There is nothing we can do at this point
                }
                System.exit(0);
            }
        }.start();
    }
    
    private static void recursiveFolderDelete(File directory) {
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                recursiveFolderDelete(file);
            }

            file.deleteOnExit();
            file.delete();
        }
        directory.deleteOnExit();
        directory.delete();
    }

    private static void showUsage(String message) {
        System.out.println(message);
        System.out.println("Usage: java -jar osgi-launcher.jar " + LAUNCHER_HOME_PARAM + "<folder-path> [" + HTTP_PORT_PARAM + "<port>] [" + OPEN_CONSOLE_PARAM + "true] [" + CLEAN_INSTALL_FOLDER_PARAM + "true]");
    }

    private static boolean isEmpty(String string) {
        return string == null || string.trim().length() == 0;
    }

    private static void validateFolder(String folderPath) {
        if (EmptyUtil.is(folderPath)) {
            throw new IllegalArgumentException("folderPath cannot be null or empty");
        }
        validateFolder(new File(folderPath));
    }

    private static void validateFolder(File folder) {
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalStateException("Folder must exist and be a directory (" + folder.getAbsolutePath() + ")");
        }
    }

    private static void loadBundles(BundleContext context, File bundlesFoler, FileFilter jarFilter, boolean startBundles)
            throws BundleException {
        validateFolder(bundlesFoler);
        List<Bundle> installedBundles = new LinkedList<Bundle>();

        File[] bundleFiles = bundlesFoler.listFiles(jarFilter);
        for (File bundlefile : bundleFiles) {
            String bundleUrl = FILE_PROTOCOL + bundlefile.getAbsolutePath();
            installedBundles.add(context.installBundle(bundleUrl));
        }

        if (startBundles) {
            List<Bundle> bundlesToStart = installedBundles;
            int previousSize = bundlesToStart.size() + 1;
            while (bundlesToStart.size() != 0 && bundlesToStart.size() < previousSize) {
                previousSize = bundlesToStart.size();
                bundlesToStart = startBundle(bundlesToStart);
            }
            
            if (bundlesToStart.size() > 0) {
                System.out.println("Warning: " + bundlesToStart.size() + " bundle(s) failed to start");
            }
        }
    }

    private static List<Bundle> startBundle(List<Bundle> installedBundles)
            throws BundleException {
        List<Bundle> bundlesStillToBeStarted = new ArrayList<Bundle>();
        for (Bundle bundle : installedBundles) {
            try {
                bundle.start();
            } catch (BundleException be) {
                bundlesStillToBeStarted.add(bundle);
                System.out.println("Failed to start bundle: " + bundle.getSymbolicName());
                be.printStackTrace();
            }
        }
        return bundlesStillToBeStarted;
    }
}
