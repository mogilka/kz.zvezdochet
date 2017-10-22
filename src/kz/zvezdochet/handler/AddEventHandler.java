package kz.zvezdochet.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import kz.zvezdochet.bean.Event;

/**
 * Обработчик добавления события
 * @author Nataly Didenko
 *
 */
public class AddEventHandler extends EventHandler {
	@Execute
	public void execute() {
		checkPart(new Event(), "kz.zvezdochet.part.event");
	}

	@CanExecute
	public boolean canExecute() {
		return true;
	}
}
