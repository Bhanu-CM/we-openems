package io.openems.edge.predictor.lstmmodel.predictor;

public class ScaleBack {
	
	public static double scaleBack( double scaledData,double minOfTrainingData, double maxOfTrainingData) {

		double minScaled = 0.2;// Collections.min(scaledData1);
		double maxScaled = 0.8;// Collections.max(scaledData1);
		double minOrginal = minOfTrainingData;// this value should be the minimum of training dataset
		double maxOrginal =maxOfTrainingData;// this value should be maximum of training dataset

		
			return (calc(scaledData, minScaled, maxScaled, minOrginal, maxOrginal));
			
		}
	private static double calc(double valScaled, double minScaled, double maxScaled, double minOrginal,
			double maxOrginal) {
		double orginal = ((valScaled-minScaled) * (maxOrginal-minOrginal )/ (maxScaled - minScaled)) + minOrginal;
		return orginal;

	}
}
