package kz.zvezdochet.util;

import kz.zvezdochet.core.bean.Reference;

import org.eclipse.swt.graphics.Color;

/**
 * Класс, представляющий абстрактный диаграммный объект
 * @author Nataly Didenko
 */
public abstract class DiagramObject extends Reference implements IColorizedObject, IDiagramObject {
	private static final long serialVersionUID = 3257825153209037032L;

	/**
	 * Цвет
	 */
	private Color color;
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Наименование для диаграммы
	 */
	private String diaName;
	
	public String getDiaName() {
		return diaName;
	}

	public void setDiaName(String diaName) {
		this.diaName = diaName;
	}
}
