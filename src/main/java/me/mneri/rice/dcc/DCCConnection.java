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

package me.mneri.rice.dcc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import me.mneri.rice.event.Event;
import me.mneri.rice.event.EventEmitter;

class DCCConnection extends EventEmitter {
	static final String CLOSE = "CLOSE";
	static final String REQUEST = "REQUEST";

	private InputThread mInput;
	private BufferedReader mReader;
	private Socket mSocket;
	private BufferedWriter mWriter;

	private class InputThreadObserver implements InputThread.Observer {
		@Override
		public void onDisconnect() {
			emit(new Event(CLOSE, null));
		}

		@Override
		public void onRequest(String line) {
			emit(new Event(REQUEST, line));
		}
	}

	DCCConnection(String address, int port) {

	}

	DCCConnection(Socket socket) {
		mSocket = socket;

		try {
			mReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
			mWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
			mInput = new InputThread(mReader, new InputThreadObserver());
			mInput.start();
		} catch (IOException ignored) {
		}
	}

	void stop() {
		try {
			mSocket.close();
			mReader.close();
			mWriter.close();
		} catch (IOException ignored) {
		}
	}
}
