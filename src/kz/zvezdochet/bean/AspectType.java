package kz.zvezdochet.bean;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;

import kz.zvezdochet.core.bean.DiagramObject;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.service.AspectTypeService;

/**
 * Тип аспекта
 * @author Nataly Didenko
 */
public class AspectType extends DiagramObject {
	private static final long serialVersionUID = 4739420822269120670L;

	/**
	 * Начертание
	 */
	private String protraction;
	/**
	 * Родительский тип
	 */
	private AspectType parentType;
	/**
	 * Дополнительный цвет (для отчетов, менее яркий)
	 */
	private Color dimColor;
	/**
	 * Цвет шрифта (для пользовательских прогнозов)
	 */
	private String fontColor;

	public String getFontColor() {
		return fontColor;
	}

	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}

	/**
	 * Символ, обозначающий тип аспекта
	 */
	private char symbol;
	/**
	 * Изображение
	 */
	private String image;
	
	public String getProtraction() {
		return protraction;
	}

	public Color getDimColor() {
		return dimColor;
	}

	public void setDimColor(Color dimColor) {
		this.dimColor = dimColor;
	}

	public void setProtraction(String protraction) {
		this.protraction = protraction;
	}
	
	public AspectType getParentType() {
		return parentType;
	}

	public void setParentType(AspectType parentType) {
		this.parentType = parentType;
	}

	public char getSymbol() {
		return symbol;
	}

	public void setSymbol(char symbol) {
		this.symbol = symbol;
	}

	public String getDiaName() {
		return name;
	}

	public DictionaryService getService() {
		return new AspectTypeService();
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * Возвращает иерархию типов аспектов
	 * @return карта типов
	 */
	public static Map<String, String[]> getHierarchy() {
		Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("NEUTRAL", new String[] {"NEUTRAL"});
		map.put("POSITIVE", new String[] {"POSITIVE", "POSITIVE_HIDDEN"});
		map.put("NEGATIVE", new String[] {"NEGATIVE", "NEGATIVE_HIDDEN"});
		map.put("CREATIVE", new String[] {"CREATIVE"});
		map.put("KARMIC", new String[] {"KARMIC"});
		map.put("SPIRITUAL", new String[] {"SPIRITUAL", "ENSLAVEMENT", "DAO", "MAGIC"});
		map.put("PROGRESSIVE", new String[] {"PROGRESSIVE", "TEMPTATION"});
		return map;
	}

	/**
	 * Текст
	 */
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Очки
	 */
	private int points;

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}
}
