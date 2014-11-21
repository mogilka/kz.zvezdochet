package kz.zvezdochet.part;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import kz.zvezdochet.bean.Aspect;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.ui.Tab;
import kz.zvezdochet.core.ui.provider.DictionaryLabelProvider;
import kz.zvezdochet.core.ui.view.ModelLabelProvider;
import kz.zvezdochet.core.ui.view.ModelListView;
import kz.zvezdochet.core.ui.view.View;
import kz.zvezdochet.core.util.DateUtil;
import kz.zvezdochet.service.AspectService;
import kz.zvezdochet.service.EventService;
import kz.zvezdochet.service.HouseService;
import kz.zvezdochet.service.PlanetService;
import kz.zvezdochet.service.SignService;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Поиск событий
 * @author Nataly Didenko
 */
public class SearchPart extends ModelListView {
	private CTabFolder folder;
	private List<Model> planetlist;
	private List<Model> aspectlist;
	private List<Model> houselist;
	
	@Inject
	public SearchPart() {}

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
		txSearch.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
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

		Label lb = new Label(group, SWT.NONE);
		lb.setText("Планета");
		final ComboViewer cvPlanet = new ComboViewer(group, SWT.BORDER | SWT.READ_ONLY);
		try {
			cvPlanet.setContentProvider(new ArrayContentProvider());
			cvPlanet.setLabelProvider(new DictionaryLabelProvider());
			cvPlanet.setInput(new PlanetService().getList());
		} catch (Exception e) {
			e.printStackTrace();
		}

		lb = new Label(group, SWT.NONE);
		lb.setText("Знак Зодиака");
		final ComboViewer cvSign = new ComboViewer(group, SWT.BORDER | SWT.READ_ONLY);
		try {
			cvSign.setContentProvider(new ArrayContentProvider());
			cvSign.setLabelProvider(new DictionaryLabelProvider());
			cvSign.setInput(new SignService().getList());
		} catch (Exception e) {
			e.printStackTrace();
		}

		Button bt = new Button(group, SWT.NONE);
		bt.setText("Искать");
		bt.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (cvPlanet.getSelection().isEmpty()
						|| cvSign.getSelection().isEmpty())
					return;
				IStructuredSelection selection = (IStructuredSelection)cvPlanet.getSelection();
				Planet planet = ((Planet)selection.getFirstElement());
				selection = (IStructuredSelection)cvSign.getSelection();
				Sign sign = ((Sign)selection.getFirstElement());
				findByPlanetSign(planet, sign);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		tab.control = group;
		GridLayoutFactory.swtDefaults().numColumns(5).applyTo(group);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(group);
		tabs[1] = tab;
		
		//////////////////////////////////////////////////////////////
		
		tab = new Tab();
		tab.name = "по домам";
		tab.image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/home.gif").createImage();
		group = new Group(folder, SWT.NONE);

		lb = new Label(group, SWT.NONE);
		lb.setText("Планета");
		final ComboViewer cvPlaneth = new ComboViewer(group, SWT.BORDER | SWT.READ_ONLY);
		try {
			cvPlaneth.setContentProvider(new ArrayContentProvider());
			cvPlaneth.setLabelProvider(new DictionaryLabelProvider());
			cvPlaneth.setInput(new PlanetService().getList());
		} catch (Exception e) {
			e.printStackTrace();
		}

		lb = new Label(group, SWT.NONE);
		lb.setText("Астрологический дом");
		final ComboViewer cvHouse = new ComboViewer(group, SWT.BORDER | SWT.READ_ONLY);
		try {
			cvHouse.setContentProvider(new ArrayContentProvider());
			cvHouse.setLabelProvider(new DictionaryLabelProvider());
			cvHouse.setInput(new HouseService().getList());
		} catch (Exception e) {
			e.printStackTrace();
		}

		bt = new Button(group, SWT.NONE);
		bt.setText("Искать");
		bt.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (cvPlaneth.getSelection().isEmpty()
						|| cvHouse.getSelection().isEmpty())
					return;
				IStructuredSelection selection = (IStructuredSelection)cvPlaneth.getSelection();
				Planet planet = ((Planet)selection.getFirstElement());
				selection = (IStructuredSelection)cvHouse.getSelection();
				House house = ((House)selection.getFirstElement());
				findByPlanetSign(planet, house);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		//////////////////////////////////////////////////////////
		
		tab.control = group;
		GridLayoutFactory.swtDefaults().numColumns(5).applyTo(group);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(group);
		tabs[2] = tab;

		tab = new Tab();
		tab.name = "по аспектам";
		tab.image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/aspect.gif").createImage();
		final Group groupAspect = new Group(folder, SWT.NONE);

