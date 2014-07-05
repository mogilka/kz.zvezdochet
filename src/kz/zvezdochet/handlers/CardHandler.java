package kz.zvezdochet.handlers;

import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.parts.EventPart;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Execute;
//import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
//import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;

/**
 * Перерисовка космограммы
 * @author Nataly Didenko
 */
public class CardHandler {

	@Execute
	public void execute(@Active MPart activePart) {
		try {
			List<String> params = getAspectParams(activePart);
			EventPart eventPart = (EventPart)activePart.getObject();
			eventPart.refreshCard(params);
//			updateStatus("Расчет завершен", false);
		} catch (Exception e) {
			DialogUtil.alertError(e.getMessage());
//			updateStatus("Ошибка создания расчетной конфигурации", true);
			e.printStackTrace();
		}
	}

	protected static List<String> getAspectParams(MPart activePart) {
		List<String> params = new ArrayList<String>();
		params.add("NEUTRAL");
		for (MToolBarElement item : activePart.getToolbar().getChildren()) {
			HandledToolItemImpl hti = (HandledToolItemImpl)item;
//			MHandledToolItem hti = (MHandledToolItem)item;
			if (hti.isSelected())
				switch (hti.getElementId()) {
				case "kz.zvezdochet.handledtoolitem.harmonic":
					params.add("POSITIVE");
					params.add("POSITIVE_HIDDEN");
					break;
				case "kz.zvezdochet.handledtoolitem.disharmonic":
					params.add("NEGATIVE");
					params.add("NEGATIVE_HIDDEN");
					break;
				case "kz.zvezdochet.handledtoolitem.creative":
					params.add("CREATIVE");
					break;
				case "kz.zvezdochet.handledtoolitem.karmic":
					params.add("KARMIC");
					break;
				case "kz.zvezdochet.handledtoolitem.spiritual":
					params.add("SPIRITUAL");
					params.add("ENSLAVEMENT");
					params.add("DAO");
					params.add("MAGIC");
					break;
				case "kz.zvezdochet.handledtoolitem.progressive":
					params.add("PROGRESSIVE");
					params.add("TEMPTATION");
					break;
				}
		}
//		for (String param : params)
//			System.out.print(param + ", ");
		return params;
	}
}
