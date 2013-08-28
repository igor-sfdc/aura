For the moment this is just a plain Java Eclipse project that needs to be built in Eclipse or using ANT

Add this to run-time VM params:
-Dorg.osgi.service.http.port=<your-port-number>

Optionally you can try using project location for resource access:
-Daura.home=../

Start your Aura OSGi bundle by running Launcher class main method (in Eclipse IDE or via command line) and test 
the application using this URL:

http://localhost:9095/auradocs/docs.app#

Bundles with yyy- prefix found under launcherHome are temporary and can be used as POC only. Will replaced by properly 
built or downloaded bundles