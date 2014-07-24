package kz.zvezdochet.handler;

import javax.inject.Inject;

import kz.zvezdochet.bean.Event;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.part.EventPart;
import kz.zvezdochet.part.SearchPart;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

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
			Model model = (Model)((SearchPart)activePart.getObject()).getModel();
			if (model != null)
				checkPart(model);
		} else
			checkPart(new Event());
	}
	
	@CanExecute
	public boolean canExecute() {
		return true;
//		return ((SearchPart)activePart).getElement() != null;
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
		} catch (IllegalStateException e) {
			//Application does not have an active window
		}
	}

	/**
	 * Проверка состояния представления события
	 * @param element событие
	 */
	protected void checkPart(Model element) {
		MPart part = partService.findPart("kz.zvezdochet.part.event");
	    if (part.isDirty()) {
			if (DialogUtil.alertConfirm(
					"Открытое ранее событие не сохранено\n"
					+ "и утратит все внесённые изменения,\n"
					+ "если Вы откроете новое событие. Продолжить?")) {
				openEvent(part, element);
			}
	    } else
	    	openEvent(part, element);
	}
}
