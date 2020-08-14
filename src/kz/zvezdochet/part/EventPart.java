package kz.zvezdochet.part;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.CardKind;
import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.MoonDay;
import kz.zvezdochet.bean.Place;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.bean.Star;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.handler.Handler;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.ui.Tab;
import kz.zvezdochet.core.ui.decoration.InfoDecoration;
import kz.zvezdochet.core.ui.decoration.RequiredDecoration;
import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.core.ui.util.GUIutil;
import kz.zvezdochet.core.ui.view.ModelPart;
import kz.zvezdochet.core.ui.view.View;
import kz.zvezdochet.core.util.CalcUtil;
import kz.zvezdochet.core.util.DateUtil;
import kz.zvezdochet.provider.CardKindLabelProvider;
import kz.zvezdochet.provider.MoonDayLabelProvider;
import kz.zvezdochet.provider.PlaceProposalProvider;
import kz.zvezdochet.provider.PlaceProposalProvider.PlaceContentProposal;
import kz.zvezdochet.service.AspectTypeService;
import kz.zvezdochet.service.CardKindService;
import kz.zvezdochet.service.HouseService;
import kz.zvezdochet.service.MoonDayService;
import kz.zvezdochet.service.PlanetService;

/**
 * Представление события
 * @author Natalie Didenko
 * @todo при любом изменении данных делать представление грязным
 */
public class EventPart extends ModelPart implements ICalculable {
	/**
	 * Режим синхронизации данных события,
	 * при котором проверяются только расчётные показатели
	 */
	public static int MODE_ASPECT_PLANET_PLANET = 1;
	public static int MODE_ASPECT_PLANET_HOUSE = 2;
	public static int MODE_ASPECT_CUSPID_HOUSE = 3;

	private Label lbGender;
	private Link lbID;
	private ComboViewer cvGender;
	private ComboViewer cvHand;
	private ComboViewer cvRectification;
	private Label lbName;
	private Text txName;
	private Text txPlace;
	private Text txLatitude;
	private Text txLongitude;
	private Text txZone;
	private ComboViewer cvDST;
	private Text txGreenwich;
	private Text txComment;
	private Text txBio;
	private Label lbBirth;
	private CDateTime dtBirth;
	private CDateTime dtDeath; 
	private Button btCelebrity;
	private ComboViewer cvHuman;
	private Text txAccuracy;
	private Text txConversation;
	private CTabFolder tabfolder;
	private ComboViewer cvMoonday;
	private ComboViewer cvCardKind;
	private Text txOptions;
	private Button btTerm;
	private Text txCurrentPlace;

	private CosmogramComposite cmpCosmogram;
	private Group grPlanets;
	private Group grHouses;
	private CTabFolder folder;
	private Group grAspectType;
	private Group grIngress;
	private Group grStars;

	private SashForm shCosmogram;
	private Button btP2PAspects;
	private Button btP2HAspects;
	private Button btH2HAspects;

	@PostConstruct
	public View create(Composite parent) {
		return super.create(parent);
	}

