package kz.zvezdochet.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kz.zvezdochet.bean.Aspect;
import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.PositionType;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.bean.SkyPoint;
import kz.zvezdochet.bean.SkyPointAspect;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.util.CalcUtil;
import kz.zvezdochet.core.util.DateUtil;
import kz.zvezdochet.core.util.NumberUtil;
import kz.zvezdochet.core.util.PlatformUtil;
import kz.zvezdochet.service.AspectService;
import kz.zvezdochet.service.AspectTypeService;
import kz.zvezdochet.service.HouseService;
import kz.zvezdochet.service.PlanetService;
import kz.zvezdochet.service.PositionTypeService;
import kz.zvezdochet.sweph.Activator;
import swisseph.SweConst;
import swisseph.SweDate;
import swisseph.SwissEph;
import swisseph.SwissLib;

/**
 * Расчётная конфигурация события
 * @author Nataly Didenko
 */
public class Configuration {
	private List<Model> planetList;
	private	List<Model> houseList;
	private	List<SkyPointAspect> aspectList;
	private Date date;

	/**
	 * Создание пустой расчетной конфигурации
	 * @param date дата
	 * @throws DataAccessException 
	 */
	public Configuration(Date date) throws DataAccessException {
  	  	planetList = new PlanetService().getList();
  	  	houseList = new HouseService().getList();
  	  	aspectList = new ArrayList<SkyPointAspect>();
  	  	this.date = date;
	}

	/**
	 * Создание расчетной конфигурации для заданного момента времени и места
	 * @param event событие
	 * @param date строковое значение даты
	 * @param time строковое значение времени
	 * @param zone строковое значение часового пояса
	 * @param latitude строковое значение широты местности
	 * @param longitude строковое значение долготы местности
	 * @throws DataAccessException 
	 */
	public Configuration(Event event, Date eventdate, String zone, String latitude, String longitude) throws DataAccessException {
  	  	planetList = new PlanetService().getList();
  	  	houseList = new HouseService().getList();
  	  	this.date = eventdate;
  	  	String date = DateUtil.formatCustomDateTime(eventdate, DateUtil.sdf.toPattern());
  	  	String time = DateUtil.formatCustomDateTime(eventdate, DateUtil.stf.toPattern());
		calculate(date, time, zone, latitude, longitude);
		initPlanetStatistics(event);
	}

