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

package it.geoframe.blogspot.geoframenewage.canopy;

import static org.hortonmachine.gears.libs.modules.HMConstants.isNovalue;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;
import oms3.annotations.Unit;


import it.geoframe.blogspot.numerical.ode.NewtonRaphson;


/**
 * @author Niccolò Tubini, Giuseppe Formetta
 * 
 */
public class Canopy{


	@Description("Input rain Hashmap")
	@In
	public HashMap<Integer, double[]> inHMRain;

	@Description("Input ETp Hashmap")
	@In
	public HashMap<Integer, double[]> inHMETp;

	@Description("Input CI Hashmap")
	@In
	public HashMap<Integer, double[]>initialConditionS_i;

	@Description("Leaf Area Index Hashmap")
	@In
	public  HashMap<Integer, double[]> inHMLAI;

	


	@Description("Coefficient canopy out, Dickinson 1984")
	@In
	@Unit("-")
	public double LAICoefficient;

	@Description("Partitioning coefficient free throughfall")
	@In
	@Unit("-")
	public double freeThroughfallCoefficient;

	@Description("Canopy drainage rate coefficient")
	@In
	@Unit("1/day")
	public double drainageRateCoefficient;

	@Description("Canopy drainage rate exponent")
	@In
	@Unit("-")
	public double drainageRateExponent;


	@Description("The output HashMap with the Water Storage  ")
	@Out
	public HashMap<Integer, double[]> outHMStorage= new HashMap<Integer, double[]>() ;

	@Description("The output HashMap with the Throughfall ")
	@Out
	public HashMap<Integer, double[]> outHMThroughfall= new HashMap<Integer, double[]>() ;

	@Description("The output HashMap with the AET ")
	@Out
	public  HashMap<Integer, double[]> outHMAET= new HashMap<Integer, double[]>() ;
	
	@Description("The output HashMap with the maximum storage of the reservoir ")
	@Out
	public  HashMap<Integer, double[]> outHMStorageMax= new HashMap<Integer, double[]>() ;

	@Description("The output HashMap with the maximum storage of the reservoir ")
	@Out
	public  HashMap<Integer, double[]> outHMDrainage= new HashMap<Integer, double[]>() ;
	
	
	
	private int step;
	
	@Description("ETp: Potential evaopotranspiration value for the given time considered")
	private double ETp;
	@Description("Rainfall value for the given time considered. This is can be the melting discharge of the snow reservoir.")
	private double rainfall;
	@Description("Leaf area index value for the given time considered")
	private double LAI;
	@Description("Maximum value for the storage of the reservoir for the given time considered")
	private double storageMax;
	@Description("Drainage value for the given time considered")
	private double drainage;
	@Description("Actula evaopotranspiration value for the given time considered")
	private double ETa;
	@Description("Throughfall value for the given time considered")
	private double throughfall;
	@Description("Storage of the reservoir. The unkwnown of the reservoir.")
	private double storage;

	
	private Set<Entry<Integer, double[]>> entrySet;

	private ODE ode;

	private NewtonRaphson newton;
	
	


	/**
	 * Process: reading of the data, computation of the
	 * storage and outflows
	 *
	 * @throws Exception the exception
	 */
	@Execute
	public void process() throws Exception {

		entrySet = inHMRain.entrySet();

		for( Entry<Integer, double[]> entry : entrySet ) {
			Integer ID = entry.getKey();

			/**Input data reading*/
			rainfall = inHMRain.get(ID)[0];
			if (isNovalue(rainfall)) rainfall= 0;
			
			LAI= inHMLAI.get(ID)[0];
			if (isNovalue(LAI))	LAI=0.6;		
			LAI=(LAI==0)?0.6:LAI;
			
			ETp=0;
			if (inHMETp != null) ETp = inHMETp.get(ID)[0];
			if (isNovalue(ETp)) ETp = 0;


			if(step==0){
				if(initialConditionS_i!=null){
					storage=initialConditionS_i.get(ID)[0];
					if (isNovalue(storage)) storage= LAICoefficient*LAI/2;
				}else{
					storage=LAICoefficient*LAI/2;
				}

				ode = new ODE();
				newton = new NewtonRaphson();

			}




			storageMax =  LAICoefficient*LAI;
			ode.set(storage, rainfall, ETp, freeThroughfallCoefficient,
					 drainageRateCoefficient, drainageRateExponent,storageMax);
			storage = newton.solve(storage, ode);

			drainage = Math.max(0, Math.pow(drainageRateCoefficient*(storage-storageMax),drainageRateExponent));
			ETa = Math.min(ETp, Math.min(storage,storageMax));

			storage = Math.max(storage - drainage - ETa, 0.0);

			throughfall = freeThroughfallCoefficient*rainfall + drainage;

			outHMStorage.put(ID, new double[]{storage});
			outHMThroughfall.put(ID, new double[]{throughfall});
			outHMAET.put(ID, new double[]{ETa});
			outHMStorageMax.put(ID, new double[]{storageMax});
			outHMDrainage.put(ID, new double[]{drainage});

//			System.out.println(rainfall +"\t" + storageMax +"\t" + ETp +"\t" + storage +"\t" + drainage +"\t" + throughfall +"\t" + ETa);

		}


		step++;

	}


}