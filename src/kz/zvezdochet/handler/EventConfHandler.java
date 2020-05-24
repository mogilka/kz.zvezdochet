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
import kz.zvezdochet.part.ConfPart;
import kz.zvezdochet.part.EventPart;

/**
 * Представление конфигураций аспектов
 * @author Natalie Didenko
 */
public class EventConfHandler extends Handler {
	@Inject
	private EPartService partService;

	@Execute
	public void execute(@Active MPart activePart) {
		try {
			EventPart eventPart = (EventPart)activePart.getObject();
			Event event = (Event)eventPart.getModel(EventPart.MODE_CALC, true);
			if (null == event) return;

			MPart part = partService.findPart("kz.zvezdochet.part.eventconf");
		    part.setVisible(true);
		    partService.showPart(part, PartState.VISIBLE);
		    ConfPart confPart = (ConfPart)part.getObject();
		    confPart.setEvent(event);
		} catch (Exception e) {
			DialogUtil.alertError(e);
			updateStatus("Ошибка", true);
			e.printStackTrace();
		}
	}
}
