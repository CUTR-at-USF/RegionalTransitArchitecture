/**
Copyright 2012 University of South Florida

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

**/

package edu.usf.cutr.fdot7.sde;

import com.esri.sde.sdk.client.*;

import edu.usf.cutr.fdot7.main.Test;

import java.util.ArrayList;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Khoa Tran
 *
 */
public class SdeConnection {
	private String server, instance, database, user, password;
	private static Logger _log = LoggerFactory.getLogger(SdeConnection.class);
	private SeConnection conn = null;
	
	public SdeConnection(String server, String instance, String database, String user, String password){
		this.server = server;
		this.instance = instance;
		this.database = database;
		this.user = user;
		this.password = password;
	}
	
	public SeConnection getSeConnection(){
		return conn;
	}
	
	public void open_connection() throws SeException{;
		conn = new SeConnection(server, instance, database, user, password);
	}
	
	public void close_connection() throws SeException{
//		_log.info("Disconnecting...");
		conn.close();
//		_log.info("Disconnected!!");
	}
	
	/**
	 * Insert data with an object ID (the table is registered in ArcSDE)
	 * @param table
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String insertData_ObjectId(SeTable table, ArrayList<Object> data) throws Exception{
		String datasetID = null;
		SeColumnDefinition[] colDefs = null;
		
        // Insert Data into table...
        SeInsert insert = null;
        try {
        	colDefs = table.describe();
            insert = new SeInsert(conn);
        } catch( SeException se )  {
        	_log.error(se.getMessage());
        }
        
        ArrayList<String> columnsArray = new ArrayList<String>();
        for(int i=0; i<colDefs.length; i++){
        	if(colDefs[i].getName().toUpperCase().contains("FID")){
        		continue;
        	} else if(colDefs[i].getName().toUpperCase().contains("DATASET_ID")){
        		continue;
        	} else if(colDefs[i].getName().toUpperCase().contains("INVI_MASK")){
        		continue;
        	}
        	columnsArray.add(colDefs[i].getName());
        }
        
        String[] columns = new String[columnsArray.size()];
        
        for(int i=0; i<columnsArray.size(); i++){
        	columns[i] = columnsArray.get(i);
        }

        try {
            insert.intoTable(table.getName(), columns);
            insert.setWriteMode(true);

            SeRow row = insert.getRowToSet();
            
            int dataIndex = 0;
            int columnIndex = 0;
            while (columnIndex < colDefs.length){
            	if(colDefs[columnIndex].getName().toUpperCase().contains("DATASET_ID")){
            		columnIndex++;
            		continue;
            	}
            	else if(colDefs[columnIndex].getName().toUpperCase().contains("FID")){
            		columnIndex++;
            		continue;
            	}
            	else if(colDefs[columnIndex].getName().toUpperCase().equals("INVI_MASK")){
            		columnIndex++;
            		continue;
            	}
            	
            	int type = colDefs[columnIndex].getType();
            	switch (type) {
            	case SeColumnDefinition.TYPE_INT16:
            		row.setShort(columnIndex, (Short)data.get(dataIndex));
            		break;
            	case SeColumnDefinition.TYPE_INT32:
            		row.setInteger(columnIndex, (Integer)data.get(dataIndex));
            		break;
            	case SeColumnDefinition.TYPE_INT64:
            		row.setLong(columnIndex, (Long)data.get(dataIndex));
            		break;
            	case SeColumnDefinition.TYPE_FLOAT32:
            		row.setFloat(columnIndex, (Float)data.get(dataIndex));
            		break;
            	case SeColumnDefinition.TYPE_FLOAT64:
            		row.setDouble(columnIndex, (Double)data.get(dataIndex));
            		break;
            	case SeColumnDefinition.TYPE_STRING:
            		row.setString(columnIndex, (String)data.get(dataIndex));
            		break;
            	case SeColumnDefinition.TYPE_DATE:
            		Calendar now = Calendar.getInstance();
            		row.setTime(columnIndex, now);
            		break;
            	case SeColumnDefinition.TYPE_SHAPE:
            		SeShape shape = (SeShape)data.get(dataIndex);
            		row.setShape(columnIndex, shape);
            		break;
            	case SeColumnDefinition.TYPE_RASTER:
            		row.setRaster(columnIndex, (SeRasterAttr)data.get(dataIndex));
            		break;
            	case SeColumnDefinition.TYPE_XML:
            		row.setXml(columnIndex, (SeXmlDoc)data.get(dataIndex));
            		break;
            	case SeColumnDefinition.TYPE_NSTRING:
            		row.setNString(columnIndex, (String)data.get(dataIndex));
            		break;
            	default:
            		_log.warn("Column type "+type+" is not supported!");
            	}
            	columnIndex++;
            	dataIndex++;
            }
            
            insert.execute();
            SeObjectId newRowID = insert.lastInsertedRowId();
            datasetID = Long.toString(newRowID.longValue());
        } catch( SeException se )  {
        	se.printStackTrace();
            _log.error(se.getMessage());
            _log.error(se.getLocalizedMessage());
            _log.error(se.toString());
            throw new Exception("Invalid data to upload: "+data.toString());
        } finally {
        	try {
        		insert.close();
        	} catch (SeException se){
        		_log.error(se.getMessage());
                _log.error(se.getLocalizedMessage());
                _log.error(se.toString());
                se.printStackTrace();
                throw new Exception("Invalid data to upload: "+data.toString());
        	}
        }
        _log.info("Done! \n ");
        return datasetID;
    } // End insertData
	
	/**
	 * Insert data with no object ID (table is not registered with ArcSDE) 
	 * @param table
	 * @param data
	 * @param datasetID
	 * @param insertLayer
	 * @throws Exception
	 */
	public void insertData_No_ObjectId(SeTable table, ArrayList<Object> data, String datasetID, SeLayer insertLayer) throws Exception {
		if(datasetID==null) 
			return;
		SeColumnDefinition[] colDefs = null;
		
        // Insert Data into table...
        SeInsert insert = null;
        try {
        	colDefs = table.describe();
            insert = new SeInsert(conn);
        } catch( SeException se )  {
        	_log.error(se.getMessage());
        }
        
        ArrayList<String> columnsArray = new ArrayList<String>();
        for(int i=0; i<colDefs.length; i++){
        	if(colDefs[i].getName().toUpperCase().contains("FID")){
        		continue;
        	} else if(colDefs[i].getName().toUpperCase().contains("LEN")){
        		continue;
        	} else if(colDefs[i].getName().toUpperCase().contains("INVI_MASK")){
        		continue;
        	}
        	columnsArray.add(colDefs[i].getName());
        }
        
        String[] columns = new String[columnsArray.size()];
        
        for(int i=0; i<columnsArray.size(); i++){
        	columns[i] = columnsArray.get(i);
        }

        try {
            insert.intoTable(table.getName(), columns);
            insert.setWriteMode(true);

            SeRow row = insert.getRowToSet();
            
            int dataIndex = 0;
            int columnIndex = 0;
            while (columnIndex < colDefs.length){
            	if(colDefs[columnIndex].getName().toUpperCase().contains("OBJECTID")){
            		columnIndex++;
            		continue;
            	}
            	else if(colDefs[columnIndex].getName().toUpperCase().contains("FID")){
            		columnIndex++;
            		continue;
            	}
            	else if(colDefs[columnIndex].getName().toUpperCase().contains("LEN")){
            		columnIndex++;
            		continue;
            	}
            	else if(colDefs[columnIndex].getName().toUpperCase().equals("DATASET_ID")){
            		row.setNString(columnIndex, datasetID);
            		columnIndex++;
            		dataIndex++;
            		continue;
            	}
            	else if(colDefs[columnIndex].getName().toUpperCase().equals("INVI_MASK")){
            		columnIndex++;
            		continue;
            	}
            	int type = colDefs[columnIndex].getType();
            	switch (type) {
            	case SeColumnDefinition.TYPE_INT16:
            		if (data.get(dataIndex)!=null) row.setShort(columnIndex, (Short.parseShort(data.get(dataIndex).toString())));
            		else row.setShort(columnIndex, null);
            		break;
            	case SeColumnDefinition.TYPE_INT32:
            		if (data.get(dataIndex)!=null) row.setInteger(columnIndex, (Integer.parseInt(data.get(dataIndex).toString())));
            		else row.setInteger(columnIndex, null);
            		break;
            	case SeColumnDefinition.TYPE_INT64:
            		if (data.get(dataIndex)!=null) row.setLong(columnIndex, (Long.parseLong(data.get(dataIndex).toString())));
            		else row.setLong(columnIndex, null);
            		break;
            	case SeColumnDefinition.TYPE_FLOAT32:
            		if (data.get(dataIndex)!=null) row.setFloat(columnIndex, (Float.parseFloat(data.get(dataIndex).toString())));
            		else row.setFloat(columnIndex,null);
            		break;
            	case SeColumnDefinition.TYPE_FLOAT64:
            		if (data.get(dataIndex)!=null) row.setDouble(columnIndex, (Double.parseDouble(data.get(dataIndex).toString())));
            		else row.setDouble(columnIndex, null);
            		break;
            	case SeColumnDefinition.TYPE_STRING:
            		row.setString(columnIndex, (String)data.get(dataIndex));
            		break;
            	case SeColumnDefinition.TYPE_DATE:
            		row.setTime(columnIndex, (Calendar)data.get(dataIndex));
            		break;
            	case SeColumnDefinition.TYPE_SHAPE:
            		if(insertLayer==null) break;
            		SeShape shape = new SeShape();
            		SeCoordinateReference coordref = insertLayer.getCoordRef();
            		shape.setCoordRef(coordref);
            		SDEPoint[] pts = (SDEPoint[])data.get(dataIndex);
            		if(table.getName().toUpperCase().contains("STOP")) {
            			shape.generatePoint(pts.length, pts);
            		} else if(table.getName().toUpperCase().contains("SHAPE")){
            			int numParts = 1;
            			int[] partOffsets = new int[numParts];
            			partOffsets[0] = 0;
            			shape.generateLine(pts.length, numParts, partOffsets, pts);
            		}
            		row.setShape(columnIndex, shape);
            		break;
            	case SeColumnDefinition.TYPE_RASTER:
            		row.setRaster(columnIndex, (SeRasterAttr)data.get(dataIndex));
            		break;
            	case SeColumnDefinition.TYPE_XML:
            		row.setXml(columnIndex, (SeXmlDoc)data.get(dataIndex));
            		break;
            	case SeColumnDefinition.TYPE_NSTRING:
            		if(data.get(dataIndex)!=null) row.setNString(columnIndex, data.get(dataIndex).toString());
            		else row.setNString(columnIndex, null);
            		break;
            	default:
            		_log.warn("Column type "+type+" is not supported!");
            	}
            	columnIndex++;
            	dataIndex++;
            }
            insert.execute();
            insert.close();
        } catch( SeException se )  {
            _log.error(se.getMessage());
            _log.error(se.getLocalizedMessage());
            _log.error(se.toString());
            se.printStackTrace();
            throw new Exception("Invalid data to upload: "+data.toString());
        } finally {
        	try {
        		insert.close();
        	} catch (SeException se){
        		_log.error(se.getMessage());
                _log.error(se.getLocalizedMessage());
                _log.error(se.toString());
                se.printStackTrace();
                throw new Exception("Invalid data to upload: "+data.toString());
        	}
        }
    } // End insertData
}