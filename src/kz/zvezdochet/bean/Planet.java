package kz.zvezdochet.bean;

import java.util.List;
import java.util.Map;

import kz.zvezdochet.core.service.ReferenceService;
import kz.zvezdochet.service.PlanetService;

import org.eclipse.swt.graphics.Image;

/**
 * Класс, представляющий Планету
 * @author Nataly
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
     * Список знаков обители
     */
    private List<Sign> signHomes;
    
    /**
     * Список знаков экзальтации
     */
    private List<Sign> signExaltations;
    
    /**
     * Список знаков изгнания
     */
    private List<Sign> signExiles;
    
    /**
     * Список знаков родства
     */
    private List<Sign> signCognation;
    
    /**
     * Список знаков падения
     */
    private List<Sign> signDeclines;

    /**
     * Список домов обители
     */
    private List<House> houseHomes;
    
    /**
     * Список домов экзальтации
     */
    private List<House> houseExaltations;
    
    /**
     * Список домов изгнания
     */
    private List<House> houseExiles;
    
    /**
     * Список домов падения
     */
    private List<House> houseDeclines;

    /**
     * Список домов родства
     */
    private List<House> houseCognation;
    
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
		return score > 1.0;
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

	public List<Sign> getSignHomes() {
		return signHomes;
	}

	public void setSignHomes(List<Sign> shome) {
		this.signHomes = shome;
	}

	public List<House> getHouseHomes() {
		return houseHomes;
	}

	public void setHouseHomes(List<House> hhome) {
		this.houseHomes = hhome;
	}

	public List<Sign> getSignExaltations() {
		return signExaltations;
	}

	public void setSignExaltations(List<Sign> sexaltation) {
		this.signExaltations = sexaltation;
	}

	public List<House> getHouseExaltations() {
		return houseExaltations;
	}

	public void setHouseExaltations(List<House> hexaltation) {
		this.houseExaltations = hexaltation;
	}

	public List<Sign> getSignExiles() {
		return signExiles;
	}

	public void setSignExiles(List<Sign> sexile) {
		this.signExiles = sexile;
	}

	public List<House> getHouseExiles() {
		return houseExiles;
	}

	public void setHouseExiles(List<House> hexile) {
		this.houseExiles = hexile;
	}

	public List<Sign> getSignDeclines() {
		return signDeclines;
	}

	public void setSignDeclines(List<Sign> sdecline) {
		this.signDeclines = sdecline;
	}

	public List<House> getHouseDeclines() {
		return houseDeclines;
	}

	public void setHouseDeclines(List<House> hdecline) {
		this.houseDeclines = hdecline;
	}

    /**
     * Метод, проверяющий, находится ли планета в шахте
     * @return <i>true</i> если планета не имеет сильных аспектов
     */
	public boolean isInMine() {
		return false; //TODO добавить реальное условие
	}

	public boolean isRetro() {
		return coord < 0.0;
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

	public List<Sign> getSignCognation() {
		return signCognation;
	}

	public void setSignCognation(List<Sign> signCognation) {
		this.signCognation = signCognation;
	}

	public List<House> getHouseCognation() {
		return houseCognation;
	}

	public void setHouseCognation(List<House> houseCognation) {
		this.houseCognation = houseCognation;
	}

	public boolean isRetrograde() {
		return retrograde;
	}

	public void setRetrograde(boolean retrograde) {
		this.retrograde = retrograde;
	}

	public static ReferenceService getService() {
		return new PlanetService();
	}
}
