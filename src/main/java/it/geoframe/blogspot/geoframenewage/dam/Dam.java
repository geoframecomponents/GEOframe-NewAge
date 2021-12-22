/*
 * GNU GPL v3 License
 *
 * Copyright 2021 Niccolò Tubini, Giuseppe Formetta, Riccardo Rigon
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

package it.geoframe.blogspot.geoframenewage.dam;


import static org.hortonmachine.gears.libs.modules.HMConstants.isNovalue;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;

import it.geoframe.blogspot.numerical.ode.NewtonRaphson;


/**
 * @author Niccolò Tubini, Giuseppe Formetta
 * 
 */
public class Dam{


	@Description("Input rain Hashmap")
	@In
	public HashMap<Integer, double[]> inHMRain;	

	@Description("Input potential evaporation Hashmap")
	@In
	public HashMap<Integer, double[]> inHMEp;

	@Description("Input CI Hashmap")
	@In
	public HashMap<Integer, double[]>initialConditionS_i;
	
	@Description("Input potential evaporation Hashmap")
	@In
	public HashMap<Integer, double[]> inHMQin;
	
	@Description("Input potential evaporation Hashmap")
	@In
	public HashMap<Integer, double[]> inHMQout;
	
	


	@Description("The maximum storage capacity")
	@In
	public double initialConditionFreeSurfaceLevel;

	@Description("Weir width")
	@In
	public double weirWidth;

	@Description("Weir height")
	@In
	public double weirHeight;
	
	@Description("Weir drag coefficient")
	@In
	public double weirCoefficient;

	

	@Description("")
	@In
	public double a;
	
	@Description("")
	@In
	public double b;

	@Description("Time step")
	@In
	public double timeStepMinutes;


	@Description("The output HashMap with the Water Storage  ")
	@Out
	public HashMap<Integer, double[]> outHMFreeSurfaceLevel = new HashMap<Integer, double[]>() ;
	
	@Description("The output HashMap with the Water Storage")
	@Out
	public HashMap<Integer, double[]> outHMVolume = new HashMap<Integer, double[]>() ;

	@Description("The output HashMap with the Water Storage")
	@Out
	public HashMap<Integer, double[]> outHMSurfaceArea = new HashMap<Integer, double[]>() ;


	@Description("The output HashMap with the Q sum of Q over the weir and the released discharge")
	@Out
	public HashMap<Integer, double[]> outHMQ = new HashMap<Integer, double[]>() ;


	@Description("The output HashMap with the outflow which drains to the lower layer")
	@Out
	public HashMap<Integer, double[]> outHMQweir= new HashMap<Integer, double[]>() ;



	private int step;
	
	private double rain;
	private double level;
	private double Ep;
	private double Qin;
	private double Qout;
	
	private Set<Entry<Integer, double[]>> entrySet;
	
	private ODE ode;

	private NewtonRaphson newton;
	
	private AreaStage areaStage;
	private WeirDischarge weirDischarge;

	double CI;





	/**
	 * Process: reading of the data, computation of the
	 * storage and outflows
	 *
	 * @throws Exception the exception
	 */
	@Execute
	public void process() throws Exception {
		//checkNull(inHMRain);

		// reading the ID of all the stations 
		entrySet = inHMRain.entrySet();



		// iterate over the station
		for( Entry<Integer, double[]> entry : entrySet ) {
			Integer ID = entry.getKey();

			if(step==0){
//				System.out.println("RZ--a:"+a+"-brz:"+b+"-Smax:"+s_RootZoneMax+"-pB_soil:"+pB_soil);

				if(initialConditionS_i!=null){
					level = initialConditionS_i.get(ID)[0];	
					if (isNovalue(level)) level = initialConditionFreeSurfaceLevel;	
					
				}else{
					level = initialConditionFreeSurfaceLevel;	
				}
				
				ode = new ODE();
				newton = new NewtonRaphson();
				areaStage = new AreaStage(a, b);
				weirDischarge = new WeirDischarge(weirCoefficient, weirWidth, weirHeight);

			}


			/**Input data reading*/
			rain = inHMRain.get(ID)[0];
			if (isNovalue(rain)) rain= 0;


			Ep=0.0;
			if (inHMEp != null) Ep = inHMEp.get(ID)[0];
			if (isNovalue(Ep)) Ep= 0;
			
			Qin = 0.0;
			if (inHMQin != null) Qin = inHMQin.get(ID)[0];
			if (isNovalue(Qin)) Qin= 0;
				
			Qout = 0.0;
			if (inHMQout != null) Qout = inHMQout.get(ID)[0];
			if (isNovalue(Qout)) Qout= 0;
		
			Qout = Math.min(Qout, areaStage.compute(level)*level/2/(60*timeStepMinutes)+Qin - 0.000001 );
			
			ode.set(areaStage.compute(level)*level/2, rain, Ep, Qin,
					Qout, timeStepMinutes, areaStage, weirDischarge);
			
			level = newton.solve(1.1, ode);
			

//			System.out.println(level + "\t" + weirDischarge.computeDischarge(level) + "\t" + areaStage.compute(level)*level/2 + "\t" + rain + "\t" + Ep);


			outHMFreeSurfaceLevel.put(ID, new double[]{level});
			outHMVolume.put(ID, new double[]{areaStage.compute(level)*level/2});
			outHMSurfaceArea.put(ID, new double[]{areaStage.compute(level)});
			outHMQ.put(ID, new double[]{Qout+weirDischarge.computeDischarge(level)});
			outHMQweir.put(ID, new double[]{weirDischarge.computeDischarge(level)});

			//initialConditionS_i.put(ID,new double[]{waterStorage});



		}


		step++;

	}


}