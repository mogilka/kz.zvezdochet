package kz.zvezdochet.handler;

import kz.zvezdochet.bean.Event;
import kz.zvezdochet.core.handler.Handler;
import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.part.EventPart;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

/**
 * Расчёт конфигурации
 * @author Nataly Didenko
 */
public class CalcHandler extends Handler {

	@Execute
	public void execute(@Active MPart activePart) {
		try {
			EventPart eventPart = (EventPart)activePart.getObject();
			Event event = (Event)eventPart.getModel(EventPart.MODE_CALC, true);
			if (null == event) return;
			updateStatus("Расчет конфигурации", false);
			event.calc();
			eventPart.setModel(event, false);
			eventPart.onCalc(null);
			updateStatus("Расчетная конфигурация создана", false);
		} catch (Exception e) {
			DialogUtil.alertError(e.getMessage());
			updateStatus("Ошибка создания расчетной конфигурации", true);
			e.printStackTrace();
		}
	}
}
