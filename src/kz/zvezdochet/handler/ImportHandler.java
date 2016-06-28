package kz.zvezdochet.handler;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

/**
 * Обработчик открытия импорта данных с сервера
 * @author Nataly Didenko
 *
 */
public class ImportHandler {
	@Inject
	private EPartService partService;

	@Execute
	public void execute(@Named("kz.zvezdochet.commandparameter.import") String dict) {
		MPart part = partService.findPart(dict);
	    part.setVisible(true);
	    partService.showPart(part, PartState.VISIBLE);
	}
}