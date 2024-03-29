package kz.zvezdochet.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Color;

import kz.zvezdochet.core.bean.DiagramObject;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.service.PlanetHousePositionService;
import kz.zvezdochet.service.PlanetSignPositionService;
import kz.zvezdochet.service.SignService;
import kz.zvezdochet.util.ISkyPoint;


/**
 * Точка Небесной сферы
 * @author Natalie Didenko
 *
 */
public abstract class SkyPoint extends DiagramObject implements ISkyPoint {
	private static final long serialVersionUID = 6159825158439746993L;

	/**
	 * Долгота
	 */
    protected double longitude = 0.0;

    /**
     * Цвет
     */
    protected Color color;

    public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

    /**
     * Символ
     */
	protected String symbol;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

    /**
     * Порядковый номер
     */
    protected int number;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double coord) {
		this.longitude = coord;
	}

	public void setAspectCountMap(Map<String, Integer> aspectMap) {
		this.aspectCountMap = aspectMap;
	}

	public Map<String, Integer> getAspectCountMap() {
		if (null == aspectCountMap)
			aspectCountMap = new HashMap<String, Integer>();
		return aspectCountMap;
	}

	/**
	 * Карта статистики типов аспектов для небесной точки
	 */
	protected Map<String, Integer> aspectCountMap;

	@Override
	public String toString() {
		return name + " " + longitude;
	}

	/**
     * Знак Зодиака, в котором находится объект
     */
	protected Sign sign;

    /**
     * Дом, в котором находится объект
     */
	protected House house;

    /**
     * Признак поражённости
     */
	protected boolean damaged = false;
    
    /**
     * Признак непоражённости
     */
	protected boolean perfect = false;
    /**
     * Признак слабости (по очкам)
     */
	protected boolean broken = false;
    /**
     * Признак закрепощённости (соединение с Кету)
     */
	protected boolean kethued = false;
    /**
     * Признак владыки гороскопа (объект в своём роде, у которого больше всего сильных аспектов)
     */
	protected boolean lord = false;
    /**
     * Признак раскрепощённости (соединение с Раху)
     */
	protected boolean rakhued = false;

    /**
     * Признак соединения с Лилит
     */
	protected boolean lilithed = false;

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
     * Метод, проверяющий, находится ли объект в шахте
     * @return <i>true</i> если объект не имеет сильных аспектов
     */
	public boolean isUnaspected() {
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

	/**
	 * Признак объекта в зените
	 */
	protected boolean onZenith = false;
	/**
	 * Признак объекта в надире
	 */
	protected boolean onNadir = false;
	/**
	 * Признак заходящего объекта
	 */
	protected boolean onSetting = false;
	/**
	 * Признак восходящего объекта
	 */
	protected boolean onRising = false;
	
	public void setOnZenith(boolean onAsc) {
		onZenith = onAsc;
	}
	public boolean isOnNadir() {
		return onNadir;
	}

	public void setOnNadir(boolean onIC) {
		this.onNadir = onIC;
	}

	public boolean isOnSetting() {
		return onSetting;
	}

	public void setOnSetting(boolean onDSC) {
		this.onSetting = onDSC;
	}

	public boolean setOnRising() {
		return onRising;
	}

	public void setOnRising(boolean onMC) {
		this.onRising = onMC;
	}

	public boolean isOnZenith() {
		return onZenith;
	}

	public boolean isDominant() {
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
	protected double points;

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
		return true;
	}

	public boolean isNegative() {
		return false;
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
	 * Король аспектов (объект в своём роде с наибольшим количеством позитивных аспектов)
	 */
	protected boolean king;

	public boolean isBenefic() {
		return king;
	}

	public void setKing() {
		this.king = true;
	}

	/**
	 * Признак соединения с Селеной
	 */
	protected boolean selened;

	public boolean isSelened() {
		return selened;
	}

	/**
	 * Массив аспектов с планетами
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
	 * Определение знака, в котором находится объект
	 * @param point координата объекта
	 * @param year год
	 * @throws DataAccessException 
	 */
	public static Sign getSign(double point, int year) throws DataAccessException {
		List<Model> signs = new SignService().getList();
		for (Model model : signs) {
			Sign sign = (Sign)model;
			if (year < 1000) {
				if (point >= sign.getI0() && point < sign.getF0()) 
					return sign;
			} else if (year < 2000) {
				if (point >= sign.getI1000() && point < sign.getF1000()) 
					return sign;
			} else if (year < 3000) {
				if (point >= sign.getI2000() && point < sign.getF2000()) 
					return sign;
			} else if (year < 4000) {
				if (point >= sign.getI3000() && point < sign.getF3000()) 
					return sign;
			}
		}
		return null;
	}

	/**
	 * Определение дома, в котором находится объект
	 * @param house1 координата дома
	 * @param house2 координата следующего дома
	 * @param coord координата объекта
	 * @return true - если координата находится внутри указанном интервале
	 */ 
	public static boolean getHouse(double house1, double house2, double coord) {
		//если границы домов находятся по разные стороны нуля
		if (house1 > 200 & house2 < 160) {
			//если градус объекта находится по другую сторону
			//от нуля относительно второй границы,
			//увеличиваем эту границу на 2*Pi
			if (coord > 200)
				house2 += 360;
			else if (coord < 160) {
				//если градус планеты меньше 160,
				//увеличиваем его, а также вторую границу на 2*Pi
		       coord += 360;
		       house2 += 360;
			}
		}
		//если же границы находятся по одну сторону от нуля,
		//оставляем всё как есть
		
		return house1 <= coord && coord <= house2;
	}

	/**
	 * Широта
	 */
	protected double latitude;
	/**
	 * Удалённость
	 */
	protected double distance;

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
	 * Скорость по долготе
	 */
	protected double speedLongitude;
	/**
	 * Скорость по широте
	 */
	protected double speedLatitude;
	/**
	 * Скорость на удалении
	 */
	protected double speedDistance;

	public double getSpeedLongitude() {
		return speedLongitude;
	}

	public void setSpeedLongitude(double speedLongitude) {
		this.speedLongitude = speedLongitude;
	}

	public double getSpeedLatitude() {
		return speedLatitude;
	}

	public void setSpeedLatitude(double speedLatitude) {
		this.speedLatitude = speedLatitude;
	}

	public double getSpeedDistance() {
		return speedDistance;
	}

	public void setSpeedDistance(double speedDistance) {
		this.speedDistance = speedDistance;
	}

	/**
	 * Проверка ретроградности
	 * @return true - попятное движение
	 */
	public boolean isRetrograde() {
		return speedLongitude < 0;
	}

	/**
	 * Проверка, есть ли у планеты соединение с фиктивными планетами (лунными узлами, Лилит или Селеной)
	 * @return true - есть соединение с одной из указанных фиктивных планет
	 */
	public boolean isFictived() {
		return isKethued() || isRakhued() || isLilithed() || isSelened();
	}

	/**
	 * Массив аспектов с домами
	 */
	private	List<SkyPointAspect> aspectHouseList;

	public void setAspectHouseList(List<SkyPointAspect> aspectList) {
		this.aspectHouseList = aspectList;
	}

	public List<SkyPointAspect> getAspectHouseList() {
		if (null == aspectHouseList)
			aspectHouseList = new ArrayList<SkyPointAspect>();
		return aspectHouseList;
	}

	/**
	 * Инициализация позиций
	 */
	public void initPositions() {
		try {
			if (sign != null) {
				PlanetSignPositionService service = new PlanetSignPositionService();
				PlanetSignPosition s = service.find(this);
				if (s != null) {
					switch (s.getType().getCode()) {
						case "HOME": setSignDomicile(); break;
						case "EXALTATION": setSignExaltation(); break;
						case "EXILE": setSignDetriment(); break;
						case "DECLINE": setSignFall(); break;
					}
				}
			}
			if (house != null) {
				PlanetHousePositionService service = new PlanetHousePositionService();
				PlanetHousePosition s = service.find(this);
				if (s != null) {
					switch (s.getType().getCode()) {
						case "HOME": setHouseDomicile(); break;
						case "EXALTATION": setHouseExaltation(); break;
						case "EXILE": setHouseDetriment(); break;
						case "DECLINE": setHouseFall(); break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	public boolean isSignDomicile() {
		return signHome;
	}

	public void setSignDomicile() {
		signHome = true;
		addPoints(1);
	//	System.out.println(this.getCode() + " is in home sign");
	}
	
	public boolean isSignExaltation() {
		return signExaltated;
	}
	
	public void setSignExaltation() {
		signExaltated = true;
		addPoints(1);
	//	System.out.println(this.getCode() + " is in exalt sign");
	}
	
	public boolean isSignDetriment() {
		return signExile;
	}
	
	public void setSignDetriment() {
		signExile = true;
		addPoints(-1);
	//	System.out.println(this.getCode() + " is in exile sign");
	}
	
	public boolean isSignFall() {
		return signDeclined;
	}
	
	public void setSignFall() {
		signDeclined = true;
		addPoints(-1);
	//	System.out.println(this.getCode() + " is in decline sign");
	}
	
	public boolean isHouseDomicile() {
		return houseHome;
	}
	
	public void setHouseDomicile() {
		houseHome = true;
		addPoints(1);
	//	System.out.println(this.getCode() + " is in home house");
	}
	
	public boolean isHouseExaltation() {
		return houseExaltated;
	}
	
	public void setHouseExaltation() {
		houseExaltated = true;
		addPoints(1);
	//	System.out.println(this.getCode() + " is in exalt house");
	}
	
	public boolean isHouseDetriment() {
		return houseExile;
	}
	
	public void setHouseDetriment() {
		houseExile = true;
		addPoints(-1);
	//	System.out.println(this.getCode() + " is in exile house");
	}
	
	public boolean isHouseFall() {
		return houseDeclined;
	}
	
	public void setHouseFall() {
		houseDeclined = true;
		addPoints(-1);
	//	System.out.println(this.getCode() + " is in decline house");
	}

}
