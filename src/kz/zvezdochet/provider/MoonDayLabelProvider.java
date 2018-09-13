package kz.zvezdochet.provider;

import org.eclipse.jface.viewers.LabelProvider;

import kz.zvezdochet.bean.MoonDay;

/**
 * Формат списка лунных дней
 * @author Nataly Didenko
 */
public class MoonDayLabelProvider extends LabelProvider { 
	public String getText(Object element) {				
		if (element instanceof MoonDay) {
			MoonDay type = (MoonDay)element;
			return type.getId() + " " + type.getSymbol();
		}
		return "";
	}
}
