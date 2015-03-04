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

package me.mneri.rice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

class InputThread extends Thread {
	private Observer mObserver;
	private BufferedReader mReader;

	interface Observer {
		public void onDisconnect();

		public void onLine(String line);
	}

	InputThread(InputStream input, Charset encoding, Observer observer) {
		mReader = new BufferedReader(new InputStreamReader(input, encoding));
		mObserver = observer;
	}

	void quit() {
		try { mReader.close(); } catch (IOException ignored) { }
	}

	@Override
	public void run() {
		boolean running = true;

		while (running) {
			String line = null;

			try {
				while ((line = mReader.readLine()) != null)
					mObserver.onLine(line);
			} catch (IOException ignored) { }

			if (line == null)
				running = false;
		}

		mObserver.onDisconnect();
	}
}
