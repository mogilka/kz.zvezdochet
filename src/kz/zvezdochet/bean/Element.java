package kz.zvezdochet.bean;

import kz.zvezdochet.core.bean.DiagramDictionary;

/**
 * Стихия
 * @author Nataly Didenko
 */
public class Element extends DiagramDictionary {
	private static final long serialVersionUID = 2457703926076101583L;
	
	/**
	 * Темперамент
	 */
	private String temperament;

	public String getTemperament() {
		return temperament;
	}

	public void setTemperament(String temperament) {
		this.temperament = temperament;
	}
}
