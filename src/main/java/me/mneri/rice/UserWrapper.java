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

public class UserWrapper {
	private boolean mAdmin;
	private boolean mOperator;
	private boolean mHalfOperator;
	private String mNick;
	private boolean mOwner;
	private boolean mVoiced;

	public UserWrapper(String nickDef) {
		if (nickDef.contains("&"))
			mAdmin = true;

		if (nickDef.contains("@"))
			mOperator = true;

		if (nickDef.contains("%"))
			mHalfOperator = true;

		if (nickDef.contains("~"))
			mOwner = true;

		if (nickDef.contains("+"))
			mVoiced = true;

		mNick = nickDef.replaceAll("[&@%~+]", "");
	}

	@Override
	public boolean equals(Object o) {
		return !(o == null || !(o instanceof UserWrapper)) && ((UserWrapper) o).getNick().equals(mNick);
	}

	public String getNick() {
		return mNick;
	}

	@Override
	public int hashCode() {
		return mNick.hashCode();
	}

	public boolean isAdmin() {
		return mAdmin;
	}

	public boolean isHalfOperator() {
		return mHalfOperator;
	}

	public boolean isOperator() {
		return mOperator;
	}

	public boolean isOwner() {
		return mOwner;
	}

	public boolean isVoiced() {
		return mVoiced;
	}
}
