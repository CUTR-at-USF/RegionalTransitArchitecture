# fdot-region-gtfs Desktop App

Source code for the GTFS Data Sync desktop application (Desktop_App) - This software automatically retrieves the GTFS-based datasets from individual transit agency web servers and stores them in the FDOT D7 spatial database.  It was written in Java, using the OneBusAway GTFS library and the ArcSDE Java API 10.0 to insert data into an Oracle 10g spatial database.  We use Apache Maven for the build system.

## Setup

To compile the source code, you'll need:
 
* [Java JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Apache Maven](https://maven.apache.org/download.cgi)

If you want to use an IDE, you can use the free community edition of [IntelliJ](https://www.jetbrains.com/idea/).

## Build

#### Maven

To build and run the project via Maven, execute the following commands:

1. `mvn install`
2. `java -jar target/fdot-regional-gtfs-1.0.0-SNAPSHOT-jar-with-dependencies.jar`

#### IntelliJ

1. On the main menu, choose `File | Open`.
2. In the dialog box that opens, select the `pom.xml` file in this directory, and click OK. In this case the import is performed automatically, with the settings defined in the Maven Integration dialog (see [this page](https://www.jetbrains.com/help/idea/2016.1/importing-project-from-maven-model.html?origin=old_help) for more details).
3. To build and run the project, click on the green play button, or "Shift-F10".