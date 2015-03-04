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

import java.util.LinkedList;

class DelayQueue<T> {
	private final LinkedList<Node<T>> mQueue = new LinkedList<>();

	static class Node<U> {
		final U element;
		final long when;

		Node(U element, long when) {
			this.element = element;
			this.when = when;
		}
	}

	public void clear() {
		synchronized (mQueue) {
			mQueue.clear();
		}
	}

	public T dequeue() throws InterruptedException {
		Node<T> node;

		synchronized (mQueue) {
			if (mQueue.size() == 0)
				mQueue.wait();

			long now = System.currentTimeMillis();
			node = mQueue.get(0);

			while (node.when > now) {
				mQueue.wait(node.when - now);
				node = mQueue.get(0);
				now = System.currentTimeMillis();
			}

			mQueue.remove(0);
		}

		return node.element;
	}


	public void enqueue(T element) {
		enqueue(element, 0);
	}

	public void enqueue(T element, long when) {
		synchronized (mQueue) {
			int i = 0;

			while (i < mQueue.size() && mQueue.get(i).when <= when)
				i++;

			mQueue.add(i, new Node<>(element, when));

			if (i == 0)
				mQueue.notify();
		}
	}

	public void enqueueDelayed(T element, long delay) {
		if (delay < 0)
			delay = 0;

		enqueue(element, System.currentTimeMillis() + delay);
	}
}
