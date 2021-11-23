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

import it.geoframe.blogspot.numerical.ode.OrdinaryDifferentialEquation;

/**
 * @author Niccolò Tubini, Giuseppe Formetta
 * 
 */
public class ODE implements OrdinaryDifferentialEquation {

	private double initialCondition;
	private double rainfall;
	private double ET;
	private double freeThroughfallCoefficient;
	private double coefficientRootZoneDischarge;
	private double exponentRootZoneDischarge;
	private double storageMaxRootZone;
	
	public void set(double initialCondition, double rainfall, double ET, double coefficientRootZoneDischarge,
			double exponentRootZoneDischarge, double storageMaxRootZone) {
		
		this.initialCondition = initialCondition;
		this.rainfall = rainfall;
		this.ET = ET;
		this.coefficientRootZoneDischarge = coefficientRootZoneDischarge;
		this.exponentRootZoneDischarge = exponentRootZoneDischarge;
		this.storageMaxRootZone = storageMaxRootZone;
		
	}
	
	@Override
	public double compute(double storage) {
		// TODO Auto-generated method stub
		
		if(storage<storageMaxRootZone) {
			
			return storage + coefficientRootZoneDischarge*Math.pow(storage, exponentRootZoneDischarge) + storage/storageMaxRootZone*ET;
			
		} else {
			
			return storage + coefficientRootZoneDischarge*Math.pow(storageMaxRootZone, exponentRootZoneDischarge) + ET;
					
		}


	}
	
	@Override
	public double computeDerivative(double storage) {
		// TODO Auto-generated method stub
		
		if(storage<storageMaxRootZone) {
			
			return 1 + exponentRootZoneDischarge*coefficientRootZoneDischarge*Math.pow(storage, exponentRootZoneDischarge-1) + 1/storageMaxRootZone*ET;
			
		} else {
			
			return 1;
					
		}		

	}

	@Override
	public double computeP(double storage) {
		// TODO Auto-generated method stub
		if(storage<storageMaxRootZone) {
			
			return  1 + exponentRootZoneDischarge*coefficientRootZoneDischarge*Math.pow(storage, exponentRootZoneDischarge-1) + 1/storageMaxRootZone*ET;
			
		} else {
			
			return  1 + exponentRootZoneDischarge*coefficientRootZoneDischarge*Math.pow(storageMaxRootZone, exponentRootZoneDischarge-1) + 1/storageMaxRootZone*ET;
			
		}
		
	}

	@Override
	public double computePIntegral(double storage) {
		// TODO Auto-generated method stub
		if(storage<storageMaxRootZone) {
			
			return compute(storage);// storage + coefficientRootZoneDischarge*Math.pow(storage, exponentRootZoneDischarge) + storage/storageMaxRootZone*ET;
			
		} else {
			
			return compute(storageMaxRootZone) + computeP(storageMaxRootZone)*(storage-storageMaxRootZone);
					//storageMaxRootZone + coefficientRootZoneDischarge*Math.pow(storageMaxRootZone, exponentRootZoneDischarge) + storageMaxRootZone/storageMaxRootZone*ET +
//					computeP(storageMaxRootZone)*(storage-storageMaxRootZone);
			
		}
	}

	@Override
	public double computeRHS() {
		// TODO Auto-generated method stub
		return initialCondition + rainfall;
	}

}
