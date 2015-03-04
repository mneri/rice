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

public class StringBundler {
	private static final int DEFAULT_CAPACITY = 16;

	private String[] mArray;
	private int mIndex;

	public StringBundler() {
		this(DEFAULT_CAPACITY);
	}

	public StringBundler(int capacity) {
		if (capacity <= 0)
			throw new IllegalArgumentException("Capacity must be greater than 0");

		mArray = new String[capacity];
	}

	public StringBundler append(boolean b) {
		append(String.valueOf(b));
		return this;
	}

	public StringBundler append(char c) {
		append(String.valueOf(c));
		return this;
	}

	public StringBundler append(double d) {
		append(String.valueOf(d));
		return this;
	}

	public StringBundler append(float f) {
		append(String.valueOf(f));
		return this;
	}

	public StringBundler append(int i) {
		append(String.valueOf(i));
		return this;
	}

	public StringBundler append(long l) {
		append(String.valueOf(l));
		return this;
	}

	public StringBundler append(String s) {
		if (TextUtils.isEmpty(s))
			return this;

		mArray[mIndex++] = s;

		if (mIndex >= mArray.length)
			expand();

		return this;
	}

	public int capacity() {
		return mArray.length;
	}

	private void expand() {
		String[] newArray = new String[mArray.length * 2];
		System.arraycopy(mArray, 0, newArray, 0, mIndex);
		mArray = newArray;
	}

	public int length() {
		return mIndex;
	}

	@Override
	public String toString() {
		if (mIndex == 0)
			return "";

		int length = 0;

		for (int i = 0; i < mIndex; i++)
			length += mArray[i].length();

		char[] charArray = new char[length];
		int offset = 0;

		for (int i = 0; i < mIndex; i++) {
			String s = mArray[i];
			s.getChars(0, s.length(), charArray, offset);
			offset += s.length();
		}

		return new String(charArray);
	}
}
