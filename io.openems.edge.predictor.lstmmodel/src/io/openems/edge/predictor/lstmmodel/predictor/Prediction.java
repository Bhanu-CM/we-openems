package io.openems.edge.predictor.lstmmodel.predictor;

import java.awt.Color;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;

import io.openems.edge.predictor.lstmmodel.preprocessing.GroupBy;
import io.openems.edge.common.test.Plot;
import io.openems.edge.common.test.Plot.AxisFormat;
import io.openems.edge.predictor.lstmmodel.interpolation.InterpolationManager;

public class Prediction {
	public ArrayList<Double> predictedAndScaledBack = new ArrayList<Double>();
	public ArrayList<Double> dataShouldBe = new ArrayList<Double>();
	

	public Prediction(double minOfTrainingData, double maxOfTrainingData,int weekNumber) {

		ArrayList<ArrayList<ArrayList<OffsetDateTime>>> dateGroupedByMinute = new ArrayList<ArrayList<ArrayList<OffsetDateTime>>>();
		ArrayList<ArrayList<Double>> dataGroupedByMinute1 = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<ArrayList<Double>>> dataGroupedByMinute = new ArrayList<ArrayList<ArrayList<Double>>>();
		ArrayList<Double> scaledData = new ArrayList<Double>();
		ArrayList<Double> predicted = new ArrayList<Double>();
		

		Data obj = new Data(weekNumber);
		ArrayList<Double> dataToPredict = obj.sevenDaysData;
		ArrayList<OffsetDateTime> dateToPredict = obj.sevenDayDates;

		dataShouldBe = obj.eighthDayData;

		/**
		 * Interpolate
		 */

		InterpolationManager obj1 = new InterpolationManager(dataToPredict);
		dataToPredict = obj1.interpolated;

		/**
		 * Scaling
		 */

		Preprocessing obj4 = new Preprocessing(dataToPredict);
		obj4.scale();
		scaledData = obj4.scaledData;
		/**
		 * Grouping data by hour
		 */
		GroupBy obj2 = new GroupBy(scaledData, dateToPredict);

		obj2.hour();

		/**
		 * Grouping data by minute
		 */

		for (int i = 0; i < obj2.groupedDataByHour.size(); i++) {

			GroupBy obj3 = new GroupBy(obj2.groupedDataByHour.get(i), obj2.groupedDateByHour.get(i));
			obj3.minute();
			dataGroupedByMinute.add(obj3.groupedDataByMin);
			dateGroupedByMinute.add(obj3.groupedDateByMin);

		}

		for (int i = 0; i < dataGroupedByMinute.size(); i++) {
			for (int j = 0; j < dataGroupedByMinute.get(i).size(); j++) {
				dataGroupedByMinute1.add(dataGroupedByMinute.get(i).get(j));

			}

		}

		/**
		 * Make prediction
		 */
		
		predicted = Predictor.Predict(dataGroupedByMinute1, ReadModels.allModel.get(ReadModels.allModel.size() - 1));
		


		/**
		 * scale back //
		 */

		for (int i = 0; i < predicted.size(); i++) {

			predictedAndScaledBack
					.add(ScaleBack.scaleBack( predicted.get(i), minOfTrainingData, maxOfTrainingData));

		}

		

	}
	public static void makePlot(ArrayList<Double> predictedValues, ArrayList<Double> p,int weekNumber) {
		Plot.Data dataActualValues = Plot.data();
		Plot.Data dataPredictedValues = Plot.data();

		for (int i = 0; i < 96; i++) {
			dataActualValues.xy(i, predictedValues.get(i));
			dataPredictedValues.xy(i, p.get(i));
		}

		Plot plot = Plot.plot(//
				Plot.plotOpts() //
						.title("Prediction Charts for week "+ weekNumber) //
						.legend(Plot.LegendFormat.BOTTOM)) //
				.xAxis("x, every 15min data for a day", Plot.axisOpts() //
						.format(AxisFormat.NUMBER_INT) //
						.range(0, 96)) //
				.yAxis("y, Kilo Watts ", Plot.axisOpts() //
						.format(AxisFormat.NUMBER_INT)) //
				.series("Actual", dataActualValues, Plot.seriesOpts() //
						.color(Color.BLACK)) //
				.series("Prediction", dataPredictedValues, Plot.seriesOpts() //
						.color(Color.RED)); //

		try {
			String path = "./testResults";
			plot.save(path + "/prediction", "png");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
}
