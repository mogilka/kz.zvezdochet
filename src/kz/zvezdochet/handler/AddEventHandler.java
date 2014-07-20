package kz.zvezdochet.handler;

import kz.zvezdochet.bean.Event;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

/**
 * Обработчик добавления события
 * @author Nataly Didenko
 *
 */
public class AddEventHandler extends EventHandler {
	@Execute
	public void execute() {
		checkPart(new Event());
	}

	@CanExecute
	public boolean canExecute() {
		return true;
	}
}
