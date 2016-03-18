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

package edu.usf.cutr.fdot7.main;

import java.io.BufferedInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

import javax.swing.JOptionPane;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

import com.esri.sde.sdk.client.SeException;
import com.esri.sde.sdk.client.SeLayer;
import com.esri.sde.sdk.client.SeTable;

import edu.usf.cutr.fdot7.gui.SessionForm;
import edu.usf.cutr.fdot7.io.FdotSchemaImpl;
import edu.usf.cutr.fdot7.io.GtfsReader;
import edu.usf.cutr.fdot7.object.AgencyInfo;
import edu.usf.cutr.fdot7.object.GenericGtfsData;
import edu.usf.cutr.fdot7.sde.SdeConnection;

/**
 * @author Khoa Tran
 *
 */
public class Test {

	private static Logger _log = LoggerFactory.getLogger(Test.class);

	public static String mainUsername = null;
	public static String mainPassword = null;

	public static Semaphore mutex = new Semaphore(0);

	private BeanFactory factory;
	private FdotSchemaImpl schema;

	/**
	 * Get GTFS dataset for a specific agency
	 * @param ai - infor for a specific agency, including the GTFS URL
	 */
	private ArrayList<GenericGtfsData> getDataFromAgency(AgencyInfo ai) throws IOException{
		ArrayList<GenericGtfsData> entities_data = null;
		_log.info("Downloading GTFS from "+ai.getUrl());
		String pathToZip = downloadFileFromUrl(new URL(ai.getUrl()));
		_log.info("Done Downloading GTFS from "+ai.getName());

		_log.info("Reading "+ai.getName()+" GTFS");
		entities_data = readGtfs(new File(pathToZip), ai.getIsStopTimes());
		_log.info("Done Reading "+ai.getName()+" GTFS");

		return entities_data;
	}

	/**
	 * Constructor of the main program. We use this to avoid static variable
	 */
	public Test(){
		//initialize logger
		org.apache.log4j.BasicConfigurator.configure();

		_log.info("Please log-in to upload data.");
		SessionForm sf = new SessionForm();

		sf.showDialog();
		try{
			mutex.acquire();
		} catch(InterruptedException ie){
			_log.error(ie.getMessage());
		}

		sf.dispose();

		if(mainUsername==null || mainPassword==null){
			_log.error("You must log-in sucessfully before continuing.");
			_log.info("Exit Program!");
			System.exit(0);
		}

		boolean isInputError = false;
		HashSet<String> errorFeeds = new HashSet<String>();
		ArrayList<AgencyInfo> ais = new ArrayList<AgencyInfo>();
		ArrayList<ArrayList<GenericGtfsData>> gtfsAgenciesData = new ArrayList<ArrayList<GenericGtfsData>>();
		_log.info("Reading 'AgencyInfo.csv'");
		ais.addAll(readAgencyInfo(System.getProperty("user.dir")+System.getProperty("file.separator")+"AgencyInfo.csv"));
		_log.info(ais.size()+" GTFS feeds to be processed.");

		factory = new XmlBeanFactory(new FileSystemResource(System.getProperty("user.dir")+System.getProperty("file.separator")+"data-source.xml"));

		for(int i=0; i<ais.size(); i++){
			AgencyInfo ai = ais.get(i);
			try {
				ArrayList<GenericGtfsData> gtfsAgencyData = new ArrayList<GenericGtfsData>();
				gtfsAgencyData.addAll(getDataFromAgency(ai));
				gtfsAgenciesData.add(gtfsAgencyData);
			} catch (IOException e){
				errorFeeds.add(ai.getName());
				_log.error("Error reading input from "+ai.getName());
				_log.error(e.getMessage());
				isInputError = true;
				continue;
			}
		}

		if(!isInputError){
			_log.info("Complete checking and reading "+ais.size()+" GTFS feeds.");
			_log.info("Start to upload data.");
			uploadAgenciesData(gtfsAgenciesData, mainUsername, mainPassword);
		} else {
			_log.info("Please check agency dataset from "+errorFeeds.toString()+" again! No data will be uploaded.");
		}
	}

