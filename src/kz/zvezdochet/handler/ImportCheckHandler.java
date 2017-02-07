package kz.zvezdochet.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.json.JSONArray;
import org.json.JSONObject;

import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.Place;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.handler.Handler;
import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.core.util.IOUtil;
import kz.zvezdochet.part.ImportPart;

/**
 * Обработчик поиска событий для импорта с сервера
 * @author Nataly Didenko
 *
 */
public class ImportCheckHandler extends Handler {
	@Inject
	private EPartService partService;
	private String partid;

	@Execute
	public void execute(@Named("kz.zvezdochet.commandparameter.importcheck") String dict,
			@Named("kz.zvezdochet.commandparameter.importurl") String url) {
		try {
			updateStatus("Поиск", false);
			partid = dict;
			MPart part = partService.findPart(dict);
			ImportPart importPart = (ImportPart)part.getObject();
			Date date = importPart.getDate();
			long time = date.getTime() / 1000;

			url = String.format(url, time);
			System.out.println(url);
			String res = IOUtil.getUriContent(url);
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

			List<Model> models = new ArrayList<Model>();
			JSONArray list = obj.getJSONArray("models");
			int count = list.length();
			for (int i = 0; i < count; i++) {
				JSONObject object = list.getJSONObject(i);
				Model model = null;
				if (partid.equals("kz.zvezdochet.part.import"))
					model = new Event(object);
				else if (partid.equals("kz.zvezdochet.editor.part.importplace"))
					model = new Place(object);
				if (model != null)
					models.add(model);
			}
		    importPart.setData(models);
			updateStatus("Поиск завершён", false);
		} catch (Exception e) {
			DialogUtil.alertError(e.getMessage());
			e.printStackTrace();
		}
	}
}
