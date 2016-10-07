package kz.zvezdochet.part;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.ui.view.ModelLabelProvider;
import kz.zvezdochet.core.ui.view.ModelListView;
import kz.zvezdochet.core.ui.view.View;
import kz.zvezdochet.core.util.DateUtil;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Импорт событий
 * @author Nataly Didenko
 */
public class ImportPart extends ModelListView {
	@Inject
	public ImportPart() {}
	
//	@PostConstruct
//	public void postConstruct(Composite parent) {}

	@PostConstruct @Override
	public View create(Composite parent) {
		super.create(parent);
		return null;
	}

	protected CDateTime dtDate;

	@Override
	public void initFilter() {
		grFilter = new Group(container, SWT.NONE);
		grFilter.setText("Поиск");
		grFilter.setLayout(new GridLayout());
		dtDate = new CDateTime(grFilter, CDT.BORDER | CDT.COMPACT | CDT.DROP_DOWN | CDT.DATE_LONG | CDT.TIME_MEDIUM);
		dtDate.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		dtDate.setSelection(new Date());
	}

	@Override
	protected String[] initTableColumns() {
		String[] columns = {
			"Пол",
			"№",
			"Имя",
			"Дата",
			"Описание",
			"Дата изменения" };
		return columns;
	}

	@Override
	public boolean check(int mode) throws Exception {
		return false;
	}

	@Override
	protected IBaseLabelProvider getLabelProvider() {
		return new ModelLabelProvider() {
			@Override
			public String getColumnText(Object element, int columnIndex) {
				kz.zvezdochet.bean.Event event = (kz.zvezdochet.bean.Event)element;
				if (event != null)
					switch (columnIndex) {
						case 1: return event.getId().toString();
						case 2: return event.getName();
						case 3: return DateUtil.formatDateTime(event.getBirth());
						case 4: return event.getDescription();
						case 5: return DateUtil.formatDateTime(event.getDate());
					}
				return null;
			}
			
			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				kz.zvezdochet.bean.Event event = (kz.zvezdochet.bean.Event)element;
				switch (columnIndex) {
					case 0: String file = event.isFemale() ? "female.png" : "male.png";
						return AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet.core", "icons/" + file).createImage();
				}
				return null;
			}
		};
	}

	/**
	 * Поиск выбранной даты
	 * @return дата последнего импорта
	 */
	public Date getDate() {
		return dtDate.getSelection();
	}

	@Override
	public Model createModel() {
		return null;
	}
}