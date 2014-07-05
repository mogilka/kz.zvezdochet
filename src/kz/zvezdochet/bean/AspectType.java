package kz.zvezdochet.bean;

import kz.zvezdochet.core.bean.Reference;
import kz.zvezdochet.core.service.ReferenceService;
import kz.zvezdochet.service.AspectTypeService;
import kz.zvezdochet.util.DiagramObject;

import org.eclipse.swt.graphics.Color;

/**
 * Класс, представляющий тип аспекта
 * @author Nataly
 *
 * @see Reference Прототип справочника
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

	public static ReferenceService getService() {
		return new AspectTypeService();
	}
}
