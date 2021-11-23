package it.geoframe.blogspot.geoframenewage.runofftest;


import java.net.URISyntaxException;
import java.util.HashMap;

import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.junit.Test;

import it.geoframe.blogspot.geoframenewage.runoff.Runoff;

public class TestRunoff{

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
		

		
		Runoff waterBudget= new Runoff();


		while( JReader.doProcess ) {
		

			
			waterBudget.storageMaxRunoff = 0.2;
			waterBudget.coefficientRunoffDischarge=1;
			waterBudget.exponentRunoffDischarge=3;

			waterBudget.area=300.79;
			

			
			JReader.nextRecord();
			
			HashMap<Integer, double[]> id2ValueMap = JReader.outData;
			waterBudget.inHMRain = id2ValueMap;
			
//            CIReader.nextRecord();
//            id2ValueMap = CIReader.outData;
//            waterBudget.initialConditionS_i = id2ValueMap;
			

//            System.out.println(JReader.tCurrent);
//            if(JReader.tCurrent.equalsIgnoreCase("2014-10-01 12:00")) {
//            	System.out.println("qui");
//            }

            waterBudget.process();
            
//            HashMap<Integer, double[]> outHMStorage = waterBudget.outHMStorage;
//            HashMap<Integer, double[]> outHMET = waterBudget.outHMEvaporation;
//            
//            HashMap<Integer, double[]> outHMR = waterBudget.outHMR;
//            
//			writerS.inData = outHMStorage ;
//			writerS.writeNextLine();
//			
//			if (pathToS != null) {
//				writerS.close();
//			}
//			
//
//			
//			writerET.inData = outHMET;
//			writerET.writeNextLine();
//			
//			if (pathToET != null) {
//				writerET.close();
//			}
//			
//			
//			
//			writerR.inData = outHMR;
//			writerR.writeNextLine();
//			
//			if (pathToR != null) {
//				writerR.close();
//			}
            
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