1) To execute the program, double-click on "AutoRun.bat" file

2) To login, the credentials must have permission to write. Furthermore, the password needs to be associated with a suffice previously specified in the “tnsnames.ora” (e.g., password@d7gis.world)

3) When the URLs of any agencies change, please refer to "AgencyInfo.csv" file
      + DONOT modify the first line (header) of this file
      + Only URLs end with ".zip" extension are valid
      + Only "TRUE" and "FALSE" values are valid for the 3rd column

4) When the configuration of the geodatabase changes, please modify the "data-source.xml" accordingly:
      + DONOT modify any "id", or "name" fields. ONLY modify the "value" fields
      + To modify the values of "Types", please refer to http://edndoc.esri.com/arcsde/9.2/api/japi/docs/constant-values.html#com.esri.sde.sdk.client.SeColumnDefinition
