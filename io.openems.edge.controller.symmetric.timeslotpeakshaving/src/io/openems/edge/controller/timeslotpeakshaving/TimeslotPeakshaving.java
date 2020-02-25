package io.openems.edge.controller.timeslotpeakshaving;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.exceptions.OpenemsException;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.sum.GridMode;
import io.openems.edge.controller.api.Controller;
import io.openems.edge.ess.api.ManagedSymmetricEss;
import io.openems.edge.ess.power.api.Phase;
import io.openems.edge.ess.power.api.Pwr;
import io.openems.edge.meter.api.SymmetricMeter;

@Designate(ocd = Config.class, factory = true)
@Component(name = "Controller.TimeslotPeakshaving", immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class TimeslotPeakshaving extends AbstractOpenemsComponent implements Controller, OpenemsComponent {

	public static final String TIME_FORMAT = "HH:mm";
	public static final String DATE_FORMAT = "dd.MM.yyyy";

	private final Logger log = LoggerFactory.getLogger(TimeslotPeakshaving.class);
	private final Clock clock;

	@Reference
	protected ComponentManager componentManager;

	private Config config;
	private LocalDate startDate;
	private LocalDate endDate;
	private LocalTime startTime;
	private LocalTime endTime;
	private LocalTime slowStartTime;
	private int forceChargeMinutes;
	private ChargeState chargeState = ChargeState.NORMAL;

	public enum ChannelId implements io.openems.edge.common.channel.ChannelId {
		;
		private final Doc doc;

		private ChannelId(Doc doc) {
			this.doc = doc;
		}

		@Override
		public Doc doc() {
			return this.doc;
		}
	}

	public TimeslotPeakshaving() {
		this(Clock.systemDefaultZone());
	}

	protected TimeslotPeakshaving(Clock clock) {
		super(//
				OpenemsComponent.ChannelId.values(), //
				Controller.ChannelId.values(), //
				ChannelId.values() //
		);
		this.clock = clock;
	}

	@Activate
	void activate(ComponentContext context, Config config) throws OpenemsException {
		this.startDate = convertDate(config.startDate());
		this.endDate = convertDate(config.endDate());
		this.startTime = convertTime(config.startTime());
		this.endTime = convertTime(config.endTime());
		this.slowStartTime = convertTime(config.slowChargeStartTime());
		this.forceChargeMinutes = calculateForceChargeMinutes(this.slowStartTime, this.startTime);
		this.config = config;

		super.activate(context, config.id(), config.alias(), config.enabled());
	}

	@Deactivate
	protected void deactivate() {
		super.deactivate();
	}

	@Override
	public void run() throws OpenemsNamedException {
		ManagedSymmetricEss ess = this.componentManager.getComponent(this.config.ess());
		SymmetricMeter meter = this.componentManager.getComponent(this.config.meter_id());

		int power = this.getPower(ess, meter);
		this.applyPower(ess, power);
	}

	/**
	 * Applies the power on the Ess
	 * 
	 * @param ess      managedSymmetricEss where the power needs to be set
	 * @param pidOuput the power to be set on ess
	 * @throws OpenemsNamedException
	 */
	private void applyPower(ManagedSymmetricEss ess, int activePower) throws OpenemsNamedException {
		ess.getSetActivePowerEqualsWithPid().setNextWriteValue(activePower);
		ess.getSetReactivePowerEquals().setNextWriteValue(0);
	}

	/**
	 * Gets the current ActivePower.
	 * 
	 * @return
	 * @throws IllegalArgumentException
	 * @throws OpenemsException
	 */
	private int getPower(ManagedSymmetricEss ess, SymmetricMeter meter)
			throws OpenemsException, IllegalArgumentException {

		LocalDateTime now = LocalDateTime.now(this.clock);
		int activePower = this.calculatePeakShavePower(ess, meter);

		if (this.isHighLoadTimeslot(now)) {
			/*
			 * We are in a High-Load period -> peak shave and discharge/ charge and
			 * hysteresis "soc" within high load timeslot
			 */
			if (ess.getSoc().value().orElse(0) >= config.hysteresisSoc()) {
				this.logInfo(log, "SoC [" + ess.getSoc().value().orElse(0) + " >= " + config.hysteresisSoc()
						+ "]. Switch to Charge-Normal state.");
				this.chargeState = ChargeState.HYSTERESIS;
				return 0;
			}
			this.chargeState = ChargeState.NORMAL;
			this.logInfo(log, "Within High-Load timeslot. charge with [" + activePower + "]");
			return activePower;
		} else if (this.isHighLoadTimeslot(now.plusMinutes(this.forceChargeMinutes))) {
			/*
			 * We are soon going to be in High-Load period -> activate FORCE_CHARGE mode
			 */
			this.logInfo(log, " We are soon going to be in High-Load period ");
			this.chargeState = ChargeState.FORCE_CHARGE;
		}
		/*
		 * We are in a Charge period
		 */
		switch (this.chargeState) {
		case NORMAL:
			/*
			 * charge with configured charge-power
			 */
			this.logInfo(log, "Outside High-Load timeslot. Charge with [" + config.slowChargePower() + "]");
			int minPower = ess.getPower().getMinPower(ess, Phase.ALL, Pwr.ACTIVE);
			if (minPower >= 0) {
				this.logInfo(log, "Min-Power [" + minPower + " >= 0]. Switch to Charge-Hystereses state.");
				// activate Charge-hysteresis if no charge power (i.e. >= 0) is allowed
				this.chargeState = ChargeState.HYSTERESIS;
			}
			return activePower;
		case HYSTERESIS:
			/*
			 * block charging till configured hysteresisSoc
			 */
			this.logInfo(log, "Outside High-Load timeslot. Charge-Hysteresis-Mode: Block charging.");
			if (ess.getSoc().value().orElse(0) <= config.hysteresisSoc()) {
				this.logInfo(log, "SoC [" + ess.getSoc().value().orElse(0) + " <= " + config.hysteresisSoc()
						+ "]. Switch to Charge-Normal state.");
				this.chargeState = ChargeState.NORMAL;
			}
			return 0;
		case FORCE_CHARGE:
			/*
			 * force full charging just before the high-load timeslot starts
			 */
			this.logInfo(log, "Just before High-Load timeslot. Charge with [" + config.slowChargePower() + "]");
			return config.slowChargePower();
		}
		// we should never come here...
		return 0;
	}

	/**
	 * This method calculates the power that is required to cut the peak during
	 * timeslot.
	 * 
	 * @param ess
	 * @param meter
	 * @return activepower to be set on the ess
	 */
	private int calculatePeakShavePower(ManagedSymmetricEss ess, SymmetricMeter meter) {
		/*
		 * Check that we are On-Grid (and warn on undefined Grid-Mode)
		 */
		GridMode gridMode = ess.getGridMode().value().asEnum();
		if (gridMode.isUndefined()) {
			this.logWarn(this.log, "Grid-Mode is [UNDEFINED]");
		}

		switch (gridMode) {
		case ON_GRID:
		case UNDEFINED:
			break;
		case OFF_GRID:
			return 0;
		}

		// Calculate 'real' grid-power (without current ESS charge/discharge)
		int gridPower = meter.getActivePower().value().orElse(0) /* current buy-from/sell-to grid */
				+ ess.getActivePower().value().orElse(0) /* current charge/discharge Ess */;

		int calculatedPower;
		if (gridPower >= config.peakShavingPower()) {
			/*
			 * Peak-Shaving
			 */
			calculatedPower = gridPower -= config.peakShavingPower();

		} else if (gridPower <= config.rechargePower()) {
			/*
			 * Recharge
			 */
			calculatedPower = gridPower -= config.rechargePower();

		} else {
			/*
			 * Do nothing
			 */
			calculatedPower = 0;
		}

		return calculatedPower;
	}

	/**
	 * Is the current time in a high-load timeslot?
	 * 
	 * @param dateTime
	 * 
	 * @return boolean specifying the within timeslot or not
	 * @throws OpenemsException
	 */
	private boolean isHighLoadTimeslot(LocalDateTime dateTime) throws OpenemsException {

		if (!isConfiguredActiveDay(this.config)) {
			return false;
		}
		if (!isActiveDate(this.startDate, this.endDate, dateTime)) {
			return false;
		}
		if (!isActiveTime(this.startTime, this.endTime, dateTime)) {
			return false;
		}
		// all tests passed
		return true;
	}

	/**
	 * Is "day" configured to run algorithm?
	 * 
	 * @param config
	 * @return configuredDay boolean value specifying the day is set or not.
	 */
	private static boolean isConfiguredActiveDay(Config config) {

		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		boolean configuredDay = false;

		switch (day) {
		case Calendar.SUNDAY:
			if (config.sunday()) {
				configuredDay = true;
			}
			break;
		case Calendar.MONDAY:
			if (config.monday()) {
				configuredDay = true;
			}
			break;
		case Calendar.TUESDAY:
			if (config.tuesday()) {
				configuredDay = true;
			}
			break;
		case Calendar.WEDNESDAY:
			if (config.wednesday()) {
				configuredDay = true;
			}
			break;
		case Calendar.THURSDAY:
			if (config.thursday()) {
				configuredDay = true;
			}
			break;
		case Calendar.FRIDAY:
			if (config.friday()) {
				configuredDay = true;
			}
			break;
		case Calendar.SATURDAY:
			if (config.saturday()) {
				configuredDay = true;
			}
			break;
		}
		return configuredDay;
	}

	protected static boolean isActiveDate(LocalDate startDate, LocalDate endDate, LocalDateTime dateTime) {
		LocalDate date = dateTime.toLocalDate();
		return !(date.isBefore(startDate) || date.isAfter(endDate));
	}

	/**
	 * Is the time of 'dateTime' within startTime and endTime?
	 * 
	 * @param startTime
	 * @param endTime
	 * @param dateTime
	 * @return
	 */
	protected static boolean isActiveTime(LocalTime startTime, LocalTime endTime, LocalDateTime dateTime) {
		LocalTime time = dateTime.toLocalTime();
		return !(time.isBefore(startTime) || time.isAfter(endTime));
	}

	/**
	 * Is 'dateTime' a Saturday or Sunday?
	 * 
	 * @param dateTime
	 * @return
	 */
	protected static boolean isWeekend(LocalDateTime dateTime) {
		DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
		return (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);
	}

	/**
	 * Converts a string to a LocalDate.
	 * 
	 * @param date
	 * @return
	 */
	protected static LocalDate convertDate(String date) throws OpenemsException {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
		LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
		return localDate;
	}

	/**
	 * Converts a string to a LocalTime.
	 * 
	 * @param time
	 * @return
	 */
	protected static LocalTime convertTime(String time) throws OpenemsException {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
		LocalTime localDate = LocalTime.parse(time, dateTimeFormatter);
		return localDate;
	}

	/**
	 * 
	 * 
	 * @param slowStartTime
	 * @param startTime
	 * @return
	 */
	private static int calculateForceChargeMinutes(LocalTime slowStartTime, LocalTime startTime) {
		int forceChargeTime = (int) ChronoUnit.MINUTES.between(slowStartTime, startTime);
		if (forceChargeTime > 0) {
			return forceChargeTime;
		} else {
			return forceChargeTime + 1440; // 1440 - total minutes in a day
		}
	}

}