package it.geoframe.blogspot.geoframenewage.groundwatertest;


import java.net.URISyntaxException;
import java.util.HashMap;

import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.junit.Test;

import it.geoframe.blogspot.geoframenewage.groundwater.GroundWater;

public class TestGroundWater{

	@Test
	public void testLinear() throws Exception {

		String startDate = "2014-10-01 00:00";
		String endDate = "2014-10-15 00:00";
		int timeStepMinutes = 60;
		String fId = "ID";

//		String inPathToPrec ="resources/Input/rainfall.csv";
//		String inPathToET ="resources/Input/ET.csv";
//		String inPathToEwc ="resources/Input/ET.csv";
//		String inPathToCI ="resources/Input/S_OUT_rz.csv";
		
		String inPathToPrec ="C:/Users/Niccolo/OMS/OMS_Project_ERM2021/output/splitter_slow.csv";
		String inPathToCI ="resources/Input/S_OUT_rz.csv";
		
		String pathToS=  "resources/Output/rootZone/S_OUT_rz.csv";
		String pathToR= "resources/Output/rootZone/R_drain_rz.csv";

		
		OmsTimeSeriesIteratorReader JReader = getTimeseriesReader(inPathToPrec, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader CIReader = getTimeseriesReader(inPathToCI, fId, startDate, startDate, timeStepMinutes);

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