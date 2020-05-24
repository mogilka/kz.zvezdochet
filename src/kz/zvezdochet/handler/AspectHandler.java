package kz.zvezdochet.handler;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

import kz.zvezdochet.bean.Aspect;
import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.SkyPointAspect;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.handler.Handler;
import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.core.util.CalcUtil;
import kz.zvezdochet.part.AspectPart;
import kz.zvezdochet.part.EventPart;
import kz.zvezdochet.service.AspectService;

/**
 * Расчёт аспектов
 * @author Natalie Didenko
 *
 */
public class AspectHandler extends Handler {
	@Inject
	private EPartService partService;

	@Execute
	public void execute(@Active MPart activePart) {
		try {
			EventPart eventPart = (EventPart)activePart.getObject();
			Event event = (Event)eventPart.getModel(EventPart.MODE_CALC, true);
			if (null == event) return;
			if (null == event.getPlanets()) return; //TODO выдавать сообщение
			updateStatus("Расчёт аспектов планет", false);

			Collection<Planet> planets = event.getPlanets().values();
			int pcount = planets.size();
			Object[][] data = new Object[pcount][pcount + 1];
			//заполняем заголовки строк названиями планет и их координатами
			for (Planet planet : planets)
				data[planet.getId().intValue() - 19][0] = planet.getName() + " (" + CalcUtil.roundTo(planet.getLongitude(), 1) + ")";

			//формируем массив аспектов планет
			List<Model> aspects = new AspectService().getList();
			for (Planet planet : planets) {
				for (Planet planet2 : planets) {
					if (planet.getId().equals(planet2.getId())) {
						data[planet.getId().intValue() - 19][planet2.getId().intValue() - 18] = null;
						continue;
					}
					double res = CalcUtil.getDifference(planet.getLongitude(), planet2.getLongitude());
					SkyPointAspect aspect = new SkyPointAspect();
					aspect.setSkyPoint1(planet);
					aspect.setSkyPoint2(planet2);
					aspect.setScore(CalcUtil.roundTo(res, 2));
					for (Model realasp : aspects) {
						Aspect a = (Aspect)realasp;
						if (a.isAspect(res)) {
							aspect.setAspect(a);
							aspect.setExact(a.isExact(res));
							aspect.setApplication(a.isApplication(res));
							continue;
						}
					}
					data[planet.getId().intValue() - 19][planet2.getId().intValue() - 18] = aspect;
				}
			}
			updateStatus("Расчёт аспектов завершён", false);

			updateStatus("Расчёт аспектов домов", false);
			if (null == event.getHouses()) return; //TODO выдавать сообщение
			Collection<House> houses = event.getHouses().values();
			int hcount = houses.size();
			Object[][] datah = new Object[pcount][hcount + 1];
			//заполняем заголовки строк названиями планет и их координатами
			for (Planet planet : planets)
				datah[planet.getId().intValue() - 19][0] = planet.getName() + " (" + CalcUtil.roundTo(planet.getLongitude(), 1) + ")";

			//формируем массив аспектов домов
			for (House house : houses) {
				for (Planet planet : planets) {
					double res = CalcUtil.getDifference(planet.getLongitude(), house.getLongitude());
					SkyPointAspect aspect = new SkyPointAspect();
					aspect.setSkyPoint1(planet);
					aspect.setSkyPoint2(house);
					aspect.setScore(CalcUtil.roundTo(res, 2));
					for (Model realasp : aspects) {
						Aspect a = (Aspect)realasp;
						if (a.isAspect(res)) {
							aspect.setAspect(a);
							aspect.setExact(a.isExact(res));
							aspect.setApplication(a.isApplication(res));
							continue;
						}
					}
					datah[planet.getId().intValue() - 19][house.getId().intValue() - 141] = aspect;
				}
			}
			updateStatus("Расчёт аспектов домов завершён", false);
			
			MPart part = partService.findPart("kz.zvezdochet.part.aspect");
		    part.setVisible(true);
		    partService.showPart(part, PartState.VISIBLE);
		    AspectPart aspectPart = (AspectPart)part.getObject();
		    aspectPart.setEvent(event);
		    aspectPart.setData(data);
		    aspectPart.setDatah(datah);
			updateStatus("Таблица аспектов сформирована", false);
		} catch (Exception e) {
			DialogUtil.alertError(e);
			updateStatus("Ошибка", true);
			e.printStackTrace();
		}
	}
}
