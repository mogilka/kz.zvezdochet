package kz.zvezdochet.part;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import kz.zvezdochet.bean.Aspect;
import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Ingress;
import kz.zvezdochet.bean.Place;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.bean.SkyPoint;
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
import kz.zvezdochet.provider.PlaceProposalProvider;
import kz.zvezdochet.provider.PlaceProposalProvider.PlaceContentProposal;
import kz.zvezdochet.service.AspectTypeService;
import kz.zvezdochet.util.Configuration;

/**
 * Представление события
 * @author Nataly Didenko
 * @todo при любом изменении данных делать представление грязным
 */
public class EventPart extends ModelPart implements ICalculable {
	/**
	 * Режим синхронизации данных события,
	 * при котором проверяются только расчётные показатели 
	 */
	public static int MODE_CALC = 1;

	private Label lbGender;
	private Label lbID;
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
	private Text txDescription;
	private Label lbBirth;
	private CDateTime dtBirth;
	private CDateTime dtDeath; 
	private Button btCelebrity;
	private ComboViewer cvHuman;
	private Text txAccuracy;
	private Text txLog;
	private CTabFolder tabfolder;

	private CosmogramComposite cmpCosmogram;
	private Group grPlanets;
	private Group grHouses;
	private CTabFolder folder;
	private Group grAspectType;
	private Group grIngress;
	
