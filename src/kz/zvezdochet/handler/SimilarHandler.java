package kz.zvezdochet.handler;

import javax.inject.Inject;

import kz.zvezdochet.bean.Event;
import kz.zvezdochet.core.handler.Handler;
import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.part.EventPart;
import kz.zvezdochet.part.SearchPart;
import kz.zvezdochet.service.EventService;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

/**
 * Поиск людей, похожих по характеру
 * (совпадение знаков Солнца, Меркурия, Венеры, Марса)
 * @author Nataly Didenko
 *
 */
public class SimilarHandler extends Handler {
	@Inject
	private EPartService partService;

	@Execute
	public void execute(@Active MPart activePart) {
		try {
			EventPart eventPart = (EventPart)activePart.getObject();
			Event event = (Event)eventPart.getModel(EventPart.MODE_CALC, true);
			if (null == event || null == event.getConfiguration()) return;
			updateStatus("Поиск", false);
			Object data = new EventService().findSimilar(event, -1);
		
			MPart part = partService.findPart("kz.zvezdochet.part.events");
		    part.setVisible(true);
		    partService.showPart(part, PartState.VISIBLE);
		    SearchPart searchPart = (SearchPart)part.getObject();
		    searchPart.setData(data);
			updateStatus("Поиск завершён", false);
		} catch (Exception e) {
			DialogUtil.alertError(e.getMessage());
			updateStatus("Ошибка", true);
			e.printStackTrace();
		}
	}
}