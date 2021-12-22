/*
 * GNU GPL v3 License
 *
 * Copyright 2021 Niccolò Tubini, Giuseppe Formetta
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.geoframe.blogspot.geoframenewage.rootzonetest;


import java.net.URISyntaxException;
import java.util.HashMap;

import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.junit.Test;

import it.geoframe.blogspot.geoframenewage.rootzone.*;

/**
 * @author Niccolò Tubini, Giuseppe Formetta
 * 
 */
public class TestRootZone{

	@Test
	public void testLinear() throws Exception {

		String startDate = "1994-01-01 00:00";
		String endDate = "1994-06-01 00:00";
		int timeStepMinutes = 60;
		String fId = "ID";

		String inPathToPrec ="resources/Input/rainfall.csv";
		String inPathToET ="resources/Input/ET.csv";
		String inPathToEwc ="resources/Input/ET.csv";

		
		String pathToStorage=  "resources/Output/rootZone/storage.csv";
		String pathToET= "resources/Output/rootZone/ET_actual.csv";
		String pathToDischarge= "resources/Output/rootZone/Discharge.csv";

		
		OmsTimeSeriesIteratorReader JReader = getTimeseriesReader(inPathToPrec, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader EwcReader = getTimeseriesReader(inPathToEwc, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader ETReader = getTimeseriesReader(inPathToET, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorWriter writerStorage = new OmsTimeSeriesIteratorWriter();
		OmsTimeSeriesIteratorWriter writerET = new OmsTimeSeriesIteratorWriter();
		OmsTimeSeriesIteratorWriter writerDischarge = new OmsTimeSeriesIteratorWriter();


		writerStorage.file = pathToStorage;
		writerStorage.tStart = startDate;
		writerStorage.tTimestep = timeStepMinutes;
		writerStorage.fileNovalue="-9999";

		
		writerET.file = pathToET;
		writerET.tStart = startDate;
		writerET.tTimestep = timeStepMinutes;
		writerET.fileNovalue="-9999";
	
		
		writerDischarge.file = pathToDischarge;
		writerDischarge.tStart = startDate;
		writerDischarge.tTimestep = timeStepMinutes;
		writerDischarge.fileNovalue="-9999";
		

		
		RootZone waterBudget= new RootZone();


		while( JReader.doProcess ) {
		

			
			waterBudget.storageMaxRootZone=0.2;
			waterBudget.coefficientRootZoneDischarge=1;
			waterBudget.exponentRootZoneDischarge=3;
			waterBudget.inTimestep=60;
			waterBudget.area=300.79;
			

			
			JReader.nextRecord();
			
			HashMap<Integer, double[]> id2ValueMap = JReader.outData;
			waterBudget.inHMRain = id2ValueMap;
			
            ETReader.nextRecord();
            id2ValueMap = ETReader.outData;
            waterBudget.inHMETp = id2ValueMap;
            
            EwcReader.nextRecord();
            id2ValueMap = EwcReader.outData;
            waterBudget.inHMEwc = id2ValueMap;

            waterBudget.process();
            
            HashMap<Integer, double[]> outHMStorage = waterBudget.outHMStorage;
            HashMap<Integer, double[]> outHMETa = waterBudget.outHMETa;
            
            HashMap<Integer, double[]> outHMRecharge = waterBudget.outHMRecharge;
            
			writerStorage.inData = outHMStorage ;
			writerStorage.writeNextLine();
			
			if (pathToStorage != null) {
				writerStorage.close();
			}
			

			
			writerET.inData = outHMETa;
			writerET.writeNextLine();
			
			if (pathToET != null) {
				writerET.close();
			}
			
			
			
			writerDischarge.inData = outHMRecharge;
			writerDischarge.writeNextLine();
			
			if (pathToDischarge != null) {
				writerDischarge.close();
			}
            
		}
		JReader.close();
        ETReader.close();
        EwcReader.close();

	}


	private OmsTimeSeriesIteratorReader getTimeseriesReader( String inPath, String id, String startDate, String endDate,
			int timeStepMinutes ) throws URISyntaxException {
		OmsTimeSeriesIteratorReader reader = new OmsTimeSeriesIteratorReader();
		reader.file = inPath;
		reader.idfield = "ID";
		reader.tStart = startDate;
		reader.tTimestep = 60;
		reader.tEnd = endDate;
		reader.fileNovalue = "-9999";
		reader.initProcess();
		return reader;
	}
}