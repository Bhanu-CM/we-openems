package io.openems.edge.energy.api.schedulable;

import com.google.common.collect.ImmutableSet;

import io.openems.edge.controller.api.Controller;
import io.openems.edge.energy.api.schedulable.Schedule.Preset;

public interface Schedulable<CONFIG> extends Controller {

	/**
	 * Get the available {@link Mode}s.
	 * 
	 * @return set of {@link Mode}s
	 */
	public ImmutableSet<? extends Preset<CONFIG>> getAvailablePresets();

	/**
	 * Apply a {@link Schedule} of {@link Preset}s.
	 * 
	 * @param schedule the {@link Schedule}
	 */
	public void applySchedule(Schedule<CONFIG> schedule);

}
