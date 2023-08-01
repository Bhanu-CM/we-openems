package io.openems.edge.predictor.lstmmodel.util;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import io.openems.edge.predictor.lstmmodel.interpolation.InterpolationManager;
import io.openems.edge.predictor.lstmmodel.model.SaveModel;
import io.openems.edge.predictor.lstmmodel.preprocessing.PreProcessingImpl;
import io.openems.edge.predictor.lstmmodel.preprocessing.ReadCsv;
import io.openems.edge.predictor.lstmmodel.preprocessing.GroupBy;
import io.openems.edge.predictor.lstmmodel.preprocessing.Suffle;
import io.openems.edge.predictor.lstmmodel.util.Engine.EngineBuilder;

/**
 * 
 * @author bishal.ghimire class to make 96 model
 *
 */
public class MakeModel {

	ArrayList<Double> values;
	ArrayList<OffsetDateTime> dates;
	ArrayList<ArrayList<ArrayList<OffsetDateTime>>> dateGroupedByMinute = new ArrayList<ArrayList<ArrayList<OffsetDateTime>>>();
	ArrayList<ArrayList<ArrayList<Double>>> dataGroupedByMinute = new ArrayList<ArrayList<ArrayList<Double>>>();
	ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> weightMatrix = new ArrayList<ArrayList<ArrayList<ArrayList<Double>>>>();
	ArrayList<ArrayList<Double>> weight1 = new ArrayList<ArrayList<Double>>();
	ArrayList<ArrayList<Double>> weight2 = new ArrayList<ArrayList<Double>>();

	/**
	 * ArrayList to store grouped data and dates according to minutes of hour e.g.
	 * the data of 2:15 pm of entire year are stored in one arraylist
	 * 
	 */
	ArrayList<ArrayList<Double>> modle = new ArrayList<ArrayList<Double>>();

	public MakeModel() {


		ReadCsv csv = new ReadCsv();
		values = csv.data;
		dates = csv.dates;
		/**
		 * compute interpolation
		 */
		InterpolationManager inter = new InterpolationManager(values); // The result of interpolation is in
																		// inter.interpolated

		/**
		 * Grouping the interpolated data by hour
		 */

		GroupBy groupAsHour = new GroupBy(inter.interpolated, dates);// The result are stored in
																		// groupAS.groupedDateByHour and
																		// groupAs.groupedDataByHour
		groupAsHour.hour();

		/**
		 * Grouping data by minute
		 */

		for (int i = 0; i < groupAsHour.groupedDataByHour.size(); i++) {

			GroupBy groupAsMinute = new GroupBy(groupAsHour.groupedDataByHour.get(i),
					groupAsHour.groupedDateByHour.get(i));
			groupAsMinute.minute();
			dataGroupedByMinute.add(groupAsMinute.groupedDataByMin);
			dateGroupedByMinute.add(groupAsMinute.groupedDateByMin);
		}
		/**
		 * compute model
		 */

		for (int i = 0; i < dataGroupedByMinute.size(); i++) {
			weight1 = generateInitialWeightMatrix(7);
			System.out.println("");
			System.out.println(i + 1 + "/" + dataGroupedByMinute.size());
			for (int j = 0; j < dataGroupedByMinute.get(i).size(); j++) {
				int windowsSize = 7;
				PreProcessingImpl preprocessing = new PreProcessingImpl(dataGroupedByMinute.get(i).get(j), windowsSize);
				preprocessing.scale(0.2, 0.8);
				try {

					double[][] trainData = preprocessing.getFeatureData(preprocessing.trainTestSplit.trainIndexLower,
							preprocessing.trainTestSplit.trainIndexHigher);

					double[][] validateData = preprocessing.getFeatureData(
							preprocessing.trainTestSplit.validateIndexLower,
							preprocessing.trainTestSplit.validateIndexHigher);

					double[] trainTarget = preprocessing.getTargetData(preprocessing.trainTestSplit.trainIndexLower,
							preprocessing.trainTestSplit.trainIndexHigher);

					double[] validateTarget = preprocessing.getTargetData(
							preprocessing.trainTestSplit.validateIndexLower,
							preprocessing.trainTestSplit.validateIndexHigher);

					Suffle obj1 = new Suffle(trainData, trainTarget);
					Suffle obj2 = new Suffle(validateData, validateTarget);

					//EngineBuilder modelTemp = new EngineBuilder();

					Engine model = new EngineBuilder() //
							.setInputMatrix(obj1.data) //
							.setTargetVector(obj1.target) //
							.setValidateData(obj2.data) //
							.setValidateTarget(obj2.target) //
							.setValidatorCounter(2500)//
							.build();

					int epochs = 500;
					model.fit(epochs, weight1);
					weight1 = model.weights.get(model.weights.size() - 1);
					
					weightMatrix.add(model.weights);
									
				}

				catch (Exception e) {
					e.printStackTrace();

				}

			}

			/**
			 * saving Model as .txt file
			 */

		}
		SaveModel.saveModels(weightMatrix);

		System.out.println("Model Saved");
	}



	public ArrayList<ArrayList<Double>> generateInitialWeightMatrix(int windowSize) {
		ArrayList<ArrayList<Double>> initialWeight = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> temp1 = new ArrayList<Double>();
		ArrayList<Double> temp2 = new ArrayList<Double>();
		ArrayList<Double> temp3 = new ArrayList<Double>();
		ArrayList<Double> temp4 = new ArrayList<Double>();
		ArrayList<Double> temp5 = new ArrayList<Double>();
		ArrayList<Double> temp6 = new ArrayList<Double>();
		ArrayList<Double> temp7 = new ArrayList<Double>();
		ArrayList<Double> temp8 = new ArrayList<Double>();

		for (int i = 1; i <= windowSize; i++)

		{
			double wi = 1.00;
			double wo = 1.00;
			double wz = 1.00;
			double ri = 1.00;
			double ro = 1.00;
			double rz = 1.00;
			double ct = 0;
			double yt = 0;

			temp1.add(wi);
			temp2.add(wo);
			temp3.add(wz);
			temp4.add(ri);
			temp5.add(ro);
			temp6.add(rz);
			temp7.add(yt);
			temp8.add(ct);

		}
		initialWeight.add(temp1);
		initialWeight.add(temp2);
		initialWeight.add(temp3);
		initialWeight.add(temp4);
		initialWeight.add(temp5);
		initialWeight.add(temp6);
		initialWeight.add(temp7);
		initialWeight.add(temp8);

		return initialWeight;

	}

}
