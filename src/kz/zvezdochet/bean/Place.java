package kz.zvezdochet.bean;

import java.lang.reflect.Field;
import java.util.Date;

import org.json.JSONObject;

import kz.zvezdochet.core.bean.Dictionary;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.core.util.DateUtil;
import kz.zvezdochet.service.PlaceService;

/**
 * Местонахождение
 * @author Nataly Didenko
 * @link https://www.horlogeparlante.com/История.html?city=515001
 */
public class Place extends Dictionary {
	private static final long serialVersionUID = 7176275971217317131L;

	/**
	 * Долгота
	 */
	private double longitude;
	/**
	 * Широта
	 */
	private double latitude;
	/**
	 * Разница с Гринвичем
	 */
	private double greenwich;
	/**
	 * Тип place|region|country город|регион|страна
	 */
	private String type;
	/**
	 * Идентификатор родителя
	 */
	private long parentid;
	
	public long getParentid() {
		return parentid;
	}
	public void setParentid(long parentid) {
		this.parentid = parentid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getGreenwich() {
		return greenwich;
	}
	public void setGreenwich(double greenwich) {
		this.greenwich = greenwich;
	}
	/**
	 * Дата изменения
	 */
	private Date date;

	public DictionaryService getService() {
		return new PlaceService();
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Поиск места по умолчанию
	 * @return Гринвич
	 */
	public Place getDefault() {
		try {
			return (Place)getService().find(7095L);
		} catch (DataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Конвертация параметров JSON в объект места
	 * @param json объект JSON
	 */
	public Place(JSONObject json) {
		super();
		setId(json.getLong("ID"));
		setName(json.getString("Name"));
		Object value = json.get("Code");
		if (value != JSONObject.NULL)
			setCode(value.toString());
		value =json.get("Description");
		if (value != JSONObject.NULL)
			setDescription(value.toString());
		value =json.get("parentid");
		if (value != JSONObject.NULL)
			setParentid((int)value);
		setLatitude(json.getDouble("Latitude"));
		setLongitude(json.getDouble("Longitude"));
		setGreenwich(json.getDouble("Greenwich"));
		setType(json.getString("type"));
		setDate(DateUtil.getDatabaseDateTime(json.getString("date")));
	}

	public Place() {}

	/**
	 * Маршализация модели для лога
	 * @return строка параметров модели
	 */
	public String toLog() {
		String res = "name:" + name + ", ";
		try {
			Field[] fields = getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++)
				res += fields[i].getName() + ":" + fields[i].get(this) + ", ";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "[" + res + "]";
	}
}
