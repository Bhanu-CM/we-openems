package io.openems.edge.app.timevariableprice;

import java.util.EnumMap;
import java.util.List;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.function.ThrowingBiFunction;
import io.openems.common.types.EdgeConfig;
import io.openems.common.types.EdgeConfig.Component;
import io.openems.common.utils.JsonUtils;
import io.openems.edge.app.timevariableprice.Tibber.Property;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.core.appmanager.AbstractOpenemsApp;
import io.openems.edge.core.appmanager.AppAssistant;
import io.openems.edge.core.appmanager.AppConfiguration;
import io.openems.edge.core.appmanager.AppDescriptor;
import io.openems.edge.core.appmanager.ComponentUtil;
import io.openems.edge.core.appmanager.ConfigurationTarget;
import io.openems.edge.core.appmanager.JsonFormlyUtil;
import io.openems.edge.core.appmanager.JsonFormlyUtil.InputBuilder.Type;
import io.openems.edge.core.appmanager.OpenemsApp;
import io.openems.edge.core.appmanager.OpenemsAppCardinality;
import io.openems.edge.core.appmanager.OpenemsAppCategory;

/**
 * Describes a App for Tibber.
 *
 * <pre>
  {
    "appId":"App.TimeVariablePrice.Tibber",
    "alias":"Tibber",
    "instanceId": UUID,
    "image": base64,
    "properties":{
    	"CTRL_ESS_TIME_OF_USE_TARIF_DISCHARGE_ID": "ctrlEssTimeOfUseTariffDischarge0",
    	"TIME_OF_USE_TARIF_ID": "timeOfUseTariff0",
    	"ACCESS_TOKEN": {token}
    },
    "appDescriptor": {
    	"websiteUrl": <a href=
"https://fenecon.de/fems-2-2/fems-app-tibber/">https://fenecon.de/fems-2-2/fems-app-tibber/</a>
    }
  }
 * </pre>
 */
@org.osgi.service.component.annotations.Component(name = "App.TimeVariablePrice.Tibber")
public class Tibber extends AbstractOpenemsApp<Property> implements OpenemsApp {

	public static enum Property {
		CTRL_ESS_TIME_OF_USE_TARIF_DISCHARGE_ID, //
		TIME_OF_USE_TARIF_ID, //
		ACCESS_TOKEN;

	}

	@Activate
	public Tibber(@Reference ComponentManager componentManager, ComponentContext context,
			@Reference ConfigurationAdmin cm, @Reference ComponentUtil componentUtil) {
		super(componentManager, context, cm, componentUtil);
	}

	@Override
	protected ThrowingBiFunction<ConfigurationTarget, EnumMap<Property, JsonElement>, AppConfiguration, OpenemsNamedException> appConfigurationFactory() {
		return (t, p) -> {
			var ctrlEssTimeOfUseTariffDischargeId = this.getId(t, p, Property.CTRL_ESS_TIME_OF_USE_TARIF_DISCHARGE_ID,
					"ctrlEssTimeOfUseTariffDischarge0");

			var timeOfUseTariffId = this.getId(t, p, Property.TIME_OF_USE_TARIF_ID, "timeOfUseTariff0");

			var accessToken = this.getValueOrDefault(p, Property.ACCESS_TOKEN, "xxx");

			// TODO ess id may be changed
			List<Component> comp = Lists.newArrayList(//
					new EdgeConfig.Component(ctrlEssTimeOfUseTariffDischargeId, this.getName(),
							"Controller.Ess.Time-Of-Use-Tariff.Discharge", JsonUtils.buildJsonObject() //
									.addProperty("ess.id", "ess0") //
									.build()), //
					new EdgeConfig.Component(timeOfUseTariffId, "timeOfUseTariff0", "TimeOfUseTariff.Tibber",
							JsonUtils.buildJsonObject() //
									.onlyIf(t.isAddOrUpdate(), c -> c.addProperty("accessToken", accessToken)) //
									.build())//
			);

			// remove access token after use so it does not get saved
			p.remove(Property.ACCESS_TOKEN);

			return new AppConfiguration(comp, Lists.newArrayList("ctrlEssTimeOfUseTariffDischarge0", "ctrlBalancing0"));
		};
	}

	@Override
	public AppAssistant getAppAssistant() {
		return AppAssistant.create(this.getName()).fields(//
				JsonUtils.buildJsonArray() //
						.add(JsonFormlyUtil.buildInput(Property.ACCESS_TOKEN) //
								.setLabel("Access token") //
								.setDescription("Access token for the Tibber API.") //
								.setInputType(Type.PASSWORD) //
								.isRequired(true) //
								.build()) //
						.build()) //
				.build();
	}

	@Override
	public AppDescriptor getAppDescriptor() {
		return AppDescriptor.create() //
				.setWebsiteUrl("https://fenecon.de/fems-2-2/fems-app-tibber/") //
				.build();
	}

	@Override
	public OpenemsAppCategory[] getCategorys() {
		return new OpenemsAppCategory[] { OpenemsAppCategory.TIME_VARIABLE_PRICE };
	}

	@Override
	public String getImage() {
		return "data:image/png;base64,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";
	}

	@Override
	public String getName() {
		return "Tibber";
	}

	@Override
	protected Class<Property> getPropertyClass() {
		return Property.class;
	}

	@Override
	public OpenemsAppCardinality getCardinality() {
		return OpenemsAppCardinality.SINGLE_IN_CATEGORY;
	}

}
