package kz.zvezdochet.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.json.JSONObject;

import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.service.PlanetService;

/**
 * Планета
 * @author Natalie Didenko
 */
public class Planet extends SkyPoint {
	private static final long serialVersionUID = -8328248201235163517L;

	/**
	 * Балл
	 */
    private double score;

    /**
     * Признак фиктивной планеты
     */
    private boolean fictious = false;

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

	public boolean isFictitious() {
		return fictious;
	}

	public void setFictitious(boolean fictitious) {
		this.fictious = fictitious;
	}

	public Planet(String string) {
    	JSONObject object = new JSONObject(string);
        setId(object.getLong("id"));
        setLongitude(object.getDouble("longitude"));
        setLatitude(object.getDouble("latitude"));
        setDistance(object.getDouble("distance"));
        setSpeedLongitude(object.getDouble("speed_longitude"));
        setSpeedLatitude(object.getDouble("speed_latitude"));
        setSpeedDistance(object.getDouble("speed_distance"));
	}

	public Planet() {}

	public Planet(Planet planet) {
		id = planet.id;
		name = planet.name;
		code = planet.code;
		longitude = planet.longitude;
		number = planet.number;
		symbol = planet.symbol;
		shortName = planet.shortName;
		negative = planet.negative;
		positive = planet.positive;
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

	public Image getImage() {
		return AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/planet/" + 
			getCode() + ".png").createImage();
	}

	public DictionaryService getService() {
		return new PlanetService();
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
//		System.out.println(this.getCode() + " is kernel");
	}

	public boolean isBelt() {
		return belt;
	}

	public void setBelt() {
		belt = true;
		setPerfect(false);
		addPoints(-1);
//		System.out.println(this.getCode() + " is belt");
	}

	public boolean isShield() {
		return shield;
	}

	public void setShield() {
		shield = true;
		addPoints(1);
//		System.out.println(this.getCode() + " is shield");
	}

	public boolean isSword() {
		return sword;
	}

	public void setSword() {
		sword = true;
		addPoints(1);
//		System.out.println(this.getCode() + " is sword");
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
//		System.out.println(this.getCode() + " is in home sign");
	}

	public boolean isSignExaltated() {
		return signExaltated;
	}

	public void setSignExaltated() {
		signExaltated = true;
		addPoints(1);
//		System.out.println(this.getCode() + " is in exalt sign");
	}

	public boolean isSignExile() {
		return signExile;
	}

	public void setSignExile() {
		signExile = true;
		addPoints(-1);
//		System.out.println(this.getCode() + " is in exile sign");
	}

	public boolean isSignDeclined() {
		return signDeclined;
	}

	public void setSignDeclined() {
		signDeclined = true;
		addPoints(-1);
//		System.out.println(this.getCode() + " is in decline sign");
	}

	public boolean isHouseHome() {
		return houseHome;
	}

	public void setHouseHome() {
		houseHome = true;
		addPoints(1);
//		System.out.println(this.getCode() + " is in home house");
	}

	public boolean isHouseExaltated() {
		return houseExaltated;
	}

	public void setHouseExaltated() {
		houseExaltated = true;
		addPoints(1);
//		System.out.println(this.getCode() + " is in exalt house");
	}

	public boolean isHouseExile() {
		return houseExile;
	}

	public void setHouseExile() {
		houseExile = true;
		addPoints(-1);
//		System.out.println(this.getCode() + " is in exile house");
	}

	public boolean isHouseDeclined() {
		return houseDeclined;
	}

	public void setHouseDeclined() {
		houseDeclined = true;
		addPoints(-1);
//		System.out.println(this.getCode() + " is in decline house");
	}

	public static Long[] getSportSet() {
		return new Long[] {19L,21L,22L,23L,25L,26L,27L,28L,29L,30L,31L,32L,33L,34L};
	}

