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
import java.util.List;

/**
 * @author Khoa Tran
 *
 */
public class GenericGtfsDataImpl implements GenericGtfsData{
	private ArrayList<List<Object>> allData;
	
	private String entityName;
	
	/**
	 * Constructor of GenericGtfsDataImpl
	 * @param entityName - Name of the entity
	 */
	public GenericGtfsDataImpl(String entityName){
		this.entityName = entityName;
		allData = new ArrayList<List<Object>>();
	}
	
	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public GenericGtfsDataImpl(ArrayList<List<Object>> allData){
		this.allData = allData;
	}
	
	public void addGtfsData(List<Object> data){
		allData.add(data);
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

	@Override
	public void setFields(List<String> fields) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void convertPointsToShape() {
		// TODO Auto-generated method stub
		
	}
}
