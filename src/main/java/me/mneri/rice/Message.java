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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.mneri.rice.text.StringBundler;
import me.mneri.rice.text.TextUtils;

public class Message {
	public static final String TAG_VENDOR_DATE = "rice.mneri.me/time";

	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

	public final String command;
	public final String host;
	public final String nick;
	public final List<String> params;
	public final Map<String, String> tags;
	public final String user;

	public static Message from(String line) {
		final char[] buffer = line.toCharArray();
		int start = 0;
		int end = 0;
		String key = null;
		String value = null;

		Map<String, String> tags = new HashMap<>(4, 2);
		String nick = null;
		String user = null;
		String host = null;
		String command;
		ArrayList<String> params = new ArrayList<>(5);

		if (end < buffer.length && buffer[end] == '@') {
			start = ++end;

			while (end < buffer.length && buffer[end] != ' ') {
				if (buffer[end] == '=') {
					key = new String(buffer, start, end - start);
					start = ++end;
				} else if (buffer[end] == ';') {
					if (key != null)
						value = new String(buffer, start, end - start);
					else
						key = new String(buffer, start, end - start);

					tags.put(key, value);
					key = null;
					value = null;
					start = ++end;
				} else {
					end++;
				}
			}

			if (start != end) {
				if (key != null)
					value = new String(buffer, start, end - start);
				else
					key = new String(buffer, start, end - start);

				tags.put(key, value);
			}

			do {
				end++;
			} while (end < buffer.length && buffer[end] == ' ');

			start = end;
		}

		if (tags.get(TAG_VENDOR_DATE) == null) {
			Date date = null;

			if (tags.get("time") != null) {
				try {
					date = format.parse(tags.remove("time"));
				} catch (ParseException ignored) { }
			}

			if (date == null)
				date = new Date();

			tags.put(TAG_VENDOR_DATE, Long.toString(date.getTime()));
		}

		// If we encounter ':' there is the prefix
		if (end < buffer.length && buffer[end] == ':') {
			start = ++end; // Ignore ':'

			// Nick ends with '!' or space
			while (end < buffer.length && buffer[end] != '!' && buffer[end] != ' ')
				end++;

			nick = new String(buffer, start, end - start);

			// If there's a '!' we have the username
			if (buffer[end] == '!') {
				start = ++end; // Ignore '!'

				// Username ends with '@' or space
				while (end < buffer.length && buffer[end] != '@' && buffer[end] != ' ')
					end++;

				user = new String(buffer, start, end - start);
			}

			// If there's a '@' we have the host name
			if (end < buffer.length && buffer[end] == '@') {
				start = ++end;

				// Host name ends with space
				while (end < buffer.length && buffer[end] != ' ')
					end++;

				host = new String(buffer, start, end - start);
			}

			// Skip white spaces
			do {
				end++;
			} while (end < buffer.length && buffer[end] == ' ');
			start = end;
		}

		while (end < buffer.length && buffer[end] != ' ')
			end++;

		command = new String(buffer, start, end - start);

		do {
			end++;
		} while (end < buffer.length && buffer[end] == ' ');

		start = end;

		// Check if we have parameters
		if (end < buffer.length) {
			boolean trailing = false;

			do {
				if (buffer[end] == ' ') { // space
					// If start == end, then we have more than one space. We
					// want to skip extra spaces.
					if (start < end)
						params.add(new String(buffer, start, end - start));

					start = end + 1;
				} else if (buffer[end] == ':') { // ':'
					trailing = true;
				}

				end++;
			} while (end < buffer.length && !trailing);

			if (trailing)
				start++;

			params.add(new String(buffer, start, buffer.length - start));
		}

		params.trimToSize();
		return new Message(tags, nick, user, host, command, params);
	}

	private Message(Map<String, String> tags, String nick, String user, String host, String command,
					List<String> params) {
		this.command = command;
		this.host = host;
		this.nick = nick;
		this.params = Collections.unmodifiableList(params);
		this.tags = Collections.unmodifiableMap(tags);
		this.user = user;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(32);

		sb.append("@");

		for (String key : tags.keySet()) {
			sb.append(key);
			String value = tags.get(key);

			if (value != null) {
				sb.append("=");
				sb.append(value);
			}

			sb.append(";");
		}

		sb.append(" ");

		if (!(TextUtils.isEmpty(nick) && TextUtils.isEmpty(user) && TextUtils.isEmpty(host))) {
			sb.append(":");

			if (!TextUtils.isEmpty(nick))
				sb.append(nick);

			if (!TextUtils.isEmpty(user)) {
				sb.append("!");
				sb.append(user);
			}

			if (!TextUtils.isEmpty(host)) {
				sb.append("@");
				sb.append(host);
			}

			sb.append(" ");
		}

		sb.append(command);
		sb.append(" ");
		int size = params.size();

		if (params.size() > 0) {
			for (int i = 0; i < size - 1; i++) {
				sb.append(params.get(i));
				sb.append(" ");
			}

			String trailing = params.get(size - 1);

			if (trailing.contains(" ") || trailing.contains(":"))
				sb.append(":");

			sb.append(trailing);
		}

		return sb.toString();
	}
}
