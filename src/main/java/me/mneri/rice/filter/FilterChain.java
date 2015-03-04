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

import java.util.ArrayList;

import me.mneri.rice.Message;

public class FilterChain {
	private ArrayList<Filter> mFilters = new ArrayList<>();

	public void add(Filter filter) {
		mFilters.add(filter);
	}

	public boolean applyTo(Message message) {
		for (Filter filter : mFilters) {
			if (!filter.doFilter(message))
				return false;
		}

		return true;
	}

	public void remove(Filter filter) {
		mFilters.remove(filter);
	}
}
