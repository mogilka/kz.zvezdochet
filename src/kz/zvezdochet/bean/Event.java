package kz.zvezdochet.bean;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.graphics.Image;
import org.json.JSONArray;
import org.json.JSONObject;

import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.core.util.CalcUtil;
import kz.zvezdochet.core.util.DateUtil;
import kz.zvezdochet.core.util.IOUtil;
import kz.zvezdochet.core.util.NumberUtil;
import kz.zvezdochet.core.util.OsUtil;
import kz.zvezdochet.core.util.PlatformUtil;
import kz.zvezdochet.part.Messages;
import kz.zvezdochet.service.AspectService;
import kz.zvezdochet.service.AspectTypeService;
import kz.zvezdochet.service.EventService;
import kz.zvezdochet.service.HouseService;
import kz.zvezdochet.service.PlaceService;
import kz.zvezdochet.service.PlanetService;
import kz.zvezdochet.service.PositionTypeService;
import kz.zvezdochet.service.StarService;
import kz.zvezdochet.sweph.Activator;
import kz.zvezdochet.util.SkyPointComparator;
import swisseph.SweConst;
import swisseph.SweDate;
import swisseph.SwissEph;
import swisseph.SwissLib;

/**
 * Событие или персона
 * @author Natalie Didenko
 */
public class Event extends Model {
	private static final long serialVersionUID = 3237544571447808520L;
	
	public Event() {
		super();
		name = "";
		birth = new Date();
//		recalculable = true;
	}

	/**
	 * Имя
	 */
	private String name;
	/**
	 * Пол
	 */
	private boolean female = true;
	/**
	 * Доминантное полушарие при рождении (левое, правое)
	 */
	private boolean rightHanded = true;
	/**
	 * Ректификация
	 */
	private int rectification = -1;
	/**
	 * Начальная дата
	 */
	private Date birth;
	/**
	 * Конечная дата
	 */
	private Date death;
	/**
	 * Признак общеизвестности события
	 */
	private boolean celebrity = false;
	/**
	 * Изображение
	 */
	private Image image;
	/**
	 * Идентификатор места рождения
	 */
	private long placeid;
	/**
	 * Идентификатор места смерти
	 */
	private long finalplaceid;
	/**
	 * Место рождения
	 */
	private Place place;
	/**
	 * Место смерти
	 */
	private Place finalplace;
	/**
	 * Часовой пояс
	 */
	private double zone = 0.0;
	/**
	 * Поправка летнего времени
	 */
	private double dst = 0.0;
	/**
	 * Описание события или биография человека
	 */
    private String bio;
	/**
	 * Краткий комментарий
	 */
	private String comment;
	/**
	 * Источник времени рождения
	 */
	private String accuracy;
	/**
	 * Признак живого существа
	 * 0|1|2 = событие|живое существо|персонаж
	 */
	private int human = 1;
	/**
	 * Дата создания
	 */
	private Date date;
	/**
	 * Имя в транслите для SEO-адаптированных URL
	 */
	private String fancy;

