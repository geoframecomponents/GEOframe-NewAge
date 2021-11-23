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

package it.geoframe.blogspot.geoframenewage.canopy;

import it.geoframe.blogspot.numerical.ode.OrdinaryDifferentialEquation;

/**
 * @author Niccolò Tubini, Giuseppe Formetta
 * 
 */
public class ODE implements OrdinaryDifferentialEquation {

	private double initialCondition;
	private double rainfall;
	private double ETp;
	private double freeThroughfallCoefficient;
	private double drainageRateCoefficient;
	private double drainageRateExponent;
	private double storageMax;
	
	public void set(double initialCondition, double rainfall, double ETp, double freeThroughfallCoefficient,
			double drainageRateCoefficient, double drainageRateExponent, double storageMax) {
		
		this.initialCondition = initialCondition;
		this.rainfall = rainfall;
		this.ETp = ETp;
		this.freeThroughfallCoefficient = freeThroughfallCoefficient;
		this.drainageRateCoefficient = drainageRateCoefficient;
		this.drainageRateExponent = drainageRateExponent;
		this.storageMax = storageMax;
		
	}
	
	@Override
	public double compute(double storage) {
		// TODO Auto-generated method stub
		
//		return Math.min( 0,  Math.min(storage,storageMax) -  Math.min( Math.min(storage,storageMax),ETp) ) + Math.max(0, Math.pow(drainageRateCoefficient*(storage-storageMax),drainageRateExponent))
//		+ Math.min(ETp, Math.min(storage,storageMax)) - initialCondition - (1-freeThroughfallCoefficient)*rainfall;
//		
		return storage - initialCondition - (1-freeThroughfallCoefficient)*rainfall;

	}
	
	@Override
	public double computeDerivative(double storage) {
		// TODO Auto-generated method stub
		
		return 1;
		
	}

	@Override
	public double computeP(double x) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double computePIntegral(double x) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double computeRHS() {
		// TODO Auto-generated method stub
		return 0;
	}

}
