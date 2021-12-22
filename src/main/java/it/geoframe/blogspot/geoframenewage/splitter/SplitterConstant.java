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

public class SplitterConstant {

	
	@Description("")
	@In
	public double alpha;
	
	@Description("")
	@In
	public HashMap<Integer, double[]> inHMRain;	
	
	@Description("")
	@Out
	public HashMap<Integer, double[]> outHMActualRainFastReservoir = new HashMap<Integer, double[]>() ;;
	
	@Description("")
	@Out
	public HashMap<Integer, double[]> outHMActualRainSlowReservoir = new HashMap<Integer, double[]>() ;;
	
	
	
	private double rain;
	private Set<Entry<Integer, double[]>> entrySet;
	private Integer ID;
	
	
	@Execute
	public void process() {
		
		
		entrySet = inHMRain.entrySet();
		
		for( Entry<Integer, double[]> entry : entrySet ) {
			
		
			ID = entry.getKey();
		
			rain = inHMRain.get(ID)[0];
			if (isNovalue(rain)) rain = 0;
								
			outHMActualRainSlowReservoir.put(ID, new double[]{rain*alpha});
			outHMActualRainFastReservoir.put(ID, new double[]{rain*(1-alpha)});
					
		}
		
	}
	

}