	public String getFancy() {
		return fancy;
	}
	public void setFancy(String fancy) {
		this.fancy = fancy;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public void setHuman(int human) {
		this.human = human;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String description) {
		this.comment = description;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String text) {
		this.bio = text;
	}
	public boolean isFemale() {
		return female;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFemale(boolean gender) {
		this.female = gender;
	}
	public Date getBirth() {
		return birth;
	}
	public void setBirth(Date birth) {
		this.birth = birth;
	}
	public Date getDeath() {
		return death;
	}
	public void setDeath(Date death) {
		this.death = death;
	}
	public Place getPlace() {
		return place;
	}
	public void setPlace(Place place) {
		this.place = place;
	}
	public boolean isCelebrity() {
		return celebrity;
	}
	public void setCelebrity(boolean celebrity) {
		this.celebrity = celebrity;
	}
	public boolean isRightHanded() {
		return rightHanded;
	}
	public void setRightHanded(boolean rightHanded) {
		this.rightHanded = rightHanded;
	}
	public int getRectification() {
		return rectification;
	}
	public void setRectification(int rectification) {
		this.rectification = rectification;
	}
	public double getZone() {
		return zone;
	}
	public void setZone(double zone) {
		this.zone = zone;
	}
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}

	public ModelService getService() {
		return new EventService();
	}

	@Override
	public void init(boolean mode) {
		initPlanetList();
		initHouseList();
		initStarList();
		if (mode)
			initData(true);
	}

	public void initPlanetList() {
		try {
	  	  	planetList = new TreeMap<>();
			List<Model> list = new PlanetService().getList();
			for (Model model : list)
				planetList.put(model.getId(), (Planet)model);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	public void initHouseList() {
		try {
			houseList = new TreeMap<>();
			List<Model> list = new HouseService().getList();
			for (Model model : list)
				houseList.put(model.getId(), (House)model);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	public void initStarList() {
		try {
	  	  	starList = new HashMap<>();
	  	  	List<Model> list = new StarService().getList();
			for (Model model : list)
				starList.put(model.getId(), (Star)model);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Инициализация полных данных о событии
	 * @param initstat true - инициализировать статистику события
	 */
	public void initData(boolean initstat) {
		try {
			//местонахождение
			if (null == place)
				place = (Place)new PlaceService().find(placeid);

			//если событие ещё не сохранено в базе, рассчитываем конфигурацию
			//в противном случае берём конфигурацию из базы
			if (null == id)
				calc(true);
			else {
				EventService service = new EventService();
				service.initHouses(this);
				service.initPlanets(this);
				service.initAspects(this);
				service.initStars(this);
				initHouses();
			}
			if (initstat)
				initPlanetStatistics();
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	public long getPlaceid() {
		return placeid;
	}
	public void setPlaceid(long placeid) {
		this.placeid = placeid;
	}

	public int getHuman() {
		return human;
	}
	public String getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(String accuracy) {
		this.accuracy = accuracy;
	}

	/**
	 * Журнал ректификации
	 */
	private String conversation;

	public String getConversation() {
		return conversation;
	}
	public void setConversation(String conversation) {
		this.conversation = conversation;
	}

	public int MAX_CHILD_AGE = 10;
	public int MAX_TEEN_AGE = 18;

	/**
	 * Определение возраста персоны
	 * @return возраст
	 */
	public int getAge() {
		int age = 0;
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(birth);
		Calendar calendar2 = GregorianCalendar.getInstance();
		if (null == death)
			age = calendar2.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
		else {
			calendar2.setTime(death);
			age = calendar2.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
		}
		return age;
	}

	@Override
	public String toString() {
		return name + " " + DateUtil.sdf.format(birth);
	}

	/**
	 * Расчёт положения планет и астрологических домов
	 * @param cachable признак сохранения данных в кэш
	 */
	public void calc(boolean cachable) {
		try {
			Place calcplace = (null == place) ? new Place().getDefault() : place;
	  	  	String date = DateUtil.formatCustomDateTime(birth, DateUtil.sdf.toPattern());
	  	  	String time = DateUtil.formatCustomDateTime(birth, DateUtil.stf.toPattern());

	  	  	if (cachable) {
	  	  		String cachekey = calcplace.getId() + "_" + DateUtil.formatCustomDateTime(birth, new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").toPattern());

//		  	  	IPreferenceStore preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, "kz.zvezdochet.runner");
//		        String dir = preferenceStore.getString(CACHE_DIR_PATH);

	  	  		String dir = OsUtil.getOS().equals(OsUtil.OS.LINUX) ? "/media/natalie/toshiba/cache/" : null;
	  	  		if (null == dir)
	  	  			dir = PlatformUtil.getPath(kz.zvezdochet.Activator.PLUGIN_ID, "/cache/").getPath();
	  	  		File file = new File(dir);
	  	  		if (!file.exists())
	  	  			return;

	  	  		String filename = dir + cachekey + ".txt";
	  	  		file = new File(filename);
	  	  		if (file.exists()) {
	  	  			String json = IOUtil.getTextFromFile(filename);
	  	  			init(json);
	  	  		} else {
	  	  			calcSweph(calcplace, date, time, cachable);
	  	  			String data = toJSON();
	  	  			IOUtil.createFile(filename, data);
	  	  		}
	  	  	} else
	  	  		calcSweph(calcplace, date, time, cachable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public double getDst() {
		return dst;
	}
	public void setDst(double dst) {
		this.dst = dst;
	}
	public long getFinalplaceid() {
		return finalplaceid;
	}
	public void setFinalPlaceid(long finalplaceid) {
		this.finalplaceid = finalplaceid;
	}
	public Place getFinalPlace() {
		return finalplace;
	}
	public void setFinalplace(Place finalplace) {
		this.finalplace = finalplace;
	}

	/**
	 * Признак необходимости сохранить расчётные данные в БД
	 */
	private boolean recalculable = false;

	public boolean isRecalcable() {
		return recalculable;
	}
	public void setRecalculable(boolean needRecalc) {
		this.recalculable = needRecalc;
	}

	/**
	 * Возвращает год события
	 * @return год события
	 */
	public int getBirthYear() {
		Calendar cal = Calendar.getInstance();
	    cal.setTime(birth);
		return cal.get(Calendar.YEAR);
	}

	/**
	 * Возвращает URL события на сайте
	 * @return URL события
	 */
	public String getUrl() {
		return "https://zvezdochet.guru/event/" + id + "/" + fancy;
	}

	/**
	 * Идентификатор обратной совместимости для импорта знаменитостей с сайта
	 */
	private long backid;

	public long getBackid() {
		return backid;
	}
	public void setBackid(long backid) {
		this.backid = backid;
	}

	/**
	 * Конвертация параметров JSON в объект события
	 * @param json объект JSON
	 */
	public Event(JSONObject json) {
		super();
		setId(json.getLong("ID"));
		setName(json.getString("name"));
		setBirth(DateUtil.getDatabaseDateTime(json.getString("InitialDate")));
		Object value =json.get("FinalDate");
		if (value != JSONObject.NULL)
			setDeath(DateUtil.getDatabaseDateTime(value.toString()));
		value = json.getInt("RightHanded");
		setRightHanded(1 == (int)value ? true : false);
		setRectification(json.getInt("Rectification"));
		value = json.getInt("Celebrity");
		setCelebrity(1 == (int)value ? true : false);
		setComment(json.get("Comment").toString());
		value = json.getInt("Gender");
		setFemale(1 == (int)value ? true : false);
		value = json.get("Placeid");
		if (value != JSONObject.NULL)
			setPlaceid(json.getLong("Placeid"));
		value = json.get("finalplaceid");
		if (value != JSONObject.NULL)
			setFinalPlaceid((int)value);
		value = json.get("Zone");
		if (value != JSONObject.NULL)
			setZone(json.getDouble("Zone"));
		setHuman(json.getInt("human"));
		setAccuracy(json.get("accuracy").toString());
		setDate(DateUtil.getDatabaseDateTime(json.getString("date")));
		setFancy(json.getString("fancy"));
		value = json.get("backid");
		if (value != JSONObject.NULL)
			setBackid(json.getLong("backid"));
		setDst(json.getDouble("dst"));
		value = json.get("tabloid");
		if (value != JSONObject.NULL)
			setTabloid(json.getLong("tabloid"));
//		recalculable = true;
		setModified(DateUtil.getDatabaseDateTime(json.getString("updated_at")));
	}

	/**
	 * Участники сообщества
	 */
	private List<Event> members;

	public List<Event> getMembers() {
		if (2 == human)
			return members;
		else
			return null;
	}
	public void setMembers(List<Event> members) {
		this.members = members;
	}

	/**
	 * Признак того, что расчётная конфигурация события создана
	 */
	boolean calculated = false;

	public boolean isCalculated() {
		return calculated;
	}
	public void setCalculated(boolean calculated) {
		this.calculated = calculated;
	}

	/**
	 * Маршализация модели для лога
	 * @return строка параметров модели
	 */
	public String toLog() {
		String res = "";
		try {
			Field[] fields = getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++)
				res += fields[i].getName() + ":" + fields[i].get(this) + ", ";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "[" + res + "]";
	}

	/**
	 * Проверка, является ли человек ребёнком или подростком
	 * @return
	 */
	public boolean isChild() {
		return getAge() < MAX_TEEN_AGE;
	}

	/**
	 * Варианты ректификации
	 */
	public static String[] calcs = {
		Messages.getString("PersonView.Fault"),
		Messages.getString("PersonView.Success"),
		Messages.getString("PersonView.Undefined"),
		"Недостаточно данных"
	};

	/**
	 * Возвращает имя события
	 * @return наименование
	 */
	public String getCallname() {
		return name.contains(" ") ? name.substring(0, name.indexOf(' ')) : name;
	}

	public Event(Date date, String name) {
		super();
		this.name = name;
		birth = date;
//		recalculable = true;
	}

	/**
	 * Поиск предыдущего события
	 * @return событие днём ранее
	 */
	public Event getPrev() {
		Event prev = null;
		try {
			String sdate = DateUtil.formatCustomDateTime(birth, "yyyy-MM-dd");
			Date edate = DateUtil.getDatabaseDateTime(sdate + " 12:00:00");
			Calendar cal = Calendar.getInstance();
			cal.setTime(edate);
			cal.add(Calendar.DATE, -1);
			sdate = DateUtil.formatCustomDateTime(cal.getTime(), "yyyy-MM-dd");
			prev = new Event(DateUtil.getDatabaseDateTime(sdate + " 12:00:00"), "Мой гороскоп");
			prev.setPlace(place);
			prev.calc(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prev;
	}

	/**
	 * Порядковый номер лунного дня
	 */
	private long moondayid;

	public long getMoondayid() {
		return moondayid;
	}
	public void setMoondayid(long moondayid) {
		this.moondayid = moondayid;
	}

	/**
	 * Вид космограммы
	 */
	private long cardkindid;

	public long getCardkindid() {
		return cardkindid;
	}
	public void setCardkindid(long cardkindid) {
		this.cardkindid = cardkindid;
	}

	/**
	 * Проверка, известно ли время события
	 * @return false - неизвестно
	 */
	public boolean isHousable() {
		return rectification < 3;
	}

	/**
	 * Идентификатор спортсмена в Tablo.moe
	 */
	private long tabloid;

	public long getTabloid() {
		return tabloid;
	}
	public void setTabloid(long tabloid) {
		this.tabloid = tabloid;
	}

	/**
	 * Карта планет
	 */
	private TreeMap<Long, Planet> planetList;
	/**
	 * Карта домов
	 */
	private	TreeMap<Long, House> houseList;
	/**
	 * Список аспектов планет с домами
	 */
	private	List<SkyPointAspect> aspecthList;
	/**
	 * Карта звёзд
	 */
	private	Map<Long, Star> starList;

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
  	 * Инициализация домов и расчёт третей
  	 * @param houses массив координат 12 домов
  	 */
  	private void calcHouseParts(double[] houses) {
		try {
	  		byte multiple;
	  		//шерстим трети домов, минуя основные куспиды
	  		for (int j = 1; j < 37; j++) {
	  			House h = houseList.get((long)j + 141);
	  			int i = CalcUtil.trunc((j + 2) / 3);
	  			double val = 0;
	  			if (h.isMain()) {
	  				val = houses[i];
	  	  			h.setLongitude(val);
	  			} else {
	  				double one = houses[i];
	  				if (12 == i)
	  					i = 0;
	  				double two = houses[i + 1];
	  				if (two < one)
	  					two += 360;
	  				//вычисляем и сохраняем значения вершин третей дома
	  				//учитываем, что индекс последней трети всегда кратен трем
	  				if (j % 3 == 0) 
	  					multiple = 2; 
	  				else 
	  					multiple = 1;
	  				val = multiple * ((two - one) / 3) + one;
	  				if (val > 360) 
	  					val = val - 360; 
	  	  			h.setLongitude(val);
	  			}
  				Sign sign = SkyPoint.getSign(val, getBirthYear());
  				h.setSign(sign);
	  		}
		} catch (Exception e) {
			e.printStackTrace();
		}
  	}

	public Map<Long, Planet> getPlanets() {
		if (null == planetList)
			initPlanetList();
		return planetList;
	}

	public Map<Long, House> getHouses() {
		if (null == houseList)
			initHouseList();
		return houseList;
	}

	public List<SkyPointAspect> getAspectHouseList() {
		if (null == aspecthList)
			aspecthList = new ArrayList<SkyPointAspect>();
		return aspecthList;
	}

	/**
	 * Определение позиций планет и звёзд в домах. 
	 * Используется для новых (не сохранённых) событий или после перерасчёта
	 */
	private void initHouses() {
		if (null == houseList)
			initHouseList();
		for (House house : houseList.values()) {
			long h = (house.getNumber() == houseList.size()) ? 142 : house.getId() + 1;
			House house2 = houseList.get(h);
			//планеты
			for (Planet planet : planetList.values()) {
				if (SkyPoint.getHouse(house.getLongitude(), house2.getLongitude(), planet.getLongitude())) {
					planet.setHouse(house);
					if (planet.getCode().equals("Lilith"))
						house.setLilithed();
					else if (planet.getCode().equals("Selena"))
						house.setSelened();
					if (planet.getCode().equals("Kethu"))
						house.setKethued();
					else if (planet.getCode().equals("Rakhu"))
						house.setRakhued();
				}
			}
			//звёзды
//			for (Star star : starList.values()) {
//				if (SkyPoint.getHouse(house.getLongitude(), house2.getLongitude(), star.getLongitude())) 
//					star.setHouse(house);
//			}
		}
	}

	/**
	 * Определение позиций планет в знаках. 
	 * Используется для новых событий или после перерасчёта
	 */
	public void initSigns() throws DataAccessException {
		for (Planet planet : planetList.values()) {
			Sign sign = SkyPoint.getSign(planet.getLongitude(), getBirthYear());
			planet.setSign(sign);
		}
	}

	/**
	 * Расчёт аспектов планет.
	 * Параллельно собираем статистику по каждой планете:
	 * сколько у неё аспектов всех существующих типов
	 * @throws DataAccessException 
	 */
	public void initAspects() throws DataAccessException {
		try {
			if (planetList != null) { 
				List<Model> aspects = new AspectService().getList();
				Collection<Planet> planets = planetList.values();
				for (Planet p : planets)
					p.setAspectList(null);
				for (Planet p : planets) {
					for (Planet p2 : planets) {
						if (p.getCode().equals(p2.getCode()))
							continue;
						if (p.getNumber() > p2.getNumber()) continue;
						double res = CalcUtil.getDifference(p.getLongitude(), p2.getLongitude());
						for (Model realasp : aspects) {
							Aspect a = (Aspect)realasp;
							long asplanetid = a.getPlanetid();
							if (asplanetid > 0 && asplanetid != p.getId())
								continue;
							if (res <= 0.17
									&& !a.getCode().equals("KERNEL")
									&& p.getCode().equals("Sun"))
								continue;
							if (a.isAspect(res)) {
//								if (19 == p.getId() && 24 == p2.getId())
//									System.out.println("aspectid=" + a.getId());
								SkyPointAspect aspect = new SkyPointAspect();
								aspect.setSkyPoint1(p);
								aspect.setSkyPoint2(p2);
								aspect.setAspect(a);
								aspect.setExact(a.isExact(res));
								aspect.setApplication(a.isApplication(res));
								p.getAspectList().add(aspect);

								aspect = new SkyPointAspect(aspect);
								aspect.setSkyPoint1(p2);
								aspect.setSkyPoint2(p);
								planetList.get(p2.getId()).getAspectList().add(aspect);
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Расчёт статистики аспектов планет
	 * @throws DataAccessException 
	 */
	private void initAspectStatistics() throws DataAccessException {
		try {
				List<Model> aspectTypes = new AspectTypeService().getList();
				Collection<Planet> planets = planetList.values();
				for (Planet p : planets)
					p.setPoints(0);

				for (Planet p : planets) {
					List<SkyPointAspect> paspects = p.getAspectList();
					if (null == paspects)
						continue;

					//создаем карту статистики по аспектам планеты
					Map<String, Integer> aspcountmap = new HashMap<String, Integer>();
					Map<String, String> aspmap = new HashMap<String, String>();
					for (Model asptype : aspectTypes)
						aspcountmap.put(((AspectType)asptype).getCode(), 0);

					for (SkyPointAspect spa : paspects) {
						Aspect a = spa.getAspect();
						String aspectTypeCode = a.getType().getCode();

						//фиксируем аспекты планеты
						aspmap.put(spa.getSkyPoint2().getCode(), a.getCode());
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

						if (a.isMain()) {
							double points = a.getPoints();
							p.addPoints(points);
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
		initAspectStatistics();
		initPlanetDamaged();
//		initAngularPlanets();
		initSunNeighbours();
		initPlanetPositions();
		initPlanetRank();
		initHouseAspects();
		initCuspidAspects();
//		initIngress();
	}

	/**
	 * Инициализация позиций планет
	 */
	private void initPlanetPositions() {
		try {
			PlanetService service = new PlanetService();
			List<Model> positions = new PositionTypeService().getList();
			Collection<Planet> planets = planetList.values();

			for (Planet planet : planets) {
//				System.out.println(planet);
				for (Model type : positions) {
					PositionType pType = (PositionType)type;
					String pCode = pType.getCode();

					if (planet.getSign() != null) {
						Sign sign = service.getSignPosition(planet, pCode, true);
						if (sign != null && sign.getId() == planet.getSign().getId()) {
							switch (pCode) {
								case "HOME": planet.setSignHome(); break;
								case "EXALTATION": planet.setSignExaltated(); break;
								case "EXILE": planet.setSignExile(); break;
								case "DECLINE": planet.setSignDeclined(); break;
							}
						}
					}
					if (null == planet.getHouse()) continue;
					Map<Long, House> houses = service.getHousePosition(planet, pCode, true);
					if (houses != null && houses.containsKey(planet.getHouse().getId())) {
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
		Collection<Planet> planets = planetList.values();
		for (Planet planet : planets) {
			final String LILITH = "Lilith";
			final String KETHU = "Kethu";
			final String SELENA = "Selena";
			final String RAKHU = "Rakhu";

			String pcode = planet.getCode();
			if (!pcode.equals(LILITH) && !pcode.equals(KETHU)
					&& !pcode.equals(SELENA)) {
				List<SkyPointAspect> aspectList = planet.getAspectList();
				if (null == aspectList)
					continue;
				for (SkyPointAspect aspect : aspectList) {
					String pcode2 = aspect.getSkyPoint2().getCode();
					if (pcode.equals(pcode2))
						continue;
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
			else if (0 == bad + badh
					&& good > 0
					&& !planet.isKethued()
					&& !planet.isLilithed())
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
			double res = Math.abs(CalcUtil.getDifference(sun.getLongitude(), planet.getLongitude()));
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

	public void setPlanets(TreeMap<Long, Planet> planets) {
		planetList = planets;
	}
	public void setHouses(TreeMap<Long, House> houses) {
		houseList = houses;
	}

	/**
	 * Поиск угловых планет, расположенных на ASC, IC, DSC, MC
	 */
	@SuppressWarnings("unused")
	private void initAngularPlanets() {
//		Model[] hangular = {
//			houseList.get(142L),
//			houseList.get(151L),	
//			houseList.get(160L),	
//			houseList.get(169L)	
//		};
//		for (Model model : hangular) {
//			House house = (House)model;
//			int prev = (1 == house.getNumber()) ? 34 : house.getNumber() - 3;
//			int next = house.getNumber() + 3;
//			House phouse = (House)houseList.get(prev);
//			House nhouse = (House)houseList.get(next);
//
//			for (Planet planet : planetList.values()) {
//				if (SkyPoint.getHouse(phouse.getLongitude(), nhouse.getLongitude(), planet.getLongitude())) {
//					switch (house.getNumber()) {
//						case 1: planet.setOnRising(true); break;
//						case 10: planet.setOnNadir(true); break;
//						case 19: planet.setOnSetting(true); break;
//						case 28: planet.setOnZenith(true); break;
//					}
//				}
//			}
//		}
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

		//к управителям знаков минорных планет добавляем +1
		Map<Long, Integer> signids = new HashMap<Long, Integer>();
		for (Planet planet : planetList.values()) {
			if (!planet.isMain())
				continue;
			Sign sign = planet.getSign();
			if (null == sign)
				continue;
	    	long sid = planet.getSign().getId();
	    	int val = signids.containsKey(sid) ? signids.get(sid) : 0;
    		signids.put(sid, val + 1);
	    }
		PlanetService service = new PlanetService();
		try {
			for (Map.Entry<Long, Integer> entry : signids.entrySet()) {
				Planet ruler = service.getRuler(entry.getKey(), true, false);
				Planet planet = planetList.get(ruler.getId());
			    planet.setPoints(planet.getPoints() + entry.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	/**
	 * Расчёт аспектов домов
	 * @throws DataAccessException 
	 */
	public void initHouseAspects() throws DataAccessException {
		try {
			if (null == houseList || houseList.isEmpty())
				return;
			aspecthList = new ArrayList<SkyPointAspect>();
			if (planetList != null) { 
				List<Model> aspects = new AspectService().getList();
				Collection<Planet> planets = planetList.values();
				Collection<House> houses = houseList.values();
				for (Planet p : planets) {
					for (House h : houses) {
						double res = CalcUtil.getDifference(p.getLongitude(), h.getLongitude());
						for (Model realasp : aspects) {
							Aspect a = (Aspect)realasp;

							long asplanetid = a.getPlanetid();
							if (asplanetid > 0)
								continue;

							if (a.isAspect(res)) {
								String aspectTypeCode = a.getType().getCode();
								if (aspectTypeCode.equals("NEUTRAL") && p.getCode().equals("Sun") && res <= 3)
									continue;
								SkyPointAspect aspect = new SkyPointAspect();
								aspect.setSkyPoint1(p);
								aspect.setSkyPoint2(h);
								aspect.setAspect(a);
								aspect.setExact(a.isExact(res));
								aspect.setApplication(a.isApplication(res));
								p.getAspectHouseList().add(aspect);
								aspecthList.add(aspect);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Расчёт ингрессий на дату
	 * @param today транзитное событие
	 * @return список ингрессий
	 */
	public Map<String, List<Object>> initIngresses(Event today) {
		Map<String, List<Object>> ingressList = new TreeMap<>();
		try {
			for (String key : Ingress.getKeys())
				ingressList.put(key, new ArrayList<Object>());

			Event yesterday = today.getPrev();
			Map<Long, Planet> yplanets = yesterday.getPlanets();
			Collection<Planet> planets = yplanets.values();
			Collection<Planet> planets2 = planetList.values();
			Map<Long, Planet> tplanets = today.getPlanets();

			List<SkyPointAspect> easpects = initTransits(today);
			List<SkyPointAspect> easpects2 = initTransits(yesterday);

			boolean housable = isHousable();
			List<SkyPointAspect> easpectsh = housable ? initHousesTransits(today) : null;
			List<SkyPointAspect> easpectsh2 = housable ? initHousesTransits(yesterday) : null;
			Collection<House> houses = getHouses().values();

	    	//изменилось ли направление планеты
			for (Planet p : planets) {
				if (p.isFictitious())
					continue;
			    Planet p2 = tplanets.get(p.getId());
			    if (p.isRetrograde() && !p2.isRetrograde())
			    	ingressList.get(Ingress._DIRECT).add(p2);
			    else if (!p.isRetrograde() && p2.isRetrograde())
			    	ingressList.get(Ingress._RETRO).add(p2);
			}

			for (Planet p : planets) {
	            boolean moonable = p.getId().equals(20L);
			    for (Planet p2 : planets2) {
		            //изменились ли транзиты планеты?
		            String acode = null;
		            SkyPointAspect trspa = null;
		            for (SkyPointAspect spa : easpects) {
		            	if (p.getId().equals(spa.getSkyPoint1().getId())
		            			&& p2.getId().equals(spa.getSkyPoint2().getId())) {
		            		acode = spa.getAspect().getCode();
		            		trspa = spa;
		            		break;
		            	}
		            }
		            String acode2 = null;
		            SkyPointAspect trspa2 = null;
		            for (SkyPointAspect spa : easpects2) {
		            	if (p.getId().equals(spa.getSkyPoint1().getId())
		            			&& p2.getId().equals(spa.getSkyPoint2().getId())) {
		            		acode2 = spa.getAspect().getCode();
		            		trspa2 = spa;
		            		break;
		            	}
		            }

		            if (null == acode && null == acode2) { //точного аспекта между планетами не было и нет
						//
		            } else if (null == acode && acode2 != null) { //точный аспект прекратился
		                if (!moonable)
		                    ingressList.get(Ingress._SEPARATION).add(trspa2);
		            } else if (acode != null && null == acode2) //точный аспект появился
						ingressList.get(Ingress._EXACT).add(trspa);
		            else if (acode.equals(acode2)) //точный аспект повторился
		                if (!moonable)
		                	if (ingressList.containsKey(Ingress._REPEAT))
		                		ingressList.get(Ingress._REPEAT).add(trspa2);
				}

			    if (!housable)
			    	continue;
			    for (House p2 : houses) {
		            //изменились ли транзиты дома?
		            String acode = null;
		            SkyPointAspect trspa = null;
		            for (SkyPointAspect spa : easpectsh) {
		            	if (p.getId().equals(spa.getSkyPoint1().getId())
		            			&& p2.getId().equals(spa.getSkyPoint2().getId())) {
		            		acode = spa.getAspect().getCode();
		            		trspa = spa;
		            		break;
		            	}
		            }
		            String acode2 = null;
		            SkyPointAspect trspa2 = null;
		            for (SkyPointAspect spa : easpectsh2) {
		            	if (p.getId().equals(spa.getSkyPoint1().getId())
		            			&& p2.getId().equals(spa.getSkyPoint2().getId())) {
		            		acode2 = spa.getAspect().getCode();
		            		trspa2 = spa;
		            		break;
		            	}
		            }

		            if (null == acode && null == acode2) { //точного аспекта между планетами не было и нет
						//
		            } else if (null == acode && acode2 != null) { //точный аспект прекратился
		                if (!moonable)
		                    ingressList.get(Ingress._SEPARATION_HOUSE).add(trspa2);
		            } else if (acode != null && null == acode2) //точный аспект появился
						ingressList.get(Ingress._EXACT_HOUSE).add(trspa);
		            else if (acode.equals(acode2)) //точный аспект повторился
		                if (!moonable)
		                	if (ingressList.containsKey(Ingress._REPEAT_HOUSE))
		                		ingressList.get(Ingress._REPEAT_HOUSE).add(trspa2);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ingressList;
	}

	public Map<Long, Star> getStars() {
		return starList;
	}

	/**
	 * Массив аспектов
	 */
	private	List<SkyPointAspect> aspectList;

	public void setAspectList(List<SkyPointAspect> aspectList) {
		this.aspectList = aspectList;
	}
	public List<SkyPointAspect> getAspectList() {
		if (null == aspectList)
			aspectList = new ArrayList<SkyPointAspect>();
		return aspectList;
	}

	/**
	 * Проверка, является ли человек ребёнком
	 * @return
	 */
	public boolean isBaby() {
		return getAge() <= MAX_CHILD_AGE;
	}

	/**
	 * Дата изменения
	 */
	private Date modified;

	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}

	/**
	 * Расчёт положения планет и астрологических домов
	 * @param place местонахождение
	 * @param date дата
	 * @param time время
	 * @param cachable признак сохранения данных в кэш
	 */
	private void calcSweph(Place place, String date, String time, boolean cachable) {
		try {
			init(false);
			double lat = place.getLatitude();
			double lon = place.getLongitude();
			double nzone = 0;
			if (cachable)
				nzone = place.getZone();
			else {
				int izone = (int)zone;
				if (izone == zone)
					nzone = zone;
				else {
					double zmin = ((NumberUtil.round(zone, 2) - izone) * 100) / 60.0;
					nzone = izone + zmin;
				}
			}
			double ndst = cachable ? (place.isDst() ? 1 : 0) : dst;
			//System.out.println("calculate\t" + date + "\t" + time + "\tzone:\t" + szone + "\tlat\t" + slat + "\tlon\t" + slon);

			try {
		  		//обрабатываем координаты места
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
//		  	  	String path = "/home/natalie/workspace/kz.zvezdochet.sweph/lib/ephe";
				String path = PlatformUtil.getPath(Activator.PLUGIN_ID, "/lib/ephe").getPath(); //$NON-NLS-1$
		  		sweph.swe_set_ephe_path(path);
		  		sweph.swe_set_sid_mode(SweConst.SE_SIDM_DJWHAL_KHUL, 0, 0);

		  		//обрабатываем дату
		  		int iyear, imonth, iday, imin = 0, isec = 0;
		  		iday = Integer.parseInt(date.substring(0, 2));
		  		imonth = Integer.parseInt(date.substring(3, 5));
		  		iyear = Integer.parseInt(date.substring(6, 10));
		  		
		  		//обрабатываем время, чтобы получить время по Гринвичу
		  		double timing = Double.parseDouble(NumberUtil.trimLeadZero(time.substring(0, 2))); //час по местному времени
		  		nzone += ndst; //часовой пояс + DST
		  		if (nzone < 0) {
		  			if (timing < (24 + nzone))
		  				timing -= nzone;
		  			else {
		  				/*
		  				 * Если час больше разности 24 часов и зоны, значит по Гринвичу будет следующий день,
		  				 * поэтому нужно увеличить указанную дату на 1 день
		  				 */
		  				timing = timing - nzone - 24;
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
		  			if (timing >= nzone)
		  				timing -= nzone;
		  			else {
		  				/*
		  				 * Если час меньше зоны, значит по Гринвичу будет предыдущий день,
		  				 * поэтому нужно уменьшить указанную дату на 1 день
		  				 */
		  				timing = timing + 24 - nzone;
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
		  		imin = Integer.parseInt(NumberUtil.trimLeadZero(time.substring(3,5)));
		  		isec = Integer.parseInt(NumberUtil.trimLeadZero(time.substring(6,8)));

		  		//обрабатываем время
		  		@SuppressWarnings("unused")
				double tjd, tjdet, tjdut, tsid, armc, dhour, deltat;
		  		@SuppressWarnings("unused")
				double eps_true, nut_long, glon, glat;
		  		dhour = timing + imin/60.0 + isec/3600.0;
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
		  		int[] plist = getPlanetList();
		  		Planet p;
		  		for (int i = 0; i < plist.length; i++) {
		  		    rflag = sweph.swe_calc_ut(tjdut, plist[i], (int)iflag, xx, sb);
		  		    planets[i] = xx[0];
		  		    long n = constToPlanet(i);
		  		    if (n >= 0) {
		  		    	p = planetList.get(n);
		  	  			p.setLongitude(xx[0]);
			  			p.setLatitude(xx[1]);
			  			p.setDistance(xx[2]);
		  	  			p.setSpeedLongitude(xx[3]);
			  			p.setSpeedLatitude(xx[4]);
			  			p.setSpeedDistance(xx[5]);
		  		    }
		  		}
		  		//рассчитываем координату Кету по значению Раху
		  		p = planetList.get(22L);
		  		if (planets[10] > 180)
		  			p.setLongitude(planets[10] - 180);
		  		else
		  			p.setLongitude(planets[10] + 180);

		  		//расчёт куспидов домов
		  		/*
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
		  		if (isHousable()) {
			  		char hsys = 'P';
	
			  		sb = new StringBuffer(new String(serr));
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
			  		sweph.swe_houses(tjdut, SweConst.SEFLG_SIDEREAL, glat, glon, hsys, hcusps, ascmc);
			  		calcHouseParts(hcusps);
		  		}
		  		//расчёт координат звёзд
		  		for (Star star : starList.values()) {
		  			sb = new StringBuffer(new String(serr));
		  			rflag = sweph.swe_fixstar_ut(new StringBuffer(star.getCode()), tjdut, (int)iflag, xx, sb);
		  			star.setLongitude(xx[0]);
		  			star.setLatitude(xx[1]);
		  			star.setDistance(xx[2]);

					Sign sign = SkyPoint.getSign(star.getLongitude(), getBirthYear());
					star.setSign(sign);
		  		}
		  		sweph.swe_close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!cachable) {
				initSigns();
				initAspects();
				initHouses();
				initPlanetStatistics();
				setRecalculable(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Маршализация расчётных данных в JSON-массив
	 * @return JSON-строка
	 */
    public String toJSON() {
    	JSONObject object = new JSONObject();

    	JSONArray array = new JSONArray();
        for (Planet planet : planetList.values())
        	array.put(planet.getId().intValue() - 19, planet.toJSON());
        object.put("planets", array);

        return object.toString();
    }

    /**
     * Инициализация расчётных данных из JSON-массива
     * @param json JSON-строка
     */
    public void init(String json) {
    	if (null == json) return;
    	try {
    		init(false);
	    	JSONObject object = new JSONObject(json);
	    	JSONArray array = object.getJSONArray("planets");
	    	for (int i = 0; i < array.length(); i++) {
	    		String str = array.getString(i);
	    		Planet jplanet = new Planet(str);
	    		Planet planet = planetList.get((long)i + 19);
	    		planet.setLongitude(jplanet.getLongitude());
	    		planet.setLatitude(jplanet.getLatitude());
	    		planet.setDistance(jplanet.getDistance());
	    		planet.setSpeedLongitude(jplanet.getSpeedLongitude());
	    		planet.setSpeedLatitude(jplanet.getSpeedLatitude());
	    		planet.setSpeedDistance(jplanet.getSpeedDistance());
	    	}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	/**
	 * Расчёт транзитов
	 * @param event транзитное событие
	 * @return список транзитов планет
	 * @throws DataAccessException
	 */
    private List<SkyPointAspect> initTransits(Event event) throws DataAccessException {
		List<SkyPointAspect> spas = new ArrayList<SkyPointAspect>();
		try {
			Collection<Planet> planets = event.getPlanets().values();
			Collection<Planet> planets2 = planetList.values();
			List<Model> aspects = new AspectService().getMajorList();

			for (Planet p : planets) {
				for (Planet p2 : planets2) {
					if (p2.getCode().equals("Rakhu") && p.getCode().equals("Kethu"))
						continue;
					if (p2.getCode().equals("Kethu") && p.getCode().equals("Kethu"))
						continue;

					double one = p.getLongitude();
					double two = p2.getLongitude();

					double res = CalcUtil.getDifference(p.getLongitude(), p2.getLongitude());
					if (p2.getCode().equals("Rakhu") || p2.getCode().equals("Kethu"))
						if ((res >= 179 && res < 180)
								|| CalcUtil.compareAngles(one, two))
							++res;

					for (Model realasp : aspects) {
						Aspect a = (Aspect)realasp;
						if (!a.isMain())
							continue;
						if (a.getPlanetid() > 0)
							continue;

						if (a.isExact(res)) {
							SkyPointAspect aspect = new SkyPointAspect();
							aspect.setSkyPoint1(p);
							aspect.setSkyPoint2(p2);
							aspect.setScore(res);
							aspect.setAspect(a);
							aspect.setRetro(p.isRetrograde());
							aspect.setExact(true);
							spas.add(aspect);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return spas;
	}

	/**
	 * Расчёт транзитов домов
	 * @param event транзитное событие
	 * @return список транзитов домов
	 * @throws DataAccessException
	 */
	private List<SkyPointAspect> initHousesTransits(Event event) throws DataAccessException {
		List<SkyPointAspect> spas = new ArrayList<SkyPointAspect>();
		try {
			Collection<Planet> planets = event.getPlanets().values();
			Collection<House> houses = houseList.values();
			List<Model> aspects = new AspectService().getMajorList();

			for (Planet p : planets) {
				for (House p2 : houses) {
					double one = p.getLongitude();
					double two = p2.getLongitude();

					double res = CalcUtil.getDifference(p.getLongitude(), p2.getLongitude());
					if ((res >= 179 && res < 180)
							|| CalcUtil.compareAngles(one, two))
						++res;

					for (Model realasp : aspects) {
						Aspect a = (Aspect)realasp;
						if (!a.isMain())
							continue;
						if (a.getPlanetid() > 0)
							continue;

						if (a.isExact(res)) {
							SkyPointAspect aspect = new SkyPointAspect();
							aspect.setSkyPoint1(p);
							aspect.setSkyPoint2(p2);
							aspect.setScore(res);
							aspect.setAspect(a);
							aspect.setRetro(p.isRetrograde());
							aspect.setExact(true);
							spas.add(aspect);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return spas;
	}

	/**
	 * Параметры космограммы для толкования
	 */
	private String options;

	public String getOptions() {
		return options;
	}
	public void setOptions(String options) {
		this.options = options;
	}

	/**
	 * Расчёт аспектов куспидов домов
	 * @throws DataAccessException 
	 */
	public void initCuspidAspects() throws DataAccessException {
		try {
			if (null == houseList || houseList.isEmpty())
				return;
			aspectcList = new ArrayList<SkyPointAspect>();
			List<Model> aspects = new AspectService().getList();
			Collection<House> houses = houseList.values();
			for (House h : houses) {
				for (House h2 : houses) {
					double res = CalcUtil.getDifference(h.getLongitude(), h2.getLongitude());
					for (Model realasp : aspects) {
						Aspect a = (Aspect)realasp;
						if (a.isAspect(res)) {
							SkyPointAspect aspect = new SkyPointAspect();
							aspect.setSkyPoint1(h);
							aspect.setSkyPoint2(h2);
							aspect.setAspect(a);
							aspect.setExact(a.isExact(res));
							aspect.setApplication(a.isApplication(res));
							h.getAspectHouseList().add(aspect);
							aspectcList.add(aspect);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Список аспектов куспидов с домами
	 */
	private	List<SkyPointAspect> aspectcList;

	/**
	 * Проверка, является ли событие персоной
	 * @return true - человек
	 */
	public boolean isHuman() {
		return (1 == human);
	}

	/**
	 * Текущее место проживания
	 */
	private Place currentPlace;

	public Place getCurrentPlace() {
		return currentPlace;
	}
	public void setCurrentPlace(Place currentPlace) {
		this.currentPlace = currentPlace;
	}

	/**
	 * Имя по-английски
	 */
	private String name_en;
	/**
	 * Комментарий по-английски
	 */
	private String comment_en;

	public String getName_en() {
		return name_en;
	}
	public void setName_en(String name_en) {
		this.name_en = name_en;
	}
	public String getComment_en() {
		return comment_en;
	}
	public void setComment_en(String comment_en) {
		this.comment_en = comment_en;
	}

	/**
	 * Проверка, является ли время рождения точным
	 * @return false - нет
	 */
	public boolean isRectified() {
		return 1 == rectification;
	}
}
