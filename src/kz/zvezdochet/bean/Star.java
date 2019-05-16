package kz.zvezdochet.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Color;

import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.service.StarService;

/**
 * Звезда
 * @author Natalie Didenko
 */
public class Star extends SkyPoint {
	private static final long serialVersionUID = 6447111682104406971L;

	private double latitude;
	private double distance;

    public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	/**
     * Знак Зодиака, в котором находится звезда
     */
    private Sign sign;

    /**
     * Дом, в котором находится звезда
     */
    private House house;

    /**
     * Признак поражённости
     */
    private boolean damaged = false;
    
    /**
     * Признак непоражённости
     */
    private boolean perfect = false;
    /**
     * Признак слабости (по очкам)
     */
    private boolean broken = false;
    /**
     * Признак соединения с Кету
     */
    private boolean kethued = false;
    /**
     * Признак владыки гороскопа (звезда, у которой больше всего сильных аспектов)
     */
    private boolean lord = false;
    /**
     * Признак силы (соединение с Раху)
     */
    private boolean rakhued = false;

    /**
     * Признак соединения с Лилит
     */
    private boolean lilithed = false;

    /**
     * Цвет
     */
    private Color color;

    public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public boolean isLilithed() {
		return lilithed;
	}

	public void setLilithed() {
		lilithed = true;
		setPerfect(false);
		addPoints(-1);
//		System.out.println(this.getCode() + " is lilithed");
	}

	/**
	 * Карта аспектов
	 */
	private Map<String, String> aspectMap;

	public Map<String, String> getAspectMap() {
		return aspectMap;
	}

	public void setAspectMap(Map<String, String> aspectMap) {
		this.aspectMap = aspectMap;
	}

	public Sign getSign() {  
		return sign;
	}

	public void setSign(Sign sign) {
		this.sign = sign;
	}

	public House getHouse() {
		return house;
	}

	public void setHouse(House house) {
		this.house = house;
	}

    /**
     * Метод, проверяющий, находится ли планета в шахте
     * @return <i>true</i> если планета не имеет сильных аспектов
     */
	public boolean inMine() {
		return aspectCountMap != null && 0 == aspectCountMap.get("COMMON");
	}

	public boolean isDamaged() {
		return damaged;
	}

	public void setDamaged(boolean damaged) {
		this.damaged = damaged;
		if (damaged) {
			setPerfect(false);
			addPoints(-1);
		}
	}

	public boolean isPerfect() {
		return perfect;
	}

	public void setPerfect(boolean perfect) {
		this.perfect = perfect;
		if (perfect) {
			addPoints(1);
		}
	}

	public boolean isBroken() {
		return broken;
	}

	public void setBroken() {
		broken = true;
		setLord(false);
		addPoints(-1);
	}

	public DictionaryService getService() {
		return new StarService();
	}

	/**
	 * Признаки угловой звезды
	 */
	private boolean onASC = false;
	private boolean onIC = false;
	private boolean onDSC = false;
	private boolean onMC = false;
	
	public void setOnASC(boolean onAsc) {
		onASC = onAsc;
	}
	public boolean onIC() {
		return onIC;
	}

	public void setOnIC(boolean onIC) {
		this.onIC = onIC;
	}

	public boolean onDSC() {
		return onDSC;
	}

	public void setOnDSC(boolean onDSC) {
		this.onDSC = onDSC;
	}

	public boolean onMC() {
		return onMC;
	}

	public void setOnMC(boolean onMC) {
		this.onMC = onMC;
	}

	public boolean onASC() {
		return onASC;
	}

	public boolean isLord() {
		return lord;
	}

	public void setLord(boolean lord) {
		this.lord = lord;
	}

	public boolean isRakhued() {
		return rakhued;
	}

	public void setRakhued() {
		this.rakhued = true;
		addPoints(1);
	}

	/**
	 * Очки
	 */
    private double points;

	public double getPoints() {
		return points;
	}

	public void setPoints(double points) {
		this.points = points;
	}

	public void addPoints(double points) {
		this.points += points;
	}

	public boolean isNeutral() {
		return !isPositive() && !isNegative();				
	}

	public boolean isPositive() {
		return isLord() || isPerfect()
			&& (!inMine() && !isBroken() && !isDamaged() && !isLilithed()
		);
	}

	public boolean isNegative() {
		return inMine() || isBroken() || isDamaged() || isLilithed()
			&& (!isLord() && !isPerfect()
		);				
	}

	public void setSelened() {
		selened = true;
		addPoints(1);
	}

	public boolean isKethued() {
		return kethued;
	}

	public void setKethued() {
		kethued = true;
		setPerfect(false);
	}

	/**
	 * Король аспектов (звезда с наибольшим количеством позитивных аспектов)
	 */
	private boolean king;

	public boolean isKing() {
		return king;
	}

	public void setKing() {
		this.king = true;
	}

	/**
	 * Признак соединения с Селеной
	 */
	private boolean selened;

	public boolean isSelened() {
		return selened;
	}

    /**
     * Негативный псевдоним
     */
    private String negative;

	public String getNegative() {
		return negative;
	}

	public void setNegative(String badname) {
		this.negative = badname;
	}

    /**
     * Позитивный псевдоним
     */
    private String positive;

	public String getPositive() {
		return positive;
	}

	public void setPositive(String positive) {
		this.positive = positive;
	}

	/**
	 * Массив аспектов звезды
	 */
	private	List<SkyPointAspect> aspectList;

	public List<SkyPointAspect> getAspectList() {
		if (null == aspectList)
			aspectList = new ArrayList<SkyPointAspect>();
		return aspectList;
	}
}
