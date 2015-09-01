package kz.zvezdochet.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import kz.zvezdochet.bean.Event;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.part.EventPart;
import kz.zvezdochet.part.SearchPart;
import kz.zvezdochet.util.Constants;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Обработчик открытия события
 * @author Nataly Didenko
 *
 */
public class EventHandler {
	@Inject
	private EPartService partService;

	@Execute
	public void execute(@Active MPart activePart) {
		Object part = activePart.getObject();
		if (part instanceof SearchPart) {
			Model model = (Model)((SearchPart)part).getModel();
			if (model != null)
				checkPart(model);
		} else
			checkPart(new Event());
	}
	
	@CanExecute
	public boolean canExecute() {
		return true;
//		return ((SearchPart)activePart).getModel() != null;
	}

	/**
	 * Отображение события в его представлении
	 * @param part представление
	 * @param model событие
	 */
	private void openEvent(MPart part, Model model) {
		if (model != null)
			((EventPart)part.getObject()).setModel(model, true);
	    part.setVisible(true);
	    try {
		    partService.showPart(part, PartState.VISIBLE);
		    persistEvent(model);
		} catch (IllegalStateException e) {
			//Application does not have an active window
		}
	}

	/**
	 * Сохраняем событие в истории
	 * @param event событие
	 */
	private void persistEvent(Model event) {
		try {
			if (null == event.getId()) return;
			Preferences preferences = InstanceScope.INSTANCE.getNode("kz.zvezdochet");
			Preferences recent = preferences.node(Constants.PREF_RECENT);
			String eventids = recent.get(Constants.PREF_RECENT_EVENT, "");
			String[] ids = eventids.split(",");
			List<String> list = new ArrayList<String>(Arrays.asList(ids));
			if (Constants.PREF_RECENT_MAX == list.size())
				list.remove(Constants.PREF_RECENT_MAX - 1);
			if (list.contains(event.getId()))
				list.remove(list.indexOf(event.getId()));
			list.add(0, event.getId().toString());
			ids = list.toArray(ids);
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

	/**
	 * Проверка состояния представления события
	 * @param model событие
	 */
	protected void checkPart(Model model) {
		MPart part = partService.findPart("kz.zvezdochet.part.event");
	    if (part.isDirty()) {
			if (DialogUtil.alertConfirm(
					"Открытое ранее событие не сохранено\n"
					+ "и утратит все внесённые изменения,\n"
					+ "если Вы откроете новое событие. Продолжить?")) {
				openEvent(part, model);
			}
	    } else
	    	openEvent(part, model);
	}
}
