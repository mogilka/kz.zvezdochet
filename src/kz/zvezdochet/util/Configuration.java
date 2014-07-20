package kz.zvezdochet.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kz.zvezdochet.bean.Aspect;
import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.bean.SkyPoint;
import kz.zvezdochet.bean.SkyPointAspect;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.util.CalcUtil;
import kz.zvezdochet.core.util.NumberUtil;
import kz.zvezdochet.core.util.PlatformUtil;
import kz.zvezdochet.service.AspectService;
import kz.zvezdochet.service.AspectTypeService;
import kz.zvezdochet.service.HouseService;
import kz.zvezdochet.service.PlanetService;
import kz.zvezdochet.sweph.Activator;
import swisseph.SweConst;
import swisseph.SweDate;
import swisseph.SwissEph;
import swisseph.SwissLib;

/**
 * Класс, представляющий Расчетную конфигурацию гороскопа
 * @author Nataly Didenko
 */
public class Configuration {
	private List<Model> planetList;
	private	List<Model> houseList;
	private	List<SkyPointAspect> aspectList;

	/**
	 * Создание пустой расчетной конфигурации
	 * @throws DataAccessException 
	 */
	public Configuration() throws DataAccessException {
  	  	planetList = new PlanetService().getList();
  	  	houseList = new HouseService().getList();
	}

