package kz.zvezdochet.bean;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;

import kz.zvezdochet.core.bean.DiagramDictionary;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.service.ElementService;

/**
 * Стихия
 * @author Natalie Didenko
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

	/**
	 * Человекопонятное описание
	 */
	private String shortname;

	public String getShortName() {
		return shortname;
	}

	public void setShortName(String shortname) {
		this.shortname = shortname;
	}

	/**
	 * Начало
	 */
	private YinYang yinyang;

	public YinYang getYinYang() {
		return yinyang;
	}

	public void setYinYang(YinYang yinyang) {
		this.yinyang = yinyang;
	}

	/**
	 * Светлый цвет
	 */
	private Color lightcolor;

	public Color getLightColor() {
		return lightcolor;
	}

	public void setLightColor(Color lightcolor) {
		this.lightcolor = lightcolor;
	}

	/**
	 * Толкование синастрии
	 */
	private String synastry;

	public String getSynastry() {
		return synastry;
	}

	public void setSynastry(String synastry) {
		this.synastry = synastry;
	}

	/**
	 * Толкование тригона
	 */
	private String triangle;

	public String getTriangle() {
		return triangle;
	}

	public void setTriangle(String triangle) {
		this.triangle = triangle;
	}

	/**
	 * Лояльность
	 */
	private boolean loyalty;

	public boolean isLoyalty() {
		return loyalty;
	}

	public void setLoyalty(boolean loyalty) {
		this.loyalty = loyalty;
	}

	public static Map<String, Double> getMap() {
		return new HashMap<String, Double>() {
			private static final long serialVersionUID = 3501100288331136747L;
			{
		        put("fire", 0.0);
		        put("earth", 0.0);
		        put("air", 0.0);
		        put("water", 0.0);
		    }
		};
	}
}