	@PostConstruct
	public View create(Composite parent) {
		Group secEvent = new Group(parent, SWT.NONE);
		secEvent.setText(Messages.getString("PersonView.Options")); //$NON-NLS-1$

		Label lb = new Label(secEvent, SWT.NONE);
		lb.setText("ID");
		lbID = new Label(secEvent, SWT.NONE);

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

		GridLayoutFactory.swtDefaults().numColumns(4).applyTo(secPlace);
		GridDataFactory.fillDefaults().span(4, 1).grab(true, false).applyTo(secPlace);

		//////////////////////////////////////////////////

		lb = new Label(secEvent, SWT.NONE);
		lb.setText("Знаменитость");
		btCelebrity = new Button(secEvent, SWT.BORDER | SWT.CHECK);
		txComment = new Text(secEvent, SWT.BORDER);
		txComment.setEditable(false);
		
//		secEvent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		tabfolder = new CTabFolder(secEvent, SWT.BORDER);
		tabfolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tabfolder.setSimple(false);
		tabfolder.setUnselectedCloseVisible(false);

		CTabItem item = new CTabItem(tabfolder, SWT.CLOSE);
		item.setText(Messages.getString("PersonView.Biography"));
		item.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/contact_enabled.gif").createImage());
		txDescription = new Text(tabfolder, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		item.setControl(txDescription);

		item = new CTabItem(tabfolder, SWT.CLOSE);
		item.setText("Журнал");
		item.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/contact_away.gif").createImage());
		txLog = new Text(tabfolder, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		item.setControl(txLog);

		tabfolder.pack();
		tabfolder.setSelection(0);
		GridDataFactory.fillDefaults().span(4, 1).grab(true, true).applyTo(tabfolder);
		
		GridLayoutFactory.swtDefaults().numColumns(4).applyTo(secEvent);
		GridDataFactory.fillDefaults().hint(500, SWT.DEFAULT).grab(false, true).applyTo(secEvent);

		//////////////////////////////////////////////////

		Group grCosmogram = new Group(parent, SWT.NONE);
		grCosmogram.setText("Космограмма");
		cmpCosmogram = new CosmogramComposite(grCosmogram, SWT.NONE);
		
		folder = new CTabFolder(grCosmogram, SWT.BORDER);
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
		
		super.create(parent);
		setModel(new Event(), true);
		return null;
	}
	
	/**
	 * Инициализация вкладок космограммы
	 * @return массив вкладок
	 */
	private Tab[] initTabs() {
		Tab[] tabs = new Tab[5];
		//настройки расчёта
		Tab tab = new Tab();
		tab.name = "Настройки";
		tab.image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet.runner", "icons/configure.gif").createImage();
		Group group = new Group(folder, SWT.NONE);
		group.setText("Общие");
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tab.control = group;
		tabs[0] = tab;

		//планеты
		tab = new Tab();
		tab.name = "Планеты";
		tab.image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/planet.gif").createImage();
		grPlanets = new Group(folder, SWT.NONE);
		Object[] titles = {
			"Планета",
			"Координата",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"Знаки",
			"Дома",
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
		
		//дома
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
		
		//аспекты
		tab = new Tab();
		tab.name = "Аспекты";
		tab.image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/aspect.gif").createImage();
		grAspectType = new Group(folder, SWT.NONE);
		grAspectType.setLayout(new GridLayout());
		List<Model> types = new ArrayList<Model>();
		try {
			types = new AspectTypeService().getList();
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		for (Model model : types) {
			AspectType type = (AspectType)model;
			if (type.getImage() != null) {
				final Button bt = new Button(grAspectType, SWT.BORDER | SWT.CHECK);
				bt.setText(type.getName());
				bt.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/aspect/" + type.getImage()).createImage());
				bt.setSelection(true);
				bt.setData("type", type.getCode());
				bt.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (bt.getSelection())
							refreshCard();
					}
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {}
				});
			}
		}
		tab.control = grAspectType;
		tabs[3] = tab;

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

		return tabs;
	}

	@Override
	protected void init(Composite parent) {
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(parent);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(parent);

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

		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).
			grab(true, true).applyTo(txDescription);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).
			grab(true, true).applyTo(txLog);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			span(2, 1).grab(true, false).applyTo(txComment);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).
			grab(false, false).applyTo(btCelebrity);

		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL).
			hint(514, 514).span(3, 1).grab(true, false).applyTo(cmpCosmogram);
		
		ModifyListener blobListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				((Event)model).setNeedSaveBlob(true);
			}
		};
		txDescription.addModifyListener(blobListener);
		txDescription.addModifyListener(blobListener);

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
		btCelebrity.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txComment.setEditable(btCelebrity.getSelection());
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
	}

	/**
	 * Инициализация представления местности события
	 * @param place местность
	 */
	private void initPlace(Place place) {
		if (null == place) return;
		txPlace.setText(place.getName());
		txLatitude.setText(CalcUtil.formatNumber("###.##", place.getLatitude())); //$NON-NLS-1$
		txLongitude.setText(CalcUtil.formatNumber("###.##", place.getLongitude())); //$NON-NLS-1$
		txGreenwich.setText(CalcUtil.formatNumber("###.##", place.getGreenwich())); //$NON-NLS-1$
		txZone.setText(String.valueOf(place.getGreenwich()));
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
	}

	@Override
	public boolean check(int mode) throws Exception {
		StringBuffer msgBody = new StringBuffer();
		if (Handler.MODE_SAVE == mode) {
			if (null == txPlace.getText()) {
				DialogUtil.alertError(Messages.getString("EventView.PlaceIsWrong"));
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
			event.setText(txDescription.getText());
			event.setDescription(txComment.getText());
			event.setCelebrity(btCelebrity.getSelection());
			event.setAccuracy(txAccuracy.getText());
			event.setConversation(txLog.getText());
		}
		event.setBirth(dtBirth.getSelection());
		double zone = (txZone.getText() != null && txZone.getText().length() > 0) ? Double.parseDouble(txZone.getText()) : 0;
		event.setZone(zone);
		event.setDst(cvDST.getCombo().getSelectionIndex() - 3);
		event.setHuman(cvHuman.getCombo().getSelectionIndex());
	}
	
	@Override
	protected void syncView() {
		try {
			reset();
			model = (null == model) ? new Event() : model;
			Event event = (Event)model;
			if (event.getId() != null && event.getId() > 0)
				lbID.setText(event.getId().toString());
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
			txComment.setEditable(btCelebrity.getSelection());
			if (event.getDescription() != null)
				txComment.setText(event.getDescription());
			if (event.getText() != null)
				txDescription.setText(event.getText());
			if (event.getPlace() != null)
				initPlace(event.getPlace());
			txZone.setText(CalcUtil.formatNumber("###.##", event.getZone()));
			cvDST.getCombo().setText(dst.get((int)event.getDst()));
			cvHuman.getCombo().setText(humans[event.getHuman()]);
			if (event.getAccuracy() != null)
				txAccuracy.setText(event.getAccuracy());
			if (event.getConversation() != null)
				txLog.setText(event.getConversation());
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
		txDescription.setText(""); //$NON-NLS-1$
		txComment.setText(""); //$NON-NLS-1$
		dtBirth.setSelection(new Date());
		dtDeath.setSelection(null);
		cvGender.setSelection(null);
		cvHand.setSelection(null);
		cvRectification.setSelection(null);
		btCelebrity.setSelection(false);
		cvHuman.getCombo().setText(humans[1]);
		txAccuracy.setText(""); //$NON-NLS-1$
		txLog.setText(""); //$NON-NLS-1$
	}
	
	public Model addModel() {
		return new Event();
	}

	@Override
	public void onCalc(Object obj) {
		part.setDirty(true);
		refreshCard();
		refreshTabs();
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
		Configuration conf = event.getConfiguration();
		if (conf != null) {
			folder.setSelection(1);
			for (Model base : conf.getPlanets()) {
				Planet planet = (Planet)base;
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(0, planet.getName());
				item.setText(1, String.valueOf(planet.getCoord()));
				item.setText(2, planet.isRetrograde() ? "R" : "");
				item.setImage(3, planet.isDamaged() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/aspect/disharmonic.gif").createImage() : null);
				item.setImage(4, planet.isPerfect() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/aspect/harmonic.gif").createImage() : null);
				item.setImage(5, planet.inMine() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/mine.gif").createImage() : null);
				item.setImage(6, planet.isSword() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/sword.png").createImage() : null);
				item.setImage(7, planet.isShield() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/shield.png").createImage() : null);
				item.setImage(8, planet.isKernel() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/core.png").createImage() : null);
				item.setImage(9, planet.isBelt() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/belt.png").createImage() : null);

				Sign sign = planet.getSign();
				item.setText(10, null == sign ? "" : sign.getName());
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
					item.setImage(10, image);

				House house = planet.getHouse();
				item.setText(11, null == house ? "" : house.getCode());
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
					item.setImage(11, image);

				item.setImage(12, planet.isLilithed() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/planet/Lilith.png").createImage() : null);

				item.setImage(13, planet.isSelened() ?
						AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/planet/Selena.png").createImage() : null);

				item.setImage(14, planet.isRakhued() ?
						AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/planet/Rakhu.png").createImage() : null);

				item.setImage(15, planet.isKethued() ?
						AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/planet/Kethu.png").createImage() : null);

				item.setImage(16, planet.isKing() ?
					AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/crown.png").createImage() : null);

				item.setImage(17, planet.isLord() ?
						AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/throne.png").createImage() : null);

				item.setImage(18, planet.isBroken() ?
						AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/ilow_obj.gif").createImage() : null);
			}
			for (int i = 0; i < table.getColumnCount(); i++)
				table.getColumn(i).pack();
		} else
			folder.setSelection(0);
			
		//дома
		controls = grHouses.getChildren();
		table = (Table)controls[0];
		table.removeAll();
		if (conf != null) {
			for (Model base : conf.getHouses()) {
				House house = (House)base;
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(0, house.getName());		
				item.setText(1, String.valueOf(house.getCoord()));

  				Sign sign;
				try {
					sign = SkyPoint.getSign(house.getCoord(), event.getBirthYear());
//  				house.setSign(sign);
					item.setText(2, null == sign ? "" : sign.getName());
				} catch (DataAccessException e) {
					e.printStackTrace();
				}
			}
			for (int i = 0; i < table.getColumnCount(); i++)
				table.getColumn(i).pack();
		}

		//ингрессии
		controls = grIngress.getChildren();
		table = (Table)controls[0];
		table.removeAll();
		if (conf != null) {
			for (Model base : conf.getIngresses()) {
				Ingress ingress = (Ingress)base;
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(0, ingress.getPlanet().getName());
				item.setText(1, ingress.getType().getName());		
				if (ingress.getObject() != null) {
					if (ingress.getType().getCode().equals("sign"))
						item.setText(2, ((Sign)ingress.getObject()).getName());
					else if (ingress.getType().getCode().equals("application"))
						item.setText(2, ((Aspect)ingress.getObject()).getName());
				}
				if (ingress.getSkyPoint() != null)
					item.setText(3, ingress.getSkyPoint().getName());
			}
			for (int i = 0; i < table.getColumnCount(); i++)
				table.getColumn(i).pack();
		}
	}

	/**
	 * Перерисовка космограммы
	 */
	private void refreshCard() {
		List<String> params = new ArrayList<String>();
		Map<String, String[]> types = AspectType.getHierarchy();
		for (Control control : grAspectType.getChildren()) {
			Button button = (Button)control;
			if (button.getSelection())
				params.addAll(Arrays.asList(types.get(button.getData("type"))));
		}
		if (params.size() < 1) return;
		Event event = (Event)model;
		cmpCosmogram.paint(event.getConfiguration(), null, params);
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
					((Event)model).setPlace(place);
					initPlace(place);
				}
			}
		});
	}

	@Override
	public void setModel(Model model, boolean sync) {
		if (sync) {
			Event event = (Event)model;
			if (model != null && event.getId() != null) {
				event.init(true);
				setTitle(event.getName());
			} else
				setTitle(Messages.getString("PersonView.New")); //$NON-NLS-1$
		}
		super.setModel(model, sync);
		if (sync) {
			refreshCard();
			refreshTabs();
		}
	}
}
