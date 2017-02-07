package kz.zvezdochet.part;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.ui.view.ListView;
import kz.zvezdochet.core.ui.view.View;
import kz.zvezdochet.core.util.CalcUtil;
import kz.zvezdochet.provider.AspectLabelProvider;
import kz.zvezdochet.util.Configuration;

/**
 * Таблица аспектов
 * @author Nataly Didenko
 *
 */
public class AspectPart extends ListView {
	/**
	 * Конфигурация события
	 */
	protected Configuration conf;

	@Inject
	public AspectPart() {}
	
	@PostConstruct @Override
	public View create(Composite parent) {
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
		removeColumns();
		if (conf != null) {
			TableColumn tableColumn = new TableColumn(table, SWT.NONE);
			for (int i = 0; i < conf.getPlanets().size(); i++) {
				Planet planet = (Planet)conf.getPlanets().get(i);
				tableColumn = new TableColumn(table, SWT.NONE);
				tableColumn.setText(CalcUtil.roundTo(planet.getCoord(), 1) + "");
				tableColumn.setImage(planet.getImage());
				tableColumn.setToolTipText(planet.getName());
			}
		}
	}

	@Override
	protected IBaseLabelProvider getLabelProvider() {
		return new AspectLabelProvider();
	}

	@Override
	protected String[] initTableColumns() {
		return null;
	}

	@Override
	public boolean check(int mode) throws Exception {
		return false;
	}
}
