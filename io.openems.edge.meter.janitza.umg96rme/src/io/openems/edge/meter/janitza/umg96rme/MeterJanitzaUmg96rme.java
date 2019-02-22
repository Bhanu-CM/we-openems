package io.openems.edge.meter.janitza.umg96rme;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.metatype.annotations.Designate;

import io.openems.edge.bridge.modbus.api.AbstractOpenemsModbusComponent;
import io.openems.edge.bridge.modbus.api.BridgeModbus;
import io.openems.edge.bridge.modbus.api.ElementToChannelConverter;
import io.openems.edge.bridge.modbus.api.ModbusProtocol;
import io.openems.edge.bridge.modbus.api.element.DummyRegisterElement;
import io.openems.edge.bridge.modbus.api.element.FloatDoublewordElement;
import io.openems.edge.bridge.modbus.api.task.FC3ReadRegistersTask;
import io.openems.edge.common.channel.doc.Doc;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.taskmanager.Priority;
import io.openems.edge.meter.api.AsymmetricMeter;
import io.openems.edge.meter.api.MeterType;
import io.openems.edge.meter.api.SymmetricMeter;

/**
 * Implements the Janitza UMG 96RM-E power analyser
 * 
 * https://www.janitza.com/umg-96rm-e.html
 * 
 * @author stefan.feilmeier
 *
 */
@Designate(ocd = Config.class, factory = true)
@Component(name = "Meter.Janitza.UMG96RME", immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class MeterJanitzaUmg96rme extends AbstractOpenemsModbusComponent
		implements SymmetricMeter, AsymmetricMeter, OpenemsComponent {

	private MeterType meterType = MeterType.PRODUCTION;

	/*
	 * Invert power values
	 */
	private boolean invert = false;

	@Reference
	protected ConfigurationAdmin cm;

	public MeterJanitzaUmg96rme() {
		Utils.initializeChannels(this).forEach(channel -> this.addChannel(channel));
	}

	@Reference(policy = ReferencePolicy.STATIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MANDATORY)
	protected void setModbus(BridgeModbus modbus) {
		super.setModbus(modbus);
	}

	@Activate
	void activate(ComponentContext context, Config config) {
		this.meterType = config.type();
		this.invert = config.invert();

		super.activate(context, config.id(), config.enabled(), config.modbusUnitId(), this.cm,
				"Modbus", config.modbus_id());
	}

	@Deactivate
	protected void deactivate() {
		super.deactivate();
	}

	public enum ChannelId implements io.openems.edge.common.channel.doc.ChannelId {
		;
		private final Doc doc;

		private ChannelId(Doc doc) {
			this.doc = doc;
		}

		public Doc doc() {
			return this.doc;
		}
	}

	@Override
	public MeterType getMeterType() {
		return this.meterType;
	}

	@Override
	protected ModbusProtocol defineModbusProtocol() {
		/*
		 * We are using the FLOAT registers from the modbus table, because they are all
		 * reachable within one ReadMultipleRegistersRequest.
		 */
		return new ModbusProtocol(this, //
				new FC3ReadRegistersTask(800, Priority.HIGH, //
						m(SymmetricMeter.ChannelId.FREQUENCY, new FloatDoublewordElement(800),
								ElementToChannelConverter.SCALE_FACTOR_3),
						new DummyRegisterElement(802, 807), //
						cm(new FloatDoublewordElement(808)) //
								.m(AsymmetricMeter.ChannelId.VOLTAGE_L1, ElementToChannelConverter.SCALE_FACTOR_3) //
								.m(SymmetricMeter.ChannelId.VOLTAGE, ElementToChannelConverter.SCALE_FACTOR_3) //
								.build(), //
						m(AsymmetricMeter.ChannelId.VOLTAGE_L2, new FloatDoublewordElement(810),
								ElementToChannelConverter.SCALE_FACTOR_3),
						m(AsymmetricMeter.ChannelId.VOLTAGE_L3, new FloatDoublewordElement(812),
								ElementToChannelConverter.SCALE_FACTOR_3),
						new DummyRegisterElement(814, 859), //
						m(AsymmetricMeter.ChannelId.CURRENT_L1, new FloatDoublewordElement(860),
								ElementToChannelConverter.SCALE_FACTOR_3),
						m(AsymmetricMeter.ChannelId.CURRENT_L2, new FloatDoublewordElement(862),
								ElementToChannelConverter.SCALE_FACTOR_3),
						m(AsymmetricMeter.ChannelId.CURRENT_L3, new FloatDoublewordElement(864),
								ElementToChannelConverter.SCALE_FACTOR_3),
						m(SymmetricMeter.ChannelId.CURRENT, new FloatDoublewordElement(866),
								ElementToChannelConverter.SCALE_FACTOR_3),
						m(AsymmetricMeter.ChannelId.ACTIVE_POWER_L1, new FloatDoublewordElement(868),
								ElementToChannelConverter.INVERT_IF_TRUE(this.invert)),
						m(AsymmetricMeter.ChannelId.ACTIVE_POWER_L2, new FloatDoublewordElement(870),
								ElementToChannelConverter.INVERT_IF_TRUE(this.invert)),
						m(AsymmetricMeter.ChannelId.ACTIVE_POWER_L3, new FloatDoublewordElement(872),
								ElementToChannelConverter.INVERT_IF_TRUE(this.invert)),
						m(SymmetricMeter.ChannelId.ACTIVE_POWER, new FloatDoublewordElement(874),
								ElementToChannelConverter.INVERT_IF_TRUE(this.invert)),
						m(AsymmetricMeter.ChannelId.REACTIVE_POWER_L1, new FloatDoublewordElement(876),
								ElementToChannelConverter.INVERT_IF_TRUE(this.invert)),
						m(AsymmetricMeter.ChannelId.REACTIVE_POWER_L2, new FloatDoublewordElement(878),
								ElementToChannelConverter.INVERT_IF_TRUE(this.invert)),
						m(AsymmetricMeter.ChannelId.REACTIVE_POWER_L3, new FloatDoublewordElement(880),
								ElementToChannelConverter.INVERT_IF_TRUE(this.invert)),
						m(SymmetricMeter.ChannelId.REACTIVE_POWER, new FloatDoublewordElement(882),
								ElementToChannelConverter.INVERT_IF_TRUE(this.invert)) //
				));
	}

	@Override
	public String debugLog() {
		return "L:" + this.getActivePower().value().asString();
	}
}
