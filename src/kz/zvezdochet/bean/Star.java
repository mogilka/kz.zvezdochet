package kz.zvezdochet.bean;

import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.service.StarService;

/**
 * Звезда
 * @author Natalie Didenko
 */
public class Star extends SkyPoint {
	private static final long serialVersionUID = 6447111682104406971L;

	public DictionaryService getService() {
		return new StarService();
	}

	public boolean isPositive() {
		return isDominant() || isPerfect()
			&& (!isUnaspected() && !isBroken() && !isDamaged() && !isLilithed()
		);
	}

	public boolean isNegative() {
		return isUnaspected() || isBroken() || isDamaged() || isLilithed()
			&& (!isDominant() && !isPerfect()
		);				
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
}
