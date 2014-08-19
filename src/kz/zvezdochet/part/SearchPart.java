package kz.zvezdochet.part;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.ui.Tab;
import kz.zvezdochet.core.ui.view.ModelLabelProvider;
import kz.zvezdochet.core.ui.view.ModelListView;
import kz.zvezdochet.core.ui.view.View;
import kz.zvezdochet.core.util.DateUtil;
import kz.zvezdochet.service.EventService;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Поиск событий
 * @author Nataly Didenko
 */
public class SearchPart extends ModelListView {
	private CTabFolder folder;
	
	@Inject
	public SearchPart() {
		
	}

	@PostConstruct @Override
	public View create(Composite parent) {
		super.create(parent);
//		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
//			@Override
//			public void doubleClick(DoubleClickEvent event) {
//				new EventHandler().execute(SearchPart.this);				
//			}
//		});
		return null;
	}
	
	@Override
	public void initFilter() {
		grFilter = new Group(container, SWT.NONE);
		grFilter.setText("Поиск");
		grFilter.setLayout(new GridLayout());
		folder = new CTabFolder(grFilter, SWT.BORDER);
		folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		folder.setSimple(false);
		folder.setUnselectedCloseVisible(false);
		Tab[] tabs = initTabs();
		for (Tab tab : tabs) {
			CTabItem item = new CTabItem(folder, SWT.CLOSE);
			item.setText(tab.name);
			item.setImage(tab.image);
			item.setControl(tab.control);
		}
		folder.setSelection(0);
	}

	/**
	 * Инициализация вкладок поиска
	 * @return массив вкладок
	 */
	private Tab[] initTabs() {
		Tab[] tabs = new Tab[5];
		Tab tab = new Tab();
		tab.name = "по имени";
		tab.image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet.core", "icons/correction_linked_rename.gif").createImage();
		final Text txSearch = new Text(folder, SWT.BORDER);
		txSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		txSearch.setFocus();
		txSearch.addListener(SWT.DefaultSelection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				String text = txSearch.getText();
				if (text.length() > 1)
					findByName(text);
			}
		});
		tab.control = txSearch;
		tabs[0] = tab;
		
		tab = new Tab();
		tab.name = "по знакам Зодиака";
		tab.image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/cosmogram.png").createImage();
		Group group = new Group(folder, SWT.NONE);
		//TODO интерфейс
		tab.control = group;
		GridLayoutFactory.swtDefaults().applyTo(group);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(group);
		tabs[1] = tab;
		
		tab = new Tab();
		tab.name = "по домам";
		tab.image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/home.gif").createImage();
		group = new Group(folder, SWT.NONE);
		//TODO интерфейс
		tab.control = group;
		GridLayoutFactory.swtDefaults().applyTo(group);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(group);
		tabs[2] = tab;

		tab = new Tab();
		tab.name = "по аспектам";
		tab.image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/aspect.gif").createImage();
		group = new Group(folder, SWT.NONE);
		//TODO интерфейс
		tab.control = group;
		GridLayoutFactory.swtDefaults().applyTo(group);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(group);
		tabs[3] = tab;

		tab = new Tab();
		tab.name = "по дате";
		tab.image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/calendar_view_day.png").createImage();
		group = new Group(folder, SWT.NONE);
		//TODO интерфейс
		tab.control = group;
		GridLayoutFactory.swtDefaults().applyTo(group);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(group);
		tabs[4] = tab;
		return tabs;
	}

	/**
	 * Поиск по имени
	 * @param text поисковое выражение
	 */
	private void findByName(String text) {
		try {
			setData(new EventService().findByName(text));
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String[] initTableColumns() {
		String[] columns = {
			"Имя",
			"Фамилия",
			"Дата",
			"Знак Зодиака",
			"Стихия",
			"Описание" };
		return columns;
	}

	@Override
	protected IBaseLabelProvider getLabelProvider() {
		return new ModelLabelProvider() {
			@Override
			public String getColumnText(Object element, int columnIndex) {
				kz.zvezdochet.bean.Event event = (kz.zvezdochet.bean.Event)element;
				switch (columnIndex) {
					case 0: return event.getName();
					case 1: return event.getSurname();
					case 2: return DateUtil.formatDateTime(event.getBirth());
					case 3: return event.getSign();
					case 4: return event.getElement();
					case 5: return event.getDescription();
				}
				return null;
			}
		};
	}
}
