package kz.zvezdochet.handler;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "kz.zvezdochet.handlers.messages"; //$NON-NLS-1$
	public static String SearchEventToolItem_Search;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
