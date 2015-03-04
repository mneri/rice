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

import java.io.Writer;
import java.util.HashSet;

class OutputInterfaceFactory {
	private static OutputInterfaceFactory _instance;

	private HashSet<OutputInterface> mInterfaces = new HashSet<>();
	private OutputThread mThread;

	private OutputInterfaceFactory() { }

	public OutputInterface createInterface(Writer writer) {
		if (mInterfaces.size() == 0) {
			mThread = new OutputThread();
			mThread.start();
		}

		OutputInterface i = new OutputInterface(mThread, writer);
		mInterfaces.add(i);

		return i;
	}

	static OutputInterfaceFactory instance() {
		if (_instance == null)
			_instance = new OutputInterfaceFactory();

		return _instance;
	}

	void release(OutputInterface i) {
		if (mInterfaces.remove(i) && mInterfaces.size() == 0) {
			mThread.interrupt();
			mThread = null;
		}
	}
}
