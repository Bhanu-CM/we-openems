package io.openems.edge.predictor.lstm.predictor;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.timedata.Resolution;
import io.openems.common.types.ChannelAddress;
import io.openems.edge.common.component.ClockProvider;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.controller.api.Controller;
import io.openems.edge.predictor.api.oneday.AbstractPredictor24Hours;
import io.openems.edge.predictor.api.oneday.Prediction24Hours;
import io.openems.edge.predictor.api.oneday.Predictor24Hours;
import io.openems.edge.predictor.lstm.utilities.UtilityConversion;
import io.openems.edge.timedata.api.Timedata;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "Lstm.Model.predictor", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE //
)
public class LstmModelPredictorImpl extends AbstractPredictor24Hours
		implements Predictor24Hours, OpenemsComponent /* , org.osgi.service.event.EventHandler */ {

	// private final Logger log = LoggerFactory.getLogger(LstmPredictorImpl.class);

	public static final Function<List<Integer>, List<Double>> INTEGER_TO_DOUBLE_LIST = UtilityConversion::convertListIntegerToListDouble;
	public static final Function<ArrayList<Double>, Integer[]> Array_List_Double_To_Integer_Array = UtilityConversion::convertDoubleToIntegerArray;

	@Reference
	private Timedata timedata;

	protected Config config;

	@Reference
	private ComponentManager componentManager;

	public LstmModelPredictorImpl() throws OpenemsNamedException {
		super(//
				OpenemsComponent.ChannelId.values(), //
				Controller.ChannelId.values(), //
				LstmModelPredictor.ChannelId.values() //

		);
	}

	@Activate
	protected void activate(ComponentContext context, Config config) throws OpenemsNamedException {
		this.config = config;
		super.activate(context, this.config.id(), this.config.alias(), this.config.enabled(),
				this.config.channelAddresses());
	}

	@Override
	@Deactivate
	protected void deactivate() {
		super.deactivate();
	}

	@Override
	protected ClockProvider getClockProvider() {
		return this.componentManager;
	}

	@Override
	protected Prediction24Hours createNewPrediction(ChannelAddress channelAddress) {

		ZonedDateTime nowDate = ZonedDateTime.of(2023, 6, 2, 0, 0, 0, 0, ZonedDateTime.now().getZone());
		ZonedDateTime until = ZonedDateTime.of(nowDate.getYear(), nowDate.getMonthValue(), //
				nowDate.minusDays(1).getDayOfMonth(), 11, 45, 0, 0, nowDate.getZone());

		ZonedDateTime temp = until.minusDays(6);
		ZonedDateTime fromDate = ZonedDateTime.of(temp.getYear(), temp.getMonthValue(), temp.getDayOfMonth(), 0, 0, 0,
				0, temp.getZone());

		System.out.println("From : " + fromDate);
		System.out.println("Till : " + until);

		SortedMap<ZonedDateTime, SortedMap<ChannelAddress, JsonElement>> querryResult = new TreeMap<ZonedDateTime, SortedMap<ChannelAddress, JsonElement>>();

		try {
			querryResult = timedata.queryHistoricData(null, fromDate, until, Sets.newHashSet(channelAddress),
					new Resolution(15, ChronoUnit.MINUTES));
		} catch (OpenemsNamedException e) {

			e.printStackTrace();
		}

		ArrayList<Double> data = this.getData(querryResult);
		ArrayList<OffsetDateTime> date = this.getDate(querryResult);

		Prediction prediction = new Prediction(data, date);
		System.out.println(prediction.predicted);
		System.out.println(prediction.predictedAndScaledBack);

		Integer[] x = Array_List_Double_To_Integer_Array.apply(prediction.predictedAndScaledBack);

		Stream.of(x).forEach(System.out::println);

		return new Prediction24Hours(Array_List_Double_To_Integer_Array.apply(prediction.predicted));
	}

	public ArrayList<Double> getData(SortedMap<ZonedDateTime, SortedMap<ChannelAddress, JsonElement>> querryResult) {

		ArrayList<Double> data = (ArrayList<Double>) querryResult.values().stream() //
				.map(SortedMap::values) //
				.flatMap(Collection::stream) //
				.map(v -> {
					if (v.isJsonNull()) {
						return null;
					}
					return v.getAsDouble();
				}).collect(Collectors.toList());

		if (isAllNulls(data)) {
			System.out.println("Data is all null, use different predictor");
		}
		return data;
	}

	public ArrayList<OffsetDateTime> getDate(
			SortedMap<ZonedDateTime, SortedMap<ChannelAddress, JsonElement>> querryResult) {

		ArrayList<OffsetDateTime> date = new ArrayList<OffsetDateTime>();

		querryResult.keySet()//
				.stream()//
				.forEach(zonedDateTime -> {
					date.add(zonedDateTime.toOffsetDateTime());
				});
		return date;
	}

	private boolean isAllNulls(ArrayList<Double> array) {
		return StreamSupport //
				.stream(array.spliterator(), true) //
				.allMatch(o -> o == null);
	}
}
