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

package edu.usf.cutr.fdot7.io;
import java.util.ArrayList;

/**
 * @author Khoa Tran
 *
 */
public class FdotSchemaImpl implements FdotSchema{
	private String fname;
	
	public void setFname(String fname) {
		this.fname = fname;
	}
	
	public String getFname() {
		return fname;
	}

	private ArrayList<String> fields;
	
	public void setFields(ArrayList<String> headers) {
		this.fields = headers;
	}

	public ArrayList<String> getFields() {
		return fields;
	}
	
	private ArrayList<Integer> types;

	public ArrayList<Integer> getTypes() {
		return types;
	}

	public void setTypes(ArrayList<Integer> types) {
		this.types = types;
	}
	
	private ArrayList<Integer> sizes;
	
	public ArrayList<Integer> getSizes() {
		return sizes;
	}

	public void setSizes(ArrayList<Integer> sizes) {
		this.sizes = sizes;
	}

	public ArrayList<Integer> getScales() {
		return scales;
	}

	public void setScales(ArrayList<Integer> scales) {
		this.scales = scales;
	}

	public ArrayList<Boolean> getIsNullAble() {
		return isNullAble;
	}

	public void setIsNullAble(ArrayList<Boolean> isNullAble) {
		this.isNullAble = isNullAble;
	}
	
	private String server, instance, database;

	public void setServer(String server) {
		this.server = server;
	}

	public String getServer() {
		return server;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public String getInstance() {
		return instance;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getDatabase() {
		return database;
	}

	private ArrayList<Integer> scales = null;
	
	private ArrayList<Boolean> isNullAble;
}
