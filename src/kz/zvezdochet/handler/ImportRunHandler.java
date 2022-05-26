package kz.zvezdochet.handler;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import kz.zvezdochet.Activator;
import kz.zvezdochet.bean.Event;
import kz.zvezdochet.core.handler.Handler;
import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.core.util.DateUtil;
import kz.zvezdochet.core.util.PlatformUtil;
import kz.zvezdochet.part.ImportPart;

public class ImportRunHandler extends Handler {
	@Inject
	private EPartService partService;

	@Execute
	public void execute() {
		try {
			updateStatus("Импорт", false);
			MPart part = partService.findPart("kz.zvezdochet.part.import");
			ImportPart importPart = (ImportPart)part.getObject();
			@SuppressWarnings("unchecked")
			List<Event> events = (List<Event>)importPart.getData();

			StringBuffer log = new StringBuffer();
			log.append(DateUtil.formatDateTime(new Date()) + "\n\n");

			int imported = 0;
			for (Event event : events) {
				event.setId(null);
				event.calc(true);
				event.setCalculated(true);
				event.save();
				log.append("Новый добавлен: " + event.getId() + " " + event.toLog() + "\n");
				++imported;
			}
			//логируем
			log.append("Добавлено: " + imported + "\t");

			String datafile = PlatformUtil.getPath(Activator.PLUGIN_ID, "/out/import.log").getPath(); //$NON-NLS-1$
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(datafile, true), "UTF-8"));
			writer.append(log);
			writer.close();
			//TODO показывать диалог, что документ сформирован
			//а ещё лучше открывать его
			System.out.println("Импорт завершён");
			updateStatus("Импорт завершён", false);
			DialogUtil.alertInfo("Импорт завершён");
		} catch (Exception e) {
			DialogUtil.alertError(e);
			e.printStackTrace();
		}
	}
}
