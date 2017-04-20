package kz.zvezdochet.bean;

import org.eclipse.swt.graphics.Color;

import kz.zvezdochet.core.bean.DiagramDictionary;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.service.ElementService;

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

	@Override
	public ModelService getService() {
		return new ElementService();
	}

	/**
	 * Тёмный цвет
	 */
	private Color dimcolor;
	
	public Color getDimColor() {
		return dimcolor;
	}
	
	public void setDimColor(Color color) {
		this.dimcolor = color;
	}
}