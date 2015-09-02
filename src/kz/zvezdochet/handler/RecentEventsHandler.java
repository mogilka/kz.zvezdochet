package kz.zvezdochet.handler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import kz.zvezdochet.bean.Event;
import kz.zvezdochet.core.handler.Handler;
import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.part.SearchPart;
import kz.zvezdochet.service.EventService;
import kz.zvezdochet.util.Constants;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.osgi.service.prefs.Preferences;

/**
 * Поиск списка недавно просматриваемых событий
 * @author Nataly Didenko
 *
 */
public class RecentEventsHandler extends Handler {
	@Inject
	private EPartService partService;

	@Execute
	public void execute() {
		try {
			updateStatus("Поиск", false);
			Preferences preferences = InstanceScope.INSTANCE.getNode("kz.zvezdochet");
			Preferences recent = preferences.node(Constants.PREF_RECENT);
			String eventids = recent.get(Constants.PREF_RECENT_EVENT, "");
			if (eventids.length() > 0) {
				String[] ids = eventids.split(",");
				List<Event> events = new ArrayList<Event>();
				EventService service = new EventService();
				for (String string : ids) {
					try {
						Event event = (Event)service.find(Long.valueOf(string));
						if (event != null)
							events.add(event);
					} catch (Exception e) {
						System.out.println(string + " is not ID");
					}
				}
				MPart part = partService.findPart("kz.zvezdochet.part.events");
			    part.setVisible(true);
			    partService.showPart(part, PartState.VISIBLE);
			    SearchPart searchPart = (SearchPart)part.getObject();
			    searchPart.setData(events);
				updateStatus("Поиск завершён", false);
			} else
				DialogUtil.alertInfo("История пуста");
		} catch (Exception e) {
			DialogUtil.alertError(e.getMessage());
			e.printStackTrace();
		}
	}		
}
