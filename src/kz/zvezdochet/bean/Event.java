package kz.zvezdochet.bean;

import java.util.Date;

import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.core.util.StringUtil;
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
	 * Фамилия
	 */
	private String surname;
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
	public String getSurname() {
		return surname != null ? surname : "";
	}
	public void setSurname(String surname) {
		this.surname = surname;
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
	/**
	 * Определение полного имени клиента
	 * @return полное имя
	 */
	public String getFullName() {
		return StringUtil.safeString(getName()) + " " + StringUtil.safeString(surname);
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
			}
			
			//конфигурация
			configuration = new Configuration(birth);
			service.initPlanets(this);
			service.initHouses(this);
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

	public boolean isHuman() {//TODO сделать поле
		return true;
	}
}
