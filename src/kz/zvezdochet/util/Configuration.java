package kz.zvezdochet.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kz.zvezdochet.bean.Aspect;
import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Ingress;
import kz.zvezdochet.bean.IngressType;
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
import kz.zvezdochet.service.IngressTypeService;
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
	private Map<Long, Planet> planetList;
	private	List<Model> houseList;
	private	List<SkyPointAspect> aspectList;
	private	List<SkyPointAspect> aspecthList;
	private Date date;
	private Event event;
	private List<Model> ingressList;

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	/**
	 * Создание пустой расчетной конфигурации
	 * @param date дата
	 * @throws DataAccessException 
	 */
	public Configuration(Date date) throws DataAccessException {
  	  	planetList = new HashMap<>();
		List<Model> list = new PlanetService().getList();
		for (Model model : list)
			planetList.put(model.getId(), (Planet)model);

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
	 * @param initstat признак расчёта статистики планет
	 * @throws DataAccessException 
	 */
	public Configuration(Event event, Date eventdate, String zone, String latitude, String longitude, boolean initstat) throws DataAccessException {
		setEvent(event);
  	  	planetList = new HashMap<>();
		List<Model> list = new PlanetService().getList();
		for (Model model : list)
			planetList.put(model.getId(), (Planet)model);

  	  	houseList = new HouseService().getList();
  	  	this.date = eventdate;
  	  	String date = DateUtil.formatCustomDateTime(eventdate, DateUtil.sdf.toPattern());
  	  	String time = DateUtil.formatCustomDateTime(eventdate, DateUtil.stf.toPattern());
		calculate(date, time, zone, latitude, longitude);
		initPlanetAspects();
		if (initstat)
			initPlanetStatistics();
	}

	/**
	 * Система домов
	 * P	Placidus
	 * K	Koch
	 * O	Porphyrius
	 * R	Regiomontanus
	 * C	Campanus
	 * A E	Equal (cusp 1 is Ascendant)
	 * V	Vehlow equal (Asc. in middle of house 1)
	 * W	Whole sign
	 * X	axial rotation system / meridian system / zariel
	 * H	azimuthal or horizontal system
	 * T	Polich/Page (“topocentric” system)
	 * B	Alcabitus
	 * M	Morinus
	 * U	Krusinski-Pisa
	 * G	Gauquelin sector
	 * Y	APC houses
	 */
	private char hsys = 'P';

	 
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
		//System.out.println("calculate\t" + sdate + "\t" + stime + "\tzone:\t" + szone + "\tlat\t" + slat + "\tlon\t" + slon);
  		
		try {
	  		//обрабатываем координаты места
	  		double lat = (slat != null && slat.length() > 0) ? Double.parseDouble(slat) : 51.48;
	  		double lon = (slon != null && slon.length() > 0) ? Double.parseDouble(slon) : 0;
	  		int ilondeg, ilonmin, ilonsec, ilatdeg, ilatmin, ilatsec;
	  		ilondeg = (int)lon;
	  		ilonmin = (int)Math.round((Math.abs(lon) - Math.abs(ilondeg)) * 100);
	  		ilonsec = 0;
	  		ilatdeg = (int)lat;
	  		ilatmin = (int)Math.round((Math.abs(lat) - Math.abs(ilatdeg)) * 100);
	  		ilatsec = 0;

	  	  	SwissEph sweph = new SwissEph();
			sweph.swe_set_topo(lon, lat, 0);
	  		long iflag = SweConst.SEFLG_SWIEPH | SweConst.SEFLG_SIDEREAL | SweConst.SEFLG_SPEED | SweConst.SEFLG_TRUEPOS | SweConst.SEFLG_TOPOCTR;
//	  	  	String path = "/home/nataly/workspace/kz.zvezdochet.sweph/lib/ephe";
			String path = PlatformUtil.getPath(Activator.PLUGIN_ID, "/lib/ephe").getPath(); //$NON-NLS-1$
	  		sweph.swe_set_ephe_path(path);
	  		sweph.swe_set_sid_mode(SweConst.SE_SIDM_DJWHAL_KHUL, 0, 0);

	  		//обрабатываем дату
	  		int iyear, imonth, iday, ihour = 0, imin = 0, isec = 0;
	  		iday = Integer.parseInt(sdate.substring(0, 2));
	  		imonth = Integer.parseInt(sdate.substring(3, 5));
	  		iyear = Integer.parseInt(sdate.substring(6, 10));
	  		
	  		//обрабатываем время
	  		double timing = Double.parseDouble(NumberUtil.trimLeadZero(stime.substring(0, 2))); //час по местному времени
	  		double zone = Double.parseDouble(szone); //зона
	  		if (zone < 0) {
	  			if (timing < (24 + zone))
	  				timing -= zone;
	  			else {
	  				/*
	  				 * Если час больше разности 24 часов и зоны, значит по Гринвичу будет следующий день,
	  				 * поэтому нужно увеличить указанную дату на 1 день
	  				 */
	  				timing = timing - zone - 24;
	  				if (iday < 28)
	  					++iday;
	  				else if (31 == iday) {
	  					iday = 1;  							
	  					if (12 == imonth) {
	  	  					++iyear;
	  	  					imonth = 1;
	  					} else
	  						++imonth;
	  				} else if (30 == iday) {
	  					if (Arrays.asList(new Integer[] {4,6,9,11}).contains(imonth)) {
	  						++imonth;
	  						iday = 1;
	  					} else
	  						iday = 31;
	  				} else if (2 == imonth) {
	  					if (29 == iday) {
	  	  					imonth = 3;
	  	  					iday = 1;
	  					} else if (28 == iday) {
	  						if (DateUtil.isLeapYear(iyear))
	  							iday = 29;
	  						else {
	  							imonth = 3;
	  							iday = 1;
	  						}
	  					}
	  				} else //28 и 29 числа месяцев кроме февраля
	  					++iday;
	  			}
	  		} else {
	  			if (timing >= zone)
	  				timing -= zone;
	  			else {
	  				/*
	  				 * Если час меньше зоны, значит по Гринвичу будет предыдущий день,
	  				 * поэтому нужно уменьшить указанную дату на 1 день
	  				 */
	  				timing = timing + 24 - zone;
	  				if (iday > 1)
	  					--iday;
	  				else {
	  					if (1 == imonth) {
	  						--iyear;
	  						imonth = 12;
	  						iday = 31;
	  					} else if (3 == imonth) {
	  						imonth = 2;
	  						iday = DateUtil.isLeapYear(iyear) ? 29 : 28;
	  					} else if (Arrays.asList(new Integer[] {2,4,6,8,9,11}).contains(imonth)) {
	  						--imonth;
	  						iday = 31;
	  					} else if (Arrays.asList(new Integer[] {5,7,10,12}).contains(imonth)) {
	  						--imonth;
	  						iday = 30;
	  					}
	  				}
	  			}
	  		}
	  		if (timing >= 24)
	  			timing -= 24;
	  		ihour = (int)timing; //гринвичский час
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
	  		    rflag = sweph.swe_calc_ut(tjdut, list[i], (int)iflag, xx, sb);
	  		    planets[i] = xx[0];
	  		    long n = constToPlanet(i);
	  		    if (n >= 0) {
	  		    	p = planetList.get(n);
	  	  			p.setCoord(xx[0]);
	  	  			if (xx[3] < 0)
	  	  				p.setRetrograde();
	  		    }
	  		}
	  		//рассчитываем координату Кету по значению Раху
	  		p = planetList.get(22L);
	  		if (Math.abs(planets[10]) > 180)
	  			p.setCoord(planets[10] - 180);
	  		else
	  			p.setCoord(planets[10] + 180);
	
	  		//расчёт куспидов домов
	  		//{ for houses: ecliptic obliquity and nutation }
	  		rflag = sweph.swe_calc_ut(tjdut, SweConst.SE_ECL_NUT, 0, xx, sb);
	  		eps_true = xx[0];
	  		nut_long = xx[2];
	  		//{ geographic position }
	  		glon = Math.abs(ilondeg) + ilonmin/60.0 + ilonsec/3600.0;
	  		if (lon < 0)
	  			glon = -glon;
	  		glat = Math.abs(ilatdeg) + ilatmin/60.0 + ilatsec/3600.0;
	  		if (lat < 0)
	  			glat = -glat;
	  		//{ sidereal time }
	  		tsid = new SwissLib().swe_sidtime(tjdut);
	  		tsid = tsid + glon / 15;
	  		armc = tsid * 15;
	  		//{ house method }
	  		double[] ascmc = new double[10];
	  		double[] hcusps = new double[13];
	  		//используем систему Плацидуса
	  		sweph.swe_houses(tjdut, SweConst.SEFLG_SIDEREAL, glat, glon, hsys, hcusps, ascmc);
	  		calcHouseParts(hcusps);
	  		sweph.swe_close();
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
  	 * Поиск соответствия планет Швейцарских эфемерид с их эквивалентами в БД
  	 * @param i индекс планеты в Швейцарских эфемеридах
  	 * @return индекс планеты в БД
  	 */
  	private long constToPlanet(int i) {
  		switch(i) {
	  		case 0: return 19L;
	  		case 1: return 20L;
	  		case 2: return 23L;
	  		case 3: return 24L;
	  		case 4: return 25L;
	  		case 5: return 28L;
	  		case 6: return 29L;
	  		case 7: return 31L;
	  		case 8: return 32L;
	  		case 9: return 33L;
	  		case 10: return 21L;
	  		case 11: return 27L;
	  		case 12: return 30L;
	  		case 13: return 26L;
	  		case 14: return 34L;
  		}
  		return 22;
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
	  			if (h.isMain()) {
	  				double val = houses[i];
	  	  			h.setCoord(val);
	  				Sign sign = SkyPoint.getSign(val, event.getBirthYear());
	  				h.setSign(sign);
	  			} else {
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
	  				Sign sign = SkyPoint.getSign(res, event.getBirthYear());
	  				h.setSign(sign);
	  			}
	  		}
		} catch (Exception e) {
			e.printStackTrace();
		}
  	}

	public Map<Long, Planet> getPlanets() {
		return planetList;
	}

	public List<Model> getHouses() {
		return houseList;
	}

	public List<SkyPointAspect> getAspects() {
		return aspectList;
	}

	public List<SkyPointAspect> getAspectsh() {
		return aspecthList;
	}

	/**
	 * Проверка инициализации домов планет
	 * @return true - для планет определены дома,<br>
	 * false - планеты не определены либо для них не указаны позиции в домах
	 */
	private boolean isPlanetHoused() {
		return (planetList != null &&
			((Planet)planetList.get(19L)).getHouse() != null);
	}

	/**
	 * Определение позиций планет в домах
	 */
	public void initHouses() {
		if (isPlanetHoused()) return;
		for (Planet planet : planetList.values()) {
			for (int j = 0; j < houseList.size(); j++) {
				House house = (House)houseList.get(j);
				double pcoord = planet.getCoord();
				Double hmargin = (j == houseList.size() - 1) ?
					((House)houseList.get(0)).getCoord() : 
					((House)houseList.get(j + 1)).getCoord();
				double[] res = CalcUtil.checkMarginalValues(house.getCoord(), hmargin, pcoord);
				hmargin = res[0];
				pcoord = res[1];
				//если градус планеты находится в пределах куспидов
				//текущей и предыдущей трети домов,
				//запоминаем, в каком доме находится планета
				if (Math.abs(pcoord) < hmargin & 
						Math.abs(pcoord) >= house.getCoord()) {
					planet.setHouse(house);
					if (planet.getCode().equals("Lilith"))
						house.setLilithed();
					else if (planet.getCode().equals("Selena"))
						house.setSelened();
				}
			}
		}
	}

	/**
	 * Определение позиций планет в знаках
	 * @param main признак вычисления только минорных планет
	 */
	public void initPlanetSigns(boolean main) throws DataAccessException {
		if (!main && isPlanetSigned()) return;
		for (Planet planet : planetList.values()) {
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
			((Planet)planetList.get(19L)).getSign() != null);
	}

	/**
	 * Расчёт аспектов планет.
	 * Параллельно собираем статистику по каждой планете:
	 * сколько у неё аспектов всех существующих типов
	 * @throws DataAccessException 
	 */
	public void initPlanetAspects() throws DataAccessException {
		try {
	  	  	aspectList = new ArrayList<SkyPointAspect>();
			List<Model> aspects = new AspectService().getList();
			List<Model> aspectTypes = new AspectTypeService().getList();
			if (planetList != null) 
				for (Planet p : planetList.values()) {

					//создаем карту статистики по аспектам планеты
					Map<String, Integer> aspcountmap = new HashMap<String, Integer>();
					Map<String, String> aspmap = new HashMap<String, String>();
					for (Model asptype : aspectTypes)
						aspcountmap.put(((AspectType)asptype).getCode(), 0);
					
					for (Planet p2 : planetList.values()) {
						if (p.getCode().equals(p2.getCode()))
							continue;
//						if (p.getNumber() > p2.getNumber()) continue;
						double res = CalcUtil.getDifference(p.getCoord(), p2.getCoord());
						for (Model realasp : aspects) {
							Aspect a = (Aspect)realasp;
							long asplanetid = a.getPlanetid();
							if (asplanetid > 0 && asplanetid != p.getId())
								continue;
							if (a.isAspect(res)) {
								String aspectTypeCode = a.getType().getCode();

								//фиксируем аспекты планеты
								aspmap.put(p2.getCode(), a.getCode());
								//суммируем аспекты каждого типа для планеты
								int score = aspcountmap.get(aspectTypeCode);
								//для людей считаем только аспекты главных планет
								aspcountmap.put(aspectTypeCode, ++score);
	
								//суммируем сильные аспекты
								String common = "COMMON";
								if (a.getType().getParentType() != null &&
										a.getType().getParentType().getCode().equals(common)) {
									score = aspcountmap.get(common);
									aspcountmap.put(common, ++score);
								}

								if (aspectTypeCode.equals("NEUTRAL") && p.getCode().equals("Sun") && res <= 3)
									continue;
								SkyPointAspect aspect = new SkyPointAspect();
								aspect.setSkyPoint1(p);
								aspect.setSkyPoint2(p2);
								aspect.setAspect(a);
								aspect.setExact(a.isExact(res));
								aspect.setApplication(a.isApplication(res));
								aspectList.add(aspect);

								if (a.isMain()) {
									double points = a.getPoints();
									p.addPoints(points);
									p2.addPoints(points);
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
	 * определяем позиции планет в целом (обитель изгнание экзальтация падение)<br>
	 * определяем пустые знаки<br>
	 * определяем пустые дома а также включенные и какие там ещё есть<br>
	 * //определяем управителей домов
		//определяем знак, где находится куспид дома,
		//и определяем планету знака
	 */
	public void initPlanetStatistics() throws DataAccessException {
		initPlanetDamaged();
//		initAngularPlanets();
		initSunNeighbours();
		initPlanetPositions();
		initPlanetRank();
//		initHouseAspects();
//		initIngress();
	}

	/**
	 * Инициализация позиций планет
	 */
	private void initPlanetPositions() {
		try {
			initPlanetSigns(false);
			initHouses();

			PlanetService service = new PlanetService();
			List<Model> positions = new PositionTypeService().getList();
			for (Planet planet : planetList.values()) {
				for (Model type : positions) {
					PositionType pType = (PositionType)type;
					String pCode = pType.getCode();
					boolean daily = true;
					if (!planet.getCode().equals("Sun") &&
							(pCode.equals("HOME") || pCode.equals("EXILE")))
						daily = DateUtil.isDaily(date);

					Sign sign = service.getSignPosition(planet, pCode, daily);
					if (sign != null && sign.getId() == planet.getSign().getId()) {
						switch (pCode) {
							case "HOME": planet.setSignHome(); break;
							case "EXALTATION": planet.setSignExaltated(); break;
							case "EXILE": planet.setSignExile(); break;
							case "DECLINE": planet.setSignDeclined(); break;
						}
					}

					if (null == planet.getHouse()) continue;
					House house = service.getHousePosition(planet, pCode, daily);
					int hnumber = CalcUtil.trunc((planet.getHouse().getNumber() + 2) / 3);
					if (house != null && CalcUtil.trunc((house.getNumber() + 2) / 3) == hnumber) {
						switch (pCode) {
							case "HOME": planet.setHouseHome(); break;
							case "EXALTATION": planet.setHouseExaltated(); break;
							case "EXILE": planet.setHouseExile(); break;
							case "DECLINE": planet.setHouseDeclined(); break;
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
	private void initPlanetDamaged() {
		for (Planet planet : planetList.values()) {
			final String LILITH = "Lilith";
			final String KETHU = "Kethu";
			final String SELENA = "Selena";
			final String RAKHU = "Rakhu";

			String pcode = planet.getCode();
			if (!pcode.equals(LILITH) && !pcode.equals(KETHU)
					&& !pcode.equals(SELENA) && !pcode.equals(RAKHU)) {
				for (SkyPointAspect aspect : aspectList) {
					String pcode2 = aspect.getSkyPoint2().getCode();
					if (!aspect.getSkyPoint1().getCode().equals(pcode)
							&& !pcode2.equals(pcode))
						continue;

					String acode = aspect.getAspect().getCode();
					if (acode.equals("CONJUNCTION") || acode.equals("BELT") || acode.equals("KERNEL")) {
						if (pcode2.equals(LILITH))
							planet.setLilithed();
						else if (pcode2.equals(KETHU))
							planet.setKethued();
						else if (pcode2.equals(SELENA))
							planet.setSelened();
						else if (pcode2.equals(RAKHU))
							planet.setRakhued();
					}
				}
			}

			//сравнение количества хороших и плохих аспектов
			Map<String, Integer> map = planet.getAspectCountMap(); 
			int good = map.get("POSITIVE");
			int goodh =	map.get("POSITIVE_HIDDEN");
			int bad = map.get("NEGATIVE");
			int badh = map.get("NEGATIVE_HIDDEN");
			int neutral = map.get("NEUTRAL") + map.get("NEUTRAL_KERNEL") + map.get("NEGATIVE_BELT");

			if (bad > 0
					&& (0 == good + goodh)
					&& (0 == neutral
						|| (1 == neutral && (planet.isKethued() || planet.isLilithed())))
						|| (2 == neutral && planet.isKethued() && planet.isLilithed()))
				planet.setDamaged(true);
			else if (0 == bad + badh && good > 0 && !planet.isKethued() && !planet.isLilithed())
				planet.setPerfect(true);
		}
	}

	/**
	 * Определяем ближайшие планеты к Солнцу
	 */
	private void initSunNeighbours() {
		Planet sun = planetList.get(19L);
		List<Planet> planets = new ArrayList<Planet>();
		planets.add(sun);
		//определяем ядро и пояс
		for (Planet planet : planetList.values()) {
			if (planet.getId().equals(sun.getId()))
				continue;
			double res = Math.abs(CalcUtil.getDifference(sun.getCoord(), planet.getCoord()));
			if (res < 0.18)
				planet.setKernel();
			else if (res <= 3)
				planet.setBelt();
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
		planetList.get(sword.getId()).setSword();

		int ishield = (0 == sunindex) ? planets.size() - 1 : sunindex - 1;
		Planet shield = planets.get(ishield);
		planetList.get(shield.getId()).setShield();
	}

	public void setPlanets(Map<Long, Planet> planets) {
		planetList = planets;
	}
	public void setHouses(List<Model> houses) {
		houseList = houses;
	}

	/**
	 * Поиск угловых планет, расположенных на ASC, IC, DSC, MC
	 */
	@SuppressWarnings("unused")
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

			for (Planet planet : planetList.values()) {
				double[] res = CalcUtil.checkMarginalValues(phouse, nhouse, planet.getCoord());
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

	/**
	 * Расчёт силы планет
	 */
	private void initPlanetRank() {
		Planet minp = new Planet();
		minp.setPoints(100);
		Planet maxp = new Planet();
		int good = 0;
		for (Planet planet : planetList.values()) {
	    	double rank = planet.getPoints();
			if (rank > maxp.getPoints())
				maxp = planet;
			else if (rank < minp.getPoints())
				minp = planet;

			Map<String, Integer> map = planet.getAspectCountMap(); 
			int pgood = map.get("POSITIVE");
			if (pgood > good)
				good = pgood;
	    }
		List<Planet> planets = new ArrayList<>();
		for (Planet planet : planetList.values()) {
	    	double rank = planet.getPoints();
			if (rank == maxp.getPoints())
				maxp.setLord(true);
			if (rank == minp.getPoints())
				minp.setBroken();

			Map<String, Integer> map = planet.getAspectCountMap(); 
			int pgood = map.get("POSITIVE");
			if (pgood == good)
				planets.add(planet);
	    }
		if (planets.size() < 2)
			planets.get(0).setKing();
	}

	/**
	 * Расчёт аспектов домов
	 * @throws DataAccessException 
	 */
	public void initHouseAspects() throws DataAccessException {
		try {
			if (aspecthList != null && aspecthList.size() > 0) return;
	  	  	aspecthList = new ArrayList<SkyPointAspect>();
			List<Model> aspects = new AspectService().getList();
			if (planetList != null) 
				for (Planet p : planetList.values()) {
					for (Model model2 : houseList) {
						House h = (House)model2;
						double res = CalcUtil.getDifference(p.getCoord(), h.getCoord());
						for (Model realasp : aspects) {
							Aspect a = (Aspect)realasp;
							if (a.isAspect(res)) {
								String aspectTypeCode = a.getType().getCode();

								if (aspectTypeCode.equals("NEUTRAL") && p.getCode().equals("Sun") && res <= 3)
									continue;
								SkyPointAspect aspect = new SkyPointAspect();
								aspect.setSkyPoint1(p);
								aspect.setSkyPoint2(h);
								aspect.setAspect(a);
								aspecthList.add(aspect);
							}
						}
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initIngress() {
		try {
			ingressList = new ArrayList<>();
			IngressTypeService iservice = new IngressTypeService();
			AspectService aservice = new AspectService();

			Event prev = event.getPrev();
			prev.getConfiguration().initPlanetAspects();

			Collection<Planet> planets1 = prev.getConfiguration().getPlanets().values();
			for (Planet planet : planetList.values()) {
				Map<String,String> map = planet.getAspectMap();

				for (Model model1 : planets1) {
					Planet planet1 = (Planet)model1;
					String icode = null;
					if (planet.getCode().equals(planet1.getCode())) {
						if (Math.abs(planet1.getCoord()) == Math.abs(planet.getCoord())) {
							//планета осталась в той же координате
							icode = "stationary";
						} else if (planet1.isRetrograde() && !planet.isRetrograde()) {
							//планета перешла в директное движение
							icode = "direct";
						} else if (planet.isRetrograde() && !planet1.isRetrograde()) {
							//планета перешла в обратное движение
							icode = "retrograde";
						}
						if (icode != null) {
							IngressType type = (IngressType)iservice.find(icode);
							Ingress ingress = new Ingress(event, planet, null, null, type);
							ingressList.add(ingress);
						}

						//изменился ли знак Зодиака планеты
						Sign sign = planet.getSign();
						if (null == sign)
							sign = SkyPoint.getSign(planet.getCoord(), event.getBirthYear());
	
						Sign sign1 = planet1.getSign();
						if (null == sign1)
							sign1 = SkyPoint.getSign(planet1.getCoord(), prev.getBirthYear());
	
						if (sign.getId() != sign1.getId()) {
							icode = "sign";
							IngressType type = (IngressType)iservice.find(icode);
							Ingress ingress = new Ingress(event, planet, null, sign, type);
							ingressList.add(ingress);
						}
					} else {
						//изменились ли аспекты
						Map<String,String> map1 = planet1.getAspectMap();
						String acode = map.get(planet1.getCode());
						String acode1 = map1.get(planet.getCode());
						if (null == acode)
							continue;
						else if (null == acode1 || !acode.equals(acode1)) {
							icode = "application";
							IngressType type = (IngressType)iservice.find(icode);
							Ingress ingress = new Ingress(event, planet, planet1, aservice.find(acode), type);
							ingressList.add(ingress);
							break;
						}
					}
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Model> getIngresses() {
		if (null == ingressList || 0 == ingressList.size())
			initIngress();
		return ingressList;
	}
}