	/**
	 * Расчет конфигурации гороскопа
	 * @param sdate строковое значение даты
	 * @param stime строковое значение времени
	 * @param szone строковое значение часового пояса
	 * @param slat строковое значение широты местности
	 * @param slon строковое значение долготы местности
	 */
  	private void calculate(String sdate, String stime, String szone, 
  							String slat, String slon) {
		System.out.println("calculate " + sdate + " " + stime + "\n" +
  			szone + " lat " + slat + " lon " + slon);
  		
		try {
	  		//обрабатываем координаты места
	  		double lat = (slat != null && slat.length() > 0) ? Double.parseDouble(slat) : 51.48;
	  		double lon = (slon != null && slon.length() > 0) ? Double.parseDouble(slon) : 0;
	  		int ilondeg, ilonmin, ilonsec, ilatdeg, ilatmin, ilatsec;
	  		ilondeg = CalcUtil.trunc(Math.abs(lon)); //TODO знак точно убираем???
	  		ilonmin = CalcUtil.trunc(Math.abs(lon) - ilondeg) * 100;
	  		ilonsec = 0;
	  		ilatdeg = CalcUtil.trunc(Math.abs(lat));
	  		ilatmin = CalcUtil.trunc(Math.abs(lat) - ilatdeg) * 100;
	  		ilatsec = 0;

	  	  	SwissEph sweph = new SwissEph();
			sweph.swe_set_topo(lon, lat, 0);
	  		long iflag = SweConst.SEFLG_SIDEREAL | SweConst.SEFLG_SPEED | SweConst.SEFLG_TRUEPOS | SweConst.SEFLG_TOPOCTR;
	  		int iyear, imonth, iday, ihour = 0, imin = 0, isec = 0;
//	  	  	String path = "/home/nataly/workspacercp/kz.zvezdochet.sweph/lib/ephe";
			String path = PlatformUtil.getPath(Activator.PLUGIN_ID, "/lib/ephe").getPath(); //$NON-NLS-1$
			System.out.println(path); // /home/nataly/soft/eclipsercp/../../workspacercp/kz.zvezdochet.sweph/lib/ephe/
	  		sweph.swe_set_ephe_path(path);
	  		sweph.swe_set_sid_mode(SweConst.SE_SIDM_DJWHAL_KHUL, 0, 0);

	  		//обрабатываем дату
	  		iday = Integer.parseInt(sdate.substring(0, 2));
	  		imonth = Integer.parseInt(sdate.substring(3, 5));
	  		iyear = Integer.parseInt(sdate.substring(6, 10));
	  		
	  		//обрабатываем время
	  		double timing = Double.parseDouble(NumberUtil.trimLeadZero(stime.substring(0, 2)));
	  		double zone = Double.parseDouble(szone);
	  		if (zone < 0) {
	  			timing = timing - zone;
	  		} else {
	  			if (timing >= zone) {
	  				timing = timing - zone;
	  			} else {
	  				timing = timing + 24 - zone;
	  	  		}
	  		}
	  		if (timing >= 24) timing -= 24;
	  		ihour = (int)Math.round(timing / 1);
	  		imin = Integer.parseInt(NumberUtil.trimLeadZero(stime.substring(3,5)));
	  		isec = Integer.parseInt(NumberUtil.trimLeadZero(stime.substring(6,8)));
	
	  		//обрабатываем время
	  		@SuppressWarnings("unused")
			double tjd, tjdet, tjdut, tsid, armc, dhour, deltat;
	  		@SuppressWarnings("unused")
			double eps_true, nut_long, glon, glat;
	  		dhour = ihour + imin/60.0 + isec/3600.0;
	  		tjd = SweDate.getJulDay(iyear, imonth, iday, dhour, true);
	  		deltat = SweDate.getDeltaT(tjd);
	  		//Universal Time
	  		tjdut = tjd;
	  		tjdet = tjd + deltat;
	  		
	  		//расчёт эфемерид планет
	  		@SuppressWarnings("unused")
			long rflag;
	  		double[] planets = new double[15];
	  		double[] xx = new double[6];
	  		char[] serr = new char[256];
	  		StringBuffer sb = new StringBuffer(new String(serr));
	  		int[] list = getPlanetList();
	  		Planet p;
	  		for (int i = 0; i < list.length; i++) {
	  		    rflag = sweph.swe_calc(tjdet, list[i], (int)iflag, xx, sb);
	  		    planets[i] = xx[0];
	  		    int n = constToPlanet(i);
	  		    if (n >= 0) {
	  		    	p = (Planet)planetList.get(n);
	  	  			p.setCoord(xx[0]);
	  	  			p.setRetrograde(xx[3] < 0);
	  		    }
	  		}
	  		//рассчитываем координату Кету по значению Раху
	  		p = (Planet)planetList.get(3);
	  		if (Math.abs(planets[10]) > 180)
	  			p.setCoord(planets[10] - 180);
	  		else
	  			p.setCoord(planets[10] + 180);
	  		for (int i = 0; i < planets.length; i++) {
	  			System.out.println(((Planet)planetList.get(constToPlanet(i))).getCode() + " " + planets[i]);
	  		}
	
	  		//расчёт куспидов домов
	  		//{ for houses: ecliptic obliquity and nutation }
	  		rflag = sweph.swe_calc(tjdet, SweConst.SE_ECL_NUT, 0, xx, sb);
	  		eps_true = xx[0];
	  		nut_long = xx[2];
	  		//{ geographic position }
	  		glon = ilondeg + ilonmin/60.0 + ilonsec/3600.0;
	  		if (lon < 0) glon = -glon;
	  		glat = ilatdeg + ilatmin/60.0 + ilatsec/3600.0;
	  		if (lat < 0) glat = -glat;
	  		//{ sidereal time }
	  		tsid = new SwissLib().swe_sidtime(tjdut);
	  		tsid = tsid + glon / 15;
	  		armc = tsid * 15;
	  		//{ house method }
	  		double[] ascmc = new double[10];
	  		double[] hcusps = new double[13];
	  		//используем систему Плацидуса
	  		sweph.swe_houses(tjdut, SweConst.SEFLG_SIDEREAL, glat, glon, 'P', hcusps, ascmc);
	  		calcHouseParts(hcusps);
	  		for (int i = 1; i < hcusps.length; i++) 
	  			System.out.println("house " + i + " = " + hcusps[i]);
		} catch (Exception e) {
			e.printStackTrace();
		}
  	}
  	
