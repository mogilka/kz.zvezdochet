package kz.zvezdochet.bean;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.core.util.DateUtil;
import kz.zvezdochet.service.EventService;
import kz.zvezdochet.service.PlaceService;
import kz.zvezdochet.util.Configuration;

import org.eclipse.swt.graphics.Image;

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
	 * Приоритет полушарий (левое, правое)
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
	 * Идентификатор места
	 */
	private long placeid;
	/**
	 * Место
	 */
	private Place place;
	/**
	 * Реальная временная разница с Гринвичем
	 */
	private double zone;
	/**
	 * Знак Зодиака
	 */
	private String sign;
	/**
	 * Стихия
	 */
	private String element;
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
	 */
	private boolean human = true;
	
	public void setHuman(boolean human) {
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
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getElement() {
		return element;
	}
	public void setElement(String element) {
		this.element = element;
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
	public void init() {
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
			configuration = new Configuration(birth);
			service.initPlanets(this);
			service.initHouses(this);
			service.initAspects(this);
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

	public boolean isHuman() {
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

	public void calc() {
		//new Configuration("12.12.2009", "23:11:16", "6.0", "43.15", "76.55");
		try {
			Configuration configuration = new Configuration(
				birth,
				Double.toString(zone),
				Double.toString(place.getLatitude()),
				Double.toString(place.getLongitude()));
			setConfiguration(configuration);		
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
}