	/**
	 * Проверка ингрессии планеты за два дня
	 * @param prev событие днём ранее
	 * @param next событие текущего дня
	 * @return если Луна, то ингрессию ставим автоматом, в противном случае проверяем позицию планеты
	 */
	public List<Object> isIngressed(Event prev, Event next) {
		List<Object> list = new ArrayList<>();

		if (2 == number) {
			list.add("D");
			return list;
		}

		try {
			Collection<Planet> planets = prev.getPlanets().values();
			Collection<Planet> planets2 = next.getPlanets().values();
			for (Planet planet : planets) {
				if (planet.getCode().equals(this.code)) {
					if (this.longitude == planet.longitude) //планета осталась в той же координате
						list.add("S");
					else if (planet.isRetrograde() && !this.isRetrograde()) //планета перешла в директное движение
						list.add("D");
					else if (this.isRetrograde() && !planet.isRetrograde()) //планета перешла в обратное движение
						list.add("R");

					//изменился ли знак Зодиака планеты
					Sign sign = this.sign;
					if (null == sign)
						sign = SkyPoint.getSign(this.longitude, next.getBirthYear());

					Sign sign2 = planet.sign;
					if (null == sign2)
						sign2 = SkyPoint.getSign(planet.longitude, prev.getBirthYear());

					if (sign.getId() != sign2.getId())
						list.add("M");
				} else {
					//изменились ли аспекты
					Map<String,String> map = planet.getAspectMap();
					if (null == map)
						continue;
					for (Model model2 : planets2) {
						Planet planet2 = (Planet)model2;
						Map<String,String> map2 = planet2.getAspectMap();
						if (null == map2)
							continue;
						String acode = map.get(planet2.getCode());
						String acode2 = map2.get(planet.getCode());
						if (null == acode)
							continue;
						else if (null == acode2 || !acode.equals(acode2)) {
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

	public boolean isPositive() {
		return isLord() || isPerfect()
//				|| isHouseExaltated() || isHouseHome() || isSignExaltated() || isSignHome()
			&& (!inMine() && !isBelt() && !isBroken() && !isDamaged() && !isRetrograde() && !isLilithed()
//				&& !isHouseDeclined() && !isHouseExile() && !isSignDeclined() && !isSignExile()
		);
	}

	public boolean isNegative() {
		return inMine() || isBelt() || isBroken() || isDamaged() || isLilithed()
//				|| isRetrograde()
//				|| isHouseDeclined() || isHouseExile() || isSignDeclined() || isSignExile()
			&& (!isLord() && !isPerfect()
//				 && !isHouseExaltated() && !isHouseHome() && !isSignExaltated() && !isSignHome()
		);				
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
	 * Признак позитивной планеты для транзита (по толкованию)
	 */
	private boolean good;

	public boolean isGood() {
		return good;
	}

	public void setGood(boolean good) {
		this.good = good;
	}

	/**
	 * Проверка, находится ли планета в нейтральном знаке
	 * @return true|false
	 */
	public boolean isInNeutralSign() {
		return !isSignExaltated() && !isSignHome()
			&& !isSignDeclined() && !isSignExile();
	}

	private String loyalty;

	public String getLoyalty() {
		return loyalty;
	}

	public void setLoyalty(String loyalty) {
		this.loyalty = loyalty;
	}

	/**
	 * Проверка, является ли планета слабой
	 * @return
	 */
	public boolean isWeak() {
		return isBroken() || isKethued() || inMine() || isRetrograde();
	}

	/**
	 * Маршализация в JSON-массив
	 * @return JSON-строка
	 */
    public String toJSON() {
    	JSONObject object = new JSONObject();
        object.put("id", getId());
        object.put("longitude", getLongitude());
        object.put("latitude", getLatitude());
        object.put("distance", getDistance());
        object.put("speed_longitude", getSpeedLongitude());
        object.put("speed_latitude", getSpeedLatitude());
        object.put("speed_distance", getSpeedDistance());
        return object.toString();
    }

    /**
     * Генерация идентификатора планеты
     * @return код планеты
     */
	public String getAnchor() {
		String res = code;
		if (house != null)
			res += "_" + house.getCode();
		return res;
	}

	/**
     * Краткое негативное описание
     */
    private String badName;

	public String getBadName() {
		return badName;
	}

	public void setBadName(String badName) {
		this.badName = badName;
	}

	/**
	 * Проверка, является ли планета негативной по своей природе
	 * @return true - если планета Лилит или Кету
	 */
	public boolean isBad() {
		String[] negatives = {"Kethu", "Lilith"};
		return Arrays.asList(negatives).contains(getCode());
	}

	/**
     * Краткое позитивное описание
     */
    private String goodName;

	public String getGoodName() {
		return goodName;
	}

	public void setGoodName(String goodName) {
		this.goodName = goodName;
	}
}
