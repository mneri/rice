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

import static me.mneri.rice.Commands.*;

import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import me.mneri.rice.ctcp.CTCP;
import me.mneri.rice.dcc.DCC;
import me.mneri.rice.event.Event;
import me.mneri.rice.event.EventEmitter;
import me.mneri.rice.flood.FloodController;
import me.mneri.rice.flood.StandardFloodController;
import me.mneri.rice.store.MemoryStoreFactory;
import me.mneri.rice.store.StoreFactory;
import me.mneri.rice.text.CaseMapping;
import me.mneri.rice.text.StringBundler;
import me.mneri.rice.text.TextUtils;

import org.apache.commons.codec.binary.Base64;

public class Connection extends EventEmitter {
	public static final String BOUNCE = "BOUNCE";
	public static final String CLOSE = "CLOSE";
	public static final String CONNECT = "CONNECT";
	public static final String NEW_CONVERSATION = "NEW_CONVERSATION";
	public static final String REGISTER = "REGISTER";
	public static final String REMOVE_CONVERSATION = "REMOVE_CONVERSATION";
	public static final String START = "START";

	public static final Set<String> SUPPORTED_CAPABILITIES =
			Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
					"away-notify",
					"extended-join",
					"multi-prefix",
					"sasl",
					"server-time",
					"znc.in/server-time-iso"
			)));

	public final CTCP ctcp;
	public final DCC dcc;

	private boolean mAutoBounce;
	private List<String> mAutoJoin;
	private boolean mAutoNickChange;
	private int mAutoReconnectTimeout;
	private Set<Character> mAvailChannelModes = new HashSet<>();
	private Set<String> mCapabilities = new HashSet<>();
	private CaseMapping mCaseMapping = CaseMapping.RFC1459;
	private LinkedHashMap<String, Conversation> mConversations = new LinkedHashMap<>();
	private String mCurrentHost;
	private Charset mEncoding;
	private HashMap<String, Object> mExtras;
	private StoreFactory<Message> mFactory;
	private FloodController mFloodController;
	private String mHost;
	private Set<String> mHostCapabilities = new HashSet<>();
	private String mHostIrcdVersion;
	private Set<String> mIgnores;
	private boolean mIsupportCompilant;
	private InputThread mInputThread;
	private String mLoginMode;
	private String mNetwork;
	private String mNick;
	private OutputInterface mOutputInterface;
	private String mPass;
	private int mPort;
	private String mReal;
	private boolean mSecure;
	private int mSoTimeout;
	private Socket mSocket;
	private State mState = State.CLOSED;
	private String mUser;
	private HashMap<Character, Boolean> mUserMode = new HashMap<>();
	private HashMap<String, User> mUsers = new HashMap<>();
	private Set<String> mWantedCapabilities;
	private String mWantedNick;

	public enum State {
		CLOSED, STARTED, CONNECTED, REGISTERED
	}

	public static class Builder {
		private static final int DEFAULT_SOCKET_TIMEOUT = 5 * 60 * 1000;

		private boolean mAutoBounce = true;
		private List<String> mAutoJoin = new ArrayList<>();
		private boolean mAutoNickChange = true;
		private int mAutoReconnectTimeout = -1;
		private Charset mEncoding;
		private HashMap<String, Object> mExtras = new HashMap<>();
		private StoreFactory<Message> mFactory;
		private FloodController mFloodController;
		private String mHost;
		private HashSet<String> mIgnores = new HashSet<>();
		private String mLoginMode;
		private String mNick;
		private String mPass;
		private int mPort;
		private String mReal;
		private boolean mSecure;
		private int mSoTimeout;
		private String mUser;
		private Set<String> mWantedCapabilities;

		public Builder autoBounce(boolean bounce) {
			mAutoBounce = bounce;
			return this;
		}

		public Builder autoJoin(List<String> channels) {
			mAutoJoin.addAll(channels);
			return this;
		}

		public Builder autoJoin(String... channels) {
			autoJoin(Arrays.asList(channels));
			return this;
		}

		public Builder autoNickChange(boolean auto) {
			mAutoNickChange = auto;
			return this;
		}

		public Builder autoReconnect(int timeout) {
			mAutoReconnectTimeout = timeout;
			return this;
		}

		public Connection build() {
			if (TextUtils.isEmpty(mHost) || TextUtils.isEmpty(mNick) || TextUtils.isEmpty(mUser)
					|| TextUtils.isEmpty(mReal))
				throw new IllegalStateException("You must set at least a host, a nick, a user and a real name.");

			Connection connection = new Connection();
			connection.mAutoBounce = mAutoBounce;
			connection.mAutoJoin = mAutoJoin;
			connection.mAutoNickChange = mAutoNickChange;
			connection.mAutoReconnectTimeout = mAutoReconnectTimeout;
			connection.mWantedCapabilities = (mWantedCapabilities != null ? mWantedCapabilities : SUPPORTED_CAPABILITIES);
			connection.mEncoding = (mEncoding != null ? mEncoding : Charset.defaultCharset());
			connection.mExtras = mExtras;
			connection.mFactory = (mFactory != null ? mFactory : new MemoryStoreFactory<Message>());
			connection.mFloodController = (mFloodController != null ? mFloodController : new StandardFloodController());
			connection.mHost = mHost;
			connection.mIgnores = mIgnores;
			connection.mLoginMode = (!TextUtils.isEmpty(mLoginMode) ? mLoginMode : "8");
			connection.mWantedNick = mNick;
			connection.mPass = mPass;
			connection.mPort = (mPort != 0 ? mPort : (mSecure ? 6697 : 6667));
			connection.mReal = mReal;
			connection.mSecure = mSecure;
			connection.mSoTimeout = (mSoTimeout > 0 ? mSoTimeout : DEFAULT_SOCKET_TIMEOUT);
			connection.mUser = mUser;

			return connection;
		}

		public Builder capabilities(Set<String> capabilities) {
			mWantedCapabilities = capabilities;
			return this;
		}

		public Builder encoding(Charset charset) {
			mEncoding = charset;
			return this;
		}

		public Builder encoding(String charset) {
			encoding(Charset.forName(charset));
			return this;
		}

		public Builder extra(String key, Object value) {
			mExtras.put(key, value);
			return this;
		}

		public Builder floodController(FloodController fc) {
			mFloodController = fc;
			return this;
		}

		public Builder host(String host) {
			mHost = host;
			return this;
		}

		public Builder ignore(List<String> users) {
			mIgnores.addAll(users);
			return this;
		}

		public Builder ignore(String... users) {
			ignore(Arrays.asList(users));
			return this;
		}

		public Builder mode(String mode) {
			mLoginMode = mode;
			return this;
		}

		public Builder nick(String nick) {
			mNick = nick;
			return this;
		}

		public Builder pass(String pass) {
			mPass = pass;
			return this;
		}

		public Builder port(int port) {
			this.mPort = port;
			return this;
		}

		public Builder real(String real) {
			mReal = real;
			return this;
		}

		public Builder secure(boolean secure) {
			mSecure = secure;
			return this;
		}

		public Builder soTimeout(int timeout) {
			mSoTimeout = timeout;
			return this;
		}

		public Builder storeFactory(StoreFactory<Message> factory) {
			mFactory = factory;
			return this;
		}

		public Builder user(String user) {
			mUser = user;
			return this;
		}
	}

	private class InputThreadObserver implements InputThread.Observer {
		@Override
		public void onDisconnect() {
			onDisconnection();
		}

		@Override
		public void onLine(String line) {
			Message message = Message.from(line);

			if (!isIgnored(message.nick))
				emit(new Event(message.command, message));
		}
	}

	private Connection() {
		initCallbacks();
		ctcp = new CTCP(this);
		dcc = new DCC(this);
	}

	public void admin() {
		admin(null);
	}

	public void admin(String target) {
		if (TextUtils.isEmpty(target))
			sendLine(ADMIN);
		else
			sendLine(ADMIN, target);
	}

	public void away() {
		away(null);
	}

	public void away(String text) {
		if (TextUtils.isEmpty(text))
			sendLine(AWAY);
		else
			sendLine(AWAY, text);
	}

	public void cap(String subcommand) {
		sendLine(CAP, subcommand);
	}

	public void cap(String subcommand, String... params) {
		String[] stuff = new String[params.length + 2];
		stuff[0] = CAP;
		stuff[1] = subcommand;
		System.arraycopy(params, 0, stuff, 2, params.length);
		sendLine(stuff);
	}

	public void devoice(String channel, String nick) {
		mode(channel, "-v " + nick);
	}

	public Set<String> getCapabilities() {
		return mCapabilities;
	}

	public CaseMapping getCaseMapping() {
		return mCaseMapping;
	}

	public Conversation getConversation(int position) {
		return new ArrayList<>(mConversations.values()).get(position);
	}

	public Conversation getConversation(String name) {
		return mConversations.get(name);
	}

	public List<Conversation> getConversations() {
		return new ArrayList<>(mConversations.values());
	}

	public Charset getEncoding() {
		return mEncoding;
	}

	public Object getExtra(String key) {
		return getExtra(key, null);
	}

	public Object getExtra(String key, Object defaultValue) {
		Object value = mExtras.get(key);
		return (value != null ? value : defaultValue);
	}

	public String getHost() {
		return (!TextUtils.isEmpty(mCurrentHost) ? mCurrentHost : mHost);
	}

	public Set<String> getHostCapabilities() {
		return Collections.unmodifiableSet(mHostCapabilities);
	}

	public String getHostIrcdVersion() {
		return mHostIrcdVersion;
	}

	public Set<String> getIgnores() {
		return mIgnores;
	}

	public HashMap<Character, Boolean> getMode() {
		return mUserMode;
	}

	public boolean getMode(char m) {
		Boolean b = mUserMode.get(m);
		return b != null && b;
	}

	public String getNetwork() {
		return mNetwork;
	}

	public String getNick() {
		return mNick;
	}

	public int getPort() {
		return mPort;
	}

	public String getReal() {
		return mReal;
	}

	public State getState() {
		return mState;
	}

	public String getUser() {
		return mUser;
	}

	public void ignore(String nick) {
		mIgnores.add(nick);
	}

	public void info() {
		info(null);
	}

	public void info(String target) {
		if (TextUtils.isEmpty(target))
			sendLine(INFO);
		else
			sendLine(INFO, target);
	}

	private void initCallbacks() {
		// Cambi di stato
		on(REGISTER, new Callback() {
			@Override
			public void performAction(Event event) {
				if (mAutoJoin.size() > 0) {
					for (String channel : mAutoJoin)
						join(channel);

					mAutoJoin.clear();
				} else {
					for (Conversation conversation : mConversations.values()) {
						if (conversation.getType() == Conversation.Type.CHANNEL)
							join(conversation.getName());
					}
				}
			}
		});
		on(CLOSE, new Callback() {
			@Override
			public void performAction(Event event) {
				//@formatter:off
				try { mSocket.close(); } catch (Exception ignored) { } finally { mSocket = null; }
				//@formatter:on

				mCapabilities.clear();
				mCurrentHost = null;
				mHostCapabilities.clear();
				mHostIrcdVersion = null;
				mInputThread.quit();
				mInputThread = null;
				OutputInterfaceFactory.instance().release(mOutputInterface);
				mOutputInterface = null;
			}
		});

		// Ricezione messaggi
		on(AUTHENTICATE, new Callback() {
			@Override
			public void performAction(Event event) {
				String body = mWantedNick + "\0" + mUser + "\0" + mPass;
				sendRawString("AUTHENTICATE " + new String(Base64.encodeBase64(body.getBytes())) + "\r\n");
			}
		});
		on(AWAY, new Callback() {
			@Override
			public void performAction(Event event) {
				Message message = (Message) event.data;

				if (message.params.size() > 0) {
					// TODO: User is away
				} else {
					// TODO: User is returned
				}
			}
		});
		on(CAP, new Callback() {
			@Override
			public void performAction(Event event) {
				Message message = (Message) event.data;
				String sub = message.params.get(1);

				if (sub.equals("ACK")) {
					ArrayList<String> caps = new ArrayList<>(Arrays.asList(message.params.get(2).split("\\s+")));

					for (String cap : caps) {
						if (cap.startsWith("-"))
							mCapabilities.remove(cap.substring(1));
						else
							mCapabilities.add(cap.replaceAll("[-~=]", ""));
					}
				}
			}
		});
		on(CAP, new Callback() {
			@Override
			public void performAction(Event event) {
				Message message = (Message) event.data;
				String sub = message.params.get(1);

				if (mState == State.CONNECTED) {
					ArrayList<String> caps = new ArrayList<>(Arrays.asList(message.params.get(2).split("\\s+")));

					switch (sub) {
						case "ACK":
							if (caps.contains("sasl"))
								sendRawString("AUTHENTICATE PLAIN\r\n");
							else
								cap("END");
							break;
						case "LS":
							mHostCapabilities.addAll(caps);
							HashSet<String> wanted = new HashSet<>(mWantedCapabilities);
							wanted.retainAll(caps);

							if (TextUtils.isEmpty(mPass))
								wanted.remove("sasl");

							if (wanted.isEmpty()) {
								cap("END");
							} else {
								StringBundler requesting = new StringBundler();

								for (String s : wanted)
									requesting.append(s).append(" ");

								cap("REQ", requesting.toString());
							}
					}
				}
			}
		});
		on(ERR_NICKNAMEINUSE, new Callback() {
			private int mTries;

			@Override
			public void performAction(Event event) {
				if (mState == State.CONNECTED && mAutoNickChange)
					nick(mWantedNick + (++mTries));
			}
		});
		on(JOIN, new Callback() {
			@Override
			public void performAction(Event event) {
				Message message = (Message) event.data;
				User user = mUsers.get(message.nick);

				if (mUser == null)
					user = new User(message.nick, message.user, message.host);

				if (mCapabilities.contains("extended-join")) {
					user.setAccount(message.params.get(1));
					user.setReal(message.params.get(2));
				}

				String channel = message.params.get(0);
			}
		});
		on(MODE, new Callback() {
			@Override
			public void performAction(Event event) {
				Message message = (Message) event.data;
				String target = message.params.get(0);
				String specs = message.params.get(1);

				// Ci interessa solo la nostra user mode
				if (target.equals(mNick)) {
					char mode;
					boolean value = false;

					for (int i = 0; i < specs.length(); i++) {
						mode = specs.charAt(i);

						//@formatter:off
						if (mode == '+')      value = true;
						else if (mode == '-') value = false;
						else if (mode != ' ') mUserMode.put(mode, value);
						//@formatter:on
					}
				}
			}
		});
		on(NICK, new Callback() {
			@Override
			public void performAction(Event event) {
				Message message = (Message) event.data;

				if (TextUtils.compareIgnoreCase(mNick, message.nick, mCaseMapping) == 0)
					mNick = message.params.get(0);
			}
		});
		on(PING, new Callback() {
			@Override
			public void performAction(Event event) {
				Message message = (Message) event.data;
				pong(message.params.get(0));
			}
		});
		on(RPL_ISUPPORT, new Callback() {
			@Override
			public void performAction(Event event) {
				Message message = (Message) event.data;
				Pattern pattern = Pattern.compile("Try server (.+), port (\\d+)");
				Matcher matcher = pattern.matcher(message.params.get(1));
				boolean isRplBounce = matcher.find();

				if (isRplBounce) {
					mIsupportCompilant = false;

					try {
						if (mAutoBounce) {
							mHost = matcher.group(1);
							mPort = Integer.parseInt(matcher.group(2));
							emit(new Event(BOUNCE, this));
							stop();
							start();
						}
					} catch (Exception ignored) { }
				} else {
					mIsupportCompilant = true;
					pattern = Pattern.compile("([A-Z]+)(=(\\S+))?");

					for (int i = 1; i < message.params.size(); i++) {
						matcher = pattern.matcher(message.params.get(i));

						while (matcher.find()) {
							String key = matcher.group(1);
							String value = matcher.group(3);

							try {
								switch (key) {
									case "CASEMAPPING":
										if (value.equals("ascii"))
											mCaseMapping = CaseMapping.ASCII;
										else if (value.equals("strict-rfc1459"))
											mCaseMapping = CaseMapping.STRICT_RFC1459;

										break;
									case "NETWORK":
										mNetwork = value;
										break;
								}
							} catch (Exception ignored) { }
						}
					}
				}
			}
		});
		on(RPL_MYINFO, new Callback() {
			@Override
			public void performAction(Event event) {
				Message message = (Message) event.data;
				mCurrentHost = message.params.get(1);
				mHostIrcdVersion = message.params.get(2);
				String userModes = message.params.get(3);
				String channelModes = message.params.get(4);

				for (int i = 0; i < userModes.length(); i++)
					mUserMode.put(userModes.charAt(i), false);

				for (int i = 0; i < channelModes.length(); i++)
					mAvailChannelModes.add(channelModes.charAt(i));

				mState = State.REGISTERED;
				emit(new Event(REGISTER, this));
			}
		});
		on(RPL_SASLSUCCESS, new Callback() {
			@Override
			public void performAction(Event event) {
				if (mState == State.CONNECTED)
					cap("END");
			}
		});
		on(RPL_WELCOME, new Callback() {
			@Override
			public void performAction(Event event) {
				Message message = (Message) event.data;
				mNick = message.params.get(0);
			}
		});

		// Stato delle conversazioni
		on(JOIN, new Callback() {
			@Override
			public void performAction(Event event) {
				Message message = (Message) event.data;
				String target = message.params.get(0);
				Conversation conversation = mConversations.get(target);

				if (conversation == null) {
					conversation = new Conversation(target, mFactory);
					mConversations.put(target, conversation);
					emit(new Event(NEW_CONVERSATION, conversation));
				}

				conversation.addUser(message.nick);
			}
		});
		on(new String[]{ KICK, PART }, new Callback() {
			@Override
			public void performAction(Event event) {
				Message message = (Message) event.data;
				String conversationName = message.params.get(0);
				String target;

				if (message.command.equals(PART))
					target = message.nick;
				else
					target = message.params.get(1);

				if (TextUtils.compareIgnoreCase(target, mNick, mCaseMapping) == 0) {
					Conversation conversation = mConversations.remove(conversationName);
					emit(new Event(REMOVE_CONVERSATION, conversation));
				} else {
					Conversation conversation = mConversations.get(conversationName);

					if (conversation == null) {
						conversation = new Conversation(conversationName, mFactory);
						mConversations.put(conversationName, conversation);
						emit(new Event(NEW_CONVERSATION, conversation));
					}

					conversation.removeUser(target);
					conversation.putMessage(message);
				}
			}
		});
		on(new String[]{ JOIN, NOTICE, PRIVMSG }, new Callback() {
			@Override
			public void performAction(Event event) {
				Message message = (Message) event.data;
				String target = message.params.get(0);

				if (message.command.equals(PRIVMSG) || message.command.equals(NOTICE)) {
					if (mState == State.REGISTERED) {
						if (TextUtils.compareIgnoreCase(target, mNick, mCaseMapping) == 0)
							target = message.nick;
					}
				}

				Conversation conversation = mConversations.get(target);

				if (conversation == null) {
					conversation = new Conversation(target, mFactory);
					mConversations.put(target, conversation);
					emit(new Event(NEW_CONVERSATION, conversation));
				}

				conversation.putMessage(message);
			}
		});
		on(NICK, new Callback() {
			@Override
			public void performAction(Event event) {
				Message message = (Message) event.data;

				for (Conversation conversation : mConversations.values()) {
					if (conversation.contains(message.nick)) {
						conversation.removeUser(message.nick);
						conversation.addUser(message.params.get(0));
						conversation.putMessage(message);
					}
				}
			}
		});
		on(new String[]{ RPL_NAMREPLY, RPL_ENDOFNAMES }, new Callback() {
			StringBundler sb;

			@Override
			public void performAction(Event event) {
				Message message = (Message) event.data;

				switch (message.command) {
					case RPL_NAMREPLY:
						if (sb == null)
							sb = new StringBundler();

						sb.append(" ");
						sb.append(message.params.get(3));
						break;
					case RPL_ENDOFNAMES:
						String target = message.params.get(1);
						Conversation conversation = mConversations.get(target);

						if (conversation == null) {
							conversation = new Conversation(target, mFactory);
							mConversations.put(target, conversation);
							emit(new Event(NEW_CONVERSATION, conversation));
						}

						conversation.clearUsers();
						conversation.addUsers(Arrays.asList(sb.toString().split("\\s+")));
						sb = null;
				}
			}
		});
		on(RPL_WHOREPLY, new Callback() {
			@Override
			public void performAction(Event event) {
				Message message = (Message) event.data;

				String user = message.params.get(2);
				String host = message.params.get(3);
				String server = message.params.get(4);
				String nick = message.params.get(5);
				boolean away = message.params.get(6).equals("G");
			}
		});
		on(QUIT, new Callback() {
			@Override
			public void performAction(Event event) {
				Message message = (Message) event.data;

				for (Conversation conversation : mConversations.values()) {
					if (conversation.contains(message.nick)) {
						conversation.removeUser(message.nick);
						conversation.putMessage(message);
					}
				}
			}
		});
	}

	public void invite(String nick, String channel) {
		sendLine(INVITE, nick, channel);
	}

	public boolean isIgnored(String nick) {
		return mIgnores.contains(nick);
	}

	public boolean isIsupportCompilant() {
		return mIsupportCompilant;
	}

	public boolean isSecure() {
		return mSecure;
	}

	public void ison(String nicks) {
		sendLine(ISON, nicks);
	}

	public void join(String channels) {
		join(channels, null);
	}

	public void join(String channels, String keys) {
		if (TextUtils.isEmpty(keys))
			sendLine(JOIN, channels);
		else
			sendLine(JOIN, channels, keys);
	}

	public void kick(String channel, String user) {
		kick(channel, user, null);
	}

	public void kick(String channel, String user, String comment) {
		if (TextUtils.isEmpty(comment))
			sendLine(KICK, channel, user);
		else
			sendLine(KICK, channel, user, comment);
	}

	public void kill(String nick, String comment) {
		sendLine(KILL, nick, comment);
	}

	public void links() {
		links(null, null);
	}

	public void links(String mask) {
		links(null, mask);
	}

	public void links(String remote, String mask) {
		if (TextUtils.isEmpty(mask)) {
			sendLine(LINKS);
		} else {
			if (TextUtils.isEmpty(remote))
				sendLine(LINKS, mask);
			else
				sendLine(LINKS, mask, remote);
		}
	}

	public void list() {
		sendLine(LIST);
	}

	public void lusers() {
		lusers(null, null);
	}

	public void lusers(String mask) {
		lusers(mask, null);
	}

	public void lusers(String mask, String target) {
		if (TextUtils.isEmpty(mask)) {
			sendLine(LUSERS);
		} else {
			if (TextUtils.isEmpty(target))
				sendLine(LUSERS, mask);
			else
				sendLine(LUSERS, mask, target);
		}
	}

	public void mind(String user) {
		mIgnores.remove(user);
	}

	public void mode(String nickOrChannel, String modeSpecs) {
		sendLine(MODE, nickOrChannel, modeSpecs);
	}

	public void motd() {
		sendLine(MOTD);
	}

	public void names(String channel) {
		sendLine(NAMES, channel);
	}

	public void nick(String nick) {
		sendLine(NICK, nick);
	}

	public void notice(String target, String text) {
		sendLine(true, NOTICE, target, text);
	}

	private void onDisconnection() {
		mState = State.CLOSED;
		emit(new Event(CLOSE, this));

		if (mAutoReconnectTimeout >= 0) {
			(new Thread() {
				@Override
				public void run() {
					try {
						sleep(mAutoReconnectTimeout);
						start();
					} catch (InterruptedException ignored) { }
				}
			}).start();
		}
	}

	public void oper(String name, String password) {
		sendLine(OPER, name, password);
	}

	public void part(String channel) {
		part(channel, null);
	}

	public void part(String channel, String message) {
		if (TextUtils.isEmpty(message))
			sendLine(PART, channel);
		else
			sendLine(PART, channel, message);
	}

	public void pass(String pass) {
		sendLine(PASS, pass);
	}

	public void pong(String target) {
		sendLine(PONG, target);
	}

	public void privmsg(String target, String text) {
		sendLine(true, PRIVMSG, target, text);
	}

	public void putExtra(String key, Object value) {
		mExtras.put(key, value);
	}

	public void quit() {
		sendLine(QUIT);
	}

	public void quit(String message) {
		sendLine(QUIT, message);
	}

	private void sendLine(String... strings) {
		sendLine(false, strings);
	}

	private void sendLine(boolean emitEvent, String... strings) {
		String line;

		if (strings.length == 1) {
			line = strings[0];
		} else {
			StringBundler sb = new StringBundler(strings.length * 2 + 1);

			for (int i = 0; i < strings.length - 1; i++) {
				sb.append(strings[i]);
				sb.append(" ");
			}

			String trailing = strings[strings.length - 1];

			if (TextUtils.isEmpty(trailing) || trailing.contains(":") || trailing.contains(" "))
				sb.append(":");

			sb.append(trailing);

			if (!trailing.endsWith("\r\n"))
				sb.append("\r\n");

			line = sb.toString();
		}

		sendRawString(line);

		if (emitEvent) {
			line = line.substring(0, line.length() - 2);
			Message message = Message.from(":" + mNick + "!" + mUser + "@localhost " + line);
			emit(new Event(message.command, message));
		}
	}

	private void sendRawString(String string) {
		long delay;

		if (mState == State.CONNECTED)
			delay = 0;
		else
			delay = mFloodController.getDelay(string);

		mOutputInterface.write(string, delay);
	}

	public void servlist() {
		servlist(null, null);
	}

	public void servlist(String mask) {
		servlist(mask, null);
	}

	public void servlist(String mask, String type) {
		if (TextUtils.isEmpty(mask)) {
			sendLine(SERVLIST);
		} else {
			if (TextUtils.isEmpty(type))
				sendLine(SERVLIST, mask);
			else
				sendLine(SERVLIST, mask, type);
		}
	}

	public void squery(String service, String text) {
		sendLine(SQUERY, service, text);
	}

	public void squit(String server, String comment) {
		sendLine(SQUIT, server, comment);
	}

	public void start() {
		if (mState != State.CLOSED)
			return;

		mState = State.STARTED;
		emit(new Event(START, this));

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (mSecure) {
						SSLContext sslContext = SSLContext.getInstance("TLS");
						String algorithm = TrustManagerFactory.getDefaultAlgorithm();
						TrustManagerFactory tmFactory = TrustManagerFactory.getInstance(algorithm);
						tmFactory.init((KeyStore) null);
						sslContext.init(null, tmFactory.getTrustManagers(), null);
						SSLSocketFactory sslFactory = sslContext.getSocketFactory();
						SSLSocket sslSocket = (SSLSocket) sslFactory.createSocket(mHost, mPort);
						sslSocket.startHandshake();
						mSocket = sslSocket;
					} else {
						mSocket = new Socket(mHost, mPort);
					}

					mSocket.setSoTimeout(mSoTimeout);
					mInputThread = new InputThread(mSocket.getInputStream(), mEncoding, new InputThreadObserver());
					mInputThread.start();
					OutputInterfaceFactory outFactory = OutputInterfaceFactory.instance();
					OutputStreamWriter outWriter = new OutputStreamWriter(mSocket.getOutputStream(), mEncoding);
					mOutputInterface = outFactory.createInterface(outWriter);

					mState = State.CONNECTED;
					emit(new Event(CONNECT, this));
					cap("LS");

					if (!TextUtils.isEmpty(mPass))
						pass(mPass);

					nick(mWantedNick);
					user(mUser, mLoginMode, "*", mReal);
				} catch (Exception e) {
					onDisconnection();
				}
			}
		}).start();
	}

	public void stats() {
		stats(null, null);
	}

	public void stats(String query) {
		stats(query, null);
	}

	public void stats(String query, String target) {
		if (TextUtils.isEmpty(query)) {
			sendLine(STATS);
		} else {
			if (TextUtils.isEmpty(target))
				sendLine(STATS, query);
			else
				sendLine(STATS, query, target);
		}
	}

	public void stop() {
		if (mState == State.CLOSED)
			return;

		mState = State.CLOSED;
		emit(new Event(CLOSE, this));
	}

	public void time() {
		time(null);
	}

	public void time(String target) {
		if (TextUtils.isEmpty(target))
			sendLine(TIME);
		else
			sendLine(TIME, target);
	}

	public void topic(String channel) {
		topic(channel, null);
	}

	public void topic(String channel, String topic) {
		if (TextUtils.isEmpty(topic))
			sendLine(TOPIC, channel);
		else
			sendLine(TOPIC, channel, topic);
	}

	public void trace() {
		trace(null);
	}

	public void trace(String target) {
		if (TextUtils.isEmpty(target))
			sendLine(TRACE);
		else
			sendLine(TRACE, target);
	}

	public void user(String user, String mode, String unused, String real) {
		sendLine(USER, user, mode, unused, real);
	}

	public void userhost(String... nicks) {
		StringBundler sb = new StringBundler();

		for (int i = 0; i < nicks.length - 1; i++)
			sb.append(nicks[i]).append(" ");

		sb.append(nicks[nicks.length - 1]);
		sendLine(USERHOST, sb.toString());
	}

	public void users() {
		users(null);
	}

	public void users(String target) {
		if (TextUtils.isEmpty(target))
			sendLine(USERS);
		else
			sendLine(USERS, target);
	}

	public void version() {
		version(null);
	}

	public void version(String target) {
		if (TextUtils.isEmpty(target))
			sendLine(VERSION);
		else
			sendLine(VERSION, target);
	}

	public void voice(String channel, String nick) {
		mode(channel, "+v " + nick);
	}

	public void wallops(String text) {
		sendLine(WALLOPS, text);
	}

	public void who() {
		who(null, false);
	}

	public void who(String mask) {
		who(mask, false);
	}

	public void who(String mask, boolean operator) {
		if (TextUtils.isEmpty(mask)) {
			sendLine(WHO);
		} else {
			if (!operator)
				sendLine(WHO, mask);
			else
				sendLine(WHO, mask, "o");
		}
	}

	public void whois(String user) {
		whois(null, user);
	}

	public void whois(String target, String user) {
		if (TextUtils.isEmpty(target))
			sendLine(WHOIS, user);
		else
			sendLine(WHOIS, target, user);
	}

	public void whowas(String nick) {
		whowas(nick, 1, null);
	}

	public void whowas(String nick, int count) {
		whowas(nick, count, null);
	}

	public void whowas(String nick, int count, String target) {
		if (TextUtils.isEmpty(target))
			sendLine(WHOWAS, nick, Integer.toString(count));
		else
			sendLine(WHOWAS, nick, Integer.toString(count), target);
	}
}
