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

package it.geoframe.blogspot.geoframenewage.splitter;

import static org.hortonmachine.gears.libs.modules.HMConstants.isNovalue;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;

/**
 * @author Niccolò Tubini, Giuseppe Formetta
 * 
 */

public class SplitterThresholdOnStorage {

	
	@Description("")
	@In
	public double thresholdOnStorage;
	
	@Description("")
	@In
	public HashMap<Integer, double[]> inHMRain;	
	
	@Description("")
	@In
	public HashMap<Integer, double[]> inHMStorage;	
	
	@Description("")
	@Out
	public HashMap<Integer, double[]> inHMActualRainFastReservoir;
	
	@Description("")
	@Out
	public HashMap<Integer, double[]> inHMActualRainSlowReservoir;
	
	@Description("")
	@Out
	public HashMap<Integer, double[]> outHMStorageAlpha;
	
	private double rain;
	private double storage;
	private double alpha;
	private Set<Entry<Integer, double[]>> entrySet;
	private Integer ID;
	
	
	@Execute
	public void process() {
		
		
		entrySet = inHMRain.entrySet();
		
		for( Entry<Integer, double[]> entry : entrySet ) {
			
		
			ID = entry.getKey();
		
			rain = inHMRain.get(ID)[0];
			if (isNovalue(rain)) rain = 0;
								
			storage = inHMStorage.get(ID)[0];
			if (isNovalue(storage)) storage = 0;
			
			if(storage>thresholdOnStorage) {
				
				alpha = 0;
				
			} else {
				
				alpha = 1;
				
			}
			
			inHMActualRainSlowReservoir.put(ID, new double[]{rain*alpha});
			inHMActualRainFastReservoir.put(ID, new double[]{rain*(1-alpha)});
			outHMStorageAlpha.put(0, new double[] {storage});
			outHMStorageAlpha.put(1, new double[] {alpha});
					
		}
		
	}
	

}
