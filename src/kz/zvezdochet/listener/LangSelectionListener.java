package kz.zvezdochet.listener;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import kz.zvezdochet.Messages;
import kz.zvezdochet.util.Constants;

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
			Button bt = (Button)e.widget;
			Preferences preferences = InstanceScope.INSTANCE.getNode("kz.zvezdochet");
			Preferences recent = preferences.node(Constants.PREF_LANG);
			String lang = bt.getText();
			recent.put(Constants.PREF_LANG, lang);
			preferences.flush();
			Messages.init();
			System.out.println("Switch app language to " + lang);
		} catch (BackingStoreException e1) {
			e1.printStackTrace();
		}
	}
}
