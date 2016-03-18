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

import java.util.ArrayList;

public class GtfsReader extends CsvEntityReader{
	
	ArrayList<String> _entities = new ArrayList<String>();
	
	public GtfsReader(boolean isStopTimes){
		_entities.add("agency");
		_entities.add("routes");
		_entities.add("trips");
		_entities.add("calendar");
		_entities.add("calendar_dates");
		_entities.add("fare_attributes");
		_entities.add("fare_rules");
		_entities.add("frequencies");
		_entities.add("transfers");
		_entities.add("stops");
		_entities.add("shapes");
		if(isStopTimes) {
			_entities.add("stop_times");
		}
	}
	
	public ArrayList<String> getEntities() {
		return _entities;
	}

}