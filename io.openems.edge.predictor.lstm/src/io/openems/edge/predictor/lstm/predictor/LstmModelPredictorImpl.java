package io.openems.edge.predictor.lstm.predictor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
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

		/// var nowDate = ZonedDateTime.now(this.componentManager.getClock());
		// From now time to Last 4 weeks
		// var fromDate = nowDate.minus(this.config.numOfWeeks(), ChronoUnit.WEEKS);

		// ZonedDateTime nowDate = ZonedDateTime.now();
		ZonedDateTime nowDate = ZonedDateTime.of(2023, 6, 24, 0, 0, 0, 0, ZonedDateTime.now().getZone());
		ZonedDateTime till = ZonedDateTime.of(nowDate.getYear(), nowDate.getMonthValue(), //
				nowDate.minusDays(1).getDayOfMonth(), 23, 45, 0, 0, nowDate.getZone());
		ZonedDateTime temp = till.minusDays(6);
		ZonedDateTime fromDate = ZonedDateTime.of(temp.getYear(), temp.getMonthValue(), temp.getDayOfMonth(), 0, 0, 0,
				0, //
				temp.getZone());

		System.out.println("From : " + fromDate);

		System.out.println("Till : " + till);

		// TEMP

		// Extract data

//		DataQuerry predictionData = new  DataQuerry(fromDate, nowDate,15,timedata);
//		
//		//get date
//
//		
//		System.out.println(predictionData.date.size());
//		
//		
//	
//		
//		//data conversion
//		//make 96datepoint prediction
//		double minOfTrainingData=Collections.max((ArrayList<Double>) predictionData.data);
//		double maxOfTrainingData =Collections.min((ArrayList<Double>) predictionData.data);
//		System.out.println("min of train data "+minOfTrainingData);
//		
//		Prediction obj =new Prediction((ArrayList<Double>) predictionData.data,predictionData.date,minOfTrainingData,maxOfTrainingData);
//		
//		System.out.println("Predicted "+obj.predictedAndScaledBack);
//		System.out.println("Target "+new  DataQuerry(ZonedDateTime.of(2023, 6,25,0,0,0,0,ZonedDateTime.now().getZone()),ZonedDateTime.of(2023, 6,26,0,0,0,0,ZonedDateTime.now().getZone()),15,timedata).data);
//		
//		Prediction.makePlot(obj.predictedAndScaledBack, new  DataQuerry(ZonedDateTime.of(2023, 6,7,0,0,0,0,ZonedDateTime.now().getZone()),ZonedDateTime.of(2023, 6,8,0,0,0,0,ZonedDateTime.now().getZone()),15,timedata).data, 0);
//		

		return null;

	}

//	@Override
//	  public void handleEvent(Event event) {
//        if (!this.isEnabled()) {
//            return;
//        }
//        switch (event.getTopic()) {
//       
//        }
//    }

}
