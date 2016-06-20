package kz.zvezdochet.provider;

import kz.zvezdochet.bean.SkyPointAspect;
import kz.zvezdochet.core.ui.ArrayLabelProvider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * Формат таблицы аспектов
 * @author Nataly Didenko
 */
public class AspectLabelProvider extends ArrayLabelProvider {
	@Override
	public String getColumnText(Object element, int columnIndex) {
		Object[] array = (Object[])element;
		Object val = array[columnIndex];
		if (null == val)
			return null;
		return (val instanceof SkyPointAspect)
			? String.valueOf(((SkyPointAspect)val).getScore())
			: val.toString();
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