	@Override
	protected void init(Composite parent) { 
		super.init(parent);
		Group secEvent = new Group(sashForm, SWT.NONE);
		secEvent.setText(Messages.getString("PersonView.Options")); //$NON-NLS-1$

		Label lb = new Label(secEvent, SWT.NONE);
		lb.setText("ID");
		lbID = new Link(secEvent, SWT.NONE);

		lbName = new Label(secEvent, SWT.NONE);
		lbName.setText(Messages.getString("PersonView.Name")); //$NON-NLS-1$
		txName = new Text(secEvent, SWT.BORDER);

		lbGender = new Label(secEvent, SWT.CENTER);
		lbGender.setText(Messages.getString("PersonView.Gender")); //$NON-NLS-1$
		cvGender = new ComboViewer(secEvent, SWT.BORDER | SWT.READ_ONLY);

		lb = new Label(secEvent, SWT.CENTER);
		lb.setText(Messages.getString("PersonView.Hand")); //$NON-NLS-1$
		cvHand = new ComboViewer(secEvent, SWT.BORDER | SWT.READ_ONLY);
		
		lbBirth = new Label(secEvent, SWT.NONE);
		lbBirth.setText(Messages.getString("PersonView.BirthDate")); //$NON-NLS-1$
		dtBirth = new CDateTime(secEvent, CDT.BORDER | CDT.COMPACT | CDT.DROP_DOWN | CDT.DATE_LONG | CDT.TIME_MEDIUM);
		dtBirth.setNullText(""); //$NON-NLS-1$
		new RequiredDecoration(lbBirth, SWT.TOP | SWT.RIGHT);

		lb = new Label(secEvent, SWT.CENTER);
		lb.setText(Messages.getString("PersonView.DeathDate")); //$NON-NLS-1$
		dtDeath = new CDateTime(secEvent, CDT.BORDER | CDT.COMPACT | CDT.DROP_DOWN | CDT.DATE_LONG | CDT.TIME_MEDIUM);
		dtDeath.setNullText(""); //$NON-NLS-1$
		
		lb = new Label(secEvent, SWT.NONE);
		lb.setText("Тип");
		cvHuman = new ComboViewer(secEvent, SWT.BORDER | SWT.READ_ONLY);

		lb = new Label(secEvent, SWT.CENTER);
		lb.setText(Messages.getString("PersonView.Rectification")); //$NON-NLS-1$
		cvRectification = new ComboViewer(secEvent, SWT.BORDER | SWT.READ_ONLY);

		lb = new Label(secEvent, SWT.NONE);
		lb.setText("Источник");
		txAccuracy = new Text(secEvent, SWT.BORDER);
		txAccuracy.setToolTipText("Источник времени рождения");

		//////////////////////////////////////////////////
		
		Group secPlace = new Group(secEvent, SWT.NONE);
		secPlace.setText(Messages.getString("PersonView.Place")); //$NON-NLS-1$
		txPlace = new Text(secPlace, SWT.BORDER);
		new InfoDecoration(txPlace, SWT.TOP | SWT.LEFT);

		lb = new Label(secPlace, SWT.NONE);
		lb.setText(Messages.getString("PersonView.Latitude")); //$NON-NLS-1$
		txLatitude = new Text(secPlace, SWT.BORDER);
		txLatitude.setEditable(false);

		lb = new Label(secPlace, SWT.NONE);
		lb.setText(Messages.getString("PersonView.Longitude")); //$NON-NLS-1$
		txLongitude = new Text(secPlace, SWT.BORDER);
		txLongitude.setEditable(false);

		lb = new Label(secPlace, SWT.NONE);
		lb.setText(Messages.getString("PersonView.Greenwith")); //$NON-NLS-1$
		txGreenwich = new Text(secPlace, SWT.BORDER);
		txGreenwich.setEditable(false);

		lb = new Label(secPlace, SWT.NONE);
		lb.setText(Messages.getString("PersonView.Zone")); //$NON-NLS-1$
		txZone = new Text(secPlace, SWT.BORDER);

		lb = new Label(secPlace, SWT.NONE);
		lb.setText("DST"); //$NON-NLS-1$
		cvDST = new ComboViewer(secPlace, SWT.BORDER | SWT.READ_ONLY);

		txCurrentPlace = new Text(secPlace, SWT.BORDER);
		new InfoDecoration(txCurrentPlace, SWT.TOP | SWT.LEFT);

		GridLayoutFactory.swtDefaults().numColumns(4).applyTo(secPlace);
		GridDataFactory.fillDefaults().span(4, 1).grab(true, false).applyTo(secPlace);

		//////////////////////////////////////////////////

		lb = new Label(secEvent, SWT.NONE);
		lb.setText("Знаменитость");
		btCelebrity = new Button(secEvent, SWT.BORDER | SWT.CHECK);
		txComment = new Text(secEvent, SWT.BORDER);

		lb = new Label(secEvent, SWT.NONE);
		lb.setText("Термины");
		btTerm = new Button(secEvent, SWT.BORDER | SWT.CHECK);

//		secEvent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		tabfolder = new CTabFolder(secEvent, SWT.BORDER);
		tabfolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tabfolder.setSimple(false);
		tabfolder.setUnselectedCloseVisible(false);

		CTabItem item = new CTabItem(tabfolder, SWT.CLOSE);
		item.setText(Messages.getString("PersonView.Biography"));
		item.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/contact_enabled.gif").createImage());
		txBio = new Text(tabfolder, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		item.setControl(txBio);

		item = new CTabItem(tabfolder, SWT.CLOSE);
		item.setText("Журнал");
		item.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/contact_away.gif").createImage());
		txConversation = new Text(tabfolder, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		item.setControl(txConversation);

		item = new CTabItem(tabfolder, SWT.CLOSE);
		item.setText("Толкования");
		item.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet.core", "icons/configure.gif").createImage());
		Group group = new Group(tabfolder, SWT.NONE);
		Link ln = new Link(group, SWT.NONE);
		String url = "http://goroskop.org/luna/form.shtml";
		ln.setText("<a href=\"" + url + "\">Лунный день</a>");
		ln.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				Program.launch(event.text);
			}
		});
		cvMoonday = new ComboViewer(group, SWT.BORDER | SWT.READ_ONLY);

		lb = new Label(group, SWT.NONE);
		lb.setText("Вид космограммы");
		cvCardKind = new ComboViewer(group, SWT.BORDER | SWT.READ_ONLY);

		lb = new Label(group, SWT.NONE);
		lb.setText("Настройки космограммы");
		txOptions = new Text(group, SWT.BORDER);
		item.setControl(group);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(group);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(group);

		item = new CTabItem(tabfolder, SWT.CLOSE);
		item.setText("Фигуры");
//		item.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/constellation.png").createImage());
//		txLog = new Text(tabfolder, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
//		item.setControl(txLog);

		tabfolder.pack();
		tabfolder.setSelection(0);
		GridDataFactory.fillDefaults().span(4, 1).grab(true, true).applyTo(tabfolder);
		
		GridLayoutFactory.swtDefaults().numColumns(4).applyTo(secEvent);
		GridDataFactory.fillDefaults().hint(500, SWT.DEFAULT).grab(false, true).applyTo(secEvent);

		//////////////////////////////////////////////////

		shCosmogram = new SashForm(sashForm, SWT.VERTICAL);
		Group grCosmogram = new Group(shCosmogram, SWT.NONE);
		grCosmogram.setText("Космограмма");
		cmpCosmogram = new CosmogramComposite(grCosmogram, SWT.NONE);
		
		Group grSettings = new Group(shCosmogram, SWT.NONE);
		folder = new CTabFolder(grSettings, SWT.BORDER);
		folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		folder.setSimple(false);
		folder.setUnselectedCloseVisible(false);
		Tab[] tabs = initTabs();
		for (Tab tab : tabs) {
			item = new CTabItem(folder, SWT.CLOSE);
			item.setText(tab.name);
			item.setImage(tab.image);
			item.setControl(tab.control);
		}
		folder.pack();

