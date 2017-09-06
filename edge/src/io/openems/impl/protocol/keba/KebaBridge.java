/*******************************************************************************
 * OpenEMS - Open Source Energy Management System
 * Copyright (c) 2016, 2017 FENECON GmbH and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *   FENECON GmbH - initial API and implementation and initial documentation
 *******************************************************************************/
package io.openems.impl.protocol.keba;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.api.bridge.Bridge;
import io.openems.api.channel.ConfigChannel;
import io.openems.api.doc.ThingInfo;

@ThingInfo(title = "KEBA KeContact Bridge")
public class KebaBridge extends Bridge {

	public KebaBridge() {
		log.info("Constructor KebaBridge");
	}

	/*
	 * Config
	 */
	private ConfigChannel<Integer> port = new ConfigChannel<Integer>("port", this).defaultValue(9070);

	/*
	 * Fields
	 */
	private Logger log = LoggerFactory.getLogger(KebaBridge.class);
	private AtomicBoolean isWriteTriggered = new AtomicBoolean(false);
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> receivingJob = null;

	/*
	 * Methods
	 */
	@Override
	public String toString() {
		return "KebaBridge []";
	}

	@Override
	protected void dispose() {
		if (this.receivingJob != null) {
			this.receivingJob.cancel(true);
			this.receivingJob = null;
		}
	}

	@Override
	protected boolean initialize() {
		/*
		 * Restart ReceivingRunnable
		 */
		// dispose();
		// Runnable receivingRunnable;
		// try {
		// receivingRunnable = new ReceivingRunnable(this.port.value());
		// } catch (InvalidValueException e) {
		// log.error("Error initializing KebaBridge: {}", e.getMessage());
		// return false;
		// }
		// this.receivingJob = scheduler.schedule(receivingRunnable, 0, TimeUnit.MILLISECONDS);
		return true;
	}
}
