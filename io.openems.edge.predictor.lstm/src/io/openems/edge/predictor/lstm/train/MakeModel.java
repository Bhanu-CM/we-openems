package io.openems.edge.predictor.lstm.train;

import java.io.File;
import java.time.OffsetDateTime;
import java.util.ArrayList;

import io.openems.common.OpenemsConstants;
import io.openems.edge.predictor.lstm.common.DataModification;
import io.openems.edge.predictor.lstm.common.HyperParameters;
import io.openems.edge.predictor.lstm.common.ReadModels;
import io.openems.edge.predictor.lstm.common.SaveModel;
import io.openems.edge.predictor.lstm.interpolation.InterpolationManager;
import io.openems.edge.predictor.lstm.preprocessing.PreProcessingImpl;
import io.openems.edge.predictor.lstm.preprocessing.Suffle;
import io.openems.edge.predictor.lstm.preprocessing.TrainTestSplit;
import io.openems.edge.predictor.lstm.util.Engine;
import io.openems.edge.predictor.lstm.util.Engine.EngineBuilder;

public class MakeModel {
	// private String path =
	// "C:\\Users\\bishal.ghimire\\git\\Lstmforecasting\\io.openems.edge.predictor.lstm\\TestFolder\\";

	// String path = file.getAbsolutePath();

	// private Model mod1 = new Model();

	public MakeModel(ArrayList<Double> data, ArrayList<OffsetDateTime> date, HyperParameters hyperParameters) {

		// Trianing seasonality
		this.trainSeasonality(data, date, hyperParameters);
		this.trainTrend(data, date, hyperParameters);

		// mod1.setSeasonalityModel(temp1.getSeasonalityModle());
		// mod1.setTrendModle(temp2.getTrendModle());
	}

	public MakeModel() {

	}

	/**
	 * Trains a seasonality model using the provided data and parameters. *
	 * 
	 * @param data            The time series data to train the model on.
	 * @param date            The corresponding timestamps for the data. * *
	 * @param hyperParameters An instance of class HyperParameters
	 * 
	 */

	public void trainSeasonality(ArrayList<Double> data, ArrayList<OffsetDateTime> date,
			HyperParameters hyperParameters) {
		ArrayList<Double> values;
		ArrayList<OffsetDateTime> dates;
		ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> weightMatrix = new ArrayList<ArrayList<ArrayList<ArrayList<Double>>>>();
		ArrayList<ArrayList<Double>> weight1 = new ArrayList<ArrayList<Double>>();
		int windowsSize = 0;
		int k = 0;
		values = data;
		dates = date;
		windowsSize = hyperParameters.getWindowSizeSeasonality();
		// hyperParameters.setModleSuffix();
		InterpolationManager inter = new InterpolationManager(values, dates, hyperParameters);
		ArrayList<ArrayList<ArrayList<Double>>> dataGroupedByMinute = DataModification
				.modifyFroLongTermPrediction(inter.getInterpolatedData(), dates);
		// Model mod = new Model();
		/**
		 * compute model
		 */

		for (int i = 0; i < dataGroupedByMinute.size(); i++) {
			for (int j = 0; j < dataGroupedByMinute.get(i).size(); j++) {

				if (hyperParameters.getCount() == 0) {
					// mod = new Model();

					weight1 = this.generateInitialWeightMatrix(windowsSize, hyperParameters);

				} else {
					// // Reading the exsisting modle
					// try {
					// mod = (Model) Model.read();
					// } catch (ClassNotFoundException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// } catch (IOException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }

					String openemsDirectory = OpenemsConstants.getOpenemsDataDir();

					File file = new File(openemsDirectory + "/models/"
						+ Integer.toString(hyperParameters.getCount() - 1) + "seasonality.txt");
					String path = file.getAbsolutePath();
					ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> allModel = ReadModels
							.getModelForSeasonality(path, hyperParameters);
					weight1 = allModel.get(allModel.size() - 1).get(k);

					// weight1 = mod.getSeasonalityModle().get(mod.getSeasonalityAllModle().size() -
					// 1).get(k);

				}
//				System.out.println(k);
//				System.out.println("making Model for " + Integer.toString(i) + ":"
//						+ Integer.toString(j * hyperParameters.getInterval()));
				PreProcessingImpl preprocessing = new PreProcessingImpl(
						DataModification.scale(dataGroupedByMinute.get(i).get(j), hyperParameters.getScalingMin(),
								hyperParameters.getScalingMax()),
						windowsSize);
				try {
					TrainTestSplit splitIndex = new TrainTestSplit(dataGroupedByMinute.get(i).get(j).size(),
							windowsSize, hyperParameters.getDataSplitTrain(), hyperParameters.getDataSplitValidate());
					double[][] trainData = preprocessing.getFeatureData(splitIndex.getTrainLowerIndex(),
							splitIndex.getTrainUpperIndex());
					double[][] validateData = preprocessing.getFeatureData(splitIndex.getValidateLowerIndex(),
							splitIndex.getValidateUpperIndex());
					double[] trainTarget = preprocessing.getTargetData(splitIndex.getTrainLowerIndex(),
							splitIndex.getTrainUpperIndex());
					double[] validateTarget = preprocessing.getTargetData(splitIndex.getValidateLowerIndex(),
							splitIndex.getValidateUpperIndex());
					Suffle obj1 = new Suffle(trainData, trainTarget);
					Suffle obj2 = new Suffle(validateData, validateTarget);
					Engine model = new EngineBuilder() //
							.setInputMatrix(DataModification.normalizeData(obj1.getData())) //
							.setTargetVector(obj1.getTarget()) //
							.setValidateData(DataModification.normalizeData(obj2.getData())) //
							.setValidateTarget(obj2.getTarget()) //
							.build();
					model.fit(hyperParameters.getGdIterration(), weight1, hyperParameters);
					weightMatrix.add(model.getWeights());
				} catch (Exception e) {
					e.printStackTrace();
				}
				k = k + 1;
			}

		}
		SaveModel.saveModels(weightMatrix, Integer.toString(hyperParameters.getCount()) + "seasonality.txt");
		System.out.println("Modle saved as : " + "seasonality.txt");

		// mod.setSeasonalityModel(weightMatrix);
		// Model.save(mod);
		// return mod;
	}

