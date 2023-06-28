package io.openems.edge.app.timeofusetariff;

import java.util.Map;
import java.util.function.Function;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.function.ThrowingTriFunction;
import io.openems.common.session.Language;
import io.openems.common.utils.JsonUtils;
import io.openems.edge.app.common.props.CommonProps;
import io.openems.edge.app.timeofusetariff.StromdaoCorrently.Property;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.core.appmanager.AbstractOpenemsApp;
import io.openems.edge.core.appmanager.AbstractOpenemsAppWithProps;
import io.openems.edge.core.appmanager.AppAssistant;
import io.openems.edge.core.appmanager.AppConfiguration;
import io.openems.edge.core.appmanager.AppDef;
import io.openems.edge.core.appmanager.AppDescriptor;
import io.openems.edge.core.appmanager.ComponentUtil;
import io.openems.edge.core.appmanager.ConfigurationTarget;
import io.openems.edge.core.appmanager.JsonFormlyUtil;
import io.openems.edge.core.appmanager.JsonFormlyUtil.InputBuilder;
import io.openems.edge.core.appmanager.Nameable;
import io.openems.edge.core.appmanager.OpenemsApp;
import io.openems.edge.core.appmanager.OpenemsAppCardinality;
import io.openems.edge.core.appmanager.OpenemsAppCategory;
import io.openems.edge.core.appmanager.TranslationUtil;
import io.openems.edge.core.appmanager.Type;

/**
 * Describes a App for StromdaoCorrently.
 *
 * <pre>
  {
    "appId":"App.TimeOfUseTariff.Stromdao",
    "alias":"Stromdao Corrently",
    "instanceId": UUID,
    "image": base64,
    "properties":{
    	"CTRL_ESS_TIME_OF_USE_TARIF_ID": "ctrlEssTimeOfUseTariff0",
    	"TIME_OF_USE_TARIF_PROVIDER_ID": "timeOfUseTariff0",
    	"ZIP_CODE": "12345678",
    	"CONTROL_MODE": {@link ControlMode}
    },
    "appDescriptor": {
    	"websiteUrl": {@link AppDescriptor#getWebsiteUrl()}
    }
  }
 * </pre>
 */
@org.osgi.service.component.annotations.Component(name = "App.TimeOfUseTariff.Stromdao")
public class StromdaoCorrently extends
		AbstractOpenemsAppWithProps<StromdaoCorrently, Property, Type.Parameter.BundleParameter> implements OpenemsApp {

	public static enum Property implements Type<Property, StromdaoCorrently, Type.Parameter.BundleParameter>, Nameable {
		// Component-IDs
		CTRL_ESS_TIME_OF_USE_TARIF_ID(AppDef.componentId("ctrlEssTimeOfUseTariff0")), //
		TIME_OF_USE_TARIF_PROVIDER_ID(AppDef.componentId("timeOfUseTariff0")), //

		// Properties
		ALIAS(CommonProps.alias()), //
		ZIP_CODE(AppDef.of(StromdaoCorrently.class)//
				.setTranslatedLabelWithAppPrefix(".zipCode.label") //
				.setTranslatedDescriptionWithAppPrefix(".zipCode.description") //
				.setField(JsonFormlyUtil::buildInput, (app, prop, l, params, f) -> //
				f.setInputType(InputBuilder.Type.NUMBER) //
						.isRequired(true))), //
		CONTROL_MODE(AppDef.copyOf(Property.class, TimeOfUseProps.controlMode()) //
				.wrapField((app, property, l, parameter, field) -> {
					field.isRequired(true);
				}));

		private final AppDef<? super StromdaoCorrently, ? super Property, ? super Type.Parameter.BundleParameter> def;

		private Property(
				AppDef<? super StromdaoCorrently, ? super Property, ? super Type.Parameter.BundleParameter> def) {
			this.def = def;
		}

		@Override
		public Property self() {
			return this;
		}

		@Override
		public AppDef<? super StromdaoCorrently, ? super Property, ? super Type.Parameter.BundleParameter> def() {
			return this.def;
		}

		@Override
		public Function<GetParameterValues<StromdaoCorrently>, Type.Parameter.BundleParameter> getParamter() {
			return Type.Parameter.functionOf(AbstractOpenemsApp::getTranslationBundle);
		}
	}

	@Activate
	public StromdaoCorrently(@Reference ComponentManager componentManager, ComponentContext context,
			@Reference ConfigurationAdmin cm, @Reference ComponentUtil componentUtil) {
		super(componentManager, context, cm, componentUtil);
	}

	@Override
	protected ThrowingTriFunction<ConfigurationTarget, Map<Property, JsonElement>, Language, AppConfiguration, OpenemsNamedException> appPropertyConfigurationFactory() {
		return (t, p, l) -> {
			final var alias = this.getString(p, l, Property.ALIAS);
			final var zipCode = this.getString(p, l, Property.ZIP_CODE);
			final var ctrlEssTimeOfUseTariffId = this.getId(t, p, Property.CTRL_ESS_TIME_OF_USE_TARIF_ID);
			final var timeOfUseTariffProviderId = this.getId(t, p, Property.TIME_OF_USE_TARIF_PROVIDER_ID);
			final var mode = this.getEnum(p, ControlMode.class, Property.CONTROL_MODE);

			var comp = TimeOfUseProps.getComponents(ctrlEssTimeOfUseTariffId, alias, "TimeOfUseTariff.Corrently",
					this.getName(l), timeOfUseTariffProviderId, mode, b -> b.addPropertyIfNotNull("zipcode", zipCode));

			return new AppConfiguration(comp, Lists.newArrayList(ctrlEssTimeOfUseTariffId, "ctrlBalancing0"));
		};
	}

	@Override
	public AppAssistant getAppAssistant(Language language) {
		var bundle = AbstractOpenemsApp.getTranslationBundle(language);
		return AppAssistant.create(this.getName(language)) //
				.fields(JsonUtils.buildJsonArray() //
						.add(JsonFormlyUtil.buildInput(Property.ZIP_CODE) //
								.setLabel(TranslationUtil.getTranslation(bundle, this.getAppId() + ".zipCode.label")) //
								.setDescription(TranslationUtil.getTranslation(bundle,
										this.getAppId() + ".zipCode.description")) //
								.isRequired(true) //
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
	public OpenemsAppCategory[] getCategories() {
		return new OpenemsAppCategory[] { OpenemsAppCategory.TIME_OF_USE_TARIFF };
	}

	@Override
	protected Property[] propertyValues() {
		return Property.values();
	}

	@Override
	public OpenemsAppCardinality getCardinality() {
		return OpenemsAppCardinality.SINGLE_IN_CATEGORY;
	}

	@Override
	protected StromdaoCorrently getApp() {
		return this;
	}

}
