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

import it.geoframe.blogspot.numerical.ode.OrdinaryDifferentialEquation;

/**
 * @author Niccolò Tubini, Giuseppe Formetta
 * 
 */
public class ODE implements OrdinaryDifferentialEquation {

	private double initialCondition;
	private double rainfall;
	private double Ep;
	private double Qin;
	private double Qout;
	private double timeStepMinutes;
	private AreaStage areaStage;
	private WeirDischarge weirDischarge;

	
	public void set(double initialCondition, double rainfall, double Ep, double Qin,
			double Qout, double timeStepMinutes, AreaStage areaStage, WeirDischarge weirDischarge) {
		
		this.rainfall = rainfall;
		this.Ep = Ep;
		this.Qin = Qin;
		this.timeStepMinutes = timeStepMinutes;
		this.areaStage = areaStage;
		this.weirDischarge = weirDischarge;
		this.initialCondition = initialCondition;
		this.Qout = Qout;

		
	}
	
	@Override
	public double compute(double level) {
		// TODO Auto-generated method stub
		
		return areaStage.compute(level)*level/2 - (rainfall+Ep)/1000*areaStage.compute(level) + 
				weirDischarge.computeDischarge(level)*timeStepMinutes*60 - initialCondition - (Qin-Qout)*timeStepMinutes*60;

	}
	
	@Override
	public double computeDerivative(double level) {
		// TODO Auto-generated method stub
		
		return areaStage.compute(level) - (rainfall+Ep)/1000*areaStage.computeDerivative(level) + 
				weirDischarge.computeDerivativeDischarge(level)*timeStepMinutes*60;


	}

	@Override
	public double computeP(double storage) {
		// TODO Auto-generated method stub

		return 0.0;
		
	}

	@Override
	public double computePIntegral(double storage) {
		// TODO Auto-generated method stub

			return 0.0;
			
	}

	@Override
	public double computeRHS() {
		// TODO Auto-generated method stub
		return 0.0;
	}

}
