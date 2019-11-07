package kz.zvezdochet.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.part.EventPart;

/**
 * Обработчик добавления события
 * @author Natalie Didenko
 *
 */
public class AddEventHandler extends EventHandler {
	@Execute
	public void execute() {
		checkPart(null, "kz.zvezdochet.part.event");
	}

	@CanExecute
	public boolean canExecute() {
		return true;
	}

	@Override
	protected void openPart(MPart part, Model model) {
		EventPart eventPart = (EventPart)part.getObject();
		eventPart.setModel(model, false);
	    part.setVisible(true);
	    try {
		    partService.showPart(part, PartState.VISIBLE);
		} catch (IllegalStateException e) {
			//Application does not have an active window
		}
	}
}
