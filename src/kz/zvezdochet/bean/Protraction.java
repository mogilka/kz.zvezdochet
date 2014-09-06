package kz.zvezdochet.bean;

import org.eclipse.swt.SWT;

import kz.zvezdochet.core.bean.Dictionary;

/**
 * Вид начертания линий
 * @author Nataly Didenko
 *
 */
public class Protraction extends Dictionary {
	private static final long serialVersionUID = -8249726863413339113L;

	/**
	 * Поиск стиля начертания по коду
	 * @return стиль начертания линии
	 */
	public int getLineStyle() {
		switch (code) {
			case "SOLID": return SWT.LINE_SOLID;
			case "DASH": return SWT.LINE_DASH;
			case "DOT": return SWT.LINE_DOT;
			case "DASHDOT": return SWT.LINE_DASHDOT;
			case "DASHDOTDOT": return SWT.LINE_DASHDOTDOT;
			default: return SWT.LINE_SOLID;
		}
	}
}
