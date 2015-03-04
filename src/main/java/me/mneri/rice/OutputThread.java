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

import java.io.IOException;
import java.io.Writer;

class OutputThread extends Thread {
	private DelayQueue<DelayedSending> mQueue = new DelayQueue<>();

	private static class DelayedSending {
		String string;
		Writer writer;

		DelayedSending(Writer writer, String string) {
			this.string = string;
			this.writer = writer;
		}
	}

	OutputThread() { }

	@Override
	public void run() {
		boolean running = true;

		while (running) {
			try {
				DelayedSending delayed = mQueue.dequeue();
				Writer writer = delayed.writer;
				String string = delayed.string;
				writer.write(string);
				writer.flush();
			} catch (InterruptedException e) {
				running = false;
			} catch (IOException ignored) { }
		}
	}

	void write(Writer writer, String string, long delay) {
		mQueue.enqueueDelayed(new DelayedSending(writer, string), delay);
	}
}
