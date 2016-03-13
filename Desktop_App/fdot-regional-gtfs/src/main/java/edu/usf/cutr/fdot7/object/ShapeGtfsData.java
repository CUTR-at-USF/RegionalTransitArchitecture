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

package edu.usf.cutr.fdot7.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.esri.sde.sdk.client.SDEPoint;
import com.esri.sde.sdk.client.SeCoordinateReference;
import com.esri.sde.sdk.client.SeException;
import com.esri.sde.sdk.client.SeObjectId;
import com.esri.sde.sdk.client.SeShape;

import edu.usf.cutr.fdot7.tools.CoordSystemPointConversion;

/**
 * @author Khoa Tran
 *
 */
public class ShapeGtfsData implements GenericGtfsData{
	private ArrayList<ArrayList<Object>> allData;
	
	private HashMap<String, Integer> shapeIds;
	
	private ArrayList<String> allIds;
	
	private ArrayList<ArrayList<SDEPoint>> allPoints;
	
	private String entityName = "";
	
	private ArrayList<String> fields;
	
	public ShapeGtfsData(String entityName){
		this.entityName = entityName;
		allData = new ArrayList<ArrayList<Object>>();
		shapeIds = new HashMap<String, Integer>();
		allPoints = new ArrayList<ArrayList<SDEPoint>>();
		allIds = new ArrayList<String>();
	}
	
	public ArrayList<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = (ArrayList<String>) fields;
	}

	public String getEntityName(){
		return entityName;
	}

	public void setEntityName(String entityName){
		this.entityName = entityName;
	}
	
	public void addGtfsData(List<Object> data){
		if(fields==null || data==null) return;
		if(fields.size()!=data.size()) return;
		
		CoordSystemPointConversion conversion = new 
		CoordSystemPointConversion("PROJCS[\"Albers Conical Equal Area [Florida Geographic Data Library]\"," +
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
		
		double wgsLatLon[] = new double[2];
		String id = (String)data.get(fields.indexOf("shape_id"));
		wgsLatLon[0] = Double.parseDouble((String)data.get(fields.indexOf("shape_pt_lon")));
		wgsLatLon[1] = Double.parseDouble((String)data.get(fields.indexOf("shape_pt_lat")));
		
		double projLatLon[] = new double[2];
		projLatLon = conversion.getConversionOf(wgsLatLon);
		
		// Get correct set of points
		ArrayList<SDEPoint> points;
		if(allPoints==null || !shapeIds.keySet().contains(id)){
			points = new ArrayList<SDEPoint>();
			shapeIds.put(id, allPoints.size());
			allIds.add(id);
			allPoints.add(points);
		} else {
			points = allPoints.get(shapeIds.get(id));
		}
		
		points.add(new SDEPoint(projLatLon[0], projLatLon[1]));
	}
	
	/* (non-Javadoc)
	 * Convert the set of points to shape
	 * @see edu.usf.cutr.fdot7.object.GenericGtfsData#convertPointsToShape()
	 */
	public void convertPointsToShape(){
		if(allData.size()!=0) return;
		try{
			for(int i=0; i<allPoints.size(); i++){
				SeShape shape = new SeShape();
				ArrayList<SDEPoint> points = allPoints.get(i);
				
				//convert to array of SDEPoint
				SDEPoint pts[] = new SDEPoint[points.size()];
				for(int j=0; j<pts.length; j++){
					pts[j] = points.get(j);
				}
				
				ArrayList<Object>data = new ArrayList<Object>();
				
				// Add null for the first column, which is dataset_id
				data.add(null);
				data.add(allIds.get(i));
				data.add(pts);
				allData.add(data);
			}
		} catch (SeException se){
			System.out.println(se.getMessage());
		}
	}
	
	public ArrayList<Object> getGtfsDataAtRow(int index){
		if(allData!=null) return (ArrayList<Object>) allData.get(index);
		return null;
	}
	
	public String toString(){
		return entityName+" with "+allData.size()+" rows";
	}
	
	public int size(){
		if(allData!=null) return allData.size();
		return -1;
	}

}