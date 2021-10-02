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
import kz.zvezdochet.service.EventService;

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

			int imported = 0, updated = 0, canceled = 0;
			EventService service = new EventService();
			for (Event event : events) {
				Event back = (Event)service.findBack(event.getId());
				if (null == back || null == back.getId() || 0 == back.getId()) { //запись по backid не найдена
					back = (Event)service.find(event.getId());
					if (null == back.getId()) { //запись по id тоже не найдена, создаём
						event.setBackid(event.getId());
						event.setId(null);
						event.calc(false);
						event.setCalculated(true);
						//event.setRecalculable(true);
						event.save();
						++imported;
						log.append("Новый добавлен: " + event.getId() + " " + event.toLog() + "\n");
					} else { //запись по id найдена
						String bdate = DateUtil.formatCustomDateTime(back.getBirth(), "yyyy-MM-dd");
						String idate = DateUtil.formatCustomDateTime(event.getBirth(), "yyyy-MM-dd");
						//если даты совпадают, перезаписываем, иначе отменяем и пишем об этом в лог
						if (idate.equals(bdate)) {
							event.setBackid(event.getId());
							event.calc(false);
							event.setCalculated(true);
							//event.setRecalculable(true);
							event.save();
							++updated;
							log.append("Старый обновлён: " + event.getId() + " " + event.toLog() + "\n\t");
							log.append("вместо: " + back.getId() + " " + back.toLog() + "\n");
						} else {
							log.append("Новый не соответствует: " + event.getId() + " " + event.toLog() + "\n\t");
							log.append("Старому: " + back.getId() + " " + back.toLog() + "\n");
							++canceled;
						}
					}
				} else { //запись по backid найдена TODO импортировать биографию и журнал
					String bdate = DateUtil.formatCustomDateTime(back.getBirth(), "yyyy-MM-dd");
					String idate = DateUtil.formatCustomDateTime(event.getBirth(), "yyyy-MM-dd");
					//если даты совпадают, перезаписываем, иначе отменяем и пишем об этом в лог
					if (idate.equals(bdate)) {
						event.setBackid(event.getId());
						event.calc(false);
						event.setCalculated(true);
						//event.setRecalculable(true);
						event.save();
						log.append("Связь обновлена: №" + event.getId() + " " + event.toLog() + "\n\t");
						log.append("вместо: " + back.getId() + " " + back.toLog() + "\n");
						++updated;
					} else {
						back.setBackid(0);
						back.save();
						log.append("Новый не соответствует: " + event.getId() + " " + event.toLog() + "\n\t");
						log.append("Старому: " + back.getId() + " " + back.toLog() + "\n");
						++canceled;
					}
				}
			}
			//логируем
			log.append("Добавлено: " + imported + "\t");
			log.append("Обновлено: " + updated + "\t");
			log.append("Отменено: " + canceled + "\n\n");

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
