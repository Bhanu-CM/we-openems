package io.openems.edge.core.appmanager;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.session.Language;
import io.openems.common.utils.EnumUtils;
import io.openems.common.utils.JsonUtils;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.core.appmanager.Type.GetParameterValues;
import io.openems.edge.core.appmanager.dependency.Dependency;

public abstract class AbstractOpenemsAppWithProps<APP extends AbstractOpenemsAppWithProps<APP, PROPERTY, PARAMETER>, //
		PROPERTY extends Enum<PROPERTY> & Type<PROPERTY, APP, PARAMETER>, PARAMETER extends Type.Parameter> //
		extends AbstractOpenemsApp<PROPERTY> implements OpenemsApp {

	protected AbstractOpenemsAppWithProps(ComponentManager componentManager, ComponentContext componentContext,
			ConfigurationAdmin cm, ComponentUtil componentUtil) {
		super(componentManager, componentContext, cm, componentUtil);
	}

	protected String getId(ConfigurationTarget target, EnumMap<PROPERTY, JsonElement> map, PROPERTY property) {
		final var parameter = this.singletonParameter(Language.DEFAULT);
		var componentId = Optional.ofNullable(property.def().getDefaultValue())
				.map(t -> t.apply(new AppDef.FieldValues<>(this.getApp(), property, Language.DEFAULT, parameter.get()))
						.getAsString())
				.orElseThrow(() -> new RuntimeException(
						"No default value set for Property '" + property + "' in app '" + this.getAppId() + "'"));
		return super.getId(target, map, property, componentId);
	}

	protected String getValueOrDefault(//
			final EnumMap<PROPERTY, JsonElement> map, //
			final Language l, //
			final PROPERTY property //
	) throws OpenemsNamedException {
		if (map.containsKey(property)) {
			return EnumUtils.getAsString(map, property);
		}
		final var parameter = this.singletonParameter(Language.DEFAULT);
		final var values = new AppDef.FieldValues<>(this.getApp(), property, Language.DEFAULT, parameter.get());
		final var defaultValue = property.def().getDefaultValue().apply(values);

		return JsonUtils.getAsString(defaultValue);
	}

	@Override
	public OpenemsAppPropertyDefinition[] getProperties() {
		final var parameter = this.singletonParameter(Language.DEFAULT);
		return Arrays.stream(this.propertyValues()) //
				.map(t -> {
					return new OpenemsAppPropertyDefinition(//
							t.name(), //
							this.mapDefaultValue(t, parameter.get()), //
							t.def().isAllowedToSave(), //
							this.mapBidirectionalValue(t, parameter.get()) //
					);
				}) //
				.toArray(OpenemsAppPropertyDefinition[]::new);
	}

	@Override
	public AppAssistant getAppAssistant(Language language) {
		final var parameter = this.singletonParameter(language);
		return AppAssistant.create(this.getName(language)) //
				.fields(Arrays.stream(this.propertyValues()) //
						.filter(p -> p.def().getField() != null) //
						.map(p -> p.def().getField()
								.apply(new AppDef.FieldValues<>(this.getApp(), p, language, parameter.get())).build()) //
						.collect(JsonUtils.toJsonArray())) //
				.build();
	}

	@Override
	public AppConfiguration getAppConfiguration(//
			final ConfigurationTarget target, //
			final JsonObject config, //
			final Language language //
	) throws OpenemsNamedException {
		return super.getAppConfiguration(//
				target, //
				this.fillUpProperties(config), //
				language //
		);
	}

	@Override
	protected List<String> getValidationErrors(//
			final JsonObject jProperties, //
			final List<Dependency> dependecies //
	) {
		return super.getValidationErrors(//
				this.fillUpProperties(jProperties), //
				dependecies //
		);
	}

	/**
	 * Creates a copy of the original configuration and fills up properties which
	 * are binded bidirectional.
	 * 
	 * <p>
	 * e. g. a property in a component is the same as one configured in the app so
	 * it directly gets stored in the component configuration and not twice to avoid
	 * miss matching errors.
	 * 
	 * @param original the original configuration
	 * @return a copy of the original one with the filled up properties
	 */
	public JsonObject fillUpProperties(//
			final JsonObject original //
	) {
		final var copy = original.deepCopy();
		for (var prop : this.getProperties()) {
			if (copy.has(prop.name)) {
				continue;
			}
			if (prop.bidirectionalValue == null) {
				continue;
			}
			var value = prop.bidirectionalValue.apply(copy);
			if (value == null) {
				continue;
			}
			// add value to configuration
			copy.add(prop.name, value);
		}
		return copy;
	}

	private Function<Language, JsonElement> mapDefaultValue(//
			final PROPERTY property, //
			final PARAMETER parameter //
	) {
		return this.functionMapper(property, AppDef::getDefaultValue, defaultValue -> {
			return l -> {
				return defaultValue.apply(//
						new AppDef.FieldValues<>(this.getApp(), property, l, parameter));
			};
		});
	}

	private Function<JsonObject, JsonElement> mapBidirectionalValue(//
			final PROPERTY property, //
			final PARAMETER parameter //
	) {
		return this.functionMapper(property, AppDef::getBidirectionalValue, bidirectionalValue -> {
			return config -> {
				return bidirectionalValue.apply(//
						new AppDef.FieldValues<>(this.getApp(), property, //
								Language.DEFAULT, parameter),
						config //
				);
			};
		});
	}

	private <M, R> R functionMapper(//
			final PROPERTY property, //
			final Function<AppDef<APP, PROPERTY, PARAMETER>, M> mapper, //
			final Function<M, R> resultMapper //
	) {
		final var firstResult = mapper.apply(property.def());
		if (firstResult == null) {
			return null;
		}

		return resultMapper.apply(firstResult);
	}

	private Singleton<PARAMETER> singletonParameter(Language l) {
		var values = this.propertyValues();
		if (values.length == 0) {
			return null;
		}
		return new Singleton<>(() -> values[0].getParamter().apply(new GetParameterValues<>(this.getApp(), l)));
	}

	public static final class Singleton<T> {

		private final Supplier<T> objectSupplier;
		private T object = null;

		public Singleton(Supplier<T> objectSupplier) {
			this.objectSupplier = objectSupplier;
		}

		/**
		 * Gets the value. If the value hasn't been created yet it gets created.
		 * 
		 * @return the value
		 */
		public final T get() {
			if (this.object == null) {
				this.object = this.objectSupplier.get();
			}
			return this.object;
		}

	}

	protected abstract APP getApp();

}
