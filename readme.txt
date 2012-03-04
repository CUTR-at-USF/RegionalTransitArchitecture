The goals of this project are to obtain and view spatial data from several public transportation agencies within the Florida Department of Transportation (FDOT) District 7 (D7) and to assist the regional public transportation planning efforts in the area.

With feedback from the regional transit agencies and FDOT D7, there are 3 different software in this project:
    1) A GTFS Data Sync desktop application (Desktop_App) - This software automatically retrieves the GTFS-based datasets from individual transit agency web servers and stores them in the FDOT D7 spatial database.  It was written in Java using Eclipse as an IDE.
	
	2) A Regional Data Visualization Web Application (Web_App) – This software queries and visualizes the regional data from transit agencies and FDOT that is stored in the FDOT D7 spatial database. The application is accessible via a web browser and is capable of showing multimodal data for the regional transportation systems that is always based on the most recent data available from the transit agency.  It was written in C# and XAML using Visual Studio.NET, the Silverlight SDK 5, and the ArcGIS API for Silverlight 2.3.
	
	3) An application for creating initial table for the D7 schema - This software creates necessary tables with defined data types so that the Desktop Application can insert data into.
