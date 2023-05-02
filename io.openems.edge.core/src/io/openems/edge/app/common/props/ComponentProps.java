package io.openems.edge.app.common.props;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.core.appmanager.AppDef;
import io.openems.edge.core.appmanager.ComponentManagerSupplier;
import io.openems.edge.core.appmanager.ComponentUtilSupplier;
import io.openems.edge.core.appmanager.JsonFormlyUtil;
import io.openems.edge.core.appmanager.Nameable;
import io.openems.edge.core.appmanager.OpenemsApp;
import io.openems.edge.core.appmanager.Type.Parameter;
import io.openems.edge.ess.api.ManagedSymmetricEss;
import io.openems.edge.meter.api.MeterType;
import io.openems.edge.meter.api.SymmetricMeter;

/**
 * Static method collection for {@link AppDef AppDefs} for selecting different
 * kinds of {@link OpenemsComponent OpenemsComponents}.
 */
public final class ComponentProps {

	/**
	 * Creates a {@link AppDef} for a input to select a enabled
	 * {@link OpenemsComponent}.
	 * 
	 * @param <APP> the type of the {@link OpenemsApp}
	 * @return the {@link AppDef}
	 */
	public static <APP extends OpenemsApp & ComponentManagerSupplier> //
	AppDef<APP, Nameable, Parameter.BundleParameter> pickComponentId() {
		return pickComponentId(app -> {
			final var componentManager = app.getComponentManager();
			return componentManager.getEnabledComponents();
		});
	}

	/**
	 * Creates a {@link AppDef} for a input to select a enabled
	 * {@link OpenemsComponent} of the given type.
	 * 
	 * @param <APP> the type of the {@link OpenemsApp}
	 * @param <T>   the type of the component
	 * @param type  the type of the {@link OpenemsComponent OpenemsComponents}
	 * @return the {@link AppDef}
	 */
	public static <APP extends OpenemsApp & ComponentUtilSupplier, T extends OpenemsComponent> //
	AppDef<APP, Nameable, Parameter.BundleParameter> pickComponentId(//
			final Class<T> type //
	) {
		return pickComponentId(type, null);
	}

	/**
	 * Creates a {@link AppDef} for a input to select a enabled
	 * {@link OpenemsComponent} of the given type and filtered by the given filter.
	 * 
	 * @param <APP>  the type of the {@link OpenemsApp}
	 * @param <T>    the type of the component
	 * @param type   the type of the {@link OpenemsComponent OpenemsComponents}
	 * @param filter the filter of the components
	 * @return the {@link AppDef}
	 */
	public static <APP extends OpenemsApp & ComponentUtilSupplier, T extends OpenemsComponent> //
	AppDef<APP, Nameable, Parameter.BundleParameter> pickComponentId(//
			final Class<T> type, //
			final Predicate<T> filter //
	) {
		return pickComponentId(app -> {
			final var componentUtil = app.getComponentUtil();
			var components = componentUtil.getEnabledComponentsOfType(type).stream();
			if (filter != null) {
				components = components.filter(filter);
			}
			return components.toList();
		});
	}

	private static <APP extends OpenemsApp> AppDef<APP, Nameable, Parameter.BundleParameter> pickComponentId(//
			Function<APP, List<? extends OpenemsComponent>> supplyComponents //
	) {
		return AppDef.<APP, Nameable, Parameter.BundleParameter, //
				OpenemsApp, Nameable, Parameter.BundleParameter>copyOfGeneric(CommonProps.defaultDef()) //
				.setLabel("Component-ID") //
				.setField(JsonFormlyUtil::buildSelectFromNameable, (app, property, l, parameter, field) -> {
					field.setOptions(supplyComponents.apply(app),
							JsonFormlyUtil.SelectBuilder.DEFAULT_COMPONENT_2_LABEL,
							JsonFormlyUtil.SelectBuilder.DEFAULT_COMPONENT_2_VALUE);
				}).setDefaultValue((app, property, l, parameter) -> {
					final var components = supplyComponents.apply(app);
					if (components.isEmpty()) {
						return JsonNull.INSTANCE;
					}
					return new JsonPrimitive(components.get(0).id());
				});
	}

	/**
	 * Creates a {@link AppDef} for a input to select a enabled
	 * {@link OpenemsComponent} with the given starting id.
	 * 
	 * @param <APP>      the type of the {@link OpenemsApp}
	 * @param startingId the starting id of the components e. g. evcs for all evcss:
	 *                   evcs0, evcs1, ...
	 * @return the {@link AppDef}
	 */
	public static <APP extends OpenemsApp & ComponentUtilSupplier> //
	AppDef<APP, Nameable, Parameter.BundleParameter> pickComponentId(//
			String startingId //
	) {
		return pickComponentId(app -> {
			final var componentUtil = app.getComponentUtil();
			return componentUtil.getEnabledComponentsOfStartingId(startingId);
		});
	}

	/**
	 * Creates a {@link AppDef} for a input to select a {@link ManagedSymmetricEss}.
	 * 
	 * @param <APP> the type of the {@link OpenemsApp}
	 * @return the {@link AppDef}
	 */
	public static <APP extends OpenemsApp & ComponentUtilSupplier> //
	AppDef<APP, Nameable, Parameter.BundleParameter> pickManagedSymmetricEssId() {
		return ComponentProps.<APP, ManagedSymmetricEss>pickComponentId(ManagedSymmetricEss.class) //
				.setTranslatedLabel("essId.label") //
				.setTranslatedDescription("essId.description");
	}

	/**
	 * Creates a {@link AppDef} for a input to select a {@link SymmetricMeter}.
	 * 
	 * @param <APP> the type of the {@link OpenemsApp}
	 * @return the {@link AppDef}
	 */
	public static <APP extends OpenemsApp & ComponentUtilSupplier> //
	AppDef<APP, Nameable, Parameter.BundleParameter> pickSymmetricMeterId() {
		return ComponentProps.<APP, SymmetricMeter>pickComponentId(SymmetricMeter.class) //
				.setTranslatedLabel("meterId.label") //
				.setTranslatedDescription("meterId.description");
	}

	/**
	 * Creates a {@link AppDef} for a input to select a {@link SymmetricMeter} with
	 * the {@link MeterType} {@link MeterType#GRID}.
	 * 
	 * @param <APP> the type of the {@link OpenemsApp}
	 * @return the {@link AppDef}
	 */
	public static <APP extends OpenemsApp & ComponentUtilSupplier> //
	AppDef<APP, Nameable, Parameter.BundleParameter> pickSymmetricGridMeterId() {
		return ComponentProps
				.<APP, SymmetricMeter>pickComponentId(SymmetricMeter.class,
						meter -> meter.getMeterType() == MeterType.GRID) //
				.setTranslatedLabel("gridMeterId.label") //
				.setTranslatedDescription("gridMeterId.description");
	}

	private ComponentProps() {
		super();
	}

}