	/**
	 * Trains a trend model using the provided data and parameters.
	 *
	 * @param data            The time series data to train the trend model on.
	 * @param date            The corresponding timestamps for the data.
	 * @param hyperParameters An instance of class HyperParameter
	 * 
	 */

	public void trainTrend(ArrayList<Double> data, ArrayList<OffsetDateTime> date, HyperParameters hyperParameters) {

		ArrayList<Double> values;
		ArrayList<OffsetDateTime> dates;
		ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> weightMatrix = new ArrayList<ArrayList<ArrayList<ArrayList<Double>>>>();
		ArrayList<ArrayList<Double>> weight1 = new ArrayList<ArrayList<Double>>();
		values = data;
		dates = date;
		// hyperParameters.setModleSuffix("trend.txt");
		InterpolationManager inter = new InterpolationManager(values, dates, hyperParameters);
		ArrayList<ArrayList<Double>> modifiedData = DataModification
				.modifyForShortTermPrediction(inter.getInterpolatedData(), dates, hyperParameters);
		// Model mod = new Model();
		for (int i = 0; i < modifiedData.size(); i++) {

			if (hyperParameters.getCount() == 0) {
				weight1 = this.generateInitialWeightMatrix(hyperParameters.getWindowSizeTrend(), hyperParameters);
				// // Reading the exsisting modle object searilized in TrainSeasonality Method
				// try {
				// mod = (Model) Model.read();
				// } catch (ClassNotFoundException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// } catch (IOException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
			} else {
				String openemsDirectory = OpenemsConstants.getOpenemsDataDir();

				File file = new File(openemsDirectory + "/models/" + Integer.toString(hyperParameters.getCount() - 1)
						+ "trend.txt");
				String path = file.getAbsolutePath();
				ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> allModel = ReadModels.getModelForSeasonality(path,
						hyperParameters);

				// Reading the exsisting modle object searilized in TrainSeasonality Method
				// try {
				// mod = (Model) Model.read();
				// } catch (ClassNotFoundException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// } catch (IOException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				weight1 = allModel.get(allModel.size() - 1).get(i);

			}
			double[][] trainData = PreProcessingImpl.groupToStiffedWindow(DataModification.scale(modifiedData.get(i),
					hyperParameters.getScalingMin(), hyperParameters.getScalingMax()),
					hyperParameters.getWindowSizeTrend());
			double[] trainTarget = PreProcessingImpl.groupToStiffedTarget(DataModification.scale(modifiedData.get(i),
					hyperParameters.getScalingMin(), hyperParameters.getScalingMax()),
					hyperParameters.getWindowSizeTrend());
			Suffle obj1 = new Suffle(trainData, trainTarget);
			Engine model = new EngineBuilder() //
					.setInputMatrix(DataModification.normalizeData(obj1.getData())) // removing normalization
					.setTargetVector(obj1.getTarget()) //
					.build();
			model.fit(hyperParameters.getGdIterration(), weight1, hyperParameters);
			weightMatrix.add(model.getWeights());
		}
		// mod.setTrendModle(weightMatrix);
		SaveModel.saveModels(weightMatrix, Integer.toString(hyperParameters.getCount()) + "trend.txt");
		System.out.println("Modle saved as : " + "trend.txt");
		// return mod;
	}

	/**
	 * Generates an initial weight matrix for a neural network with specified
	 * parameters.
	 *
	 * @param windowSize      The size of the window or context for the neural
	 *                        network.
	 * @param hyperParameters An instance of class HyperParameter
	 * @return An ArrayList of ArrayLists containing the initial weight matrix.
	 */

	public ArrayList<ArrayList<Double>> generateInitialWeightMatrix(int windowSize, HyperParameters hyperParameters) {
		ArrayList<ArrayList<Double>> initialWeight = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> temp1 = new ArrayList<Double>();
		ArrayList<Double> temp2 = new ArrayList<Double>();
		ArrayList<Double> temp3 = new ArrayList<Double>();
		ArrayList<Double> temp4 = new ArrayList<Double>();
		ArrayList<Double> temp5 = new ArrayList<Double>();
		ArrayList<Double> temp6 = new ArrayList<Double>();
		ArrayList<Double> temp7 = new ArrayList<Double>();
		ArrayList<Double> temp8 = new ArrayList<Double>();

		for (int i = 1; i <= windowSize; i++) {
			double wi = hyperParameters.getWiInit();
			double wo = hyperParameters.getWoInit();
			double wz = hyperParameters.getWzInit();
			final double ri = hyperParameters.getRiInit();
			final double ro = hyperParameters.getRoInit();
			final double rz = hyperParameters.getRzInit();
			final double ct = hyperParameters.getCtInit();
			final double yt = hyperParameters.getYtInit();

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
	// public Model getModle() {
	// return this.mod1;
	// }

}
