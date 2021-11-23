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

package it.geoframe.blogspot.geoframenewage.groundwater;

import it.geoframe.blogspot.numerical.ode.OrdinaryDifferentialEquation;

/**
 * @author Niccolò Tubini, Giuseppe Formetta
 * 
 */
public class ODE implements OrdinaryDifferentialEquation {

	private double initialCondition;
	private double rainfall;
	private double coefficientRunoffDischarge;
	private double exponentRunoffDischarge;
	private double storageMaxRunoff;
	
	public void set(double initialCondition, double rainfall, double coefficientRunoffDischarge,
			double exponentRunoffDischarge, double storageMaxRunoff) {
		
		this.initialCondition = initialCondition;
		this.rainfall = rainfall;
		this.coefficientRunoffDischarge = coefficientRunoffDischarge;
		this.exponentRunoffDischarge = exponentRunoffDischarge;
		this.storageMaxRunoff = storageMaxRunoff;
		
	}
	
	@Override
	public double compute(double storage) {
		// TODO Auto-generated method stub

		if(storage<storageMaxRunoff) {
			
			return storage + coefficientRunoffDischarge*Math.pow(storage, exponentRunoffDischarge);
			
		} else {
			
			return storage + coefficientRunoffDischarge*Math.pow(storageMaxRunoff, exponentRunoffDischarge);
					
		}

	}
	
	@Override
	public double computeDerivative(double storage) {
		// TODO Auto-generated method stub
		
		if(storage<storageMaxRunoff) {
			
			return 1 + exponentRunoffDischarge*coefficientRunoffDischarge*Math.pow(storage, exponentRunoffDischarge-1);
			
		} else {
			
			return 1 ;
					
		}		

	}

	@Override
	public double computeP(double storage) {
		// TODO Auto-generated method stub
		if(storage<storageMaxRunoff) {
			
			return  1 + exponentRunoffDischarge*coefficientRunoffDischarge*Math.pow(storage, exponentRunoffDischarge-1);
			
		} else {
			
			return  1 + exponentRunoffDischarge*coefficientRunoffDischarge*Math.pow(storageMaxRunoff, exponentRunoffDischarge-1);
			
		}
		
	}

	@Override
	public double computePIntegral(double storage) {
		// TODO Auto-generated method stub
		if(storage<storageMaxRunoff) {
			
			return  compute(storage);
			
		} else {
			
			return  compute(storageMaxRunoff) + computeP(storageMaxRunoff)*(storage-storageMaxRunoff);
			
		}
	}

	@Override
	public double computeRHS() {
		// TODO Auto-generated method stub
		return initialCondition + rainfall;
	}

}
