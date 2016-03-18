To install jar files to this bundled Maven repo, run the below from the ROOT directory:

`mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file -Dfile=lib/concurrent.jar -DgroupId=arcsde -DartifactId=arcsde4 -Dversion=1.0 -Dpackaging=jar -DlocalRepositoryPath=lib`