	/**
	 * Read the agency information from AgencyInfo.csv file
	 * @param fName
	 * @return
	 */
	public ArrayList<AgencyInfo> readAgencyInfo(String fName){
		ArrayList<AgencyInfo> data = new ArrayList<AgencyInfo>();

		String thisLine;
		String [] elements;
		int agencyNameKey=-1, agencyUrlKey=-1, agencyIsStopTimes=-1;
		try {
			BufferedReader br = new BufferedReader(new FileReader(fName));
			boolean isFirstLine = true;
			while ((thisLine = br.readLine()) != null) {
				if (isFirstLine) {
					isFirstLine = false;
					thisLine = thisLine.replace("\"", "");
					String[] keys = thisLine.split(",");
					for(int i=0; i<keys.length; i++){
						if(keys[i].equals("agency_name")) agencyNameKey = i;
						else if(keys[i].equals("agency_url")) agencyUrlKey = i;
						else if(keys[i].equals("isStopTimes")) agencyIsStopTimes = i;
					}
				}
				else {
					boolean lastIndexEmpty=false;
					thisLine = thisLine.trim();

					if(thisLine.contains("\"")) {
						String[] temp = thisLine.split("\"");
						for(int x=0; x<temp.length; x++){
							if(x%2==1) temp[x] = temp[x].replace(",", "");
						}
						thisLine = "";
						for(int x=0; x<temp.length; x++){
							thisLine = thisLine + temp[x];
						}
					}
					elements = thisLine.split(",");
					if(thisLine.charAt(thisLine.length()-1)==',') lastIndexEmpty=true;
					//add leading 0's to gtfs_id
					AgencyInfo ai = new AgencyInfo(elements[agencyNameKey],elements[agencyUrlKey]);
					if(elements[agencyIsStopTimes].toUpperCase().equals("TRUE")){
						ai.setIsStopTimes(true);
					} else if(elements[agencyIsStopTimes].toUpperCase().equals("FALSE")){
						ai.setIsStopTimes(false);
					}

					data.add(ai);
				}
			}
		}
		catch (IOException e) {
			_log.error("Error: " + e);
		}
		return data;
	}

	/**
	 * Main program
	 * @param args
	 */
	public static void main(String[] args) {
		new Test();

		_log.info("Program Completed!");
	}

	/**
	 * Download file method
	 * @param downloadUrl - URL of the file to be downloaded
	 * @return A string of data
	 * @throws IOException
	 */
	private String downloadFileFromUrl(URL downloadUrl) throws IOException{
		String filename = "gtfs.zip";
		File downloadedFolder = new File("GTFS_Temp");
		String downloadedLocation = downloadedFolder.getAbsolutePath() + System.getProperty("file.separator"); //"\\"; //temporary folder to store downloaded files
		String full_path = downloadedLocation+filename;
		try {
			downloadedFolder.mkdir(); //create the directory if not already created
		} catch (SecurityException ex) {
			_log.info("Unable to create temporary directory to download the GTFS data to. \n" + ex.getLocalizedMessage());
			return null;
		}
		if (downloadedFolder.listFiles().length > 0) { //if the folder has old files in it
			for (File f : downloadedFolder.listFiles()) {
				f.delete(); //delete all the old files
			}
		}

		BufferedInputStream in = new BufferedInputStream(downloadUrl.openStream());

		byte[] buffer = new byte[1024];
		int count;

		FileOutputStream out = new FileOutputStream(full_path);

		while((count = in.read(buffer, 0, 1024)) > 0) {
			out.write(buffer, 0, count);
		}

		out.close();

		return full_path;
	}

