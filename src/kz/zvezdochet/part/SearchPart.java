package kz.zvezdochet.part;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;

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

/**
 * Поиск гороскопов
 * @author Natalie Didenko
 */
public class SearchPart extends ModelListView {
	private CTabFolder folder;
	
	@Inject
	public SearchPart() {}

	@PostConstruct @Override
	public View create(Composite parent) {
		return super.create(parent);
//		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
//			@Override
//			public void doubleClick(DoubleClickEvent event) {
//				new EventHandler().execute(SearchPart.this);				
//			}
//		});
	}
	
	@Override
	public void initFilter(Composite parent) {
		grFilter = new Group(parent, SWT.NONE);
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
//			if (tab.image != null)
//				item.setImage(tab.image);
			item.setControl(tab.control);
		}
		folder.setSelection(0);
	}

	/**
	 * Инициализация вкладок поиска
	 * @return массив вкладок
	 */
	private Tab[] initTabs() {
		Tab[] tabs = new Tab[7];
		Tab tab = new Tab();
		tab.name = "по имени";
		Image image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet.core", "icons/correction_linked_rename.gif").createImage();
		tab.image = image;
		image.dispose();
		final Text txSearch = new Text(folder, SWT.BORDER);
		txSearch.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		txSearch.setFocus();
		txSearch.addListener(SWT.DefaultSelection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				String text = txSearch.getText();
				if (text.length() > 1)
					try {
						setData(new EventService().findByName(text, null, null));
					} catch (DataAccessException e) {
						e.printStackTrace();
					}			
			}
		});
		tab.control = txSearch;
		tabs[0] = tab;
		
		///////////////////////////////////////////////////////////
		
		tab = new Tab();
		tab.name = "по знакам Зодиака";
		image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/cosmogram.png").createImage();
		tab.image = image;
		image.dispose();
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
				try {
					setData(new EventService().findByPlanetSign(planet, sign));
				} catch (DataAccessException e1) {
					e1.printStackTrace();
				}
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
		image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/home.gif").createImage();
		tab.image = image;
		image.dispose();
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
				try {
					setData(new EventService().findByPlanetHouse(planet, house));
				} catch (DataAccessException e1) {
					e1.printStackTrace();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		tab.control = group;
		GridLayoutFactory.swtDefaults().numColumns(5).applyTo(group);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(group);
		tabs[2] = tab;
		
		////////////////////////////////////////////////////////////////

		tab = new Tab();
		tab.name = "по аспектам";
		image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/aspect.gif").createImage();
		tab.image = image;
		image.dispose();
		group = new Group(folder, SWT.NONE);

		lb = new Label(group, SWT.NONE);
		lb.setText("Планета");
		final ComboViewer cvPlaneta = new ComboViewer(group, SWT.BORDER | SWT.READ_ONLY);
		try {
			cvPlaneta.setContentProvider(new ArrayContentProvider());
			cvPlaneta.setLabelProvider(new DictionaryLabelProvider());
			cvPlaneta.setInput(new PlanetService().getList());
		} catch (Exception e) {
			e.printStackTrace();
		}

		lb = new Label(group, SWT.NONE);
		lb.setText("Астрологический аспект");
		final ComboViewer cvAspect = new ComboViewer(group, SWT.BORDER | SWT.READ_ONLY);
		try {
			cvAspect.setContentProvider(new ArrayContentProvider());
			cvAspect.setLabelProvider(new DictionaryLabelProvider());
			cvAspect.setInput(new AspectService().getList());
		} catch (Exception e) {
			e.printStackTrace();
		}

		lb = new Label(group, SWT.NONE);
		lb.setText("Планета");
		final ComboViewer cvPlaneta2 = new ComboViewer(group, SWT.BORDER | SWT.READ_ONLY);
		try {
			cvPlaneta2.setContentProvider(new ArrayContentProvider());
			cvPlaneta2.setLabelProvider(new DictionaryLabelProvider());
			cvPlaneta2.setInput(new PlanetService().getList());
		} catch (Exception e) {
			e.printStackTrace();
		}

		bt = new Button(group, SWT.NONE);
		bt.setText("Искать");
		bt.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ((cvPlaneta.getSelection().isEmpty()
						|| cvAspect.getSelection().isEmpty()
						|| cvPlaneta2.getSelection().isEmpty())
							|| cvPlaneta.getSelection().equals(cvPlaneta2.getSelection()))
					return;
				IStructuredSelection selection = (IStructuredSelection)cvPlaneta.getSelection();
				Planet planet = ((Planet)selection.getFirstElement());
				selection = (IStructuredSelection)cvAspect.getSelection();
				Aspect aspect = ((Aspect)selection.getFirstElement());
				selection = (IStructuredSelection)cvPlaneta2.getSelection();
				Planet planet2 = ((Planet)selection.getFirstElement());
				try {
					setData(new EventService().findByPlanetAspect(planet, planet2, aspect));
				} catch (DataAccessException e1) {
					e1.printStackTrace();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		tab.control = group;
		GridLayoutFactory.swtDefaults().numColumns(7).applyTo(group);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(group);
		tabs[3] = tab;
		
		////////////////////////////////////////////////////////////////

		tab = new Tab();
		tab.name = "по дате";
		image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/calendar_view_day.png").createImage();
		tab.image = image;
		image.dispose();
		group = new Group(folder, SWT.NONE);

		lb = new Label(group, SWT.NONE);
		lb.setText("Начало");
		final DateTime dt = new DateTime(group, SWT.DROP_DOWN);

		bt = new Button(group, SWT.NONE);
		bt.setText("Искать");
		bt.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				if (null == dt.getSelection() || null == dt2.getSelection())
//					return;
				try {
					Calendar calendar = Calendar.getInstance();
					calendar.set(Calendar.DAY_OF_MONTH, dt.getDay());
					calendar.set(Calendar.MONTH, dt.getMonth());
					calendar.set(Calendar.YEAR, dt.getYear());
					calendar.set(Calendar.HOUR_OF_DAY, 0);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.SECOND, 0);
					Date date = calendar.getTime();

					calendar.add(Calendar.DATE, 1);
					Date date2 = calendar.getTime();

					setData(new EventService().findByDateRange(date, date2));
				} catch (DataAccessException e1) {
					e1.printStackTrace();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		tab.control = group;
		GridLayoutFactory.swtDefaults().numColumns(5).applyTo(group);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(group);
		tabs[4] = tab;
		
		/////////////////////////////////////////////////////////////////////
		
		tab = new Tab();
		tab.name = "по номеру";
		image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/keycolumn.gif").createImage();
		tab.image = image;
		image.dispose();
		final Text txNumber = new Text(folder, SWT.BORDER);
		txNumber.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		txNumber.addListener(SWT.DefaultSelection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				String text = txNumber.getText();
				if (text.length() > 0)
					try {
						Model model = new EventService().find(Long.valueOf(text));
						if (model != null)
							setData(new Model[] {model});
					} catch (NumberFormatException e) {
						//
					} catch (DataAccessException e) {
						e.printStackTrace();
					}
			}
		});
		tab.control = txNumber;
		tabs[5] = tab;

		/////////////////////////////////////////////////////////////////////

		tab = new Tab();
		tab.name = "по биографии";
		image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/document.gif").createImage();
		tab.image = image;
		image.dispose();
		final Text txText = new Text(folder, SWT.BORDER);
		txText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		txText.setFocus();
		txText.addListener(SWT.DefaultSelection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				String text = txText.getText();
				if (text.length() > 1)
					try {
						setData(new EventService().findByText(text));
					} catch (DataAccessException e) {
						e.printStackTrace();
					}			
			}
		});
		tab.control = txText;
		tabs[6] = tab;

		return tabs;
	}

	@Override
	protected String[] initTableColumns() {
		String[] columns = {
			"Пол",
			"№",
			"Имя",
			"Дата",
			"Дата создания",
			"Дата изменения" };
		return columns;
	}

	@Override
	protected IBaseLabelProvider getLabelProvider() {
		return new ModelLabelProvider() {
			@Override
			public String getColumnText(Object element, int columnIndex) {
				kz.zvezdochet.bean.Event event = (kz.zvezdochet.bean.Event)element;
				if (event != null)
					switch (columnIndex) {
						case 0: return event.isFemale() ? "♀" : "♂";
						case 1: return event.getId().toString();
						case 2: return event.getName();
						case 3: return DateUtil.formatDateTime(event.getBirth());
						case 4: return DateUtil.formatDateTime(event.getDate());
						case 5: return DateUtil.formatDateTime(event.getModified());
					}
				return null;
			}

			@Override
			public Color getForeground(Object element, int columnIndex) {
				kz.zvezdochet.bean.Event event = (kz.zvezdochet.bean.Event)element;
				if (event != null)
					switch (columnIndex) {
						case 0: int color = event.isFemale() ? SWT.COLOR_RED : SWT.COLOR_BLUE;
						return Display.getCurrent().getSystemColor(color);
					}
				return null;
			}
		};
	}

	@Override
	public boolean check(int mode) throws Exception {
		return false;
	}

	@Override
	public Model createModel() {
		return new kz.zvezdochet.bean.Event();
	}
}
