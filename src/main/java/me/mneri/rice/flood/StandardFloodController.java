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

package me.mneri.rice.flood;

public class StandardFloodController implements FloodController {
	private static final long TIME_PENALTY_MILLIS = 2000;
	private static final long PENALTY_WINDOW_MILLIS = 10000;

	private long mMessageTimer;

	@Override
	public long getDelay(String string) {
		long currentTime = System.currentTimeMillis();

		if (mMessageTimer < currentTime)
			mMessageTimer = currentTime;

		mMessageTimer += TIME_PENALTY_MILLIS;

		if (mMessageTimer >= currentTime + PENALTY_WINDOW_MILLIS)
			return mMessageTimer - PENALTY_WINDOW_MILLIS - currentTime;

		return 0;
	}
}
