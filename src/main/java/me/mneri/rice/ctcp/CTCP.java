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

package me.mneri.rice.ctcp;

import static me.mneri.rice.ctcp.CTCPCommands.*;

import me.mneri.rice.Connection;
import me.mneri.rice.event.EventEmitter;
import me.mneri.rice.text.StringBundler;
import me.mneri.rice.text.TextUtils;

public class CTCP extends EventEmitter {
	private Connection mConnection;

	public CTCP(Connection connection) {
		mConnection = connection;
	}

	public void action(String target, String text) {
		request(target, ACTION, text);
	}

	public void finger(String target) {
		request(target, FINGER);
	}

	public void ping(String target) {
		request(target, PING);
	}

	public void reply(String target, String command, String param) {
		StringBundler sb = new StringBundler(5);
		sb.append("\u0001").append(command).append(" ");

		if (!TextUtils.isEmpty(param))
			sb.append(param);

		sb.append("\u0001");
		mConnection.notice(target, sb.toString());
	}

	private void request(String target, String command) {
		request(target, command, null);
	}

	private void request(String target, String command, String param) {
		StringBundler sb = new StringBundler(5);
		sb.append("\u0001").append(command).append(" ");

		if (!TextUtils.isEmpty(param))
			sb.append(param);

		sb.append("\u0001");
		mConnection.privmsg(target, sb.toString());
	}

	public void time(String target) {
		request(target, TIME);
	}

	public void version(String target) {
		request(target, VERSION);
	}
}
