package kz.zvezdochet.handler;

import java.util.ArrayList;
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
 * @author Natalie Didenko
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
			long id = importPart.getObject();

			url = String.format(url, id, 2);
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
/*
{"success":true,"message":"","models":[{"ID":37730,"name":"Сауль Альварес","Gender":0,"Placeid":1131,"Zone":-6,"Celebrity":1,"Comment":"боксёр","Rectification":2,"RightHanded":1,"InitialDate":"1990-07-18 00:00:00","FinalDate":null,"date":"2015-06-27 23:25:11","human":1,"accuracy":null,"userid":null,"calculated":1,"fancy":"saul-alvares","dst":0,"finalplaceid":null,"top":0,"moondayid":null,"cardkindid":null,"updated_at":"2022-04-23 05:04:37","biography":"","conversation":"","options":null,"name_en":"Saul Alvarez\n","comment_en":"boxer\n"}]}
 */
		} catch (Exception e) {
			DialogUtil.alertError(e);
			e.printStackTrace();
		}
	}
}
