package backup_ToBeDeleted;


import com.esri.sde.sdk.client.*;
import com.esri.sde.sdk.client.SeTable.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArcSDEConnection1 {
	private String server, instance, database, user, password;
	private static Logger _log = LoggerFactory.getLogger(ArcSDEConnection1.class);
	private SeConnection conn = null;
	
	public ArcSDEConnection1(String server, String instance, String database, String user, String password){
		this.server = server;
		this.instance = instance;
		this.database = database;
		this.user = user;
		this.password = password;
	}
	
	public SeConnection getSeConnection(){
		return conn;
	}
	
	public void open_connection() throws SeException{
		//prepare connection parameters
		//SeTable t = null;
		//connect to SDE
		_log.info("Connecting...");
		conn = new SeConnection(server, instance, database, user, password);
		_log.info("Connected");
			
//			//get the list of layers available in this SDE database
//			Vector layerList = conn.getLayers();
//			_log.info("There are " + layerList.size() + " layers in this SDE database.");
//			
//			//get a layer
//			int i = 1; //getLayerByName(layerList, "bainbridge");
//			SeLayer theLayer = (SeLayer) layerList.elementAt(i);
//			
//			//get feature from a layer
//			SeSqlConstruct sqlConstruct = new SeSqlConstruct(theLayer.getName());
//			String[] cols = new String[2];
//			cols[0] = "name";
//			cols[1] = "shape";
//			SeQuery query = new SeQuery(conn, cols, sqlConstruct);
//			query.prepareQuery();
//			query.execute();
//			SeRow row = query.fetch();
//			
//			//it is always important to release resource when done
//			query.close();
	}
	
	public void close_connection() throws SeException{
		_log.info("Disconnecting...");
		conn.close();
		_log.info("Disconnected!!");
	}
	
	public void insertData(SeTable table, ArrayList<Object> data) {
		SeColumnDefinition[] colDefs = null;
		
        // Insert Data into table...
        SeInsert insert = null;
        try {
        	colDefs = table.describe();
            insert = new SeInsert(conn);
        } catch( SeException se )  {
        	_log.error(se.getMessage());
        }
        String[] columns = new String[colDefs.length-1];
        
        for(int i=0; i<colDefs.length-1; i++){
        	columns[i] = colDefs[i+1].getName();
        }
        
//        Calendar cal = Calendar.getInstance();
//        cal.set(1798,00,01,1,2,3);

        _log.info("\n--> Inserting Data into table... ");
        try {
            insert.intoTable(table.getName(), columns);
            insert.setWriteMode(true);

            //            for( int count = 1 ; count <= 5 ; count++){
            SeRow row = insert.getRowToSet();
            //                Date date = new Date(100000);
            //                row.setString(0, "");
            
//            TYPE_INT16
//            TYPE_INT32
//            TYPE_INT64
//            TYPE_FLOAT32
//            TYPE_FLOAT64
//            TYPE_STRING
//            TYPE_BLOB
//            TYPE_DATE
//            TYPE_SHAPE
//            TYPE_RASTER
//            TYPE_XML
            _log.info(Double.toString(SeColumnDefinition.TYPE_INT16));
            _log.info(Double.toString(SeColumnDefinition.TYPE_INT32));
            _log.info(Double.toString(SeColumnDefinition.TYPE_INT64));
            _log.info(Double.toString(SeColumnDefinition.TYPE_FLOAT32));
            _log.info(Double.toString(SeColumnDefinition.TYPE_FLOAT64));
            _log.info(Double.toString(SeColumnDefinition.TYPE_STRING));
            _log.info(Double.toString(SeColumnDefinition.TYPE_DATE));
            _log.info(Double.toString(SeColumnDefinition.TYPE_SHAPE));
            _log.info(Double.toString(SeColumnDefinition.TYPE_RASTER));
            _log.info(Double.toString(SeColumnDefinition.TYPE_XML));
            _log.info(Double.toString(SeColumnDefinition.TYPE_NSTRING));
            for(int i=0; i<colDefs.length-1; i++){
            	int type = colDefs[i+1].getType();
            	switch (type) {
            	case SeColumnDefinition.TYPE_INT16:
            		row.setShort(i, (Short)data.get(i));
            		break;
            	case SeColumnDefinition.TYPE_INT32:
            		row.setInteger(i, (Integer)data.get(i));
            		break;
            	case SeColumnDefinition.TYPE_INT64:
            		row.setLong(i, (Long)data.get(i));
            		break;
            	case SeColumnDefinition.TYPE_FLOAT32:
            		row.setFloat(i, (Float)data.get(i));
            		break;
            	case SeColumnDefinition.TYPE_FLOAT64:
            		row.setDouble(i, (Double)data.get(i));
            		break;
            	case SeColumnDefinition.TYPE_STRING:
            		row.setString(i, (String)data.get(i));
            		break;
//            	case SeColumnDefinition.TYPE_BLOB:
//            		row.setInteger(i, (Integer)data.get(i));
//            		break;
            	case SeColumnDefinition.TYPE_DATE:
            		row.setTime(i, (Calendar)data.get(i));
            		break;
            	case SeColumnDefinition.TYPE_SHAPE:
            		row.setShape(i, (SeShape)data.get(i));
            		break;
            	case SeColumnDefinition.TYPE_RASTER:
            		row.setRaster(i, (SeRasterAttr)data.get(i));
            		break;
            	case SeColumnDefinition.TYPE_XML:
            		row.setXml(i, (SeXmlDoc)data.get(i));
            		break;
            	case SeColumnDefinition.TYPE_NSTRING:
            		row.setNString(i, (String)data.get(i));
            		break;
            	default:
            		_log.warn("Column type "+type+" is not supported!");

            	}
            }
            
            //                row.setDate(5, cal.getTime() );
            //                cal.roll(Calendar.YEAR, true);

            //                byte[] buf = new byte[5];
            //                buf[1] = 1;
            //                buf[2] = 2;
            //                buf[3] = 3;
            //                buf[4] = 4;
            //                buf[0] = 5;
            //                ByteArrayInputStream blobData = new ByteArrayInputStream(buf);
            //                row.setBlob(5, blobData);

            insert.execute();
            //            }
            insert.close();
        } catch( SeException se )  {
            _log.error(se.getMessage());
            _log.error(se.toString());
        }
        _log.info("Done! \n ");

    } // End insertData
	
	public void create_table_ObjectId(String tableName, ArrayList<String> headers, ArrayList<Integer> types, ArrayList<Integer> sizes, ArrayList<Boolean> isNullAble) throws SeException{
		_log.info("Create table ... "+tableName);
		SeTable table = new SeTable(conn, tableName);	        
		
		SeColumnDefinition colDefs[] = new SeColumnDefinition[headers.size()+1];
        
        colDefs[0] = new SeColumnDefinition("OBJECTID", SeColumnDefinition.TYPE_UUID, 38, 0, true);
        
        for(int i=0; i<headers.size(); i++){
        	colDefs[i+1] = new SeColumnDefinition(headers.get(i), types.get(i), sizes.get(i), 0, isNullAble.get(i)); 
        	}        
        table.create( colDefs, "DEFAULTS" );
	}
	
	public void create_table_No_ObjectId(String tableName, ArrayList<String> headers, ArrayList<Integer> types, ArrayList<Integer> sizes, ArrayList<Boolean> isNullAble) throws SeException{
		_log.info("Create table ...");
		SeTable table = new SeTable(conn, tableName);	        
		
		SeColumnDefinition colDefs[] = new SeColumnDefinition[headers.size()];
        
//        colDefs[0] = new SeColumnDefinition("OBJECTID", SeColumnDefinition.TYPE_UUID, 38, 0, true);
        
        for(int i=0; i<headers.size(); i++){
        	colDefs[i] = new SeColumnDefinition(headers.get(i), types.get(i), sizes.get(i), 0, isNullAble.get(i)); 
        	}        
        table.create( colDefs, "DEFAULTS" );
	}
	
	/**
	 * Create spatial column
	 * @param tableName name of the table
	 */
	public void create_spatial_column(String tableName, String colName, int shapeType) throws SeException{
		SeLayer layer = new SeLayer(conn);
		layer.setSpatialColumnName(colName);
		layer.setTableName(tableName);
		layer.setShapeTypes(shapeType);
		layer.setGridSizes(1100.0, 0.0, 0.0);
		//layer.setDescription("Layer Example");
		 
        SeExtent ext = new SeExtent(-180, -90, 180, 90);
        layer.setExtent(ext);

        /*
         *   Define the layer's Coordinate Reference
         */
        SeCoordinateReference coordref = new SeCoordinateReference();
        long wgs84 = new Long(4326);
        coordref.setCoordSysByID(new SeObjectId(wgs84));
        coordref.setXY(-210,-120,1000000);
        layer.setCoordRef(coordref);
        
        layer.create(0, 0);
	}
	
	public static void getTableAttr( SeTable table ){

        System.out.println("\n Table attributes... ");

        // Get table name
        System.out.println("Table name  " + table.getName() );

        // Get table owner
        System.out.println("Table owner  " + table.getOwner() );

        // Get table's qualified name
        System.out.println("Table qualified name  " + table.getQualifiedName() );

        // Get database associated with current connection
        System.out.println("Database name " + table.getDatabase() );

        /*
        *   Get table's column definition
        */
        try {
            SeColumnDefinition columnDef[] = table.describe();
            /*
            *   Print out table's column names
            */
            System.out.println("Table columns "   );
            for( int i = 0 ; i < columnDef.length ; i++ ) {

                System.out.println("Column " + (i+1) + " Col Name  " + columnDef[i].getName().toUpperCase() );
                System.out.println("Qualified Col  " + table.qualifyColumn(columnDef[i].getName()).toUpperCase() );
                System.out.println("Column " + (i+1) + " Col Scale " + columnDef[i].getScale() );
                System.out.println("Column " + (i+1) + " Col Size  " + columnDef[i].getSize() );
                System.out.println("Column " + (i+1) + " Col Type  " + columnDef[i].getType() + " -> " + resolveType( columnDef[i].getType() ) );
                System.out.println("Column " + (i+1) + " Row Type  " + columnDef[i].getRowIdType() + " -> " + resolveIdType( columnDef[i].getRowIdType() ) );
                System.out.println("Column " + (i+1) + " Def.  " + columnDef[i].toString() );
            }
        } catch ( SeException se ) {
            _log.error(se.getMessage());
        }

    } // End getTableAttr method
	
	/**
     *   Takes an integer corresponding to an SDE Row Id type
     *   and returns a string description of the type.
     *  @param  type    SDE Row ID type bit-mask.
     */
    public static String resolveIdType( int type ) {

        String typeName = "Wrong type!";
        switch( type ) {

            case 1:
                typeName ="SE_REGISTRATION_ROW_ID_COLUMN_TYPE_SDE ";
                break;
            case 2:
                typeName ="SE_REGISTRATION_ROW_ID_COLUMN_TYPE_USER ";
                break;
            case 3:
                typeName ="SE_REGISTRATION_ROW_ID_COLUMN_TYPE_NONE ";
                break;
        }
        return typeName;
    } // end method resolveIdType
    
    /**
     *   Takes an integer corresponding to an ArcSDE data type
     *   and returns a string description of the type.
     *  @param  type    SDE data type bit-mask.
     */
    public static String resolveType( int type ) {

        String typeName = "Invalid Type";
        switch( type ) {

            case SeColumnDefinition.TYPE_INT16 :
                typeName ="Small Int";
                break;
            case SeColumnDefinition.TYPE_INT32:
                typeName ="Int";
                break;
            case SeColumnDefinition.TYPE_FLOAT32:
                typeName ="Float";
                break;
            case SeColumnDefinition.TYPE_FLOAT64:
                typeName ="Double";
                break;
            case SeColumnDefinition.TYPE_STRING:
                typeName ="String";
                break;
            case SeColumnDefinition.TYPE_BLOB:
                typeName ="Blob";
                break;
            case SeColumnDefinition.TYPE_DATE:
                typeName ="Date";
                break;
            case SeColumnDefinition.TYPE_SHAPE:
                typeName ="Shape";
                break;
            case SeColumnDefinition.TYPE_RASTER:
                typeName ="Raster";
                break;
        }
        return typeName;
    } // End method resolveType
    
    public void delete_table(String tableName) throws SeException{
    	SeTable table = new SeTable( conn, tableName );

        try {
            table.delete();
        } catch( SeException e) {
            /*
            *   If the table doesn't exist don't worry. Otherwise print
            *   the stack trace.
            */
            if( SeError.SE_TABLE_NOEXIST != e.getSeError().getSdeError())
                _log.error("Table "+tableName+" does not exist");
        }
        _log.info("Table "+tableName+" has been deleted!");
    }
}