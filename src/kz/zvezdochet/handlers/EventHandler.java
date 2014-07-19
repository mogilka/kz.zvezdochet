package kz.zvezdochet.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import kz.zvezdochet.bean.Event;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.parts.EventPart;
import kz.zvezdochet.parts.SearchPart;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Обработчик открытия события
 * @author Nataly Didenko
 *
 */
public class EventHandler {
	@Inject
	protected EPartService partService;

	@Execute
	public void execute(@Active MPart activePart,
			@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
		Object part = activePart.getObject();
		if (part instanceof SearchPart) {
			Model model = (Model)((SearchPart)activePart.getObject()).getModel();
			if (model != null)
				checkPart(shell, model);
		} else
			checkPart(shell, new Event());
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
	    partService.showPart(part, PartState.VISIBLE);
	}

	/**
	 * Проверка состояния представления события
	 * @param shell окно приложения
	 * @param element событие
	 */
	protected void checkPart(Shell shell, Model element) {
		MPart part = partService.findPart("kz.zvezdochet.part.event");
	    if (part.isDirty()) {
			if (MessageDialog.openConfirm(shell, "Подтверждение",
					"Просматриваемое Вами ранее событие не сохранено\n"
					+ "и утратит все внесённые изменения,\n"
					+ "если Вы откроете новое событие. Продолжить?"))
				openEvent(part, element);
	    } else
	    	openEvent(part, element);
	}
}
