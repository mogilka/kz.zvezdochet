package kz.zvezdochet.listener;

import java.util.Locale;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import kz.zvezdochet.Messages;
import kz.zvezdochet.core.util.Constants;

/**
 * Слушатель выбора языка
 * @author Natalie Didenko
 *
 */
public class LangSelectionListener implements SelectionListener {

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {}

	@Override
	public void widgetSelected(SelectionEvent e) {
		try {
			Preferences preferences = InstanceScope.INSTANCE.getNode("kz.zvezdochet");
			Preferences recent = preferences.node(Constants.PREF_LANG);
			String lang = e.widget.getData().toString();
			recent.put(Constants.PREF_LANG, lang);
			preferences.flush();
			Messages.init();
			kz.zvezdochet.core.Messages.init();
			//System.out.println("Switch app language to " + lang);
			Locale locale = lang.equals("ru") ? new Locale("ru", "RU") : new Locale("en", "US");
			Locale.setDefault(locale);
		} catch (BackingStoreException e1) {
			e1.printStackTrace();
		}
	}
}