		GridLayoutFactory.swtDefaults().applyTo(grCosmogram);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(grCosmogram);
		GridLayoutFactory.swtDefaults().applyTo(grSettings);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(grSettings);
		setModel(null, false);
	}
	
	/**
	 * Инициализация вкладок космограммы
	 * @return массив вкладок
	 */
	private Tab[] initTabs() {
		Tab[] tabs = new Tab[6];
		//настройки расчёта
		Tab tab = new Tab();
		tab.name = "Настройки";
		tab.image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet.runner", "icons/configure.gif").createImage();
		Group group = new Group(folder, SWT.NONE);
		group.setText("Общие");
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tab.control = group;
		tabs[0] = tab;

//-------- планеты
		tab = new Tab();
		tab.name = "Планеты";
		tab.image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/planet.gif").createImage();
		grPlanets = new Group(folder, SWT.NONE);
		Object[] titles = {
			"Планета",
			"Долгота",
			"Широта",
			"Расстояние",
			"Скорость д",
			"Скорость ш",
			"Скорость р",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"Знак",
			"Дом",
			"",
			"",
			"",
			"",
			"",
			""
		};
		Table table = new Table(grPlanets, SWT.BORDER | SWT.V_SCROLL);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setSize(grPlanets.getSize());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(table);
		for (Object title : titles) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(title.toString());
		}	
		tab.control = grPlanets;
		GridLayoutFactory.swtDefaults().applyTo(grPlanets);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(grPlanets);
		tabs[1] = tab;
		
//-------- дома
		tab = new Tab();
		tab.name = "Дома";
		tab.image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/home.gif").createImage();
		grHouses = new Group(folder, SWT.NONE);
		String[] titles2 = {"Дом", "Координата", "Знак"};
		table = new Table(grHouses, SWT.BORDER | SWT.V_SCROLL);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setSize(grHouses.getSize());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(table);
		for (int i = 0; i < titles2.length; i++) {
			TableColumn column = new TableColumn (table, SWT.NONE);
			column.setText(titles2[i]);
		}
		tab.control = grHouses;
		GridLayoutFactory.swtDefaults().applyTo(grHouses);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(grHouses);
		tabs[2] = tab;
		
