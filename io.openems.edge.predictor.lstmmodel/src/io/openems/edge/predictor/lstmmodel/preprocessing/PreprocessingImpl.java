package io.openems.edge.predictor.lstmmodel.preprocessing;

import static io.openems.edge.predictor.lstmmodel.utilities.SlidingWindowSpliterator.windowed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.openems.edge.predictor.lstmmodel.utilities.UtilityConversion;

public class PreprocessingImpl implements PreProcessing {

	public static final Function<double[], ArrayList<Double>> DOUBLE_TO_DOUBLE_ARRAYLIST = UtilityConversion::doubleToArrayListDouble;
	public static final Function<List<List<Double>>, double[][]> DOUBLE_TO_DOUBLE_LIST = UtilityConversion::convert2DArrayListTo2DArray;

	private double max = 0;
	private double min = 0;
	private int windowSize = 24;

	private ArrayList<Double> dataList;
	private ArrayList<Double> scaleDataList;

	public TrainTestSplit trainTestSplit;

	public double[][] trainData;
	public double[][] validateData;
	public double[][] testData;

	public double[] trainTarget;
	public double[] validateTarget;
	public double[] testTarget;

	public PreprocessingImpl(List<Double> data, int windowSize) {
		this.dataList = (ArrayList<Double>) data;
		this.windowSize = windowSize;

		this.max = Collections.max(this.dataList);
		this.min = Collections.min(this.dataList);
		// TODO make percentage dynamic
		this.trainTestSplit = new TrainTestSplit(data.size(), windowSize, 0.7);
	}

	/**
	 * Gets the feature data.
	 * 
	 * @param lower lowest index of the data list
	 * @param upper upper index of the data list
	 * @return featureData featureData for model training.
	 * @throws Exception when the scaleDatalist is empty
	 */
	public double[][] getFeatureData(int lower, int upper) throws Exception {

		if (this.scaleDataList.isEmpty()) {
			throw new Exception("Scaled data is empty");
		}

		double[] subArr = IntStream.range(lower, upper) //
				.mapToDouble(index -> this.scaleDataList.get(index)) //
				.toArray();

		List<List<Double>> res = windowed(DOUBLE_TO_DOUBLE_ARRAYLIST.apply(subArr), this.windowSize) //
				.map(s -> s.collect(Collectors.toList())) //
				.collect(Collectors.toList());

		return DOUBLE_TO_DOUBLE_LIST.apply(res);

	}

	/**
	 * Gets the target data.
	 * 
	 * @param lower lowest index of the data list
	 * @param upper upper index of the data list
	 * @return targetData targetDataList for model training.
	 * @throws Exception when the scaleDatalist is empty
	 */
	public double[] getTargetData(int lower, int upper) throws Exception {

		if (this.scaleDataList.isEmpty()) {
			throw new Exception("Scaled data is empty");
		}

		double[] subArr = IntStream.range(lower + this.windowSize, upper + 1) //
				.mapToDouble(index -> this.scaleDataList.get(index)) //
				.toArray();

		return subArr;
	}

	/**
	 * Scale the Data with min and max values of the list.
	 * 
	 * @param minScaled minimum scale
	 * @param maxScaled maximum scale
	 */
	public void scale(double minScaled, double maxScaled) {

		double scaleFactor = maxScaled - minScaled;

		this.scaleDataList = (ArrayList<Double>) this.dataList.stream() //
				.map(item -> (((item - this.min) / this.max) * (scaleFactor)) + minScaled) //
				.collect(Collectors.toList());
	}

	/**
	 * Reverse Scale the Data with min and max values of the list.
	 * 
	 * @param minScaled minimum scale
	 * @param maxScaled maximum scale
	 * @param data      list to be reverse scaled
	 * @return result Integer array of reverse scaled data
	 */
	public Integer[] reverseScale(double minScaled, double maxScaled, double[] data) {

		double scaleFactor = maxScaled - minScaled;

		List<Integer> second = DOUBLE_TO_DOUBLE_ARRAYLIST.apply(data) //
				.stream() //
				.map(item -> (((item * this.max) / (scaleFactor)) + this.min)) //
				.map(p -> p.intValue()) //
				.collect(Collectors.toList());

		return second.stream() //
				.toArray(Integer[]::new);

	}

}