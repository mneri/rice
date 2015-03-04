package me.mneri.rice.filter;

import me.mneri.rice.Message;

public class FloodFilter implements Filter {
	@Override
	public boolean doFilter(Message message) {
		return false;
	}
}
