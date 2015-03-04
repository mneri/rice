/*
 * This file is part of Rice.
 * © Copyright Massimo Neri 2014 <hello@mneri.me>
 *
 * Rice is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Rice is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Rice. If not, see <http://www.gnu.org/licenses/>.
*/

package me.mneri.rice.event;

import java.util.ArrayList;
import java.util.HashMap;

public class EventEmitter {
	public static final String ANY_EVENT = "ANY_EVENT";

	private HashMap<String, ArrayList<Callback>> mHandlers = new HashMap<>();
	private ArrayList<Callback> mCatchAlls = new ArrayList<>();

	public static interface Callback {
		public abstract void performAction(Event event);
	}

	public synchronized void emit(Event event) {
		ArrayList<Callback> callbacks = mHandlers.get(event.type);

		if (callbacks != null) {
			for (Callback callback : callbacks)
				callback.performAction(event);
		}

		for (Callback callback : mCatchAlls)
			callback.performAction(event);
	}

	public synchronized void off(String event, Callback callback) {
		if (event.equals(ANY_EVENT)) {
			mCatchAlls.remove(callback);
			return;
		}

		ArrayList<Callback> callbacks = mHandlers.get(event);

		if (callbacks != null)
			callbacks.remove(callback);
	}

	public synchronized void on(String event, Callback callback) {
		if (event.equals(ANY_EVENT)) {
			mCatchAlls.add(callback);
			return;
		}

		ArrayList<Callback> callbacks = mHandlers.get(event);

		if (callbacks == null) {
			callbacks = new ArrayList<>();
			mHandlers.put(event, callbacks);
		}

		callbacks.add(callback);
	}

	public void on(String[] events, Callback callback) {
		for (String event : events)
			on(event, callback);
	}
}