	/**
	 * Создание расчетной конфигурации для заданного момента времени и места
	 * @param date строковое значение даты
	 * @param time строковое значение времени
	 * @param zone строковое значение часового пояса
	 * @param latitude строковое значение широты местности
	 * @param longitude строковое значение долготы местности
	 * @throws DataAccessException 
	 */
	public Configuration(String date, String time, String zone, String latitude, String longitude) throws DataAccessException {
  	  	planetList = new PlanetService().getList();
  	  	houseList = new HouseService().getList();
		calculate(date, time, zone, latitude, longitude);
//		getPlanetStatistics();
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
	  		long iflag = SweConst.SEFLG_SIDEREAL | SweConst.SEFLG_SPEED;
	  		int iyear, imonth, iday, ihour = 0, imin = 0, isec = 0;
	  	  	SwissEph sweph = new SwissEph();
			String path = PlatformUtil.getPath(Activator.PLUGIN_ID, "/lib/ephe").getPath(); //$NON-NLS-1$
	  		sweph.swe_set_ephe_path(path); //TODO вынести в конфиги
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
	
	  		//обрабатываем координаты места
	  		double lat = (slat != null && slat.length() > 0) ? Double.parseDouble(slat) : 43.15;
	  		double lon = (slon != null && slon.length() > 0) ? Double.parseDouble(slon) : 76.55;
	  		int ilondeg, ilonmin, ilonsec, ilatdeg, ilatmin, ilatsec;
	  		ilondeg = CalcUtil.trunc(Math.abs(lon));
	  		ilonmin = CalcUtil.trunc(Math.abs(lon) - ilondeg) * 100;
	  		ilonsec = 0;
	  		ilatdeg = CalcUtil.trunc(Math.abs(lat));
	  		ilatmin = CalcUtil.trunc(Math.abs(lat) - ilatdeg) * 100;
	  		ilatsec = 0;
	  		
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
	  				double one = CalcUtil.degToDec(houses[i]);
	  				if (i == 12) i = 0;
	  				double two = CalcUtil.degToDec(houses[i + 1]);
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
	  					res = CalcUtil.decToDeg(res - 360); 
	  				else 
	  					res = CalcUtil.decToDeg(res);
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
	 */
	public void initPlanetSigns() throws DataAccessException {
		if (isPlanetSigned()) return;
		for (Model model : planetList) {
			Planet planet = (Planet)model;
			Sign sign = AstroUtil.getSkyPointSign(planet.getCoord());
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
	 * Расчет аспектов планет.
	 * Параллельно выполняется составление статистики по каждой планете:
	 * сколько у нее аспектов всех существующих типов
	 * @throws DataAccessException 
	 */
	public void initPlanetAspects() throws DataAccessException {
		try {
	  	  	aspectList = new ArrayList<SkyPointAspect>();
			List<Model> aspects = new AspectService().getList();
			if (planetList != null) 
				for (Model model : planetList) {
					Planet p = (Planet)model;
					
					//создаем карту статистики по аспектам планеты
					Map<String, Integer> aspcountmap = new HashMap<String, Integer>();
					Map<String, String> aspmap = new HashMap<String, String>();
					List<Model> aspectTypes = new AspectTypeService().getList();
					for (Model entity4 : aspectTypes) 
						aspcountmap.put(((AspectType)entity4).getCode(), 0);
					
					for (Model model2 : planetList) {
						Planet p2 = (Planet)model2;
						if (p.getCode().equals(p2.getCode())) continue;
						if (((p.getCode().equals("Rakhu")) && (p2.getCode().equals("Kethu"))) ||
								((p.getCode().equals("Kethu")) && (p2.getCode().equals("Rakhu")))) 
							continue;
						double res = CalcUtil.getDifference(
								CalcUtil.degToDec(p.getCoord()), 
								CalcUtil.degToDec(p2.getCoord()));
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
	 * - позиция планеты на угловых точках
	 * - является ли планета управителем домов
	 * - является ли планета управителем угловых домов
	 * - является ли планета одновременно управителем и хозяином дома
	 * - насколько планета приближена к Солнцу
	 * - позиции планеты (обитель, экзальтация, изгнание, падение)
	 * - аспектированность планеты (шахта, пораженность, непораженность)
	 * - ретроградность планеты
	 * - заполненность знака и дома, хозяином которых является планета
	 * - благоприятная связь (хороший или нейтральный аспект планеты с Раху и Селеной)
	 * - неблагоприятная связь (плохой или нейтральный аспект планеты с Кету и Лилит)
	 * - нахождение планеты в градусе
	 */
	public void getPlanetStatistics() throws DataAccessException {
		getDamagedPlanets();
		getBrokenPlanets();
//		getSunNeighbours();
	}

	/**
	 * Поиск пораженных планет
	 */
	private void getDamagedPlanets() {
		if (planetList != null) 
			for (Model entity : planetList) {
				Planet planet = (Planet)entity;
				
				//сравнение количества хороших и плохих аспектов
				int good = planet.getAspectCountMap().get("POSITIVE") +
						planet.getAspectCountMap().get("POSITIVE_HIDDEN");
				int bad = planet.getAspectCountMap().get("NEGATIVE") +
						planet.getAspectCountMap().get("NEGATIVE_HIDDEN");
				if (good == 0 && bad > 0) {
					planet.setDamaged(true); 
					System.out.println(planet.getCode() + " is damaged");
					continue;
				}
				if (aspectList != null) {
					final String LILITH = "Lilith";
					for (SkyPointAspect aspect : aspectList) {
						if (aspect.getSkyPoint1().getCode().equals(LILITH) &&
								!aspect.getSkyPoint2().getCode().equals(LILITH) &&
								aspect.getSkyPoint2().getCode().equals(planet.getCode()) &&
								aspect.getAspect().getCode().equals("CONJUNCTION")) {
							planet.setDamaged(true); 
							System.out.println(planet.getCode() + " is damaged");
							continue;
						}
					}
				}
			}
	}

	/**
	 * Поиск ослабленных планет
	 */
	private void getBrokenPlanets() {
		if (planetList != null) 
			for (Model entity : planetList) {
				Planet planet = (Planet)entity;
				if (aspectList != null) {
					final String KETHU = "Kethu";
					for (SkyPointAspect aspect : aspectList) {
						if (aspect.getSkyPoint1().getCode().equals(KETHU) &&
								!aspect.getSkyPoint2().getCode().equals(KETHU) &&
								aspect.getSkyPoint2().getCode().equals(planet.getCode()) &&
								aspect.getAspect().getCode().equals("CONJUNCTION")) {
							planet.setBroken(true); 
							System.out.println(planet.getCode() + " is broken");
							continue;
						}
					}
				}
			}
	}
	
	/**
	 * Поиск планет, приближенных к Солнцу
	 */
	private void getSunNeighbours() {
		if (planetList == null) return; 
		Planet sun = (Planet)planetList.get(0);
		//if sun+5<=360 then min:=sun+5 else min:=sun+5-360;
		//max:=sun;
		SkyPoint shield = getNearestToSun(sun, 3.0, 1);                          //планета-Щит
		//min:=sun;
		//if sun>=5 then max:=sun-5 else max:=360-5+sun;
		SkyPoint sword = getNearestToSun(sun, 3.0, 2);                           //планета-Меч

		//if sun>=3 then min:=sun-3 else min:=360-3+sun;
		//if sun<=357 then max:=sun+3 else max:=sun+3-360;
		SkyPoint belt = getNearestToSun(sun, 0.17, 0);                           //планета-Пояс (сожженная)

		//if sun>=0.17 then min:=sun-0.17 else min:=360-0.17+sun;
		//if sun<=359.43 then max:=sun+0.17 else max:=sun+0.17-360;
		SkyPoint kernel = getNearestToSun(sun, 0.0, 0);                          //планета-Ядро
	}
	
	/**
	 * Поиск ближайшей планеты к Солнцу.
	 * С помощью данной функции вычисляюся
	 * ближайшие к Солнцу позиции планет (щит, меч, пояс, ядро)
	 */
	private SkyPoint getNearestToSun(SkyPoint point, double limit, int sign) {
		int sun;
		double min, res;
		SkyPoint minim, result;
		/**
		 * Массив разниц координаты Солнца с координатами других планет
		 */
		double[] diffs = new double[16]; 
		List<Planet> planets = new ArrayList<Planet>();

		//определяем расстояние между планетами
		for (Model entity : planetList) {
			Planet planet = (Planet)entity;
			planets.add(planet);
			if (planet.getCode().equals(point.getCode())) continue;
			res = CalcUtil.getDifference(
					Math.abs(CalcUtil.degToDec(planet.getCoord())), 
					Math.abs(CalcUtil.degToDec(point.getCoord())));
			//если планета укладывается в диапазон, запоминаем ее индекс
			int index = planet.getNumber() - 1;
			if ((limit > 2 && res <= limit) ||
					(limit < 0.18 && (res < 0.18 || res > 3)) ||
					(limit < 0.1 && res > 0.17)) {
				diffs[index] = 360.0; continue;
			} else 
				diffs[index] = res;
		}
		
		//упорядочиваем массив планет по возрастанию
		Collections.sort(planets, new SkyPointComparator());
		//определяем новый индекс солнца
		sun = planets.indexOf(point);

		//определяем щит и меч
		for (int i = 0; i < 10; i++) {
			if (sun == planets.size() - 1)
				minim = planets.get(0); 
			else 
				minim = planets.get(sun - 1);
			
			if (CalcUtil.getDifference(
					Math.abs(CalcUtil.degToDec(point.getCoord())), 
					Math.abs(CalcUtil.degToDec(minim.getCoord()))) > 3) {
				result = minim; break;
			}
		}

		//определяем наименьшее соединение
		min = 360.0;
		result = planets.get(0);
		for (int i = 0; i < diffs.length; i++)
			if (diffs[i] < min) {
				min = diffs[i];
				for (Planet planet : planets)
					if (i > 0 &&
							planet.getNumber() == i + 1) {
						result = planet; break;
					}
			}
		return result;
	}	
	
/*
 * //--------------------------------------------------------------
//определяем ближайшую планету к указанному объекту
//--------------------------------------------------------------
function TfmData.GetNearest(coord,margin1,margin2:real):byte;
{данная функция создана для определения планеты,
 ближайшей к таким точкам гороскопа как ASC и МС.
 И в этом случае мы рассматриваем только непосредственно близкие
 к точкам планеты, т.е. находящиеся соответственно
 в XII и I домах - для ASC, в IX и X домах - для МС}
var
i:byte;
planet,min:real;
diffs:array[2..17] of real; //массив разниц с планетами
begin
for i:=2 to 17 do
  begin
   planet:=abs(fmPersonal.qrPlanets.Fields[i].AsFloat);
   MarginalValuesEx(margin1,margin2,planet);
   //если планета укладывается в диапазон,
   //рассчитываем ее расстояние до точки
   //и запоминаем ее индекс
   if(planet>=margin1)and(planet<=margin2)
    then diffs[i]:=Difference(coord,planet)
    else diffs[i]:=360.0;
  end;
//определяем планету с наименьшим расстоянием
min:=360.0;
result:=0;
for i:=2 to 17 do
  if diffs[i]<min
   then
   begin
    min:=diffs[i];
    result:=i
   end;
end;

 */

	public void setPlanets(List<Model> planets) {
		planetList = planets;
	}
	public void setHouses(List<Model> houses) {
		houseList = houses;
	}
}