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

package edu.usf.cutr.fdot7.tools;

import com.esri.sde.sdk.pe.PeCSTransformations;
import com.esri.sde.sdk.pe.PeProjectedCS;
import com.esri.sde.sdk.pe.PeProjectionException;

/**
 * @author Khoa Tran
 *
 */
public class CoordSystemPointConversion {
	private PeProjectedCS pcs;
	public CoordSystemPointConversion(String projString){
		try {
			pcs = new PeProjectedCS(projString.trim());
		} catch (PeProjectionException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public double[] getConversionOf(double[] latlon){
		double results[]= new double[2];
		results[0] = latlon[0];
		results[1] = latlon[1];
		
		try {
			PeCSTransformations.geogToProj(pcs, 1, results);
		} catch (PeProjectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return results;
	}
}
