package io.openems.edge.core.appmanager.dependency;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import com.google.common.base.Function;
import com.google.gson.JsonObject;

import io.openems.edge.core.appmanager.OpenemsAppInstance;

public class DependencyDeclaration {

	public final String key;
	public final String appId;
	public final String alias;
	public final JsonObject properties;

	public final CreatePolicy createPolicy;
	public final UpdatePolicy updatePolicy;
	public final DeletePolicy deletePolicy;

	public final DependencyDeletePolicy dependencyDeletePolicy;

	// Dependency Supplier?
	private final Supplier<String> supplierForAppId = null;
	private final Function<List<OpenemsAppInstance>, String> supplierForInstanceIdFromExisting = null;

	public DependencyDeclaration(String key, String appId, String alias, CreatePolicy createPolicy,
			UpdatePolicy updatePolicy, DeletePolicy deletePolicy, DependencyDeletePolicy dependencyDeletePolicy, JsonObject properties) {
		this.key = key;
		this.appId = appId;
		this.alias = alias;
		this.properties = properties;
		this.createPolicy = createPolicy;
		this.updatePolicy = updatePolicy;
		this.deletePolicy = deletePolicy;
		this.dependencyDeletePolicy = dependencyDeletePolicy;
	}

	public static enum CreatePolicy {

		/**
		 * Always creates the dependent app except an {@link OpenemsAppInstance} is
		 * already created and not a dependency of another app.
		 */
		ALWAYS((instances, app) -> true), //

		/**
		 * lazy singleton.
		 */
		IF_NOT_EXISTING((instances, app) -> instances.stream().anyMatch(t -> t.appId.equals(app))), //

		/**
		 * Never allowed to create the app.
		 */
		NEVER((instances, app) -> false), //
		;

		private final BiFunction<List<OpenemsAppInstance>, String, Boolean> isAllowedToCreateFunction;

		private CreatePolicy(BiFunction<List<OpenemsAppInstance>, String, Boolean> isAllowedToCreateFunction) {
			this.isAllowedToCreateFunction = isAllowedToCreateFunction;
		}

		/**
		 * Determines if the app of the given appId is allowed to create. This does not
		 * mean an existing app can't be used as the dependency.
		 * 
		 * @param allInstances all app instances
		 * @param appId        the appId
		 * @returns true if the app is allowed to create
		 */
		public final boolean isAllowedToCreate(List<OpenemsAppInstance> allInstances, String appId) {
			return this.isAllowedToCreateFunction.apply(allInstances, appId);
		}
	}

	public static enum UpdatePolicy {
		ALWAYS(v -> true), //
		IF_MINE(v -> v.allInstances.stream()
				.anyMatch(a -> !a.equals(v.parent) && a.dependencies != null
						&& a.dependencies.stream().anyMatch(d -> d.instanceId.equals(v.app2Update.instanceId)))), //
		NEVER(v -> false), //
		;

		private final Function<AllowedToValues, Boolean> isAllowedToUpdateFunction;

		private UpdatePolicy(Function<AllowedToValues, Boolean> isAllowedToUpdate) {
			this.isAllowedToUpdateFunction = isAllowedToUpdate;
		}

		public final boolean isAllowedToUpdate(List<OpenemsAppInstance> allInstances, OpenemsAppInstance parent,
				OpenemsAppInstance app2Update) {
			return this.isAllowedToUpdateFunction.apply(new AllowedToValues(allInstances, parent, app2Update));
		}

	}

	public static enum DeletePolicy {
		ALWAYS(v -> true), //
		IF_MINE(v -> v.allInstances.stream()
				.anyMatch(a -> !a.equals(v.parent) && a.dependencies != null
						&& a.dependencies.stream().anyMatch(d -> d.instanceId.equals(v.app2Update.instanceId)))), //
		NEVER(v -> false), //
		;

		private final Function<AllowedToValues, Boolean> isAllowedToDeleteFunction;

		private DeletePolicy(Function<AllowedToValues, Boolean> isAllowedToDelete) {
			this.isAllowedToDeleteFunction = isAllowedToDelete;
		}

		public final boolean isAllowedToDelete(List<OpenemsAppInstance> allInstances, OpenemsAppInstance parent,
				OpenemsAppInstance app2Delete) {
			return isAllowedToDeleteFunction.apply(new AllowedToValues(allInstances, parent, app2Delete));
		}
	}

	private static class AllowedToValues {
		public final List<OpenemsAppInstance> allInstances;
		public final OpenemsAppInstance parent;
		public final OpenemsAppInstance app2Update;

		public AllowedToValues(List<OpenemsAppInstance> allInstances, OpenemsAppInstance parent,
				OpenemsAppInstance app2Update) {
			this.allInstances = allInstances;
			this.parent = parent;
			this.app2Update = app2Update;
		}
	}

	// TODO
	public static enum DependencyUpdatePolicy {
		ALL, //
		ONLY_NOT_CONFIGURED_PROPERTIES, //
		NO_PROPERTIES, //
		;
	}

	public static enum DependencyDeletePolicy {
		NOT_ALLOWED, //
		ALLOWED, //
		;
	}

}
