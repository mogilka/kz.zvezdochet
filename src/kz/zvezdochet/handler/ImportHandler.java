package kz.zvezdochet.handler;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

/**
 * Обработчик открытия импорта событий с сервера
 * @author Nataly Didenko
 *
 */
public class ImportHandler {
	@Inject
	private EPartService partService;

	@Execute
	public void execute() {
		MPart part = partService.findPart("kz.zvezdochet.part.import");
	    part.setVisible(true);
	    partService.showPart(part, PartState.VISIBLE);
	}
}