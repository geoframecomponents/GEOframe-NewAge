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

/**
 * @author Niccolò Tubini, Giuseppe Formetta
 * 
 */
public class WeirDischarge {
	
	double weirCoefficient;
	double weirWidth;
	double weirHeight;

	public WeirDischarge(double weirCoefficient, double weirWidth, double weirHeight) {
		
		this.weirCoefficient = weirCoefficient;
		this.weirWidth = weirWidth;
		this.weirHeight = weirHeight;
		
	}
	
	public double computeDischarge(double level) {
		
		if(level<=weirHeight) {
			
			return 0.0;
			
		} else {
			
			return weirCoefficient*weirWidth*Math.sqrt(2*9.81)*Math.pow(level-weirHeight, 3/2);
			
		}
		
	}
	
	public double computeDerivativeDischarge(double level) {
		
		if(level<=weirHeight) {
			
			return 0.0;
			
		} else {
			
			return 3/2*weirCoefficient*weirWidth*Math.sqrt(2*9.81)*Math.pow(level-weirHeight, 3/2-1);
			
		}
		
	}
	
}
