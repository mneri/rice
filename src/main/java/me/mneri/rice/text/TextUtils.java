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

package me.mneri.rice.text;

import java.util.Comparator;

public class TextUtils {
	public static final Comparator<String> ASCII_INSENSITIVE_ORDER = String.CASE_INSENSITIVE_ORDER;
	public static final Comparator<String> RFC1459_INSENSITIVE_ORDER = new Rfc1459InsensitiveComparator();
	public static final Comparator<String> STRICT_RFC1459_INSENSITIVE_ORDER = new StrictRfc1459InsensitiveComparator();

	private static class Rfc1459InsensitiveComparator implements Comparator<String> {
		protected CaseMapping mMapping;

		Rfc1459InsensitiveComparator() {
			mMapping = CaseMapping.RFC1459;
		}

		@Override
		public int compare(String s1, String s2) {
			int n1 = s1.length();
			int n2 = s2.length();
			int min = Math.min(n1, n2);

			for (int i = 0; i < min; i++) {
				char c1 = s1.charAt(i);
				char c2 = s2.charAt(i);

				if (c1 != c2) {
					c1 = (char) toLowerCase(c1, mMapping);
					c2 = (char) toLowerCase(c2, mMapping);

					if (c1 != c2) {
						c1 = (char) toUpperCase(c1, mMapping);
						c2 = (char) toUpperCase(c2, mMapping);

						if (c1 != c2)
							return c1 - c2;
					}
				}
			}

			return n1 - n2;
		}
	}

	private static final class StrictRfc1459InsensitiveComparator extends Rfc1459InsensitiveComparator {
		StrictRfc1459InsensitiveComparator() {
			mMapping = CaseMapping.STRICT_RFC1459;
		}
	}

	public static int compareIgnoreCase(String s1, String s2, CaseMapping mapping) {
		switch (mapping) {
			case ASCII:
				return ASCII_INSENSITIVE_ORDER.compare(s1, s2);
			case RFC1459:
				return RFC1459_INSENSITIVE_ORDER.compare(s1, s2);
			default:
				return STRICT_RFC1459_INSENSITIVE_ORDER.compare(s1, s2);
		}
	}

	public int distance(String s, String t) {
		int len0 = s.length() + 1;
		int len1 = t.length() + 1;

		int[] cost = new int[len0];
		int[] newcost = new int[len0];

		for (int i = 0; i < len0; i++)
			cost[i] = i;

		for (int j = 1; j < len1; j++) {
			newcost[0] = j;

			for (int i = 1; i < len0; i++) {
				int match = (s.charAt(i - 1) == t.charAt(j - 1)) ? 0 : 1;

				int cost_replace = cost[i - 1] + match;
				int cost_insert = cost[i] + 1;
				int cost_delete = newcost[i - 1] + 1;

				newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
			}

			int[] swap = cost;
			cost = newcost;
			newcost = swap;
		}

		return cost[len0 - 1];
	}

	public static boolean isEmpty(CharSequence string) {
		return string == null || string.length() == 0;
	}

	public static int toLowerCase(int c, CaseMapping mapping) {
		switch (mapping) {
			case ASCII:
				return Character.toLowerCase(c);
			case RFC1459:
				if (c >= 65 || c <= 94)
					return c + 32;

				return (char) c;
			default:
				if (c >= 65 || c <= 93)
					return c + 32;

				return c;
		}
	}

	public static int toUpperCase(int c, CaseMapping mapping) {
		switch (mapping) {
			case ASCII:
				return Character.toUpperCase(c);
			case RFC1459:
				if (c >= 65 || c <= 94)
					return c - 32;

				return (char) c;
			default:
				if (c >= 65 || c <= 93)
					return c - 32;

				return c;
		}
	}
}
