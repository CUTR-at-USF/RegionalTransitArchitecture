

import com.esri.sde.sdk.client.*;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Khoa Tran
 *
 */
public class ArcSDEConnection {
	private String server, instance, database, user, password;
	
	private static Logger _log = LoggerFactory.getLogger(ArcSDEConnection.class);
	
	private SeConnection conn = null;
	
	/**
	 * Create an instance of ArcSDE Connection
	 * @param server - Name of the connecting server
	 * @param instance - The type of geodatabase (e.g. Oracle 10g is 'sde:oracle10g')
	 * @param database - Name of the geodatabase
	 * @param user - Username to connect to the server
	 * @param password - Password of the corresponding username
	 */
	public ArcSDEConnection(String server, String instance, String database, String user, String password){
		this.server = server;
		this.instance = instance;
		this.database = database;
		this.user = user;
		this.password = password;
	}
	
	/**
	 * Get the current SDE Connection
	 * @return The current SDE Connection
	 */
	public SeConnection getSeConnection(){
		return conn;
	}
	
	/**
	 * Open new SDE Connection
	 * @throws SeException SDE Exception
	 */
	public void open_connection() throws SeException{
		_log.info("Connecting...");
		conn = new SeConnection(server, instance, database, user, password);
		_log.info("Connected");
	}
	
	/**
	 * Close the current SDE Connection
	 * @throws SeException SDE Exception
	 */
	public void close_connection() throws SeException{
		_log.info("Disconnecting...");
		conn.close();
		_log.info("Disconnected!!");
	}
	
	/**
	 * Create Table with the specified parameters
	 * @param tableName - Name of the table to be created
	 * @param headers - A list of column names of the new table
	 * @param types - A list of column types of the new table
	 * @param sizes - A list of column sizes of the new table
	 * @param isNullAble - A list of column isNullAble of the new table
	 * @param objectIdColumnName - The name of the spatial column if creating a table with OBJECTID column type. 
	 * 							   'Null' if no OBJECTID column type  
	 * @throws SeException ArcSDE Exception
	 */
	public void create_table(String tableName, ArrayList<String> headers, ArrayList<Integer> types, ArrayList<Integer> sizes, ArrayList<Boolean> isNullAble, String objectIdColumnName) throws SeException{
		_log.info("Create table ... "+tableName);
		SeTable table = new SeTable(conn, (conn.getUser()+"."+tableName));	        
		
		int numOfCols = headers.size();
		
		SeColumnDefinition colDefs[] = new SeColumnDefinition[numOfCols];
        
        for(int i=0; i<headers.size(); i++){
        	colDefs[i] = new SeColumnDefinition(headers.get(i), types.get(i), sizes.get(i), 0, isNullAble.get(i));
        } 
        table.create( colDefs, "DEFAULTS" );
        
        if(objectIdColumnName!=null){
        	SeRegistration registration = null;
    		registration = new SeRegistration( conn, tableName);

    		// Check if table has been registered as ArcSDE maintained table.
    		// If already registered return to main.
    		if( registration.getRowIdColumnType() == SeRegistration.SE_REGISTRATION_ROW_ID_COLUMN_TYPE_SDE )
    			return;

    		// Update the table's registration to give it an ArcSDE maintained row id.
    		registration.setRowIdColumnName(objectIdColumnName);
    		registration.setRowIdColumnType(SeRegistration.SE_REGISTRATION_ROW_ID_COLUMN_TYPE_SDE);

    		registration.alter();
        }
	}
	
	/**
	 * Create feature column. In order to create a feature class, this method should be invoked AFTER creating the base table
	 * @param tableName - Name of the base table
	 * @param colName - Name of the spatial column to be inserted 
	 * @param shapeType - Type of the spatial column (e.g. line, point,...)
	 * @param description - Description of the spatial column
	 * @throws SeException
	 */
	public void create_spatial_column(String tableName, String colName, int shapeType, String description) throws SeException{
		SeLayer layer = new SeLayer(conn);
		layer.setSpatialColumnName(colName);
		layer.setTableName(conn.getUser()+"."+tableName);
		layer.setShapeTypes(shapeType);
		
		/*
         *   Define the layer's Coordinate Reference
         */
        SeCoordinateReference coordref = new SeCoordinateReference();
        
//        long wgs84 = new Long(4326);
//        coordref.setCoordSysByID(new SeObjectId(wgs84));
      
//        SeExtent ext = new SeExtent(-180, -90, 180, 90);
//        coordref.setXY(-210,-120,1000000);
        
        coordref.setCoordSysByDescription("PROJCS[\"Albers Conical Equal Area [Florida Geographic Data Library]\"," +
				"GEOGCS[\"GCS_North_American_1983_HARN\"," +
				"DATUM[\"D_North_American_1983_HARN\"," +
				"SPHEROID[\"GRS_1980\",6378137.0,298.257222101]]," +
				"PRIMEM[\"Greenwich\",0.0]," +
				"UNIT[\"Degree\",0.0174532925199433]]," +
				"PROJECTION[\"Albers\"]," +
				"PARAMETER[\"False_Easting\",400000.0]," +
				"PARAMETER[\"False_Northing\",0.0]," +
				"PARAMETER[\"Central_Meridian\",-84.0]," +
				"PARAMETER[\"Standard_Parallel_1\",24.0]," +
				"PARAMETER[\"Standard_Parallel_2\",31.5]," +
				"PARAMETER[\"Central_Parallel\",24.0]," +
				"UNIT[\"Meter\",1.0]]");
       
        SeExtent ext = new SeExtent(-450359962737.05, -450359962737.05, 450359962737.049, 450359962737.049);
        coordref.setXY(-450359962737.05,-450359962737.05,10000);
        
        layer.setExtent(ext);
        layer.setCoordRef(coordref);
        layer.create(3, 1);
	}
}