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

package it.geoframe.blogspot.geoframenewage.rootzone;


import static org.jgrasstools.gears.libs.modules.JGTConstants.isNovalue;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;

import org.geotools.feature.SchemaException;

import it.geoframe.blogspot.geoframenewage.rootzone.ODE;
import it.geoframe.blogspot.numerical.ode.NestedNewton;
import it.geoframe.blogspot.numerical.ode.NewtonRaphson;

import java.io.IOException;

import org.apache.commons.math3.ode.*;


/**
 * @author Niccolò Tubini, Giuseppe Formetta
 * 
 */
public class RootZone{


	@Description("Input rain Hashmap")
	@In
	public HashMap<Integer, double[]> inHMRain;	

	@Description("Input ET wet canopy Hashmap")
	@In
	public HashMap<Integer, double[]> inHMEwc;

	@Description("Input ET Hashmap")
	@In
	public HashMap<Integer, double[]> inHMETp;

	@Description("Input CI Hashmap")
	@In
	public HashMap<Integer, double[]>initialConditionS_i;

//	@Description("The maximum storage capacity")
//	@In
//	public double pCmax;

	@Description("Maximum percolation rate")
	@In
	public double coefficientRootZoneDischarge;

	@Description("Exponential of non-linear reservoir")
	@In
	public double exponentRootZoneDischarge;



	@Description("Maximum value of the water storage, needed for the"
			+ "computation of the Actual EvapoTraspiration")
	@In
	@Out
	public double storageMaxRootZone;
	
//	@Description("CI of the water storage")
//	@In
//	@Out
//	public double s_RootZoneCI;
	
	@Description("Initial saturation_degree")
	@In
	public double saturationDegree=0.6;


	@Description("The area of the HRUs in km2")
	@In
	public double area;

	@Description("Time step")
	@In
	public double inTimestep;


//	@Description("The HashMap with the Actual input of the layer ")
//	@Out
//	public HashMap<Integer, double[]> outHMActualInput= new HashMap<Integer, double[]>() ;

	@Description("The output HashMap with the Water Storage  ")
	@Out
	public HashMap<Integer, double[]> outHMStorage= new HashMap<Integer, double[]>() ;


	@Description("The output HashMap with the AET ")
	@Out
	public HashMap<Integer, double[]> outHMETa = new HashMap<Integer, double[]>() ;


	@Description("The output HashMap with the outflow which drains to the lower layer")
	@Out
	public HashMap<Integer, double[]> outHMRecharge= new HashMap<Integer, double[]>() ;

//	@Description("The output HashMap with the quick outflow ")
//	@Out
//	public HashMap<Integer, double[]> outHMquick= new HashMap<Integer, double[]>() ;
//
//	@Description("The output HashMap with the quick outflow ")
//	@Out
//	public HashMap<Integer, double[]> outHMquick_mm= new HashMap<Integer, double[]>() ;


	private int step;
	
	private double rain;
	private double storage;
	private double ETp;
	private double Ewc;
	private double ETNet;
	private double ETa;
	private double recharge;
	
	private Set<Entry<Integer, double[]>> entrySet;
	
	private ODE ode;

//	private NewtonRaphson newton;
	private NestedNewton newton;

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
					storage = initialConditionS_i.get(ID)[0];	
					if (isNovalue(storage)) storage = storageMaxRootZone*saturationDegree;	
					
				}else{
					storage = storageMaxRootZone*saturationDegree;	
				}
				
				ode = new ODE();
//				newton = new NewtonRaphson();
				newton = new NestedNewton();

			}


			/**Input data reading*/
			rain = inHMRain.get(ID)[0];
			if (isNovalue(rain)) rain= 0;


			ETp=0;
			if (inHMETp != null) ETp = inHMETp.get(ID)[0];
			if (isNovalue(ETp)) ETp= 0;

			Ewc=0;
			if (inHMEwc != null) Ewc = inHMEwc.get(ID)[0];
			if (isNovalue(Ewc)) Ewc= 0;

			ETNet = Math.max(0,ETp-Ewc);
			

			ode.set(storage, rain, ETNet, coefficientRootZoneDischarge,
					exponentRootZoneDischarge, storageMaxRootZone);
			storage = newton.solve(storageMaxRootZone*0.9, ode);
			
			recharge = coefficientRootZoneDischarge*Math.pow(Math.min(storage,storageMaxRootZone), exponentRootZoneDischarge) + Math.max(0,  storage - storageMaxRootZone);
			ETa = Math.min(1, storage/storageMaxRootZone)*ETNet;
			storage = storage - Math.max(0,  storage - storageMaxRootZone);
//			System.out.println(storage + "\t" + rain + "\t" + ETNet + "\t" + recharge + "\t" + ETa);


			outHMStorage.put(ID, new double[]{storage});
			outHMETa.put(ID, new double[]{ETa});
			outHMRecharge.put(ID, new double[]{recharge});


			//initialConditionS_i.put(ID,new double[]{waterStorage});



		}


		step++;

	}


}