  	/**
  	 * Метод, возвращающий массив планет по Швейцарским эфемеридам
  	 * @return массив индексов планет
  	 */
  	private int[] getPlanetList() {
  		int[] list = new int[15];
  		for (int i = SweConst.SE_SUN; i < SweConst.SE_MEAN_NODE; i++)
  			list[i] = i;
  		list[10] = SweConst.SE_TRUE_NODE;
  		list[11] = SweConst.SE_MEAN_APOG;
  		list[12] = SweConst.SE_CHIRON;
  		list[13] = SweConst.SE_WHITE_MOON;
  		list[14] = SweConst.SE_PROSERPINA;
  		return list;
  	}
	
  	/**
  	 * Поиск соответствия планет Швейцарских эфемерид
  	 * с их эквивалентами в данной программе
  	 * @param i индекс планеты в Швейцарских эфемеридах
  	 * @return индекс планеты в системе
  	 */
  	private int constToPlanet(int i) {
  		switch(i) {
  		case 0: case 1: return i;
  		case 2: case 3: case 4: return i + 2;
  		case 5: case 6: return i + 4;
  		case 7: case 8: case 9: return i + 5;
  		case 10: return 2;
  		case 11: return 8;
  		case 12: return 11;
  		case 13: return 7;
  		case 14: return 15;
  		default: return -1;
  		}
  	}
  	
  	/**
  	 * Расчет третей домов
  	 * @param houses массив вычисленных основных домов
  	 */
  	private void calcHouseParts(double[] houses) {
		try {
	  		byte multiple;
	  		//шерстим трети домов, минуя основные куспиды
	  		for (int j = 1; j < 37; j++) {
	  			House h = (House)houseList.get(j - 1);
	  			int i = CalcUtil.trunc((j + 2) / 3);
	  			if (h.isMain())
	  	  			h.setCoord(houses[i]);
	  			else {
	  				double one = houses[i];
	  				if (12 == i) i = 0;
	  				double two = houses[i + 1];
	  				if ((one > 300) && (one < 360) && (two < 60))
	  					two = two + 360;
	  				//вычисляем и сохраняем значения вершин третей дома
	  				//учитываем, что индекс последней трети всегда кратен трем
	  				if (j % 3 == 0) 
	  					multiple = 2; 
	  				else 
	  					multiple = 1;
	  				double res = multiple * ((two - one) / 3) + one;
	  				if (res > 360) 
	  					res = res - 360; 
	  	  			h.setCoord(res);
	  			}
	  		}
		} catch (Exception e) {
			e.printStackTrace();
		}
  	}

	public List<Model> getPlanets() {
		return planetList;
	}

	public List<Model> getHouses() {
		return houseList;
	}

	public List<SkyPointAspect> getAspects() {
		return aspectList;
	}

	/**
	 * Проверка инициализации домов планет
	 * @return true - для планет определены дома,<br>
	 * false - планеты не определены либо для них не указаны позиции в домах
	 */
	private boolean isPlanetHoused() {
		return (planetList != null &&
			((Planet)planetList.get(0)).getHouse() != null);
	}

	/**
	 * Определение позиций планет в домах
	 */
	public void initPlanetHouses() {
		if (isPlanetHoused()) return;
		for (Model model : planetList) {
			Planet planet = (Planet)model;
			for (int j = 0; j < houseList.size(); j++) { 
				House house = ((House)houseList.get(j));
				double pcoord = planet.getCoord();
				Double hmargin = (j == houseList.size() - 1) ?
					((House)houseList.get(0)).getCoord() : 
					((House)houseList.get(j + 1)).getCoord();
				double[] res = checkMarginalValues(house.getCoord(), hmargin, pcoord);
				hmargin = res[0];
				pcoord = res[1];
				//если градус планеты находится в пределах куспидов
				//текущей и предыдущей трети домов,
				//запоминаем, в каком доме находится планета
				if (Math.abs(pcoord) < hmargin & 
						Math.abs(pcoord) >= house.getCoord())
					planet.setHouse(house);
			}
		}
	}

