package kz.zvezdochet.handler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import kz.zvezdochet.bean.Event;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.handler.ModelOpenHandler;
import kz.zvezdochet.part.EventPart;
import kz.zvezdochet.part.SearchPart;
import kz.zvezdochet.util.Constants;

/**
 * Обработчик открытия события
 * @author Natalie Didenko
 *
 */
public class EventHandler extends ModelOpenHandler {
	@Inject
	protected EPartService partService;

	@Execute
	public void execute(@Active MPart activePart) {
		Object part = activePart.getObject();
		String partid = "kz.zvezdochet.part.event";
		if (part instanceof SearchPart) {
			Model model = (Model)((SearchPart)part).getModel();
			if (model != null)
				checkPart(model, partid);
		} else
			checkPart(new Event(), partid);
	}
	
	@CanExecute
	public boolean canExecute() {
		return true;
//		return ((SearchPart)activePart).getModel() != null;
	}

	@Override
	protected void openPart(MPart part, Model model) {
		if (model != null)
			((EventPart)part.getObject()).setModel(model, true);
	    part.setVisible(true);
	    try {
		    partService.showPart(part, PartState.VISIBLE);
		    afterOpenPart(model);
		} catch (IllegalStateException e) {
			//Application does not have an active window
		}
	}

	/**
	 * Сохраняем событие в истории
	 * @param event событие
	 */
	protected void afterOpenPart(Object object) {
		Model event = (Model)object;
		if (null == event)
			return;
		try {
			if (!event.isExisting())
				return;
			String id = event.getId().toString();
			Preferences preferences = InstanceScope.INSTANCE.getNode("kz.zvezdochet");
			Preferences recent = preferences.node(Constants.PREF_RECENT);
			String eventids = recent.get(Constants.PREF_RECENT_EVENT, "");
			String[] ids = eventids.split(",");
			Set<String> list = new HashSet<String>();
			list.addAll(Arrays.asList(ids));
			if (list.contains(id))
				list.remove(id);
			if (Constants.PREF_RECENT_MAX == list.size())
				list.remove(String.valueOf(Constants.PREF_RECENT_MAX - 1));
			list.add(id.toString());
			ids = new String[list.size()];
			list.toArray(ids);
			eventids = "";
			for (int i = 0; i < ids.length; i++) {
				if (i > 0)
					eventids += ",";
				eventids += ids[i];
			}
			System.out.println("event history: " + eventids);
			recent.put(Constants.PREF_RECENT_EVENT, eventids);
			preferences.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
}
