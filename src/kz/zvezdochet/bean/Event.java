package kz.zvezdochet.bean;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.json.JSONObject;

import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.core.util.DateUtil;
import kz.zvezdochet.service.EventService;
import kz.zvezdochet.service.PlaceService;
import kz.zvezdochet.util.Configuration;

/**
 * Событие или персона
 * @author Nataly Didenko
 */
public class Event extends Model {
	private static final long serialVersionUID = 3237544571447808520L;
	
	public Event() {
		super();
		name = "";
		birth = new Date();
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
	 * Расчетная конфигурация события
	 */
	private Configuration configuration;
	/**
	 * Описание события или биография человека
	 */
    private String text;
	/**
	 * Краткий комментарий
	 */
	private String description;
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
	 * Дата изменения
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
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
	public Configuration getConfiguration() {
		return configuration;
	}
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
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

	/**
	 * Инициализация полных данных о событии
	 */
	public void init(boolean initstat) {
		try {
			//местонахождение
			if (null == place)
				place = (Place)new PlaceService().find(placeid);
			
			//блобы
			EventService service = new EventService();
			Object[] blob = service.findBlob(id);
			if (blob != null && blob.length > 0) {
				if (blob[0] != null)
					text = blob[0].toString();
//				if (blob[1] != null) {
//	                InputStream is = new ByteArrayInputStream((byte[])blob[1]);
//					image = new Image(Display.getDefault(), is);
//				}
				if (blob[2] != null)
					conversation = blob[2].toString();
			}
			
			//конфигурация
			if (null == id)
				calc(false);
			else {
				configuration = new Configuration(birth);
				configuration.setEvent(this);
				service.initPlanets(this);
				service.initHouses(this);
				service.initAspects(this);
				if (initstat)
					configuration.initPlanetStatistics();
			}
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
	public int MAX_TEEN_AGE = 17;

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
	 * @param boolean признак расчёта статистики планет
	 */
	public void calc(boolean initstat) {
		//new Configuration("12.12.2009", "23:11:16", "6.0", "43.15", "76.55");
		try {
			Place calcplace = (null == place) ? new Place().getDefault() : place;
			Configuration configuration = new Configuration(
				this,
				birth,
				Double.toString(zone + dst),
				Double.toString(calcplace.getLatitude()),
				Double.toString(calcplace.getLongitude()),
				initstat);
			setConfiguration(configuration);
			setNeedSaveCalc(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Идентификатор пользователя
	 */
	private long userid;

	public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
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
	private boolean needSaveCalc = false;
	/**
	 * Признак необходимости сохранить медиа-данные в БД
	 */
	private boolean needSaveBlob = false;

	public boolean isNeedSaveCalc() {
		return needSaveCalc;
	}
	public void setNeedSaveCalc(boolean needRecalc) {
		this.needSaveCalc = needRecalc;
	}
	public boolean isNeedSaveBlob() {
		return needSaveBlob;
	}
	public void setNeedSaveBlob(boolean needReblob) {
		this.needSaveBlob = needReblob;
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
		setDescription(json.get("Comment").toString());
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
		setUserid(1);
		setDate(DateUtil.getDatabaseDateTime(json.getString("date")));
		setFancy(json.getString("fancy"));
		value = json.get("backid");
		if (value != JSONObject.NULL)
			setBackid(json.getLong("backid"));
		setDst(json.getDouble("dst"));
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
	 * Проверка, является ли человек ребёнком
	 * @return
	 */
	public boolean isChild() {
		return getAge() < MAX_TEEN_AGE;
	}
}
