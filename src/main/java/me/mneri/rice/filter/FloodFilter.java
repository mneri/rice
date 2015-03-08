package me.mneri.rice.filter;

import me.mneri.rice.Message;

import java.util.HashMap;

public class FloodFilter implements Filter {
	private static final long TIME_PENALTY_MILLIS = 2000;
	private static final long PENALTY_WINDOW_MILLIS = 50000;

	private HashMap<String, Long> mMessageTimers = new HashMap<>();

	@Override
	public boolean doFilter(Message message) {
		long currentTime = System.currentTimeMillis();
		Long messageTimer = mMessageTimers.get(message.nick);

		if (messageTimer == null || messageTimer < currentTime)
			messageTimer = currentTime;

		messageTimer += TIME_PENALTY_MILLIS;
		mMessageTimers.put(message.nick, messageTimer);

		return messageTimer >= currentTime + PENALTY_WINDOW_MILLIS;

	}
}
