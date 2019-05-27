package kz.zvezdochet.handler;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

/**
 * Обработчик открытия поиска событий
 * @author Natalie Didenko
 *
 */
public class EventsHandler {
	@Inject
	private EPartService partService;

	@Execute
	public void execute() {
		MPart part = partService.findPart("kz.zvezdochet.part.events");
	    part.setVisible(true);
	    partService.showPart(part, PartState.VISIBLE);
	}
}