	/**
	 * Корректировка координат для определения
	 * местонахождения объекта на участке космограммы
	 * (используется для домов)
	 * @param margin1 начальный градус сектора
	 * @param margin2 конечный градус сектора
	 * @param point координата точки
	 * @return массив модифицированных значений точки и верхней границы сектора
	 */ 
	private double[] checkMarginalValues(double margin1, double margin2, double point) {
		//если границы сектора находятся по разные стороны нуля
		if (margin1 > 200 & margin2 < 160) {
			//если координата точки находится по другую сторону
			//от нуля относительно второй границы,
			//увеличиваем эту границу на 2*Pi
			if (Math.abs(point) > 200)
				margin2 += 360;
			else if (Math.abs(point) < 160) {
				//если градус планеты меньше 160,
				//увеличиваем его, а также вторую границу на 2*Pi
		       point = Math.abs(point) + 360;
		       margin2 += 360;
			}
		}
		//если же границы находятся по одну сторону от нуля,
		//оставляем координаты как есть
		return new double[] {margin2, point};
	}

	/**
	 * Определение позиций планет в знаках
	 * @param event событие
	 */
	public void initPlanetSigns(Event event) throws DataAccessException {
		if (isPlanetSigned()) return;
		for (Model model : planetList) {
			Planet planet = (Planet)model;
			Sign sign = SkyPoint.getSign(planet.getCoord(), event.getBirthYear());
			planet.setSign(sign);
		}
	}

	/**
	 * Проверка инициализации знаков планет
	 * @return true - для планет определены знаки,<br>
	 * false - планеты не определены либо для них не указаны позиции в знаках
	 */
	private boolean isPlanetSigned() {
		return (planetList != null &&
			((Planet)planetList.get(0)).getSign() != null);
	}

