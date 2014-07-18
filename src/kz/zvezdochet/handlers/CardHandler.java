package kz.zvezdochet.handlers;

import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.parts.EventPart;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Execute;
//import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
//import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

/**
 * Перерисовка космограммы
 * @author Nataly Didenko
 */
public class CardHandler {

	@Execute
	public void execute(@Active MPart activePart) {
		try {
			EventPart eventPart = (EventPart)activePart.getObject();
			eventPart.refreshCard();
//			updateStatus("Расчет завершен", false);
		} catch (Exception e) {
			DialogUtil.alertError(e.getMessage());
//			updateStatus("Ошибка создания расчетной конфигурации", true);
			e.printStackTrace();
		}
	}
}
