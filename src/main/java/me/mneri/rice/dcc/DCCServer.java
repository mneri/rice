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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import me.mneri.rice.event.Event;
import me.mneri.rice.event.EventEmitter;

class DCCServer extends EventEmitter {
	static final String CONNECTION = "CONNECTION";
	static final String START = "START";

	private final static int DEFAULT_SOCKET_TIMEOUT = 5 * 60 * 1000;

	private Socket mClient;
	private ServerSocket mSocket;

	DCCServer() {
	}

	int getLocalPort() {
		return mSocket.getLocalPort();
	}

	void start() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mSocket = new ServerSocket(0);
					mSocket.setSoTimeout(DEFAULT_SOCKET_TIMEOUT);
					emit(new Event(START, null));

					mClient = mSocket.accept();
					mSocket.close();
					emit(new Event(CONNECTION, new DCCConnection(mClient)));
				} catch (IOException ignored) {
				}
			}
		}).start();
	}
}