	/**
	 * Расчёт аспектов планет.
	 * Параллельно выполняется составление статистики по каждой планете:
	 * сколько у неё аспектов всех существующих типов
	 * @throws DataAccessException 
	 */
	public void initPlanetAspects() throws DataAccessException {
		try {
			if (aspectList != null && aspectList.size() > 0) return;
	  	  	aspectList = new ArrayList<SkyPointAspect>();
			List<Model> aspects = new AspectService().getList();
			List<Model> aspectTypes = new AspectTypeService().getList();
			if (planetList != null) 
				for (Model model : planetList) {
					Planet p = (Planet)model;
					
					//создаем карту статистики по аспектам планеты
					Map<String, Integer> aspcountmap = new HashMap<String, Integer>();
					Map<String, String> aspmap = new HashMap<String, String>();
					for (Model asptype : aspectTypes)
						aspcountmap.put(((AspectType)asptype).getCode(), 0);
					
					for (Model model2 : planetList) {
						Planet p2 = (Planet)model2;
						if (p.getCode().equals(p2.getCode())) continue;
//						if (p.getNumber() > p2.getNumber()) continue;
						double res = CalcUtil.getDifference(p.getCoord(), p2.getCoord());
						for (Model realasp : aspects) {
							Aspect a = (Aspect)realasp;
							if (a.isAspect(res)) {
								SkyPointAspect aspect = new SkyPointAspect();
								aspect.setSkyPoint1(p);
								aspect.setSkyPoint2(p2);
								aspect.setAspect(a);
								aspectList.add(aspect);
								
								//фиксируем аспекты планеты
								aspmap.put(p2.getCode(), a.getCode());
								//суммируем аспекты каждого типа для планеты
								String aspectTypeCode = a.getType().getCode();
								int score = aspcountmap.get(aspectTypeCode);
								//для людей считаем только аспекты главных планет
								aspcountmap.put(aspectTypeCode, ++score);

								//суммируем сильные аспекты
								aspectTypeCode = "COMMON";
								if (a.getType().getParentType() != null &&
										a.getType().getParentType().getCode() != null &&
										a.getType().getParentType().getCode().equals(aspectTypeCode)) {
									score = aspcountmap.get(aspectTypeCode);
									aspcountmap.put(aspectTypeCode, ++score);
								}
							}
						}
					}
					p.setAspectCountMap(aspcountmap);
					p.setAspectMap(aspmap);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Расчет статистики по каждой планете.
	 * Учитываются следующие факторы:<br>
	 * - позиция планеты на угловых точках<br>
	 * - является ли планета управителем домов<br>
	 * - является ли планета управителем угловых домов<br>
	 * - является ли планета одновременно управителем и хозяином дома<br>
	 * - насколько планета приближена к Солнцу<br>
	 * - позиции планеты (обитель, экзальтация, изгнание, падение)<br>
	 * - аспектированность планеты (шахта, пораженность, непораженность)<br>
	 * - ретроградность планеты<br>
	 * - заполненность знака и дома, хозяином которых является планета<br>
	 * - благоприятная связь (хороший или нейтральный аспект планеты с Раху и Селеной)<br>
	 * - неблагоприятная связь (плохой или нейтральный аспект планеты с Кету и Лилит)<br>
	 * - нахождение планеты в градусе<br>
	 * //TODO расчитать все важные позиции планет
	 * определяем позиции планет в целом (обитель изгнание экзальтация падение)<br>
	 * определяем пустые знаки<br>
	 * определяем пустые дома а также включенные и какие там ещё есть<br>
	 * //определяем управителей домов
		//определяем знак, где находится куспид дома,
		//и определяем планету знака
	 * @param event событие
	 */
	public void initPlanetStatistics(Event event) throws DataAccessException {
		initPlanetAspects();
		initDamagedPlanets();
		initBrokenPlanets();
		initAngularPlanets();
		initSunNeighbours();
		initPlanetPositions(event);
	}

	/**
	 * Инициализация позиций планет
	 * @param event событие
	 */
	private void initPlanetPositions(Event event) {
		try {
			initPlanetSigns(event);
			initPlanetHouses();

			PlanetService service = new PlanetService();
			for (Model model : planetList) {
				Planet planet = (Planet)model;
				for (Model type : new PositionTypeService().getList()) {
					PositionType pType = (PositionType)type;
					String pCode = pType.getCode();
					boolean daily = true;
					if (!planet.getCode().equals("Sun") &&
							(pCode.equals("HOME") || pCode.equals("EXILE")))
						daily = DateUtil.isDaily(date);

					Sign sign = service.getSignPosition(planet, pCode, daily);
					if (sign != null && sign.getId() == planet.getSign().getId()) {
						switch (pCode) {
						case "HOME": planet.setSignHome(true); break;
						case "EXALTATION": planet.setSignExaltated(true); break;
						case "EXILE": planet.setSignExile(true); break;
						case "DECLINE": planet.setSignDeclined(true); break;
						}
					}

					if (null == planet.getHouse()) continue;
					House house = service.getHousePosition(planet, pCode, daily);
					int hnumber = CalcUtil.trunc((planet.getHouse().getNumber() + 2) / 3);
					if (house != null && CalcUtil.trunc((house.getNumber() + 2) / 3) == hnumber) {
						switch (pCode) {
						case "HOME": planet.setHouseHome(true); break;
						case "EXALTATION": planet.setHouseExaltated(true); break;
						case "EXILE": planet.setHouseExile(true); break;
						case "DECLINE": planet.setHouseDeclined(true); break;
						}
					}
				}
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Поиск поражённых и непоражённых планет
	 */
	private void initDamagedPlanets() {
		for (Model model : planetList) {
			Planet planet = (Planet)model;
			
			//сравнение количества хороших и плохих аспектов
			int good = planet.getAspectCountMap().get("POSITIVE") +
					planet.getAspectCountMap().get("POSITIVE_HIDDEN");
			int bad = planet.getAspectCountMap().get("NEGATIVE") +
					planet.getAspectCountMap().get("NEGATIVE_HIDDEN");
			if (0 == good && bad > 2) {
				planet.setDamaged(true);
				System.out.println(planet.getCode() + " is damaged");
				continue;
			} else if (0 == bad && good > 0) {
				planet.setPerfect(true); 
				System.out.println(planet.getCode() + " is perfect");
				continue;
			}
			final String LILITH = "Lilith";
			for (SkyPointAspect aspect : aspectList) {
				if (aspect.getAspect().getCode().equals("CONJUNCTION") &&
						aspect.getSkyPoint1().getCode().equals(LILITH) &&
						!aspect.getSkyPoint2().getCode().equals(LILITH) &&
						aspect.getSkyPoint2().getCode().equals(planet.getCode())) {
					planet.setDamaged(true); 
					System.out.println(planet.getCode() + " is damaged");
					continue;
				}
			}
		}
	}

	/**
	 * Поиск ослабленных планет
	 */
	private void initBrokenPlanets() {
		for (Model model : planetList) {
			Planet planet = (Planet)model;
			final String KETHU = "Kethu";
			for (SkyPointAspect aspect : aspectList) {
				if (aspect.getAspect().getCode().equals("CONJUNCTION") &&
						aspect.getSkyPoint1().getCode().equals(KETHU) &&
						!aspect.getSkyPoint2().getCode().equals(KETHU) &&
						aspect.getSkyPoint2().getCode().equals(planet.getCode())) {
					planet.setBroken(true); 
					System.out.println(planet.getCode() + " is broken");
					continue;
				}
			}
		}
	}
	
	/**
	 * Определяем ближайшие планеты к Солнцу
	 */
	private void initSunNeighbours() {
		Planet sun = (Planet)planetList.get(0);
		List<Planet> planets = new ArrayList<Planet>();
		planets.add(sun);
		//определяем ядро и пояс
		for (int i = 1; i < planetList.size(); i++) {
			Planet planet = (Planet)planetList.get(i);
			double res = Math.abs(CalcUtil.getDifference(sun.getCoord(), planet.getCoord()));
			if (res < 0.18)
				planet.setKernel(true);
			else if (res <= 3)
				planet.setBelt(true);
			else
				planets.add(planet);
		}
		//упорядочиваем массив планет по возрастанию
		Collections.sort(planets, new SkyPointComparator());
		//определяем новый индекс солнца
		int sunindex = planets.indexOf(sun);

		//определяем щит и меч
		int isword = (sunindex == planets.size() - 1) ? 0 : sunindex + 1;
		Planet sword = planets.get(isword);
		int pindex = planetList.indexOf(sword);
		((Planet)planetList.get(pindex)).setSword(true);

		int ishield = (0 == sunindex) ? planets.size() - 1 : sunindex - 1;
		Planet shield = planets.get(ishield);
		pindex = planetList.indexOf(shield);
		((Planet)planetList.get(pindex)).setShield(true);
	}

	public void setPlanets(List<Model> planets) {
		planetList = planets;
	}
	public void setHouses(List<Model> houses) {
		houseList = houses;
	}

	/**
	 * Поиск угловых планет, расположенных на ASC, IC, DSC, MC
	 */
	private void initAngularPlanets() {
		Model[] hangular = {
			houseList.get(0),
			houseList.get(9),	
			houseList.get(18),	
			houseList.get(27)	
		};
		for (Model model : hangular) {
			House house = (House)model;
			int prev = (1 == house.getNumber()) ? 34 : house.getNumber() - 3;
			int next = house.getNumber() + 3;
			double phouse = ((House)houseList.get(prev)).getCoord();
			double nhouse = ((House)houseList.get(next)).getCoord();

			for (Model pmodel : planetList) {
				Planet planet = (Planet)pmodel;
				double[] res = checkMarginalValues(phouse, nhouse, planet.getCoord());
				if (Math.abs(res[1]) < res[0] & 
						Math.abs(res[1]) >= nhouse) {
					switch (house.getNumber()) {
					case 1: planet.setOnASC(true); break;
					case 10: planet.setOnIC(true); break;
					case 19: planet.setOnDSC(true); break;
					case 28: planet.setOnMC(true); break;
					}
				}
			}
		}
	}
}
