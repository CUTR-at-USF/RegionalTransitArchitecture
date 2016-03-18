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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.zip.ZipFile;

import org.onebusaway.csv_entities.CsvInputSource;
import org.onebusaway.csv_entities.CsvTokenizerStrategy;
import org.onebusaway.csv_entities.FileCsvInputSource;
import org.onebusaway.csv_entities.TokenizerStrategy;
import org.onebusaway.csv_entities.ZipFileCsvInputSource;
import org.onebusaway.csv_entities.exceptions.CsvEntityIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.usf.cutr.fdot7.object.GenericGtfsData;

public class CsvEntityReader {
	
	private static Logger _log = LoggerFactory.getLogger(CsvEntityReader.class);

	private CsvInputSource _source;

	private TokenizerStrategy _tokenizerStrategy = new CsvTokenizerStrategy();

	private boolean _trimValues = false;

	private boolean _internStrings = false;
	
	private IndividualCsvEntityReader entityLoader = null;

	public GenericGtfsData getData() {
		if (entityLoader!=null) 
			return entityLoader.getData();
		return null;
	}

	public CsvInputSource getInputSource() {
		return _source;
	}

	public void setInputSource(CsvInputSource source) {
		_source = source;
	}

	public void setInputLocation(File path) throws IOException {
		if (path.isDirectory())
			_source = new FileCsvInputSource(path);
		else
			_source = new ZipFileCsvInputSource(new ZipFile(path));
	}

	public void setTokenizerStrategy(TokenizerStrategy tokenizerStrategy) {
		_tokenizerStrategy = tokenizerStrategy;
	}

	public void setTrimValues(boolean trimValues) {
		_trimValues = trimValues;
	}

	public void setInternStrings(boolean internStrings) {
		_internStrings = internStrings;
	}

	public void readEntities(String entityName) throws IOException {
		entityLoader = null;
		readEntities(entityName, _source);
	}

	public void readEntities(String entityName, CsvInputSource source)
	throws IOException {
		InputStream is = openInputStreamForEntityClass(source, entityName);
		if (is != null)
			readEntities(entityName, is);
	}

	public void readEntities(String entityName, InputStream is)
	throws IOException, CsvEntityIOException {
		readEntities(entityName, new InputStreamReader(is, "UTF-8"));
	}

	public void readEntities(String entityName, Reader reader)
	throws IOException, CsvEntityIOException {

		entityLoader = new IndividualCsvEntityReader(entityName);
		entityLoader.setTrimValues(_trimValues);

		BufferedReader lineReader = new BufferedReader(reader);

		/**
		 * Skip the initial UTF BOM, if present
		 */
		lineReader.mark(1);
		int c = lineReader.read();

		if (c != 0xFEFF) {
			lineReader.reset();
		}

		String line = null;
		int lineNumber = 1;

		try {
			while ((line = lineReader.readLine()) != null) {
				List<String> values = _tokenizerStrategy.parse(line);
				if( _internStrings )
					internStrings(values);
				entityLoader.handleLine(values);
				lineNumber++;
			}
			if(!entityLoader.getEmptyFields().isEmpty())
				_log.warn(entityLoader.getEmptyFields().toString()+" has '' value");
			if(!entityLoader.getNoMatchFields().isEmpty())
				_log.warn(entityLoader.getNoMatchFields().toString()+" cannot be mapped with FDOT Schema");
		} catch (Exception ex) {
			System.out.println("Read error at file "+entityName+" line "+lineNumber+" "+ex.getMessage());
		} finally {
			try {
				lineReader.close();
			} catch (IOException ex) {
				System.out.println("Error while closing file "+entityName);
			}
		}
		
	}

	public InputStream openInputStreamForEntityClass(CsvInputSource source,
			String name) throws IOException {

		String full_name = name+".txt";
		if (!_source.hasResource(full_name)) {
			return null;
		}

		return _source.getResource(full_name);
	}

	public void close() throws IOException {
		if (_source != null)
			_source.close();
	}


	private void internStrings(List<String> values) {
		for( int i=0; i<values.size(); i++ ) {
			String value = values.get(i);
			value = value.intern();
			values.set(i, value);
		}
	}
}
