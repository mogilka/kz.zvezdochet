package kz.zvezdochet.bean;

import kz.zvezdochet.core.bean.DiagramObject;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.service.AspectTypeService;

import org.eclipse.swt.graphics.Color;

/**
 * Тип аспекта
 * @author Nataly Didenko
 */
public class AspectType extends DiagramObject {
	private static final long serialVersionUID = 4739420822269120670L;

	/**
	 * Начертание
	 */
	private Protraction protraction;
	/**
	 * Родительский тип
	 */
	private AspectType parentType;
	/**
	 * Дополнительный цвет (для отчетов, менее яркий)
	 */
	private Color dimColor;
	/**
	 * Символ, обозначающий тип аспекта
	 */
	private char symbol;
	/**
	 * Изображение
	 */
	private String image;
	
	public Protraction getProtraction() {
		return protraction;
	}

	public Color getDimColor() {
		return dimColor;
	}

	public void setDimColor(Color dimColor) {
		this.dimColor = dimColor;
	}

	public void setProtraction(Protraction protraction) {
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
}
