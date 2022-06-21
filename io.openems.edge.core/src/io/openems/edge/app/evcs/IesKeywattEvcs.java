package io.openems.edge.app.evcs;

import java.util.EnumMap;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.function.ThrowingTriFunction;
import io.openems.common.session.Language;
import io.openems.common.utils.EnumUtils;
import io.openems.common.utils.JsonUtils;
import io.openems.edge.app.evcs.IesKeywattEvcs.Property;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.core.appmanager.AbstractOpenemsApp;
import io.openems.edge.core.appmanager.AppAssistant;
import io.openems.edge.core.appmanager.AppConfiguration;
import io.openems.edge.core.appmanager.AppDescriptor;
import io.openems.edge.core.appmanager.ComponentUtil;
import io.openems.edge.core.appmanager.ConfigurationTarget;
import io.openems.edge.core.appmanager.DefaultEnum;
import io.openems.edge.core.appmanager.JsonFormlyUtil;
import io.openems.edge.core.appmanager.JsonFormlyUtil.InputBuilder.Type;
import io.openems.edge.core.appmanager.OpenemsApp;
import io.openems.edge.core.appmanager.OpenemsAppCardinality;

/**
 * Describes a IES Keywatt evcs App.
 *
 * <pre>
  {
    "appId":"App.Evcs.IesKeywatt",
    "alias":"IES Keywatt Ladestation",
    "instanceId": UUID,
    "image": base64,
    "properties":{
      "EVCS_ID": "evcs0",
      "CTRL_EVCS_ID": "ctrlEvcs0",
      "OCCP_CHARGE_POINT_IDENTIFIER":"IES 1",
      "OCCP_CONNECTOR_IDENTIFIER": "1"
    },
    "appDescriptor": {
    	"websiteUrl": URL
    }
  }
 * </pre>
 */
@Component(name = "App.Evcs.IesKeywatt")
public class IesKeywattEvcs extends AbstractEvcsApp<Property> implements OpenemsApp {

	public static enum Property implements DefaultEnum {
		ALIAS("IES Keywatt Ladestation"), //
		EVCS_ID("evcs0"), //
		CTRL_EVCS_ID("ctrlEvcs0"), //
		OCCP_CHARGE_POINT_IDENTIFIER("IES1"), //
		OCCP_CONNECTOR_IDENTIFIER("1"), //
		;

		private final String defaultValue;

		private Property(String defaultValue) {
			this.defaultValue = defaultValue;
		}

		@Override
		public String getDefaultValue() {
			return this.defaultValue;
		}

	}

	@Activate
	public IesKeywattEvcs(@Reference ComponentManager componentManager, ComponentContext componentContext,
			@Reference ConfigurationAdmin cm, @Reference ComponentUtil componentUtil) {
		super(componentManager, componentContext, cm, componentUtil);
	}

	@Override
	protected ThrowingTriFunction<ConfigurationTarget, EnumMap<Property, JsonElement>, Language, AppConfiguration, OpenemsNamedException> appConfigurationFactory() {
		return (t, p, l) -> {
			// values the user enters
			var alias = this.getValueOrDefault(p, Property.ALIAS, this.getName(l));

			// values which are being auto generated by the appmanager
			var evcsId = this.getId(t, p, Property.EVCS_ID);
			var ctrlEvcsId = this.getId(t, p, Property.CTRL_EVCS_ID);
			var ocppId = this.getValueOrDefault(p, Property.OCCP_CHARGE_POINT_IDENTIFIER);

			var connectorId = EnumUtils.getAsInt(p, Property.OCCP_CONNECTOR_IDENTIFIER);

			var factoryId = "Evcs.Ocpp.IesKeywattSingle";
			var components = this.getComponents(evcsId, alias, factoryId, null, ctrlEvcsId);
			var evcs = AbstractOpenemsApp.getComponentWithFactoryId(components, factoryId);
			evcs.getProperties().put("ocpp.id", new JsonPrimitive(ocppId));
			evcs.getProperties().put("connectorId", new JsonPrimitive(connectorId));

			return new AppConfiguration(components, Lists.newArrayList(ctrlEvcsId, "ctrlBalancing0"));
		};
	}

	@Override
	public AppAssistant getAppAssistant(Language language) {
		var bundle = AbstractOpenemsApp.getTranslationBundle(language);
		return AppAssistant.create(this.getName(language)) //
				.fields(JsonUtils.buildJsonArray() //
						.add(JsonFormlyUtil.buildInput(Property.OCCP_CHARGE_POINT_IDENTIFIER) //
								.setLabel(bundle.getString(this.getAppId() + ".chargepoint.label")) //
								.setDescription(bundle.getString(this.getAppId() + ".chargepoint.description")) //
								.setDefaultValue(Property.OCCP_CHARGE_POINT_IDENTIFIER.getDefaultValue()) //
								.isRequired(true) //
								.build()) //
						.add(JsonFormlyUtil.buildInput(Property.OCCP_CONNECTOR_IDENTIFIER) //
								.setLabel(bundle.getString(this.getAppId() + ".connector.label")) //
								.setDescription(bundle.getString(this.getAppId() + ".connector.description")) //
								.setDefaultValue(Property.OCCP_CONNECTOR_IDENTIFIER.getDefaultValue()) //
								.isRequired(true) //
								.setInputType(Type.NUMBER) //
								.setMin(0) //
								.build()) //
						.build()) //
				.build();
	}

	@Override
	public AppDescriptor getAppDescriptor() {
		return AppDescriptor.create() //
				.build();
	}

	@Override
	protected Class<Property> getPropertyClass() {
		return Property.class;
	}

	@Override
	public OpenemsAppCardinality getCardinality() {
		return OpenemsAppCardinality.MULTIPLE;
	}

}
