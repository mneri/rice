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

class OutputInterface {
	private OutputThread mThread;
	private Writer mWriter;

	OutputInterface(OutputThread thread, Writer writer) {
		mThread = thread;
		mWriter = writer;
	}

	void write(String string) {
		write(string, 0);
	}

	void write(String string, long delay) {
		mThread.write(mWriter, string, delay);
	}
}
