package kz.zvezdochet.bean;

import java.util.Map;

import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.service.PlanetService;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

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
     * Признак ослабленности
     */
    private boolean broken = false;

    /**
     * Признак силы
     */
    private boolean strong = false;

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
		return (aspectCountMap != null) ?
			0 == aspectCountMap.get("COMMON") : false;
	}

	public boolean isDamaged() {
		return damaged;
	}

	public void setDamaged(boolean damaged) {
		this.damaged = damaged;
	}

	public boolean isPerfect() {
		return perfect;
	}

	public void setPerfect(boolean perfect) {
		this.perfect = perfect;
	}

	public Image getImage() {
		return AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/planet/" + 
			getCode() + ".png").createImage();

	}

	public boolean isBroken() {
		return broken;
	}

	public void setBroken(boolean broken) {
		this.broken = broken;
	}

	public boolean isRetrograde() {
		return retrograde;
	}

	public void setRetrograde(boolean retrograde) {
		this.retrograde = retrograde;
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

	public void setKernel(boolean kernel) {
		this.kernel = kernel;
	}

	public boolean isBelt() {
		return belt;
	}

	public void setBelt(boolean belt) {
		this.belt = belt;
	}

	public boolean isShield() {
		return shield;
	}

	public void setShield(boolean shield) {
		this.shield = shield;
	}

	public boolean isSword() {
		return sword;
	}

	public void setSword(boolean sword) {
		this.sword = sword;
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

	public void setSignHome(boolean signHome) {
		this.signHome = signHome;
	}

	public boolean isSignExaltated() {
		return signExaltated;
	}

	public void setSignExaltated(boolean signExaltated) {
		this.signExaltated = signExaltated;
	}

	public boolean isSignExile() {
		return signExile;
	}

	public void setSignExile(boolean signExile) {
		this.signExile = signExile;
	}

	public boolean isSignDeclined() {
		return signDeclined;
	}

	public void setSignDeclined(boolean signDeclined) {
		this.signDeclined = signDeclined;
	}

	public boolean isHouseHome() {
		return houseHome;
	}

	public void setHouseHome(boolean houseHome) {
		this.houseHome = houseHome;
	}

	public boolean isHouseExaltated() {
		return houseExaltated;
	}

	public void setHouseExaltated(boolean houseExaltated) {
		this.houseExaltated = houseExaltated;
	}

	public boolean isHouseExile() {
		return houseExile;
	}

	public void setHouseExile(boolean houseExile) {
		this.houseExile = houseExile;
	}

	public boolean isHouseDeclined() {
		return houseDeclined;
	}

	public void setHouseDeclined(boolean houseDeclined) {
		this.houseDeclined = houseDeclined;
	}

	public boolean isStrong() {
		return strong;
	}

	public void setStrong(boolean strong) {
		this.strong = strong;
	}
}
