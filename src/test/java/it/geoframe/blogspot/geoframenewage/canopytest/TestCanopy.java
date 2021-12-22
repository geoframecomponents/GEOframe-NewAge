/*
 * GNU GPL v3 License
 *
 * Copyright 2021 Niccolo` Tubini, Giuseppe Formetta
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

package it.geoframe.blogspot.geoframenewage.canopytest;


import java.net.URISyntaxException;
import java.util.HashMap;

import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.junit.Test;

import it.geoframe.blogspot.geoframenewage.canopy.Canopy;

/**
 * @author Niccolò Tubini, Giuseppe Formetta
 * 
 */
public class TestCanopy{

	@Test
	public void testLinear() throws Exception {

		String startDate = "1994-01-01 00:00";
		String endDate = "1994-06-01 00:00";
		int timeStepMinutes = 60;
		String fId = "ID";
		



		String inPathToPrec = "resources/Input/rainfall.csv";
		String inPathToET ="resources/Input/ET.csv";
		String inPathToLAI= "resources/Input/LAI.csv";	

		
		String pathToS= "resources/Output/canopy/S_Canopy.csv";
		String pathToET= "resources/Output/canopy/ET_Canopy.csv";
		String pathToThroughfall= "resources/Output/canopy/Q_Canopy.csv";

		
		OmsTimeSeriesIteratorReader ETReader = getTimeseriesReader(inPathToET, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader RainReader = getTimeseriesReader(inPathToPrec, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader LAIReader = getTimeseriesReader(inPathToLAI, fId, startDate, endDate, timeStepMinutes);

		
		OmsTimeSeriesIteratorWriter writerS = new OmsTimeSeriesIteratorWriter();
		OmsTimeSeriesIteratorWriter writerAET = new OmsTimeSeriesIteratorWriter();
		OmsTimeSeriesIteratorWriter writerThroughfall = new OmsTimeSeriesIteratorWriter();


		writerS.file = pathToS;
		writerS.tStart = startDate;
		writerS.tTimestep = timeStepMinutes;
		writerS.fileNovalue="-9999";

		
		writerAET.file = pathToET;
		writerAET.tStart = startDate;
		writerAET.tTimestep = timeStepMinutes;
		writerAET.fileNovalue="-9999";
		
		
		writerThroughfall.file = pathToThroughfall;
		writerThroughfall.tStart = startDate;
		writerThroughfall.tTimestep = timeStepMinutes;
		writerThroughfall.fileNovalue="-9999";
	
		
		Canopy waterBudget= new Canopy();


		while( RainReader.doProcess ) {
			

		    waterBudget.freeThroughfallCoefficient=0.65;	
			waterBudget.LAICoefficient= 0.5;
			waterBudget.drainageRateCoefficient = 1;
			waterBudget.drainageRateExponent = 1;

			
			RainReader.nextRecord();
			
			HashMap<Integer, double[]> id2ValueMap = RainReader.outData;
			waterBudget.inHMRain= id2ValueMap;
			            
            ETReader.nextRecord();
            id2ValueMap = ETReader.outData;
            waterBudget.inHMETp = id2ValueMap;
            

            LAIReader.nextRecord();
            id2ValueMap = LAIReader.outData;
            waterBudget.inHMLAI = id2ValueMap;

            waterBudget.process();
            
            HashMap<Integer, double[]> outHMStorage = waterBudget.outHMStorage;
            HashMap<Integer, double[]> outHMET = waterBudget.outHMAET;
            HashMap<Integer, double[]> outHThroughfall = waterBudget.outHMThroughfall;
            
            
			writerS.inData = outHMStorage ;
			writerS.writeNextLine();
			
			if (pathToS != null) {
				writerS.close();
			}
			

			
			writerAET.inData = outHMET;
			writerAET.writeNextLine();
			
			if (pathToET != null) {
				writerAET.close();
			}
			
			
			writerThroughfall.inData =outHThroughfall ;
			writerThroughfall.writeNextLine();
			
			if (pathToThroughfall != null) {
				writerThroughfall.close();
			}
			

		}

        LAIReader.close();
        ETReader.close();
        RainReader.close();

	}


	private OmsTimeSeriesIteratorReader getTimeseriesReader( String inPath, String id, String startDate, String endDate,
			int timeStepMinutes ) throws URISyntaxException {
		OmsTimeSeriesIteratorReader reader = new OmsTimeSeriesIteratorReader();
		reader.file = inPath;
		reader.idfield = "ID";
		reader.tStart = startDate;
		reader.tTimestep = 60*24;
		reader.tEnd = endDate;
		reader.fileNovalue = "-9999";
		reader.initProcess();
		return reader;
	}
}