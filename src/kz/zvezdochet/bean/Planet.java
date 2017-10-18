package kz.zvezdochet.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.service.PlanetService;

/**
 * Планета
 * @author Nataly Didenko
 */
public class Planet extends SkyPoint {
	private static final long serialVersionUID = -8328248201235163517L;

	/**
	 * Балл
	 */
    private double score;

	/**
	 * Признак ретроградности
	 */
	private boolean retrograde = false;
	
    /**
     * Знак Зодиака, в котором находится планета
     */
    private Sign sign;

    /**
     * Дом, в котором находится планета
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
     * Признак слабой планеты (по очкам)
     */
    private boolean broken = false;
    /**
     * Признак соединения с Кету
     */
    private boolean kethued = false;
    /**
     * Признак владыки гороскопа (планета, у которой больше всего сильных аспектов)
     */
    private boolean lord = false;
    /**
     * Признак силы (соединение с Раху)
     */
    private boolean rakhued = false;

    /**
     * Признак фиктивной планеты
     */
    private boolean fictious = false;

    /**
     * Признак соединения с Лилит
     */
    private boolean lilithed = false;

    /**
     * Символ
     */
    private String symbol;
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

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public boolean isLilithed() {
		return lilithed;
	}

	public void setLilithed() {
		lilithed = true;
		setPerfect(false);
		addPoints(-1);
		System.out.println(this.getCode() + " is lilithed");
	}

	/**
     * Краткое описание
     */
    private String shortName;

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * Карта аспектов планеты
	 */
	private Map<String, String> aspectMap;

	public Map<String, String> getAspectMap() {
		return aspectMap;
	}

	public void setAspectMap(Map<String, String> aspectMap) {
		this.aspectMap = aspectMap;
	}

	public boolean isFictitious() {
		return fictious;
	}

	public void setFictitious(boolean fictitious) {
		this.fictious = fictitious;
	}

	public Planet(String name) {
		setName(name);
	}

	public Planet() {}

	public Planet(Planet planet) {
		id = planet.id;
		name = planet.name;
		code = planet.code;
		coord = planet.coord;
		number = planet.number;
		symbol = planet.symbol;
		shortName = planet.shortName;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

    /**
     * Метод, проверяющий, имеет ли планета признак главной планеты
     * @return <i>true</i> если планета является главной
     */
	public boolean isMain() {
		return score > 1;
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
		System.out.println(this.getCode() + " is damaged");
	}

	public boolean isPerfect() {
		return perfect;
	}

	public void setPerfect(boolean perfect) {
		this.perfect = perfect;
		if (perfect) {
			addPoints(1);
			System.out.println(this.getCode() + " is perfect");
		}
	}

	public Image getImage() {
		return AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/planet/" + 
			getCode() + ".png").createImage();
	}

	public boolean isBroken() {
		return broken;
	}

	public void setBroken() {
		broken = true;
		setPerfect(false);
		setLord(false);
		setDamaged(false);
		addPoints(-1);
		System.out.println(this.getCode() + " is broken");
	}

	public boolean isRetrograde() {
		return retrograde;
	}

	public void setRetrograde() {
		retrograde = true;
		addPoints(-1);
		System.out.println(this.getCode() + " is retro");
	}

	public DictionaryService getService() {
		return new PlanetService();
	}

	/**
	 * Признаки угловой планеты
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

	/**
	 * Признаки соединения с Солнцем
	 */
	private boolean kernel = false;
	private boolean belt = false;
	private boolean shield = false;
	private boolean sword = false;

	public boolean isKernel() {
		return kernel;
	}

	public void setKernel() {
		kernel = true;
		addPoints(1);
		System.out.println(this.getCode() + " is kernel");
	}

	public boolean isBelt() {
		return belt;
	}

	public void setBelt() {
		belt = true;
		setPerfect(false);
		addPoints(-1);
		System.out.println(this.getCode() + " is belt");
	}

	public boolean isShield() {
		return shield;
	}

	public void setShield() {
		shield = true;
		addPoints(1);
		System.out.println(this.getCode() + " is shield");
	}

	public boolean isSword() {
		return sword;
	}

	public void setSword() {
		sword = true;
		addPoints(1);
		System.out.println(this.getCode() + " is sword");
	}

	/**
	 * Позиции в знаках и домах
	 */
	private boolean signHome = false;
	private boolean signExaltated = false;
	private boolean signExile = false;
	private boolean signDeclined = false;

	private boolean houseHome = false;
	private boolean houseExaltated = false;
	private boolean houseExile = false;
	private boolean houseDeclined = false;

	public boolean isSignHome() {
		return signHome;
	}

	public void setSignHome() {
		signHome = true;
		addPoints(1);
		System.out.println(this.getCode() + " is in home sign");
	}

	public boolean isSignExaltated() {
		return signExaltated;
	}

	public void setSignExaltated() {
		signExaltated = true;
		addPoints(1);
		System.out.println(this.getCode() + " is in exalt sign");
	}

	public boolean isSignExile() {
		return signExile;
	}

	public void setSignExile() {
		signExile = true;
		addPoints(-1);
		System.out.println(this.getCode() + " is in exile sign");
	}

	public boolean isSignDeclined() {
		return signDeclined;
	}

	public void setSignDeclined() {
		signDeclined = true;
		addPoints(-1);
		System.out.println(this.getCode() + " is in decline sign");
	}

	public boolean isHouseHome() {
		return houseHome;
	}

	public void setHouseHome() {
		houseHome = true;
		addPoints(1);
		System.out.println(this.getCode() + " is in home house");
	}

