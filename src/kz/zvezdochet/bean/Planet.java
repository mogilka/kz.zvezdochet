package kz.zvezdochet.bean;

import java.util.Map;

import kz.zvezdochet.core.service.ReferenceService;
import kz.zvezdochet.service.PlanetService;

import org.eclipse.swt.graphics.Image;

/**
 * Класс, представляющий Планету
 * @author Nataly Didenko
 * 
 * @see SkyPoint Точка небесной сферы
 */
public class Planet extends SkyPoint {
	private static final long serialVersionUID = -8328248201235163517L;

	/**
	 * Балл
	 */
    private double score;

	/**
	 * Изображение
	 */
    private Image image;
    
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
     * Признак пораженности
     */
    private boolean damaged = false;
    
    /**
     * Признак непораженности
     */
    private boolean perfect = false;
    
    /**
     * Признак ослабленности
     */
    private boolean broken = false;
    
    /**
     * Описание планеты-меч
     */
    private String swordText;
    
    /**
     * Описание планеты-щит
     */
    private String shieldText;
    
    /**
     * Описание планеты-пояс
     */
    private String beltText;
    
    /**
     * Описание планеты-ядро
     */
    private String kernelText;
    
    /**
     * Описание планеты в шахте
     */
    private String mineText;
    
    /**
     * Описание ретроградной планеты
     */
    private String retroText;
    
    /**
     * Описание силы планеты
     */
    private String strongText;
    
    /**
     * Описание слабости планеты
     */
    private String weakText;
    
    /**
     * Описание пораженности планеты
     */
    private String damagedText;
    
    /**
     * Описание благоприятной планеты
     */
    private String perfectText;
    
    /**
     * Признак фиктивной планеты
     */
    private boolean fictitious = false;
    
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

	public String getSwordText() {
		return swordText;
	}

	public boolean isFictitious() {
		return fictitious;
	}

	public void setFictitious(boolean fictitious) {
		this.fictitious = fictitious;
	}

	public void setSwordText(String sword) {
		this.swordText = sword;
	}

	public String getShieldText() {
		return shieldText;
	}

	public void setShieldText(String shield) {
		this.shieldText = shield;
	}

	public String getBeltText() {
		return beltText;
	}

	public void setBeltText(String belt) {
		this.beltText = belt;
	}

	public String getKernelText() {
		return kernelText;
	}

	public void setKernelText(String kernel) {
		this.kernelText = kernel;
	}

	public String getMineText() {
		return mineText;
	}

	public void setMineText(String mine) {
		this.mineText = mine;
	}

	public String getStrongText() {
		return strongText;
	}

	public void setStrongText(String strong) {
		this.strongText = strong;
	}

	public String getWeakText() {
		return weakText;
	}

	public void setWeakText(String weak) {
		this.weakText = weak;
	}

	public String getDamagedText() {
		return damagedText;
	}

	public void setDamagedText(String damage) {
		this.damagedText = damage;
	}

	public String getPerfectText() {
		return perfectText;
	}

	public void setPerfectText(String unharmed) {
		this.perfectText = unharmed;
	}

	public Planet(String name) {
		setName(name);
	}

	public String getRetroText() {
		return retroText;
	}

	public void setRetroText(String retroText) {
		this.retroText = retroText;
	}

	public Planet() {}

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
				0 == getAspectCountMap().get("COMMON") : false;
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
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
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

	public ReferenceService getService() {
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
}