//-------- аспекты
		try {
			tab = new Tab();
			tab.name = "Аспекты";
			tab.image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/aspect.gif").createImage();
			grAspectType = new Group(folder, SWT.NONE);
			grAspectType.setLayout(new GridLayout());
	
			//типы аспектов
			Group gr = new Group(grAspectType, SWT.NONE);
			List<Model> types = new AspectTypeService().getList();
			for (Model model : types) {
				AspectType type = (AspectType)model;
				if (type.getImage() != null) {
					final Button bt = new Button(gr, SWT.BORDER | SWT.CHECK);
					bt.setText(type.getName());
					bt.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/aspect/" + type.getImage()).createImage());
					bt.setSelection(true);
					bt.setData("type", type.getCode());
				}
			}
			GridLayoutFactory.swtDefaults().applyTo(gr);
			GridDataFactory.fillDefaults().grab(false, true).applyTo(gr);
	
			//планеты аспектов
			final Group grp = new Group(grAspectType, SWT.NONE);
			Button bt = new Button(grp, SWT.NONE);
			bt.setText("Выбрать все");
			bt.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					for (Control control : grp.getChildren()) {
						Button button = (Button)control;
						if (button.getData("planet") != null)
							button.setSelection(true);
					}
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {}
			});
			bt = new Button(grp, SWT.NONE);
			bt.setText("Снять выделение");
			bt.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					for (Control control : grp.getChildren()) {
						Button button = (Button)control;
						if (button.getData("planet") != null)
							button.setSelection(false);
					}
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {}
			});

			List<Model> planets = new PlanetService().getList();
			for (Model model : planets) {
				Planet planet = (Planet)model;
				final Button b = new Button(grp, SWT.BORDER | SWT.CHECK);
				b.setText(planet.getName());
				b.setImage(planet.getImage());
				b.setSelection(true);
				b.setData("planet", planet.getCode());
			}
			GridLayoutFactory.swtDefaults().applyTo(grp);
			GridDataFactory.fillDefaults().grab(false, true).applyTo(grp);

			//дома аспектов
			ScrolledComposite scroll = new ScrolledComposite(grAspectType, SWT.V_SCROLL);
			scroll.setLayout(new GridLayout(1, false));
			scroll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		    
			final Group grh = new Group(scroll, SWT.NONE);
			bt = new Button(grh, SWT.NONE);
			bt.setText("Выбрать все");
			bt.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					for (Control control : grh.getChildren()) {
						Button button = (Button)control;
						if (button.getData("house") != null)
							button.setSelection(true);
					}
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {}
			});
			bt = new Button(grh, SWT.NONE);
			bt.setText("Снять выделение");
			bt.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					for (Control control : grh.getChildren()) {
						Button button = (Button)control;
						if (button.getData("house") != null)
							button.setSelection(false);
					}
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {}
			});

			List<Model> houses = new HouseService().getList();
			for (Model model : houses) {
				House house = (House)model;
				final Button b = new Button(grh, SWT.BORDER | SWT.CHECK);
				b.setText(house.getDesignation() + " - " + house.getName());
				b.setData("house", house.getCode());
			}
			GridLayoutFactory.swtDefaults().applyTo(grh);
			GridDataFactory.fillDefaults().grab(false, true).applyTo(grh);
			scroll.setContent(grh);
			scroll.setExpandHorizontal(true);
			scroll.setExpandVertical(true);
			scroll.setMinSize(grh.computeSize(SWT.DEFAULT, SWT.DEFAULT));

			//настройки аспектов
			gr = new Group(grAspectType, SWT.NONE);
			btP2PAspects = new Button(gr, SWT.BORDER | SWT.RADIO);
			btP2PAspects.setText("Аспекты планет с планетами");
			btP2PAspects.setSelection(true);

			btP2HAspects = new Button(gr, SWT.BORDER | SWT.RADIO);
			btP2HAspects.setText("Аспекты планет с куспидами");

			btH2HAspects = new Button(gr, SWT.BORDER | SWT.RADIO);
			btH2HAspects.setText("Аспекты домов с куспидами");

			bt = new Button(gr, SWT.NONE);
			bt.setText("Расчёт");
			bt.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					int mode = MODE_ASPECT_PLANET_PLANET;
					if (btP2HAspects.getSelection())
						mode = MODE_ASPECT_PLANET_HOUSE;
					else if (btH2HAspects.getSelection())
						mode = MODE_ASPECT_CUSPID_HOUSE;
					refreshCard(mode);
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {}
			});
			GridLayoutFactory.swtDefaults().applyTo(gr);
			GridDataFactory.fillDefaults().grab(false, true).applyTo(gr);

			GridLayoutFactory.swtDefaults().numColumns(4).applyTo(grAspectType);
			GridDataFactory.fillDefaults().span(4, 1).grab(false, true).applyTo(grAspectType);
			tab.control = grAspectType;
			tabs[3] = tab;
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

		//ингрессии
		tab = new Tab();
		tab.name = "Ингрессии";
		tab.image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/runtoline_co.gif").createImage();
		grIngress = new Group(folder, SWT.NONE);
		titles2 = new String[] {"Планета", "Тип", "Объект", "Точка"};
		table = new Table(grIngress, SWT.BORDER | SWT.V_SCROLL);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setSize(grIngress.getSize());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(table);
		for (int i = 0; i < titles2.length; i++) {
			TableColumn column = new TableColumn (table, SWT.NONE);
			column.setText(titles2[i]);
		}
		tab.control = grIngress;
		GridLayoutFactory.swtDefaults().applyTo(grIngress);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(grIngress);
		tabs[4] = tab;

		//звёзды
		tab = new Tab();
		tab.name = "Звёзды";
		tab.image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/star.png").createImage();
		grStars = new Group(folder, SWT.NONE);
		titles = new String[] {
			"Звезда",
			"Долгота",
			"Широта",
			"",
			"",
			"",
			"Знак",
			"Дом",
			"",
			"",
			"",
			"",
			"",
			""
		};
		table = new Table(grStars, SWT.BORDER | SWT.V_SCROLL);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setSize(grStars.getSize());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(table);
		for (Object title : titles) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(title.toString());
		}	
		tab.control = grStars;
		GridLayoutFactory.swtDefaults().applyTo(grStars);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(grStars);
		tabs[5] = tab;
		return tabs;
	}

	@Override
	protected void arrange(Composite parent) {
		super.arrange(parent);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(shCosmogram);
		GridLayoutFactory.swtDefaults().applyTo(shCosmogram);

		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			span(3, 1).grab(true, false).applyTo(lbID);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			span(3, 1).grab(true, false).applyTo(txName);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			grab(true, false).applyTo(cvGender.getCombo());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			grab(true, false).applyTo(cvHand.getCombo());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			span(3, 1).grab(true, false).applyTo(dtBirth);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			span(3, 1).grab(true, false).applyTo(dtDeath);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			grab(true, false).applyTo(cvHuman.getCombo());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			grab(true, false).applyTo(cvRectification.getCombo());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			span(3, 1).grab(true, false).applyTo(txAccuracy);

		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			span(4, 1).grab(true, false).applyTo(txPlace);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			grab(true, false).applyTo(txLatitude);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			grab(true, false).applyTo(txLongitude);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			grab(true, false).applyTo(txZone);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			grab(true, false).applyTo(txGreenwich);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			grab(true, false).applyTo(cvDST.getCombo());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			span(4, 1).grab(true, false).applyTo(txCurrentPlace);

//		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
//			grab(true, false).applyTo(cvMoonday.getCombo());

		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).
			grab(true, true).applyTo(txBio);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).
			grab(true, true).applyTo(txConversation);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			span(2, 1).grab(true, false).applyTo(txComment);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).
			grab(false, false).applyTo(btCelebrity);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).
			grab(false, false).applyTo(btTerm);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			grab(true, false).applyTo(txOptions);

		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL).
			hint(514, 514).grab(true, false).applyTo(cmpCosmogram);
		
		ModifyListener blobListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				part.setDirty(true);
			}
		};
		txBio.addModifyListener(blobListener);
		txConversation.addModifyListener(blobListener);

