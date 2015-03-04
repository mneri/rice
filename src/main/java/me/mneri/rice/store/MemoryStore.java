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

package me.mneri.rice.store;

import java.util.ArrayList;

public class MemoryStore<E> implements Store<E> {
	private ArrayList<E> mItems = new ArrayList<E>();

	@Override
	public synchronized void append(E item) {
		mItems.add(item);
	}

	@Override
	public synchronized void clear() {
		mItems.clear();
	}

	@Override
	public synchronized E get(int position) {
		return mItems.get(position);
	}

	@Override
	public synchronized int size() {
		return mItems.size();
	}
}
