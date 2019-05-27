package kz.zvezdochet.provider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import kz.zvezdochet.bean.SkyPointAspect;
import kz.zvezdochet.core.ui.ArrayLabelProvider;

/**
 * Формат таблицы аспектов
 * @author Natalie Didenko
 */
public class AspectLabelProvider extends ArrayLabelProvider {
	@Override
	public String getColumnText(Object element, int columnIndex) {
		Object[] array = (Object[])element;
		Object val = array[columnIndex];
		if (null == val)
			return null;
		String text = "";
		if (val instanceof SkyPointAspect) {
			SkyPointAspect aspect = (SkyPointAspect)val;
			if (aspect.isExact())
				text += "•";
			if (aspect.isApplication())
				text += "⇥";
			text += aspect.getScore();
		} else
			text = val.toString();
		return text;
	}
	@Override
	public Color getBackground(Object element, int columnIndex) {
		Object[] array = (Object[])element;
		Object val = array[columnIndex];
		if (val instanceof SkyPointAspect) {
			SkyPointAspect aspect = (SkyPointAspect)val;
			if (aspect.getAspect() != null)
				return aspect.getAspect().getType().getDimColor();
		}
		int color = (null == val) ? SWT.COLOR_BLACK : SWT.COLOR_WHITE;
		return  Display.getDefault().getSystemColor(color);
	}
}
