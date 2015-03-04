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

import java.util.HashSet;

import me.mneri.rice.Message;
import me.mneri.rice.text.CaseMapping;
import me.mneri.rice.text.TextUtils;

public class IgnoreListFilter implements Filter {
	private CaseMapping mMapping;
	private HashSet<String> mNicks = new HashSet<>();

	public IgnoreListFilter(CaseMapping mapping) {
		mMapping = mapping;
	}

	public void add(String nick) {

	}

	@Override
	public boolean doFilter(Message message) {
		return mNicks.contains(message.nick);
	}

	public void remove(String nick) {

	}
}
