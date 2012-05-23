Development of a Regional Public Transportation GIS Architecture and Data Model

Sponsored by the National Center for Transit Research and the Florida Department of Transportation

The goals of this project were to assist the regional public transportation planning efforts in the Tampa Bay Area in Florida by creating a software architecture and data model that would facilitate the automated sharing of transit data among several public transportation agencies within the Florida Department of Transportation (FDOT) District 7 (D7) and FDOT D7 operations.  Transit data were retrieved from transit agency websites and stored with FDOT D7 data in FDOT D7's enterprise GIS system so it could be viewed via a website.

With feedback from the regional transit agencies and FDOT D7, 3 different software applications were created for this project:
1) A GTFS Data Sync desktop application (Desktop_App) - This software automatically retrieves the GTFS-based datasets from individual transit agency web servers and stores them in the FDOT D7 spatial database.  It was written in Java using Eclipse as an IDE, and the OneBusAway GTFS library and the ArcSDE Java API 10.0 to insert data into an Oracle 10g spatial database.
	
2) A Regional Data Visualization Web Application (Web_App) - This software queries and visualizes the regional data from transit agencies and FDOT that is stored in the FDOT D7 spatial database. The application is accessible via a web browser and is capable of showing multimodal data for the regional transportation systems that is always based on the most recent data available from the transit agency.  It was written in C# and XAML using Visual Studio.NET, the Silverlight SDK 5, and the ArcGIS API for Silverlight 2.3.
	
3) An application for creating initial table for the D7 schema (FDOT7_Schema_Creation) - This software creates necessary tables with defined data types so that the Desktop Application can insert data into the FDOT D7 spatial database.

Read more about the project at: https://github.com/CUTR-at-USF/RegionalTransitArchitecture/wiki