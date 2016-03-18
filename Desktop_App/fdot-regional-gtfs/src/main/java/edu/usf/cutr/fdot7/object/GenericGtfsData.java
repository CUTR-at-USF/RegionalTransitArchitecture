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

package edu.usf.cutr.fdot7.object;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Khoa Tran
 *
 */
public interface GenericGtfsData {
	
	/**
	 * Get name of the current entity
	 * @return The name of the current entity
	 */
	public String getEntityName();

	/**
	 * Set name to the current entity
	 * @param entityName - Name to be set to the entity
	 */
	public void setEntityName(String entityName);
	
	/**
	 * Set names to each field of the current entity
	 * @param fields - List of name fields for the entity
	 */
	public void setFields(List<String> fields);
	
	/**
	 * Add new data to the entity. 
	 * @param line - Data to be added to the entity
	 */
	public void addGtfsData(List<Object> line);
	
	/**
	 * Get data from a specified row
	 * @param index - The row index to be retrieved data from
	 * @return Data of the specified row of the entity 
	 */
	public ArrayList<Object> getGtfsDataAtRow(int index);
	
	/**
	 * Override the default toString() method
	 * @return new toString() method
	 */
	public String toString();
	
	/**
	 * Get the size of the entity
	 * @return The size of the entity
	 */
	public int size();
	
	/**
	 * Convert regular points to an ArcSDE shapes
	 */
	public void convertPointsToShape();

}