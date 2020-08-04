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
			Event event = (Event)eventPart.getModel(EventPart.MODE_ASPECT_PLANET_PLANET, true);
			if (null == event) return;
			if (null == event.getPlanets()) return; //TODO выдавать сообщение
			updateStatus("Расчёт аспектов между планетами", false);

			Collection<Planet> planets = event.getPlanets().values();
			int pcount = planets.size();
			Object[][] datap2p = new Object[pcount][pcount + 1];
			//заполняем заголовки строк названиями планет и их координатами
			for (Planet planet : planets)
				datap2p[planet.getId().intValue() - 19][0] = planet.getName() + " (" + CalcUtil.roundTo(planet.getLongitude(), 1) + ")";

			//формируем массив аспектов планет
			List<Model> aspects = new AspectService().getList();
			for (Planet planet : planets) {
				for (Planet planet2 : planets) {
					if (planet.getId().equals(planet2.getId())) {
						datap2p[planet.getId().intValue() - 19][planet2.getId().intValue() - 18] = null;
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
					datap2p[planet.getId().intValue() - 19][planet2.getId().intValue() - 18] = aspect;
				}
			}
			updateStatus("Расчёт аспектов между планетами завершён", false);

			updateStatus("Расчёт аспектов планет с домами", false);
			if (null == event.getHouses()) return; //TODO выдавать сообщение
			Collection<House> houses = event.getHouses().values();
			int hcount = houses.size();
			Object[][] datap2h = new Object[pcount][hcount + 1];
			//заполняем заголовки строк названиями планет и их координатами
			for (Planet planet : planets)
				datap2h[planet.getId().intValue() - 19][0] = planet.getName() + " (" + CalcUtil.roundTo(planet.getLongitude(), 1) + ")";

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
					datap2h[planet.getId().intValue() - 19][house.getId().intValue() - 141] = aspect;
				}
			}
			updateStatus("Расчёт аспектов планет с домами завершён", false);

			updateStatus("Расчёт аспектов домов с домами", false);
			Object[][] datah2h = new Object[hcount][hcount + 1];
			//заполняем заголовки строк названиями планет и их координатами
			for (House house : houses)
				datah2h[house.getId().intValue() - 142][0] = house.getName() + " (" + CalcUtil.roundTo(house.getLongitude(), 1) + ")";

			//формируем массив аспектов домов
			for (House house : houses) {
				for (House house2 : houses) {
					if (house.getId().equals(house2.getId())) {
						datah2h[house.getId().intValue() - 142][house2.getId().intValue() - 141] = null;
						continue;
					}

					double res = CalcUtil.getDifference(house2.getLongitude(), house.getLongitude());
					SkyPointAspect aspect = new SkyPointAspect();
					aspect.setSkyPoint1(house);
					aspect.setSkyPoint2(house2);
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
					datah2h[house.getId().intValue() - 142][house2.getId().intValue() - 141] = aspect;
				}
			}
			updateStatus("Расчёт аспектов домов с домами завершён", false);

			MPart part = partService.findPart("kz.zvezdochet.part.aspect");
		    part.setVisible(true);
		    partService.showPart(part, PartState.VISIBLE);
		    AspectPart aspectPart = (AspectPart)part.getObject();
		    aspectPart.setEvent(event);
		    aspectPart.setData(datap2p);
		    aspectPart.setDatap2h(datap2h);
		    aspectPart.setDatah2h(datah2h);
			updateStatus("Таблица аспектов сформирована", false);
		} catch (Exception e) {
			DialogUtil.alertError(e);
			updateStatus("Ошибка", true);
			e.printStackTrace();
		}
	}
}
