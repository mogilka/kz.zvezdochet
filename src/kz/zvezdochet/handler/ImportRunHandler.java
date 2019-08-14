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
				if (null == back.getId()) { //связанная запись не найдена
					back = (Event)service.find(event.getId());
					if (null == back.getId()) { //оригинальная запись тоже не найдена, создаём
						event.setBackid(event.getId());
						event.setId(null);
						event.calc(false);
						event.setCalculated(true);
						service.save(event);
						++imported;
						log.append("Новый добавлен: " + event.toLog() + "\n");
					} else { //оригинальная запись найдена
						String bdate = DateUtil.formatCustomDateTime(back.getBirth(), "yyyy-MM-dd");
						String idate = DateUtil.formatCustomDateTime(event.getBirth(), "yyyy-MM-dd");
						//если даты совпадают, перезаписываем, иначе отменяем и пишем об этом в лог
						if (idate.equals(bdate)) {
							event.setBackid(event.getId());
							event.calc(false);
							event.setCalculated(true);
							service.save(event);
							++updated;
							log.append("Старый обновлён: " + event.toLog() + "\n");
						} else {
							log.append("Новый не соответствует: " + event.toLog() + "\n\t");
							log.append("Старому: " + back.toLog() + "\n");
							++canceled;
						}
					}
				} else { //связанная запись найдена
					back.setName(event.getName());
					back.setBirth(event.getBirth());
					back.setDeath(event.getDeath());
					back.setFemale(event.isFemale());
					back.setPlaceid(event.getPlaceid());
					back.setFinalPlaceid(event.getFinalplaceid());
					back.setZone(event.getZone());
					back.setDst(event.getDst());
					back.setCelebrity(true);
					back.setComment(event.getComment());
					back.setRectification(event.getRectification());
					back.setRightHanded(event.isRightHanded());
					back.setHuman(event.getHuman());
					back.setAccuracy(event.getAccuracy());
					back.setFancy(event.getFancy());
					event.calc(false);
					event.setCalculated(true);
					service.save(back);
					log.append("Связь обновлена: " + back.toLog() + "\n");
					++updated;
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
		} catch (Exception e) {
			DialogUtil.alertError(e.getMessage());
			e.printStackTrace();
		}
	}
}
