package kz.zvezdochet.part;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.ui.view.ModelLabelProvider;
import kz.zvezdochet.core.ui.view.ModelListView;
import kz.zvezdochet.core.ui.view.View;
import kz.zvezdochet.core.util.DateUtil;

/**
 * Импорт событий
 * @author Natalie Didenko
 */
public class ImportPart extends ModelListView {
	@Inject
	public ImportPart() {}
	
	@PostConstruct @Override
	public View create(Composite parent) {
		return super.create(parent);
	}

	protected Text txID;

	@Override
	public void initFilter(Composite parent) {
		grFilter = new Group(parent, SWT.NONE);
		grFilter.setText("Поиск");
		grFilter.setLayout(new GridLayout());
		txID = new Text(grFilter, SWT.BORDER);
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
						case 2: return event.getName("ru");
						case 3: return DateUtil.formatDateTime(event.getBirth());
						case 4: return event.getComment();
						case 5: return DateUtil.formatDateTime(event.getModified());
					}
				return null;
			}
		};
	}

	/**
	 * Поиск выбранного идентификатора
	 * @return идентификатор
	 */
	public long getObject() {
		return Long.parseLong(txID.getText());
	}

	@Override
	public Model createModel() {
		return null;
	}
}
