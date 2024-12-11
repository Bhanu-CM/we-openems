package io.openems.edge.predictor.weather.forecast;

import static io.openems.common.utils.DateUtils.roundDownToQuarter;
import static io.openems.edge.predictor.api.prediction.Prediction.EMPTY_PREDICTION;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.stream.Collectors;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.timedata.Resolution;
import io.openems.common.types.ChannelAddress;
import io.openems.common.types.OpenemsType;
import io.openems.edge.common.component.ClockProvider;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.sum.Sum;
import io.openems.edge.common.type.TypeUtils;
import io.openems.edge.controller.api.Controller;
import io.openems.edge.predictor.api.prediction.AbstractPredictor;
import io.openems.edge.predictor.api.prediction.Prediction;
import io.openems.edge.predictor.api.prediction.Predictor;
import io.openems.edge.timedata.api.Timedata;

@Designate(ocd = Config.class, factory = true)
@Component(//
        name = "Predictor.WeatherForecastMode", //
        immediate = true, //
        configurationPolicy = ConfigurationPolicy.REQUIRE //
)
public class PredictorWeatherForecastModelImpl extends AbstractPredictor implements Predictor, OpenemsComponent {

    private final Logger log = LoggerFactory.getLogger(PredictorWeatherForecastModelImpl.class);
    private double factor;// simple factor to multiply with short wave solar radiation, to forecast PV production

    @Reference
    private ComponentManager componentManager;

    private Config config;
    private OpenMeteoForecast openMeteoForecast;

    public PredictorWeatherForecastModelImpl() throws OpenemsNamedException {
        super(OpenemsComponent.ChannelId.values(),
                Controller.ChannelId.values(),
                PredictorWeatherForecastModel.ChannelId.values());
    }

    @Activate
    private void activate(ComponentContext context, Config config) throws Exception {
        this.config = config;
        super.activate(context, this.config.id(), this.config.alias(), this.config.enabled(),
                this.config.channelAddresses(), this.config.logVerbosity());

        this.openMeteoForecast = new OpenMeteoForecast(); // future implementation other weather forecast services can be added and selected from the config page 
        this.openMeteoForecast.fetchData(this.config.latitude(), this.config.longitude());//provide location
        this.factor = config.factor();
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

    protected Prediction createNewPrediction(ChannelAddress channelAddress) {
        try {
            Optional<List<Double>> shortWaveRadiationOpt = openMeteoForecast.getShortWaveRadiation();
            
            
           /* other weather factors can be implemeted and fetch the data used here for calculation of pv production 
            * 
            * each individual facors can be fetched separately as needed 
            */

            if (shortWaveRadiationOpt.isEmpty()) {
                return Prediction.EMPTY_PREDICTION;
            }

            List<Double> shortwaveRadiation = shortWaveRadiationOpt.get();

            if (shortwaveRadiation.size() < 192) {
                return Prediction.EMPTY_PREDICTION;
            }

            ZonedDateTime now = ZonedDateTime.now(this.componentManager.getClock());
            ZonedDateTime startOfDay = now.truncatedTo(ChronoUnit.DAYS);
            int currentIntervalIndex = (int) ChronoUnit.MINUTES.between(startOfDay, now) / 15;

            if (currentIntervalIndex >= shortwaveRadiation.size()) {
                return Prediction.EMPTY_PREDICTION;
            }
            
            /*I am taking 2 days of weather forecast data here, from url/api i am fetching forecast of 3 days , its enough for TOT controller             * 
             * api provides data from the beginning of the day , i.e. 0 OClock, so we have to take the data from current timestamp , done in the next step 
             */

            var values = new Integer[192];
            for (int i = 0; i < 192; i++) {
                int dataIndex = currentIntervalIndex + i;
                values[i] = dataIndex < shortwaveRadiation.size()
                        ? (int) Math.round(shortwaveRadiation.get(dataIndex) * this.factor)
                        : 0;
            }

            return Prediction.from(startOfDay.plusMinutes(currentIntervalIndex * 15), values);

        } catch (Exception e) {
            log.error("Error creating prediction: ", e);
            return Prediction.EMPTY_PREDICTION;
        }
    }
}