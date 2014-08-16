package kz.zvezdochet.part;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.SkyPointAspect;
import kz.zvezdochet.core.ui.view.ListView;
import kz.zvezdochet.util.Configuration;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Таблица аспектов
 * @author Nataly Didenko
 *
 */
public class AspectPart extends ListView {
	/**
	 * Конфигурация события
	 */
	private Configuration conf;

	@Inject
	public AspectPart() {
		
	}
	
	@PostConstruct @Override
	public Composite create(Composite parent) {
		return super.create(parent);
	}

	/**
	 * Инициализация конфигурации события
	 * @param configuration конфигурация события
	 */
	public void setConfiguration(Configuration configuration) {
		conf = configuration;
		addColumns();
	}

	@Override
	protected void addColumns() {
		if (conf != null) {
			TableColumn tableColumn = new TableColumn(table, SWT.NONE);
			tableColumn.setText("");		
			for (int i = 0; i < conf.getPlanets().size(); i++) {
				Planet planet = (Planet)conf.getPlanets().get(i);
				tableColumn = new TableColumn(table, SWT.NONE);
				tableColumn.setText(planet.getName());		
			}
		}
	}

	@Override
	protected IBaseLabelProvider getLabelProvider() {
		return new ArrayLabelProvider() {
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
		};
	}
}
