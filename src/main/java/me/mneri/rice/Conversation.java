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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import me.mneri.rice.event.Event;
import me.mneri.rice.event.EventEmitter;
import me.mneri.rice.store.Store;
import me.mneri.rice.store.StoreFactory;

public class Conversation extends EventEmitter {
	public static final String MESSAGE = "MESSAGE";
	private static final String SERVER_CONVERSATION_NAME = "(server)";

	private final Object mUserLock = new Object();
	private String mName;
	private Store<Message> mStore;
	private Type mType;
	private Set<UserWrapper> mUsers = new HashSet<>();

	public enum Type {
		SERVER, CHANNEL, QUERY
	}

	public final MessageList messages = new MessageList() {
		@Override
		public Message get(int position) {
			return mStore.get(position);
		}

		@Override
		public int size() {
			return mStore.size();
		}
	};

	public Conversation(String name, StoreFactory<Message> factory) {
		mName = name;
		mStore = factory.createStore(name);

		if (name.startsWith("#")) mType = Type.CHANNEL;
		else if (name.equals(SERVER_CONVERSATION_NAME)) mType = Type.SERVER;
		else mType = Type.QUERY;
	}

	public void addUser(String nick) {
		synchronized (mUserLock) {
			mUsers.add(new UserWrapper(nick));
		}
	}

	public void addUsers(Collection<String> nicks) {
		synchronized (mUserLock) {
			for (String nick : nicks)
				mUsers.add(new UserWrapper(nick));
		}
	}

	public void clearUsers() {
		synchronized (mUserLock) {
			mUsers.clear();
		}
	}

	public boolean contains(String nick) {
		synchronized (mUserLock) {
			return contains(new UserWrapper(nick));
		}
	}

	public boolean contains(UserWrapper user) {
		synchronized (mUserLock) {
			return mUsers.contains(user);
		}
	}

	public String getName() {
		return mName;
	}

	public int getSize() {
		return mStore.size();
	}

	public Type getType() {
		return mType;
	}

	public Set<UserWrapper> getUsers() {
		synchronized (mUserLock) {
			return Collections.unmodifiableSet(mUsers);
		}
	}

	public void putMessage(Message message) {
		mStore.append(message);
		emit(new Event(MESSAGE, message));
	}

	public void removeUser(String nick) {
		synchronized (mUserLock) {
			mUsers.remove(new UserWrapper(nick));
		}
	}
}
