package kz.zvezdochet.bean;

import kz.zvezdochet.core.bean.TextGenderDictionary;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.service.CardKindService;


/**
 * Вид космограммы
 * @author Natalie Didenko
 */
public class CardKind extends TextGenderDictionary {
	private static final long serialVersionUID = 7203713223993014957L;

	@Override
	public ModelService getService() {
		return new CardKindService();
	}

	/**
	 * Направление рисунка up|down|left|right (для лука и т.п.)
	 */
	private String direction;

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	/**
	 * Толкование высокого уровня
	 */
	private String high;
	/**
	 * Толкование среднего уровня
	 */
	private String medium;
	/**
	 * Толкование низкого уровня
	 */
	private String low;

	public String getHigh() {
		return high;
	}

	public void setHigh(String high) {
		this.high = high;
	}

	public String getMedium() {
		return medium;
	}

	public void setMedium(String medium) {
		this.medium = medium;
	}

	public String getLow() {
		return low;
	}

	public void setLow(String low) {
		this.low = low;
	}

	/**
	 * Описание занимаемых градусов
	 */	
	private String degree;

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}
}