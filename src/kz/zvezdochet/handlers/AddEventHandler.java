package kz.zvezdochet.handlers;

import javax.inject.Named;

import kz.zvezdochet.bean.Event;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

/**
 * Обработчик добавления события
 * @author Nataly Didenko
 *
 */
public class AddEventHandler extends EventHandler {
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
		checkPart(shell, new Event());
	}

	@CanExecute
	public boolean canExecute() {
		return true;
	}
}
