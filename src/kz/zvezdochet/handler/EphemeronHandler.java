package kz.zvezdochet.handler;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

import kz.zvezdochet.bean.Event;
import kz.zvezdochet.core.handler.Handler;
import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.part.EventPart;
import kz.zvezdochet.part.SearchPart;
import kz.zvezdochet.service.EventService;

/**
 * Поиск людей, родившихся в эту же дату
 * @author Natalie Didenko
 *
 */
public class EphemeronHandler extends Handler {
	@Inject
	private EPartService partService;

	@Execute
	public void execute(@Active MPart activePart) {
		try {
			EventPart eventPart = (EventPart)activePart.getObject();
			Event event = (Event)eventPart.getModel(EventPart.MODE_ASPECT_PLANET_PLANET, true);
			if (null == event) return;
			updateStatus("Поиск", false);
			Object data = new EventService().findEphemeron(event.getBirth());
		
			MPart part = partService.findPart("kz.zvezdochet.part.events");
		    part.setVisible(true);
		    partService.showPart(part, PartState.VISIBLE);
		    SearchPart searchPart = (SearchPart)part.getObject();
		    searchPart.setData(data);
			updateStatus("Поиск завершён", false);
		} catch (Exception e) {
			DialogUtil.alertError(e);
			updateStatus("Ошибка", true);
			e.printStackTrace();
		}
	}
}
