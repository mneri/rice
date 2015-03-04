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

public class User {
	private String mAccount;
	private boolean mAway;
	private String mHost;
	private String mNick;
	private boolean mOperator;
	private String mReal;
	private String mUser;

	public User(String nick, String user, String host) {
		mNick = nick;
		mUser = user;
		mHost = host;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof User && ((User) obj).mNick.equals(mNick));
	}

	public String getAccount() {
		return mAccount;
	}

	public String getHost() {
		return mHost;
	}

	public String getNick() {
		return mNick;
	}

	public String getReal() {
		return mReal;
	}

	public String getUser() {
		return mUser;
	}

	public boolean isAway() {
		return mAway;
	}

	public boolean isOperator() {
		return mOperator;
	}

	public void setAccount(String account) {
		mAccount = account;
	}

	public void setAway(boolean away) {
		mAway = away;
	}

	public void setHost(String host) {
		mHost = host;
	}

	public void setNick(String nick) {
		mNick = nick;
	}

	public void setReal(String real) {
		mReal = real;
	}

	public void setUser(String user) {
		mUser = user;
	}

	public void setOperator(boolean operator) {
		mOperator = operator;
	}

	@Override
	public String toString() {
		return mNick;
	}
}
