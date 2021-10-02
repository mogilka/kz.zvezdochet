package kz.zvezdochet.part;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.ui.view.ModelLabelProvider;
import kz.zvezdochet.core.ui.view.ModelListView;
import kz.zvezdochet.core.ui.view.View;
import kz.zvezdochet.core.util.DateUtil;
import kz.zvezdochet.service.EventService;

/**
 * Импорт событий
 * @author Natalie Didenko
 */
public class ImportPart extends ModelListView {
	@Inject
	public ImportPart() {}
	
//	@PostConstruct
//	public void postConstruct(Composite parent) {}

	@PostConstruct @Override
	public View create(Composite parent) {
		return super.create(parent);
	}

	protected DateTime dtDate;
	protected DateTime dtTime;

	@Override
	public void initFilter(Composite parent) {
		grFilter = new Group(parent, SWT.NONE);
		grFilter.setText("Поиск");
		grFilter.setLayout(new GridLayout());
		dtDate = new DateTime(grFilter, SWT.DROP_DOWN);
		dtTime = new DateTime(grFilter, SWT.TIME);
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
						case 4: return event.getComment();
						case 5: return DateUtil.formatDateTime(event.getModified());
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
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, dtDate.getDay());
		calendar.set(Calendar.MONTH, dtDate.getMonth());
		calendar.set(Calendar.YEAR, dtDate.getYear());
		calendar.set(Calendar.HOUR_OF_DAY, dtTime.getHours());
		calendar.set(Calendar.MINUTE, dtTime.getMinutes());
		calendar.set(Calendar.SECOND, dtTime.getSeconds());
		return calendar.getTime();
	}

	@Override
	public Model createModel() {
		return null;
	}

	@Override
	protected void initControls() throws DataAccessException {
		super.initControls();
		try {
			Date date = new EventService().findLastDate();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			dtDate.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
			dtTime.setDate(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
