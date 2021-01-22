package api.lemonico.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptEncoder extends BCryptPasswordEncoder {

	private static volatile BCryptPasswordEncoder INSTANCE = null;

	private BCryptEncoder() {

	}

	public static BCryptPasswordEncoder getInstance() {
		if (INSTANCE == null) {
			synchronized (BCryptPasswordEncoder.class) {
				if (INSTANCE == null) {
					INSTANCE = new BCryptPasswordEncoder();
				}
			}
		}
		return INSTANCE;
	}

}