//		StateChangedListener listener = new StateChangedListener(); TODO
//		dtBirth.addSelectionListener(listener);
//		dtDeath.addSelectionListener(listener);
//		cvGender.addSelectionChangedListener(listener);
//		cvHand.addSelectionChangedListener(listener);
//		cvRectification.addSelectionChangedListener(listener);
//		txName.addModifyListener(listener);
//		txSurname.addModifyListener(listener);
//		txPlace.addModifyListener(listener);
//		txLatitude.addModifyListener(listener);
//		txLongitude.addModifyListener(listener);
//		txZone.addModifyListener(listener);
//		txZone.addListener(SWT.Verify, new NumberInputListener());
//		txGreenwich.addModifyListener(listener); 
//		txBiography.addModifyListener(listener);
//		txComment.addModifyListener(listener);
//		btCelebrity.addSelectionListener(listener);
//		txCurrentPlace.addModifyListener(listener);
	}

	/**
	 * Инициализация представления местности события
	 * @param place местность
	 * @param current true|false текущее|натальное
	 */
	private void initPlace(Place place, boolean current) {
		if (null == place) return;
		if (current)
			txCurrentPlace.setText(place.getName());
		else {
			txPlace.setText(place.getName());
			txLatitude.setText(CalcUtil.formatNumber("###.##", place.getLatitude())); //$NON-NLS-1$
			txLongitude.setText(CalcUtil.formatNumber("###.##", place.getLongitude())); //$NON-NLS-1$
			txGreenwich.setText(CalcUtil.formatNumber("###.##", place.getGreenwich())); //$NON-NLS-1$
			txZone.setText(String.valueOf(place.getGreenwich()));
		}
	}

	private String[] genders = {"",
		Messages.getString("PersonView.Male"),
		Messages.getString("PersonView.Female")};
	private String[] hands = {"",
		Messages.getString("PersonView.Right-handed"),
		Messages.getString("PersonView.Left-handed")};
	private String[] humans = {"Событие",
		"Живое существо",
		"Сообщество людей"};
	private Map<Integer, String> dst = new TreeMap<Integer, String>();

	@Override
	protected void initControls() {
		cvGender.setContentProvider(new ArrayContentProvider());
		cvGender.setInput(genders);

		cvHand.setContentProvider(new ArrayContentProvider());
		cvHand.setInput(hands);

		cvHuman.setContentProvider(new ArrayContentProvider());
		cvHuman.setInput(humans);

		cvRectification.setContentProvider(new ArrayContentProvider());
		cvRectification.setInput(Event.calcs);

		cvDST.setContentProvider(new ArrayContentProvider());
		dst.put(-3, "-3");
		dst.put(-2, "-2");
		dst.put(-1, "-1");
		dst.put(0, "0");
		dst.put(1, "+1");
		dst.put(2, "+2");
		dst.put(3, "+3");
		cvDST.setInput(dst.values());
		setPlaces();

		try {
			cvMoonday.setContentProvider(new ArrayContentProvider());
			cvMoonday.setLabelProvider(new MoonDayLabelProvider());
			List<Model> list = new MoonDayService().getList();
			MoonDay day = new MoonDay();
			day.setId(0L);
			list.add(0, day);
			cvMoonday.setInput(list);

			cvCardKind.setContentProvider(new ArrayContentProvider());
			cvCardKind.setLabelProvider(new CardKindLabelProvider());
			list = new CardKindService().getList();
			CardKind kind = new CardKind();
			kind.setId(0L);
			list.add(0, kind);
			cvCardKind.setInput(list);

			lbID.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(org.eclipse.swt.widgets.Event event) {
					Program.launch(event.text);
				}
			});

			txOptions.setText("{\"cardkind\":{\"planet\":0,\"planet2\":0,\"direction\":\"\",\"signs\":\"\",\"houses\":\"\"}}");
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean check(int mode) throws Exception {
		StringBuffer msgBody = new StringBuffer();
		if (Handler.MODE_SAVE == mode) {
			if (null == txPlace.getText()) {
				DialogUtil.alertWarning(Messages.getString("EventView.PlaceIsWrong"));
				return false;
			}
			if (dtBirth.getSelection() != null && dtDeath.getSelection() != null)
				if (!DateUtil.isDateRangeValid(dtBirth.getSelection(), dtDeath.getSelection())) {
					DialogUtil.alertWarning(GUIutil.INVALID_DATE_RANGE);
					return false;
				}
			if (txName.getText().length() == 0) 
				msgBody.append(lbName.getText());
			if (cvGender.getSelection().isEmpty())
				msgBody.append(lbGender.getText());
		}
		if (null == dtBirth.getSelection())
			msgBody.append(lbBirth.getText());

		if (msgBody.length() > 0) {
			DialogUtil.alertWarning(GUIutil.SOME_FIELDS_NOT_FILLED + msgBody);
			return false;
		} else
			return true;
	}

	@Override
	public void syncModel(int mode) throws Exception {
		if (!check(mode)) return;//TODO часто дублируется вызов из хэндлеров
		model = (null == model) ? new Event() : model;
		Event event = (Event)model;
		if (Handler.MODE_SAVE == mode) {
			event.setName(txName.getText());
			event.setFemale(2 == cvGender.getCombo().getSelectionIndex());
			event.setRightHanded(2 == cvHand.getCombo().getSelectionIndex());
			event.setRectification(cvRectification.getCombo().getSelectionIndex());
			event.setDeath(dtDeath.getSelection());
			event.setBio(txBio.getText());
			event.setComment(txComment.getText());
			event.setCelebrity(btCelebrity.getSelection());
			event.setAccuracy(txAccuracy.getText());
			event.setConversation(txConversation.getText());
			event.setOptions(txOptions.getText());
		}
		event.setBirth(dtBirth.getSelection());
		double zone = (txZone.getText() != null && txZone.getText().length() > 0) ? Double.parseDouble(txZone.getText()) : 0;
		event.setZone(zone);
		event.setDst(cvDST.getCombo().getSelectionIndex() - 3);
		event.setHuman(cvHuman.getCombo().getSelectionIndex());

		IStructuredSelection selection = (IStructuredSelection)cvMoonday.getSelection();
		if (selection.getFirstElement() != null) {
			MoonDay day = (MoonDay)selection.getFirstElement();
			if (day.getId() > 0)
				event.setMoondayid(day.getId());
		}

		selection = (IStructuredSelection)cvCardKind.getSelection();
		if (selection.getFirstElement() != null) {
			CardKind kind = (CardKind)selection.getFirstElement();
			if (kind.getId() > 0)
				event.setCardkindid(kind.getId());
		}
	}
	
	@Override
	protected void syncView() {
		try {
			reset();
			model = (null == model) ? new Event() : model;
			Event event = (Event)model;
			long id = (null == event) ? 0 : event.getId();
			if (id > 0) {
				Calendar calendar = Calendar.getInstance();
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH) + 1;
				long placeid = (null == event.getCurrentPlace()) ? Place._GREENWICH : event.getCurrentPlace().getId();
				String url = "http://zvezdochet.local/month/transits?id=" + month + "&year=" + year + "&eventid=" + id + "&placeid=" + placeid;
				lbID.setText(id + " " + "<a href=\"" + url + "\">транзиты</a>");
			}
			txName.setText(event.getName());
			cvGender.getCombo().setText(genders[event.isFemale() ? 2 : 1]);
			cvHand.getCombo().setText(hands[event.isRightHanded() ? 0 : 1]);
			if (event.getRectification() > 0)
				cvRectification.getCombo().setText(Event.calcs[event.getRectification()]);
			if (event.getBirth() != null)
				dtBirth.setSelection(event.getBirth());
			if (event.getDeath() != null)
				dtDeath.setSelection(event.getDeath());
			btCelebrity.setSelection(event.isCelebrity());
			if (event.getComment() != null)
				txComment.setText(event.getComment());
			if (event.getBio() != null)
				txBio.setText(event.getBio());
			if (event.getPlace() != null)
				initPlace(event.getPlace(), false);
			txZone.setText(CalcUtil.formatNumber("###.##", event.getZone()));
			String dststr = dst.get((int)event.getDst());
			cvDST.getCombo().setText(null == dststr ? "0" : dststr);
			if (event.getCurrentPlace() != null)
				initPlace(event.getCurrentPlace(), true);
			int human = event.getHuman();
			if (human > -1)
				cvHuman.getCombo().setText(humans[human]);
			if (event.getAccuracy() != null)
				txAccuracy.setText(event.getAccuracy());
			if (event.getConversation() != null)
				txConversation.setText(event.getConversation());
			if (event.getMoondayid() > 0) {
				MoonDay day = (MoonDay)new MoonDayService().find(event.getMoondayid());
				cvMoonday.getCombo().setText(day.getId() + " " + day.getSymbol());
			}
			if (event.getCardkindid() > 0) {
				CardKind kind = (CardKind)new CardKindService().find(event.getCardkindid());
				cvCardKind.getCombo().setText(kind.getName() + " - " + kind.getDegree());
			}
			if (event.getOptions() != null)
				txOptions.setText(event.getOptions());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void reset() {
		lbID.setText("");
		txName.setText(""); //$NON-NLS-1$
		txPlace.setText(""); //$NON-NLS-1$
		txLatitude.setText(""); //$NON-NLS-1$
		txLongitude.setText(""); //$NON-NLS-1$
		txZone.setText(""); //$NON-NLS-1$
		cvDST.setSelection(null);
		txGreenwich.setText(""); //$NON-NLS-1$
		txBio.setText(""); //$NON-NLS-1$
		txComment.setText(""); //$NON-NLS-1$
		dtBirth.setSelection(new Date());
		dtDeath.setSelection(null);
		cvGender.setSelection(null);
		cvHand.setSelection(null);
		cvRectification.setSelection(null);
		btCelebrity.setSelection(false);
		btTerm.setSelection(false);
		cvHuman.getCombo().setText(humans[1]);
		txAccuracy.setText(""); //$NON-NLS-1$
		txConversation.setText(""); //$NON-NLS-1$
		cvMoonday.setSelection(null);
		cvCardKind.setSelection(null);
		txOptions.setText("{\"cardkind\":{\"planet\":0,\"planet2\":0,\"direction\":\"\",\"signs\":\"\",\"houses\":\"\"}}");
		txCurrentPlace.setText(""); //$NON-NLS-1$
		refreshCard(MODE_ASPECT_PLANET_PLANET);
		refreshTabs();
	}
	
	public Model addModel() {
		return new Event();
	}

	@Override
	public void onCalc(Object obj) {
		if (null == obj)
			obj = MODE_ASPECT_PLANET_PLANET;
		int mode = (int)obj;
		if (MODE_ASPECT_PLANET_PLANET == mode) {
			part.setDirty(true);
			refreshCard(mode);
			refreshTabs();
		} else if (MODE_ASPECT_PLANET_HOUSE == mode) {
			Map<String, Object> params = new HashMap<>();
			List<String> aparams = new ArrayList<String>();
			Map<String, String[]> types = AspectType.getHierarchy();
			for (Control control : grAspectType.getChildren()) {
				Button button = (Button)control;
				if (button.getSelection())
					aparams.addAll(Arrays.asList(types.get(button.getData("type"))));
			}
			params.put("aspects", aparams);
			params.put("houseAspectable", true);
			Event event = (Event)model;
			cmpCosmogram.paint(event, null, params);
		}
	}

	/**
	 * Обновление вкладок
	 */
	private void refreshTabs() {
		//планеты
		Control[] controls = grPlanets.getChildren();
		Table table = (Table)controls[0];
		table.removeAll();
		Event event = (Event)model;
		if (event != null) {
			folder.setSelection(1);
			Collection<Planet> planets = event.getPlanets().values();
			for (Planet planet : planets) {
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(0, planet.getName());
				item.setText(1, String.valueOf(CalcUtil.roundTo(planet.getLongitude(), 3)));
				item.setText(2, String.valueOf(CalcUtil.roundTo(planet.getLatitude(), 3)));
				item.setText(3, String.valueOf(CalcUtil.roundTo(planet.getDistance(), 3)));
				item.setText(4, String.valueOf(CalcUtil.roundTo(planet.getSpeedLongitude(), 3)));
				item.setText(5, String.valueOf(CalcUtil.roundTo(planet.getSpeedLatitude(), 3)));
				item.setText(6, String.valueOf(CalcUtil.roundTo(planet.getSpeedDistance(), 3)));

				item.setText(7, planet.isRetrograde() ? "R" : "");
				item.setImage(8, planet.isDamaged() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/aspect/disharmonic.gif").createImage() : null);
				item.setImage(9, planet.isPerfect() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/aspect/harmonic.gif").createImage() : null);
				item.setImage(10, planet.inMine() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/mine.gif").createImage() : null);
				item.setImage(11, planet.isSword() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/sword.png").createImage() : null);
				item.setImage(12, planet.isShield() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/shield.png").createImage() : null);
				item.setImage(13, planet.isKernel() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/core.png").createImage() : null);
				item.setImage(14, planet.isBelt() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/belt.png").createImage() : null);

				Sign sign = planet.getSign();
				item.setText(15, null == sign ? "" : sign.getName());
				Image image = null;
				if (planet.isSignHome())
					image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/home.gif").createImage();
				else if (planet.isSignExaltated())
					image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/previous.gif").createImage();
				else if (planet.isSignExile())
					image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/exile.png").createImage();
				else if (planet.isSignDeclined())
					image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/next_nav.gif").createImage();
				if (image != null)
					item.setImage(15, image);

				House house = planet.getHouse();
				item.setText(16, null == house ? "" : house.getCode());
				image = null;
				if (planet.isHouseHome())
					image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/home.gif").createImage();
				else if (planet.isHouseExaltated())
					image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/previous.gif").createImage();
				else if (planet.isHouseExile())
					image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/exile.png").createImage();
				else if (planet.isHouseDeclined())
					image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/next_nav.gif").createImage();
				if (image != null)
					item.setImage(16, image);

				item.setImage(17, planet.isLilithed() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/planet/Lilith.png").createImage() : null);

				item.setImage(18, planet.isSelened() ?
						AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/planet/Selena.png").createImage() : null);

				item.setImage(19, planet.isRakhued() ?
						AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/planet/Rakhu.png").createImage() : null);

				item.setImage(20, planet.isKethued() ?
						AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/planet/Kethu.png").createImage() : null);

				item.setImage(21, planet.isKing() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/crown.png").createImage() : null);

				item.setImage(22, planet.isLord() ?
						AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/throne.png").createImage() : null);

				item.setImage(23, planet.isBroken() ?
						AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/ilow_obj.gif").createImage() : null);
			}
			for (int i = 0; i < table.getColumnCount(); i++)
				table.getColumn(i).pack();
//			folder.setSelection(0);
		}

		//дома
		controls = grHouses.getChildren();
		table = (Table)controls[0];
		table.removeAll();
		if (event != null) {
			if (event.isHousable()) {
				for (House house : event.getHouses().values()) {
					TableItem item = new TableItem(table, SWT.NONE);
					item.setText(0, house.getName());		
					item.setText(1, String.valueOf(house.getLongitude()));

					Sign sign = house.getSign();
					item.setText(2, null == sign ? "" : sign.getName());
				}
				for (int i = 0; i < table.getColumnCount(); i++)
					table.getColumn(i).pack();
			}
		}

		//ингрессии
//		controls = grIngress.getChildren();
//		table = (Table)controls[0];
//		table.removeAll();
//		if (conf != null) {
//			for (Model base : conf.getIngresses()) {
//				Ingress ingress = (Ingress)base;
//				TableItem item = new TableItem(table, SWT.NONE);
//				item.setText(0, ingress.getPlanet().getName());
//				item.setText(1, ingress.getType().getName());		
//				if (ingress.getObject() != null) {
//					if (ingress.getType().getCode().equals("sign"))
//						item.setText(2, ((Sign)ingress.getObject()).getName());
//					else if (ingress.getType().getCode().equals("application"))
//						item.setText(2, ((Aspect)ingress.getObject()).getName());
//				}
//				if (ingress.getSkyPoint() != null)
//					item.setText(3, ingress.getSkyPoint().getName());
//			}
//			for (int i = 0; i < table.getColumnCount(); i++)
//				table.getColumn(i).pack();
//		}

		//звёзды
		controls = grStars.getChildren();
		table = (Table)controls[0];
		table.removeAll();
		if (event != null) {
			Collection<Star> stars = event.getStars().values();
			for (Star star : stars) {
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(0, star.getName());
				item.setText(1, String.valueOf(CalcUtil.roundTo(star.getLongitude(), 3)));
				item.setText(2, String.valueOf(CalcUtil.roundTo(star.getLatitude(), 3)));

				item.setImage(3, star.isDamaged() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/aspect/disharmonic.gif").createImage() : null);
				item.setImage(4, star.isPerfect() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/aspect/harmonic.gif").createImage() : null);
				item.setImage(5, star.inMine() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/mine.gif").createImage() : null);

				Sign sign = star.getSign();
				item.setText(6, null == sign ? "" : sign.getName());

				House house = star.getHouse();
				item.setText(7, null == house ? "" : house.getCode());

				item.setImage(8, star.isLilithed() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/planet/Lilith.png").createImage() : null);

				item.setImage(9, star.isSelened() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/planet/Selena.png").createImage() : null);

				item.setImage(10, star.isRakhued() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/planet/Rakhu.png").createImage() : null);

				item.setImage(11, star.isKethued() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/planet/Kethu.png").createImage() : null);

				item.setImage(12, star.isKing() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/crown.png").createImage() : null);

				item.setImage(13, star.isLord() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/throne.png").createImage() : null);

				item.setImage(14, star.isBroken() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/ilow_obj.gif").createImage() : null);
			}
			for (int i = 0; i < table.getColumnCount(); i++)
				table.getColumn(i).pack();
		}
	}

	/**
	 * Перерисовка космограммы
	 * @param mode 0|1|2 аспекты планет с планетами|планет с домами|домов с домами
	 */
	private void refreshCard(int mode) {
		if (null == model)
			return;
		Map<String, Object> params = new HashMap<>();
		List<String> subparams = new ArrayList<String>();
		Map<String, String[]> types = AspectType.getHierarchy();
		Group group = (Group)grAspectType.getChildren()[0];
		for (Control control : group.getChildren()) {
			Button button = (Button)control;
			if (button.getSelection())
				subparams.addAll(Arrays.asList(types.get(button.getData("type"))));
		}
		params.put("aspects", subparams);

		if (MODE_ASPECT_PLANET_HOUSE == mode) {
			params.put("aspectMode", "houseAspectable");
			
			subparams = new ArrayList<String>();
			group = (Group)grAspectType.getChildren()[1];
			for (Control control : group.getChildren()) {
				Button button = (Button)control;
				if (button.getSelection())
					subparams.add(button.getData("planet").toString());
			}
			params.put("planets", subparams);

			subparams = new ArrayList<String>();
			ScrolledComposite scroll = (ScrolledComposite)grAspectType.getChildren()[2];
			group = (Group)scroll.getChildren()[0];
			for (Control control : group.getChildren()) {
				Button button = (Button)control;
				if (button.getSelection())
					subparams.add(button.getData("house").toString());
			}
			params.put("houses", subparams);
		} else if (MODE_ASPECT_CUSPID_HOUSE == mode) {
			params.put("aspectMode", "houseCuspidable");

			subparams = new ArrayList<String>();
			ScrolledComposite scroll = (ScrolledComposite)grAspectType.getChildren()[2];
			group = (Group)scroll.getChildren()[0];
			for (Control control : group.getChildren()) {
				Button button = (Button)control;
				if (button.getSelection())
					subparams.add(button.getData("house").toString());
			}
			params.put("houses", subparams);
		}
		cmpCosmogram.paint((Event)model, null, params);
	}

	/**
	 * Инициализация местностей
	 */
	private void setPlaces() {
	    PlaceProposalProvider proposalProvider = new PlaceProposalProvider();
	    ContentProposalAdapter adapter = new ContentProposalAdapter(
	        txPlace, new TextContentAdapter(),
	        proposalProvider, KeyStroke.getInstance(SWT.CTRL, 32), new char[] {' '});
	    adapter.setPropagateKeys(true);
	    adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
	    adapter.addContentProposalListener(new IContentProposalListener() {
			@Override
			public void proposalAccepted(IContentProposal proposal) {
				Place place = (Place)((PlaceContentProposal)proposal).getObject();
				if (place != null) {
					if (null == model)
						model = new Event();
					((Event)model).setPlace(place);
					initPlace(place, false);
				}
			}
		});

	    adapter = new ContentProposalAdapter(
		        txCurrentPlace, new TextContentAdapter(),
		        proposalProvider, KeyStroke.getInstance(SWT.CTRL, 32), new char[] {' '});
		    adapter.setPropagateKeys(true);
		    adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		    adapter.addContentProposalListener(new IContentProposalListener() {
				@Override
				public void proposalAccepted(IContentProposal proposal) {
					Place place = (Place)((PlaceContentProposal)proposal).getObject();
					if (place != null) {
						if (null == model)
							model = new Event();
						((Event)model).setCurrentPlace(place);
						initPlace(place, true);
					}
				}
			});
}

	@Override
	public void setModel(Model model, boolean sync) {
		if (sync) {
			Event event = (Event)model;
			if (model != null && event.getId() != null)
				setTitle(event.getName());
			else
				setTitle(Messages.getString("PersonView.New")); //$NON-NLS-1$
		}
		super.setModel(model, sync);
		if (sync) {
			refreshCard(MODE_ASPECT_PLANET_PLANET);
			refreshTabs();
		}
	}

	/**
	 * Определяем тип отчёта по выделению пункта "Астрологические термины":
	 * 	true - делаем отчёт гороскопа с указанием названий планет, аспектов и других терминов
	 * 	false - пишем человекопонятные заменители вместо терминов
	 * @return
	 */
	public boolean isTerm() {
		return btTerm.getSelection();
	}
}
