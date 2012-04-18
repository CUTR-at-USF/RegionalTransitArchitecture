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

public interface GtfsSchemaCreation {
	static final int TYPE_INT16 = 1;
	static final int TYPE_INT32 = 2;
	static final int TYPE_FLOAT32 = 3;
	static final int TYPE_FLOAT64 = 4;
	static final int TYPE_STRING = 5;
	static final int TYPE_BLOB = 6;
	static final int TYPE_DATE = 7;
	static final int TYPE_SHAPE = 8;
	static final int TYPE_RASTER = 9;
	static final int TYPE_XML = 10;
	static final int TYPE_INT64 = 11;
	static final int TYPE_UUID = 12;
	static final int TYPE_CLOB = 13;
	static final int TYPE_NSTRING = 14;
	static final int TYPE_NCLOB = 15;
}