	public boolean isHouseExaltated() {
		return houseExaltated;
	}

	public void setHouseExaltated() {
		houseExaltated = true;
		addPoints(1);
		System.out.println(this.getCode() + " is in exalt house");
	}

	public boolean isHouseExile() {
		return houseExile;
	}

	public void setHouseExile() {
		houseExile = true;
		addPoints(-1);
		System.out.println(this.getCode() + " is in exile house");
	}

	public boolean isHouseDeclined() {
		return houseDeclined;
	}

	public void setHouseDeclined() {
		houseDeclined = true;
		addPoints(-1);
		System.out.println(this.getCode() + " is in decline house");
	}

	public boolean isLord() {
		return lord;
	}

	public void setLord(boolean lord) {
		this.lord = lord;
		if (lord)
			System.out.println(this.getCode() + " is Lord");
	}

	public boolean isRakhued() {
		return rakhued;
	}

	public void setRakhued() {
		this.rakhued = true;
		addPoints(1);
		System.out.println(this.getCode() + " is strong");
	}

	public static Long[] getSportSet() {
		return new Long[] {19L,21L,22L,23L,25L,26L,27L,28L,29L,30L,31L,32L,33L,34L};
	}

	public List<Object> isIngressed(Event prev, Event next) {
		List<Object> list = new ArrayList<>();
		try {
			List<Model> planets = prev.getConfiguration().getPlanets();
			List<Model> planets2 = next.getConfiguration().getPlanets();
			for (Model model : planets) {
				Planet planet = (Planet)model;
				if (planet.getCode().equals(this.code)) {
					if (Math.abs(this.coord) == Math.abs(planet.coord)) //планета осталась в той же координате
						list.add("S");
					else if (planet.retrograde && !this.retrograde) //планета перешла в директное движение
						list.add("D");
					else if (this.retrograde && !planet.retrograde) //планета перешла в обратное движение
						list.add("R");

					//изменился ли знак Зодиака планеты
					Sign sign = this.sign;
					if (null == sign)
						sign = SkyPoint.getSign(this.coord, next.getBirthYear());

					Sign sign2 = planet.sign;
					if (null == sign2)
						sign2 = SkyPoint.getSign(planet.coord, prev.getBirthYear());

					if (sign.getId() != sign2.getId())
						list.add("M");

					//изменились ли аспекты
					Map<String,String> map = planet.getAspectMap();
					for (Model model2 : planets2) {
						Planet planet2 = (Planet)model2;
						Map<String,String> map2 = planet2.getAspectMap();
						String acode = map.get(planet2.getCode());
						String acode2 = map2.get(planet.getCode());
						if (acode != acode2) {
							list.add("A");
							break;
						}
					}
/*
SELECT * FROM stargazer.eventaspects
where eventid in (47457,47456)
and planetid = 19
and planet2id = 26
order by planetid, planet2id
limit 500
*/
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Поиск знака силы планеты
	 * @param type sign|house|null для знака|дома|описания
	 * @return знак силы
	 */
	public String getMark(String type) {
		String res = "";
		if (type != null) {
			if (type.equals("sign")) {
				if (isSignHome())
					res = "обт "; //U+1F3E0
				else if (isSignExaltated())
					res = "экз ";
				else if (isSignDeclined())
					res = "пдн ";
				else if (isSignExile())
					res = "изг ";
			} else if (type.equals("house")) {
				if (isHouseHome())
					res = "обт ";
				else if (isHouseExaltated())
					res = "экз ";
				else if (isHouseDeclined())
					res = "пдн ";
				else if (isHouseExile())
					res = "изг ";
			}
		}
		if (isBelt() || isDamaged() || isLilithed() || isBroken() || inMine())
			res += "\u2193";
		else if (isKernel() || isPerfect())
			res += "\u2191";

		if (isLord())
			res += "влд ";
		if (isKing())
			res += "крл ";
		if (isRetrograde())
			res += "R";
		return res;
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
		return isLord() || isKernel() || isPerfect()
				|| isHouseExaltated() || isHouseHome() || isSignExaltated() || isSignHome()				
			&& (!inMine() && !isBelt() && !isBroken() && !isDamaged() && !isRetrograde() && !isLilithed()
				&& !isHouseDeclined() && !isHouseExile() && !isSignDeclined() && !isSignExile());
	}

	public boolean isNegative() {
		return inMine() || isBelt() || isBroken() || isDamaged() || isRetrograde() || isLilithed()
				|| isHouseDeclined() || isHouseExile() || isSignDeclined() || isSignExile()
			&& (!isLord() && !isKernel() && !isPerfect()
				 && !isHouseExaltated() && !isHouseHome() && !isSignExaltated() && !isSignHome());				
	}

	public void setSelened() {
		selened = true;
		addPoints(1);
		System.out.println(this.getCode() + " is selened");
	}

	public boolean isKethued() {
		return kethued;
	}

	public void setKethued() {
		kethued = true;
		setPerfect(false);
		System.out.println(this.getCode() + " is weak");
	}

	/**
	 * Король аспектов (планета с наибольшим количеством позитивных аспектов)
	 */
	private boolean king;

	public boolean isKing() {
		return king;
	}

	public void setKing() {
		this.king = true;
	}

	/**
	 * Описание для синастрии
	 */
	private String synastry;

	public String getSynastry() {
		return synastry;
	}

	public void setSynastry(String synastry) {
		this.synastry = synastry;
	}

	/**
	 * Признак соединения с Селеной
	 */
	private boolean selened;

	public boolean isSelened() {
		return selened;
	}
}
