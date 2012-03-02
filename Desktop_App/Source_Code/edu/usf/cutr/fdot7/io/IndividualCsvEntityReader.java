/*
 * Copyright 2008 Brian Ferris
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * Modified by Khoa Tran on behalf of the University of South Florida 2012.
 * 
 * All modifications are licensed under Apache License, Version 2.0. 
 */
package edu.usf.cutr.fdot7.io;

import org.onebusaway.csv_entities.CSVListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

import com.esri.sde.sdk.client.SDEPoint;
import com.esri.sde.sdk.client.SeColumnDefinition;
import com.esri.sde.sdk.client.SeShape;

import edu.usf.cutr.fdot7.object.GenericGtfsData;
import edu.usf.cutr.fdot7.object.GenericGtfsDataImpl;
import edu.usf.cutr.fdot7.object.ShapeGtfsData;
import edu.usf.cutr.fdot7.tools.CoordSystemPointConversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class IndividualCsvEntityReader implements CSVListener {

	private static Logger _log = LoggerFactory.getLogger(IndividualCsvEntityReader.class);

	private String _entity;

	private boolean _initialized = false;

	private List<String> _fields;

	private int _line = 1;

	private boolean _verbose = false;

	private boolean _trimValues = false;
	
	private HashMap<String, Integer> mapping = null;
	
	private GenericGtfsData data;
	
	private FdotSchemaImpl fdSchema = null;
	
	private HashSet<String> emptyFields = new HashSet<String>();
	private HashSet<String> noMatchFields = new HashSet<String>();

	public IndividualCsvEntityReader(String entity) {
		_entity = entity;
		BeanFactory factory = new XmlBeanFactory(new FileSystemResource(System.getProperty("user.dir")+System.getProperty("file.separator")+"data-source.xml"));
		fdSchema =	(FdotSchemaImpl) factory.getBean(_entity);
		if(_entity.equals("shapes")){
			data = new ShapeGtfsData(_entity);
		} else {
			data = new GenericGtfsDataImpl(_entity);
		}
	}

	public IndividualCsvEntityReader(List<String> fields) {
		_initialized = true;
		_fields = fields;
	}

	public void setVerbose(boolean verbose) {
		_verbose = verbose;
	}

	public void setTrimValues(boolean trimValues) {
		_trimValues = trimValues;
	}

	public void handleLine(List<String> line) throws Exception {

		if (line.size() == 0)
			return;

		if (_trimValues) {
			for (int i = 0; i < line.size(); i++)
				line.set(i, line.get(i).trim());
		}

		if (!_initialized) {
			readSchema(line);
			_initialized = true;
		} else {
			readEntity(line);
		}
		_line++;
		if (_verbose && _line % 1000 == 0)
			System.out.println("entities=" + _line);
	}

	private void readSchema(List<String> line) {
		_fields = line;
		if(_entity.equals("shapes"))
			data.setFields(_fields);
		mapGtfsToFdotSchema();
	}
	
	/**
	 * Maps the schema key with gtfs key
	 * @return - the mapping of all the special keys
	 */
	private HashMap<String, String> getSpecialMapping(){
		HashMap<String, String> specialMapping = new HashMap<String, String>();
		specialMapping.put("date", "exception_date");  // 'date' is not allowed in Oracle database
//		specialMapping.put("shape_pt_lat", "route_shape");  // shape column in shapes.txt
		return specialMapping;
	}
	
	
	/**
	 * Map data in the order of FDOT schema as described in the data-sources.xml
	 */
	private void mapGtfsToFdotSchema(){		
		mapping = new HashMap<String, Integer>();
		List<String> fdotFields = fdSchema.getFields();
		
		if(_entity.contains("stops")){
			fdotFields.remove("stop_pos");
			fdotFields.add("stop_pos");
		}
		
		ArrayList<String> gtfsFieldsUnmapped = new ArrayList<String>();
		
		HashMap<String, String> specialMapping = getSpecialMapping();
		
		for(int i=0; i<_fields.size(); i++){
			String gtfsField = _fields.get(i);
			boolean isMapped = false;
			
			if(specialMapping.keySet().contains(gtfsField))
				gtfsField = specialMapping.get(gtfsField);
			
			for(int j=0; j<fdotFields.size(); j++){
				if(gtfsField.equals(fdotFields.get(j))){
					mapping.put(_fields.get(i), j);
					isMapped = true;
					break;
				}
			}
			if(!isMapped) 
				gtfsFieldsUnmapped.add(gtfsField);
		}
	}
	
	/**
	 * Return the correct type of the data
	 * @param fdIndex - the index of the column in FDOT schema
	 * @param data - data to get type
	 * @return
	 */
	private Object getValueWithCorrectType(int fdIndex, Object data){
		Object valueWithType = data;
		int type = fdSchema.getTypes().get(fdIndex);
		switch (type) {
		case SeColumnDefinition.TYPE_INT16:
			valueWithType = Short.parseShort((String)data);
			break;
		case SeColumnDefinition.TYPE_INT32:
			valueWithType = Integer.parseInt((String)data);
			break;
		case SeColumnDefinition.TYPE_INT64:
			valueWithType = Long.parseLong((String)data);
			break;
		case SeColumnDefinition.TYPE_FLOAT32:
			valueWithType = Float.parseFloat((String)data);
			break;
		case SeColumnDefinition.TYPE_FLOAT64:
			valueWithType = Double.parseDouble((String)data);
			break;
		case SeColumnDefinition.TYPE_STRING:
			valueWithType = (String)data;
			break;
		case SeColumnDefinition.TYPE_SHAPE:
			valueWithType = (SeShape)data;
			break;
		case SeColumnDefinition.TYPE_NSTRING:
			valueWithType = (String)data;
			break;
		default:
			_log.warn("Column type "+type+" is not supported!");

		}
		return valueWithType;
	}

	/**
	 * Read data and convert it to the appropriate data for inserting purpose
	 * @param line
	 */
	private void readEntity(List<String> line) {
		
		if (line.size() != _fields.size()) {
			_log.warn("expected and actual number of csv fields differ: type="
					+ _entity + " line # " + _line
					+ " expected=" + _fields.size() + " actual=" + line.size());
			while (line.size() < _fields.size())
				line.add("");
		}

		ArrayList<Object> v = new ArrayList<Object>();
		
		if(_entity.equals("shapes")){
			for(int i=0; i<line.size(); i++){
				v.add(line.get(i));
			}
			data.addGtfsData(v);
		} else {	
			//initialize arraylist v
			for(int i=0; i<fdSchema.getFields().size();i++){
				v.add(null);
			}
			for(int i=0; i<line.size(); i++){
				String field = _fields.get(i);
				String value = line.get(i);
				int indexFdot = -1; 
				if (mapping.get(field)!=null) indexFdot = new Integer(mapping.get(field));
				if(indexFdot!=-1) {
					if(value.equals("")) {
						emptyFields.add(field);
						continue;
					}
					Object valueWithType = getValueWithCorrectType(indexFdot, value);
					v.set(indexFdot, valueWithType);
				}
				else 
					noMatchFields.add(field);
			}
			
			if(_entity.equals("stops")){
				double wgsLatLon[] = new double[2];
				wgsLatLon[0] = Double.parseDouble((String)line.get(_fields.indexOf("stop_lon")));
				wgsLatLon[1] = Double.parseDouble((String)line.get(_fields.indexOf("stop_lat")));
				
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
				
				double projLatLon[] = new double[2];
				projLatLon = conversion.getConversionOf(wgsLatLon);
				
				SDEPoint[] pts =  new SDEPoint[] {new SDEPoint(projLatLon[0], projLatLon[1])};
				
				v.set(v.size()-1,pts);
			}
			data.addGtfsData(v);
		}
	}
	
	public HashSet<String> getEmptyFields(){
		return emptyFields;
	}
	
	public HashSet<String> getNoMatchFields(){
		return noMatchFields;
	}

	public void setData(GenericGtfsData data) {
		this.data = data;
	}

	public GenericGtfsData getData() {
		if(_entity.equals("shapes"))
			data.convertPointsToShape();
		return data;
	}
}