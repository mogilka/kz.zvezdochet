package kz.zvezdochet.provider;

import org.eclipse.jface.viewers.LabelProvider;

import kz.zvezdochet.bean.CardKind;

/**
 * Формат списка видов космограммы
 * @author Natalie Didenko
 */
public class CardKindLabelProvider extends LabelProvider { 
	public String getText(Object element) {				
		if (element instanceof CardKind) {
			CardKind type = (CardKind)element;
			return type.getName() + " - " + type.getDescription();
		}
		return "";
	}
}