		bt = new Button(groupAspect, SWT.NONE);
		bt.setText("+");
		bt.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addAspectSearch(groupAspect);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		bt = new Button(groupAspect, SWT.NONE);
		bt.setText("Искать");
		bt.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<Planet> planets = new ArrayList<Planet>();
				List<Aspect> aspects = new ArrayList<Aspect>();
				List<Planet> planets2 = new ArrayList<Planet>();
				for (Control cont : groupAspect.getChildren()) {
					if (cont instanceof Group) {
						for (Control control : ((Group)cont).getChildren()) {
							if (control instanceof Combo) {
								int index = ((Combo)control).getSelectionIndex();
								Object code = control.getData("code");
								if (code.equals("aspectPlanet1") && index > -1)
									planets.add((Planet)planetlist.get(index));
								else if (code.equals("aspectPlanet2") && index > -1)
									planets2.add((Planet)planetlist.get(index));
								else if (code.equals("planetAspect") && index > -1)
									aspects.add((Aspect)aspectlist.get(index));
							}
						}
					}
				}
				if (planets.size() == planets2.size() && planets.size() == aspects.size())
					findByPlanetAspect(planets, planets2, aspects);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		tab.control = groupAspect;
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(groupAspect);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(groupAspect);
		tabs[3] = tab;
		
		////////////////////////////////////////////////////////////

		tab = new Tab();
		tab.name = "по дате";
		tab.image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/calendar_view_day.png").createImage();
		group = new Group(folder, SWT.NONE);

		lb = new Label(group, SWT.NONE);
		lb.setText("Начало");
		final CDateTime dt = new CDateTime(group, CDT.BORDER | CDT.COMPACT | CDT.DROP_DOWN | CDT.DATE_LONG | CDT.TIME_MEDIUM);
		dt.setNullText(""); //$NON-NLS-1$

		lb = new Label(group, SWT.NONE);
		lb.setText("Конец");
		final CDateTime dt2 = new CDateTime(group, CDT.BORDER | CDT.COMPACT | CDT.DROP_DOWN | CDT.DATE_LONG | CDT.TIME_MEDIUM);
		dt2.setNullText(""); //$NON-NLS-1$

		bt = new Button(group, SWT.NONE);
		bt.setText("Искать");
		bt.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (null == dt.getSelection() || null == dt2.getSelection())
					return;
				findByDateRange(dt.getSelection(), dt2.getSelection());
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		tab.control = group;
		GridLayoutFactory.swtDefaults().numColumns(5).applyTo(group);
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
			setData(new EventService().findByName(text, -1));
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String[] initTableColumns() {
		String[] columns = {
			"Пол",
			"№",
			"Имя",
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
					case 1: return event.getId().toString();
					case 2: return event.getName();
					case 3: return DateUtil.formatDateTime(event.getBirth());
					case 4: return event.getSign();
					case 5: return event.getElement();
					case 6: return event.getDescription();
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

	@Override
	public boolean check(int mode) throws Exception {
		return false;
	}

	/**
	 * Поиск по знаку Зодиака планеты
	 * @param planet планета
	 * @param sign знак Зодиака
	 */
	private void findByPlanetSign(Planet planet, Sign sign) {
		try {
			setData(new EventService().findByPlanetSign(planet, sign));
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Поиск по астрологическому дому планеты
	 * @param planet планета
	 * @param house астрологический дом
	 */
	private void findByPlanetSign(Planet planet, House house) {
		try {
			setData(new EventService().findByPlanetHouse(planet, house));
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Поиск по аспектам планет
	 * @param planets массив планет
	 * @param planets2 массив планет
	 * @param aspects массив астрологических аспектов
	 */
	private void findByPlanetAspect(List<Planet> planets, List<Planet> planets2, List<Aspect> aspects) {
		try {
			setData(new EventService().findByPlanetAspect(planets, planets2, aspects));
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Поиск по периоду
	 * @param date начальная дата
	 * @param date2 конечная дата
	 */
	private void findByDateRange(Date date, Date date2) {
		try {
			setData(new EventService().findByDateRange(date, date2));
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	private void addAspectSearch(Group container) {
		try {
			final Group group = new Group(container, SWT.NONE);
			GridLayoutFactory.swtDefaults().numColumns(1).applyTo(group);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(group);

			new Label(group, SWT.NONE).setText("Планета");
			ComboViewer cvPlaneta = new ComboViewer(group, SWT.BORDER | SWT.READ_ONLY);
			cvPlaneta.setContentProvider(new ArrayContentProvider());
			cvPlaneta.setLabelProvider(new DictionaryLabelProvider());
			cvPlaneta.setInput(planetlist);
			cvPlaneta.getCombo().setData("code", "aspectPlanet1");

			new Label(group, SWT.NONE).setText("Астрологический аспект");
			ComboViewer cvAspect = new ComboViewer(group, SWT.BORDER | SWT.READ_ONLY);
			cvAspect.setContentProvider(new ArrayContentProvider());
			cvAspect.setLabelProvider(new DictionaryLabelProvider());
			cvAspect.setInput(new AspectService().getList());
			cvAspect.getCombo().setData("code", "planetAspect");

			new Label(group, SWT.NONE).setText("Планета");
			ComboViewer cvPlaneta2 = new ComboViewer(group, SWT.BORDER | SWT.READ_ONLY);
			cvPlaneta2.setContentProvider(new ArrayContentProvider());
			cvPlaneta2.setLabelProvider(new DictionaryLabelProvider());
			cvPlaneta2.setInput(planetlist);
			cvPlaneta2.getCombo().setData("code", "aspectPlanet2");

			Button bt = new Button(group, SWT.NONE);
			bt.setText("-");
			bt.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					group.dispose();
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void initControls() throws DataAccessException {
		planetlist = new PlanetService().getList();
		aspectlist = new AspectService().getList();
		houselist = new HouseService().getList();
	}
}
