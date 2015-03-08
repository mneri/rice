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

package me.mneri.rice.filter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import me.mneri.rice.Message;

//TODO: Connection's case mapping should be taken into account

public class IgnoreFilter implements Filter {
	private HashSet<String> mNicks = new HashSet<>();

	public void add(Collection<String> nicks) {
		mNicks.addAll(nicks);
	}

	public void add(String nick) {
		mNicks.add(nick);
	}

	public boolean contains(String nick) {
		return mNicks.contains(nick);
	}

	@Override
	public boolean doFilter(Message message) {
		return mNicks.contains(message.nick);
	}

	public Set<String> get() {
		return mNicks;
	}

	public void remove(String nick) {
		mNicks.remove(nick);
	}
}
