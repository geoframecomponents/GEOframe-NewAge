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

package it.geoframe.blogspot.geoframenewage.groundwatertest;


import java.net.URISyntaxException;
import java.util.HashMap;

import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.junit.Test;

import it.geoframe.blogspot.geoframenewage.groundwater.GroundWater;

/**
 * @author Niccolò Tubini, Giuseppe Formetta
 * 
 */
public class TestGroundWater{

	@Test
	public void testLinear() throws Exception {

		String startDate = "2014-10-01 00:00";
		String endDate = "2014-10-15 00:00";
		int timeStepMinutes = 60;
		String fId = "ID";

		
		String inPathToPrec = "resources/Input/splitter_slow.csv";
		
		String pathToS=  "resources/Output/groundWater/storage.csv";
		String pathToR= "resources/Output/groundWater/recharge.csv";

		
		OmsTimeSeriesIteratorReader JReader = getTimeseriesReader(inPathToPrec, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorWriter writerS = new OmsTimeSeriesIteratorWriter();
		OmsTimeSeriesIteratorWriter writerR = new OmsTimeSeriesIteratorWriter();


		writerS.file = pathToS;
		writerS.tStart = startDate;
		writerS.tTimestep = timeStepMinutes;
		writerS.fileNovalue="-9999";

	
		
		writerR.file = pathToR;
		writerR.tStart = startDate;
		writerR.tTimestep = timeStepMinutes;
		writerR.fileNovalue="-9999";
		

		
		GroundWater waterBudget= new GroundWater();


		while( JReader.doProcess ) {
		

			
			waterBudget.storageMaxGroundWater = 0.2;
			waterBudget.coefficientGroundWaterDischarge = 1;
			waterBudget.exponentGroundWaterDischarge = 3;

			waterBudget.area = 300.79;
			

			
			JReader.nextRecord();
			
			HashMap<Integer, double[]> id2ValueMap = JReader.outData;
			waterBudget.inHMRecharge = id2ValueMap;
			

            waterBudget.process();
            
            HashMap<Integer, double[]> outHMStorage = waterBudget.outHMStorage;
            HashMap<Integer, double[]> outHMDischarge = waterBudget.outHMDischarge;
            
            
			writerS.inData = outHMStorage ;
			writerS.writeNextLine();
			
			if (pathToS != null) {
				writerS.close();
			}
			
			
			
			
			writerR.inData = outHMDischarge;
			writerR.writeNextLine();
			
			if (pathToR != null) {
				writerR.close();
			}
            
		}
		JReader.close();

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