	/**
	 * Read the given GTFS dataset
	 * @param path - GTFS file to be read
	 * @throws IOException
	 */
	private ArrayList<GenericGtfsData> readGtfs(File path, boolean isStopTimes) throws IOException{
		GtfsReader reader = new GtfsReader(isStopTimes);
		reader.setInputLocation(path);
		reader.setInternStrings(true);

		ArrayList<String> entities = reader.getEntities();

		ArrayList<GenericGtfsData> all_entities_data = new ArrayList<GenericGtfsData>();

		for (String entity : entities) {
			_log.info("reading entities: " + entity);

			reader.readEntities(entity);

			GenericGtfsData entity_data = reader.getData();

			if (entity_data!=null){
				all_entities_data.add(entity_data);
			}
			else {
				_log.info(entity+ " has no data");
			}
		}
		return all_entities_data;
	}

	private void uploadAgenciesData(ArrayList<ArrayList<GenericGtfsData>> all_entities_data, String username, String password){
		try{
			for (int i=0; i<all_entities_data.size(); i++){
				uploadAgencyData(all_entities_data.get(i), username, password);
			}
		} catch(Exception e){
			_log.error(e.getMessage());
			return;
		}
	}

	/**
	 * Insert data to database
	 * @throws Exception
	 */
	private void uploadAgencyData(ArrayList<GenericGtfsData> all_entities_data, String username, String password) throws Exception{
		if(all_entities_data==null) return;

		schema = (FdotSchemaImpl) factory.getBean("connection_specs");
		SdeConnection conn = new SdeConnection(schema.getServer(),
				schema.getInstance(),
				schema.getDatabase(),
				username,
				password);
		try{
			try {
				_log.info("Opening connection...");
				conn.open_connection();
			} catch (SeException se){
				if(conn!=null) {
					_log.info("Closing connection...");
					conn.close_connection();
				}
				throw new Exception("Connection Failed!!");
			}

			_log.info("Start transaction");
			conn.getSeConnection().setTransactionAutoCommit(0);
			conn.getSeConnection().startTransaction();
			try{
				int i=0;
				String datasetID=null;
				while (i<all_entities_data.size()) {
					GenericGtfsData entityData = all_entities_data.get(i);
					schema = (FdotSchemaImpl) factory.getBean(entityData.getEntityName());
					String tableName = conn.getSeConnection().getUser()+"."+schema.getFname();
					SeTable table = new SeTable(conn.getSeConnection(), tableName);

					_log.info("Inserting Data into table "+tableName+"... ");
					if(entityData.getEntityName().toLowerCase().contains("agency")){
						for(int j=0; j<entityData.size(); j++) {
							ArrayList<Object> rowData = entityData.getGtfsDataAtRow(j);
							datasetID = conn.insertData_ObjectId(table, rowData);
							_log.info(datasetID);
						}
					} else {
						if(datasetID==null) {
							_log.error("Unable to find datasetID");
							return;
						}

						SeLayer myLayer = null;
						for(Iterator l = conn.getSeConnection().getLayers().iterator(); l.hasNext(); ) {
							SeLayer tempLayer = ((SeLayer)(l.next()));
							if ((table.getQualifiedName()).toUpperCase().equalsIgnoreCase(tempLayer.getQualifiedName())) {
								myLayer = tempLayer;
								break;
							}
						}

						int row;
						for(row=0; row<entityData.size(); row++) {
							ArrayList<Object> rowData = entityData.getGtfsDataAtRow(row);
							conn.insertData_No_ObjectId(table, rowData, datasetID, myLayer);
						}
						_log.info("Done Inserting "+row+" row out of "+entityData.size()+
								" row data into table "+tableName);
					}

					i++;

				}
				_log.info("Commit transaction");
				conn.getSeConnection().commitTransaction();
			} catch (Exception e){
				_log.error(e.getMessage());
				e.printStackTrace();
				_log.info("Roll back transaction");
				conn.getSeConnection().rollbackTransaction();
			}

			if(conn!=null) {
				_log.info("Closing connection...");
				conn.close_connection();
			}

			_log.info("Uploading GTFS ... Done!");
		} catch (SeException e) {
			_log.error(e.getMessage());
			_log.error(e.getSeError().getErrDesc());
			_log.error(e.getSeError().getSdeErrMsg());

		}
	}
}