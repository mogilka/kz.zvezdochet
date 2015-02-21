package kz.zvezdochet.handler;

import javax.inject.Inject;

import kz.zvezdochet.core.handler.Handler;
import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.part.SearchPart;
import kz.zvezdochet.service.EventService;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

/**
 * Поиск списка недавно изменённых событий
 * @author Nataly Didenko
 *
 */
public class RecentModifiedHandler extends Handler {
	@Inject
	private EPartService partService;

	@Execute
	public void execute() {
		try {
			updateStatus("Поиск", false);
			MPart part = partService.findPart("kz.zvezdochet.part.events");
		    part.setVisible(true);
		    partService.showPart(part, PartState.VISIBLE);
		    SearchPart searchPart = (SearchPart)part.getObject();
		    searchPart.setData(new EventService().findRecent());
			updateStatus("Поиск завершён", false);
		} catch (Exception e) {
			DialogUtil.alertError(e.getMessage());
			e.printStackTrace();
		}
	}
}
