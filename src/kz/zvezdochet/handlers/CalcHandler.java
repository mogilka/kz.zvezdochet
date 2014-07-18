package kz.zvezdochet.handlers;

import kz.zvezdochet.bean.Event;
import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.core.util.DateUtil;
import kz.zvezdochet.parts.EventPart;
import kz.zvezdochet.util.Configuration;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

/**
 * Расчёт конфигурации
 * @author Nataly Didenko
 */
public class CalcHandler {

	@Execute
	public void execute(@Active MPart activePart) {
		try {
			EventPart eventPart = (EventPart)activePart.getObject();
			Event event = (Event)eventPart.getElement(EventPart.MODE_CALC);
			if (null == event) return;
//			updateStatus("Расчет конфигурации", false);
			//new Configuration("12.12.2009", "23:11:16", "6.0", "43.15", "76.55");
			Configuration configuration = new Configuration(
				DateUtil.formatCustomDateTime(event.getBirth(), DateUtil.sdf.toPattern()),
				DateUtil.formatCustomDateTime(event.getBirth(), DateUtil.stf.toPattern()),
				Double.toString(event.getZone()),
				Double.toString(event.getPlace().getLatitude()),
				Double.toString(event.getPlace().getLongitude()));
//			updateStatus("Расчет завершен", false);
			event.setConfiguration(configuration);
			eventPart.setElement(event, false);
			eventPart.onCalc();
//			updateStatus("Расчетная конфигурация создана", false);
		} catch (Exception e) {
			DialogUtil.alertError(e.getMessage());
//			updateStatus("Ошибка создания расчетной конфигурации", true);
			e.printStackTrace();
		}
	}
}
