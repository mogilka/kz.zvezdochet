package kz.zvezdochet.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import kz.zvezdochet.bean.Event;
import kz.zvezdochet.core.handler.Handler;
import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.core.util.IOUtil;
import kz.zvezdochet.part.ImportPart;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Обработчик поиска событий для импорта с сервера
 * @author Nataly Didenko
 *
 */
public class ImportCheckHandler extends Handler {
	@Inject
	private EPartService partService;

	@Execute
	public void execute() {
		try {
			updateStatus("Поиск", false);
			MPart part = partService.findPart("kz.zvezdochet.part.import");
			ImportPart importPart = (ImportPart)part.getObject();
			Date date = importPart.getDate();
			long time = date.getTime() / 1000;

			String res = IOUtil.getUriContent("https://zvezdochet.guru/event/export?datetime=" + time + "&limit=30");
			if (null == res) {
				DialogUtil.alertInfo("Нет данных для импорта");
				return;
			}

			JSONObject obj = new JSONObject(res);
			boolean success = obj.getBoolean("success");
			if (!success) {
				DialogUtil.alertInfo(obj.getString("message"));
				return;
			}

			List<Event> events = new ArrayList<Event>();
			JSONArray list = obj.getJSONArray("events");
			int count = list.length();
			for (int i = 0; i < count; i++) {
				JSONObject object = list.getJSONObject(i);
				Event event = new Event(object);
				events.add(event);
			}
		    importPart.setData(events);
			updateStatus("Поиск завершён", false);
		} catch (Exception e) {
			DialogUtil.alertError(e.getMessage());
			e.printStackTrace();
		}
	}
		
}