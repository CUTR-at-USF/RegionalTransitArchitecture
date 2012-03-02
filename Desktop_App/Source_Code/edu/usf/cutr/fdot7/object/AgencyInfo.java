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

/**
 * @author Khoa Tran
 *
 */
public class AgencyInfo {
	private String name, url;
	private boolean isStopTimes = false;
	
	public AgencyInfo(String name, String url){
		this.name = name;
		this.url = url;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setIsStopTimes(boolean isStopTimes) {
		this.isStopTimes = isStopTimes;
	}

	public boolean getIsStopTimes() {
		return isStopTimes;
	}
}