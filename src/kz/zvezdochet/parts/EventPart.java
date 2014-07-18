/**
 * 
 */
package kz.zvezdochet.parts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Place;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.bean.Base;
import kz.zvezdochet.core.bean.Reference;
import kz.zvezdochet.core.handler.Handler;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.ui.decoration.InfoDecoration;
import kz.zvezdochet.core.ui.decoration.RequiredDecoration;
import kz.zvezdochet.core.ui.listener.NumberInputListener;
import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.core.ui.util.GUIutil;
import kz.zvezdochet.core.ui.view.ElementView;
import kz.zvezdochet.core.util.CalcUtil;
import kz.zvezdochet.core.util.DateUtil;
import kz.zvezdochet.provider.PlaceProposalProvider;
import kz.zvezdochet.provider.PlaceProposalProvider.PlaceContentProposal;
import kz.zvezdochet.service.PlaceService;
import kz.zvezdochet.util.Configuration;

import org.eclipse.e4.ui.di.Focus;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.SwitchToWindowMenu;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Представление события
 * @author Nataly Didenko
 */
public class EventPart extends ElementView {
	
	public static int MODE_CALC = 1;

	private Label lbGender;
	private ComboViewer cvGender;
	private Combo cmbGender;
	private ComboViewer cvHand;
	private Combo cmbHand;
	private ComboViewer cvRectification;
	private Combo cmbRectification;
	private Label lbName;
	private Text txName;
	private Text txSurname;
	private Text txPlace;
	private Text txLatitude;
	private Text txLongitude;
	private Text txZone;
	private Text txGreenwich;
	private Text txCelebrity;
	private Text txBiography;
	private Label lbBirth;
	private CDateTime dtBirth; 
	private CDateTime dtDeath; 
	private Button btCelebrity;
	private CosmogramComposite cmpCosmogram;
	private Group grPlanets;
	private Group grHouses;
	private CTabFolder folder;
	
	
	@PostConstruct
	public void create(Composite parent) {
		Group secEvent = new Group(parent, SWT.NONE);
		secEvent.setText(Messages.getString("PersonView.Options")); //$NON-NLS-1$

		lbName = new Label(secEvent, SWT.NONE);
		lbName.setText(Messages.getString("PersonView.Name")); //$NON-NLS-1$
		txName = new Text(secEvent, SWT.BORDER);

		Label lb = new Label(secEvent, SWT.NONE);
		lb.setText(Messages.getString("PersonView.Surname")); //$NON-NLS-1$
		txSurname = new Text(secEvent, SWT.BORDER);
		
		lbGender = new Label(secEvent, SWT.CENTER);
		lbGender.setText(Messages.getString("PersonView.Gender")); //$NON-NLS-1$
		cvGender = new ComboViewer(secEvent, SWT.BORDER | SWT.READ_ONLY);
		cmbGender = cvGender.getCombo();
		
		lb = new Label(secEvent, SWT.CENTER);
		lb.setText(Messages.getString("PersonView.Hand")); //$NON-NLS-1$
		cvHand = new ComboViewer(secEvent, SWT.BORDER | SWT.READ_ONLY);
		cmbHand = cvHand.getCombo();
		
		lbBirth = new Label(secEvent, SWT.NONE);
		lbBirth.setText(Messages.getString("PersonView.BirthDate")); //$NON-NLS-1$
		dtBirth = new CDateTime(secEvent, CDT.BORDER | CDT.COMPACT | CDT.DROP_DOWN | CDT.DATE_LONG | CDT.TIME_MEDIUM);
		dtBirth.setNullText(""); //$NON-NLS-1$
		new RequiredDecoration(lb, SWT.TOP | SWT.RIGHT);

		lb = new Label(secEvent, SWT.CENTER);
		lb.setText(Messages.getString("PersonView.DeathDate")); //$NON-NLS-1$
		dtDeath = new CDateTime(secEvent, CDT.BORDER | CDT.COMPACT | CDT.DROP_DOWN | CDT.DATE_LONG | CDT.TIME_MEDIUM);
		dtDeath.setNullText(""); //$NON-NLS-1$
		
		lb = new Label(secEvent, SWT.CENTER);
		lb.setText(Messages.getString("PersonView.Rectification")); //$NON-NLS-1$
		cvRectification = new ComboViewer(secEvent, SWT.BORDER | SWT.READ_ONLY);
		cmbRectification = cvRectification.getCombo();
		
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

		GridLayoutFactory.swtDefaults().numColumns(4).applyTo(secPlace);
		GridDataFactory.fillDefaults().span(4, 1).grab(true, false).applyTo(secPlace);

		//////////////////////////////////////////////////

		Group secDescription = new Group(secEvent, SWT.NONE);
		secDescription.setText(Messages.getString("PersonView.Biography")); //$NON-NLS-1$
		secEvent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txBiography = new Text(secDescription, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);

		btCelebrity = new Button(secDescription, SWT.BORDER | SWT.CHECK);
		txCelebrity = new Text(secDescription, SWT.BORDER);
		txCelebrity.setEditable(false);

		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(secDescription);
		GridDataFactory.fillDefaults().span(4, 1).grab(true, true).applyTo(secDescription);
		
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
			CTabItem item = new CTabItem(folder, SWT.CLOSE);
			item.setText(tab.name);
			item.setImage(tab.image);
			item.setControl(tab.control);
		}
		GridLayoutFactory.swtDefaults().applyTo(grCosmogram);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(grCosmogram);
		
		super.create(parent);
		try {
			setDefaultElement();
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Инициализация вкладок космограммы
	 * @return массив вкладок
	 */
	private Tab[] initTabs() {
		Tab[] tabs = new Tab[3];
		Tab tab = new Tab();
		tab.name = "Настройки";
		tab.image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet.runner", "icons/configure.gif").createImage();
		tab.control = new Group(folder, SWT.NONE);
		//TODO кнопки из тулбара
		tabs[0] = tab;
		
		tab = new Tab();
		tab.name = "Планеты";
		tab.image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/planet.gif").createImage();
		grPlanets = new Group(folder, SWT.NONE);
		tab.control = grPlanets;
		GridLayoutFactory.swtDefaults().applyTo(grPlanets);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(grPlanets);
		tabs[1] = tab;
		
		tab = new Tab();
		tab.name = "Дома";
		tab.image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/home.gif").createImage();
		grHouses = new Group(folder, SWT.NONE);
		tab.control = grHouses;
		GridLayoutFactory.swtDefaults().applyTo(grHouses);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(grHouses);
		tabs[2] = tab;
		return tabs;
	}

	/**
	 * Вспомогательный класс для формирования вкладок космограммы
	 * @author Nataly Didenko
	 */
	private class Tab {
		@SuppressWarnings("unused")
		public String name;
		@SuppressWarnings("unused")
		public Image image;
		@SuppressWarnings("unused")
		public Control control;
	}

	/**
	 * Инициализация события по умолчанию
	 * @throws DataAccessException 
	 */
	private void setDefaultElement() throws DataAccessException {
		element = new Event();
		((Event)element).setZone(6.0); //TODO задавать через конфиг
		Place place = (Place)new PlaceService().find(115L); //TODO задавать через конфиг
		((Event)element).setPlace(place);
		setElement(element, true);
	}

	@Focus
	public void setFocus() {
//		tableViewer.getTable().setFocus();
	}

	@Override
	protected void init(Composite parent) {
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(parent);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(parent);

		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			span(3, 1).grab(true, false).applyTo(txName);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			span(3, 1).grab(true, false).applyTo(txSurname);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			grab(true, false).applyTo(cmbGender);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			grab(true, false).applyTo(cmbHand);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			span(3, 1).grab(true, false).applyTo(dtBirth);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			span(3, 1).grab(true, false).applyTo(dtDeath);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			span(3, 1).grab(true, false).applyTo(cmbRectification);

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

		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).
			span(2, 1).grab(true, true).applyTo(txBiography);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			grab(true, false).applyTo(txCelebrity);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).
			grab(false, false).applyTo(btCelebrity);

		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL).
			hint(514, 514).span(3, 1).grab(true, false).applyTo(cmpCosmogram);
	}

	protected void setListeners() {
		StateChangedListener listener = new StateChangedListener();
		dtBirth.addSelectionListener(listener);
		dtDeath.addSelectionListener(listener);
		cvGender.addSelectionChangedListener(listener);
		cvHand.addSelectionChangedListener(listener);
		cvRectification.addSelectionChangedListener(listener);
		txName.addModifyListener(listener);
		txSurname.addModifyListener(listener);
		txPlace.addModifyListener(listener);
		txLatitude.addModifyListener(listener);
		txLongitude.addModifyListener(listener);
		txZone.addModifyListener(listener);
		txZone.addListener(SWT.Verify, new NumberInputListener());
		txGreenwich.addModifyListener(listener); 
		txBiography.addModifyListener(listener);
		txCelebrity.addModifyListener(listener);
		btCelebrity.addSelectionListener(listener);
		btCelebrity.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txCelebrity.setEditable(btCelebrity.getSelection());
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
	}

	/**
	 * Инициализация местности события
	 * @param place местность
	 */
	private void setPlace(Place place) {
		if (place == null) return;
		txPlace.setText(place.getName());
		txLatitude.setText(CalcUtil.formatNumber("###.##", place.getLatitude())); //$NON-NLS-1$
		txLongitude.setText(CalcUtil.formatNumber("###.##", place.getLongitude())); //$NON-NLS-1$
		txGreenwich.setText(CalcUtil.formatNumber("###.##", place.getGreenwich())); //$NON-NLS-1$
		txZone.setText(String.valueOf(Math.abs(place.getGreenwich())));
	}
	
	private String[] genders = {"",
		Messages.getString("PersonView.Male"),
		Messages.getString("PersonView.Female")};
	private String[] hands = {"",
		Messages.getString("PersonView.Right-handed"),
		Messages.getString("PersonView.Left-handed")};
	private String[] calcs = {"",
		Messages.getString("PersonView.Success"),
		Messages.getString("PersonView.Fault"),
		Messages.getString("PersonView.Undefined")};

	@Override
	protected void initializeControls() {
		cvGender.setContentProvider(new ArrayContentProvider());
		cvGender.setInput(genders);

		cvHand.setContentProvider(new ArrayContentProvider());
		cvHand.setInput(hands);
		
		cvRectification.setContentProvider(new ArrayContentProvider());
		cvRectification.setInput(calcs);

		setPlaces();
	}

	@Override
	public boolean checkViewValues(int mode) throws Exception {
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
		} else return true;
	}

	@Override
	protected void viewToModel(int mode) throws Exception {
		if (!checkViewValues(mode)) return;
		element = (element == null) ? new Event() : element;
		Event event = (Event)element;
		if (Handler.MODE_SAVE == mode) {
			event.setName(txName.getText());
			event.setSurname(txSurname.getText());
			event.setFemale(0 == cmbGender.getSelectionIndex());
			event.setRightHanded(0 == cmbHand.getSelectionIndex());
			event.setRectification(cmbRectification.getSelectionIndex());
			event.setDeath(dtDeath.getSelection());
			event.setText(txBiography.getText());
			event.setDescription(txCelebrity.getText());
			event.setCelebrity(btCelebrity.getSelection());
		}
//		Place place = places.get(txPlace.getText()); //TODO идентифицировать по имени неправильно
		if (null == event.getPlace()) {
			Place place = new Place();
			place.setLatitude(51.48);
			place.setLongitude(0);
			event.setPlace(place);
		}
		event.setBirth(dtBirth.getSelection());
		double zone = (txZone.getText() != null) ? Double.parseDouble(txZone.getText()) : 0;
		event.setZone(zone);
	}
	
	protected void modelToView() {
		clear();
		element = (element == null) ? new Event() : element;
		Event event = (Event)element;
		setCodeEdit(true);
		txName.setText(event.getName());
		if (event.getSurname() != null)
			txSurname.setText(event.getSurname());
		cmbGender.setText(genders[event.isFemale() ? 0 : 1]);
		cmbHand.setText(hands[event.isRightHanded() ? 0 : 1]);
		if (event.getRectification() > 0)
			cmbRectification.setText(calcs[event.getRectification()]);
		if (event.getBirth() != null)
			dtBirth.setSelection(event.getBirth());
		if (event.getDeath() != null)
			dtDeath.setSelection(event.getDeath());
		btCelebrity.setSelection(event.isCelebrity());
		if (event.getDescription() != null)
			txCelebrity.setText(event.getDescription());
		if (event.getText() != null)
			txBiography.setText(event.getText());
		if (event.getPlace() != null)
			setPlace(event.getPlace());
		txZone.setText(CalcUtil.formatNumber("###.##", event.getZone()));
		setCodeEdit(false); 
	}
	
	public void clear() {
		setCodeEdit(true);
		txName.setText(""); //$NON-NLS-1$
		txSurname.setText(""); //$NON-NLS-1$
		txPlace.setText(""); //$NON-NLS-1$
		txLatitude.setText(""); //$NON-NLS-1$
		txLongitude.setText(""); //$NON-NLS-1$
		txZone.setText(""); //$NON-NLS-1$
		txGreenwich.setText(""); //$NON-NLS-1$
		txBiography.setText(""); //$NON-NLS-1$
		txCelebrity.setText(""); //$NON-NLS-1$
		dtBirth.setSelection(new Date());
		dtDeath.setSelection(null);
		cvGender.setSelection(null);
		cvHand.setSelection(null);
		cvRectification.setSelection(null);
		btCelebrity.setSelection(false);
		setCodeEdit(false);
	}
	
	public Object addElement() {
		return new Event();
	}

	/**
	 * Обновление содержимого представления после расчёта
	 */
	public void onCalc() {
		refreshCard();
		refreshTabs();
	}

	/**
	 * Обновление вкладок
	 */
	private void refreshTabs() {
		Table table;
		Event event = (Event)element;

		//планеты
		folder.setSelection(1);
		String[] titles = {"Планета", "Координата", "Направление"};
		Control[] controls = grPlanets.getChildren();
		if (controls.length > 0) {
			table = (Table)controls[0];
			table.removeAll();
		} else {
			table = new Table(grPlanets, SWT.BORDER | SWT.V_SCROLL);
			table.setLinesVisible(true);
			table.setHeaderVisible(true);
			table.setSize(grPlanets.getSize());
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(table);
			for (int i = 0; i < titles.length; i++) {
				TableColumn column = new TableColumn (table, SWT.NONE);
				column.setText(titles[i]);
			}	
		}
		for (Base base : event.getConfiguration().getPlanets()) {
			Planet planet = (Planet)base;
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, planet.getName());		
			item.setText(1, String.valueOf(planet.getCoord()));
			item.setText(2, planet.isRetrograde() ? "R" : "");
		}
		for (int i = 0; i < titles.length; i++)
			table.getColumn(i).pack();

		//дома
		String[] titles2 = {"Дом", "Координата"};
		controls = grHouses.getChildren();
		if (controls.length > 0) {
			table = (Table)controls[0];
			table.removeAll();
		} else {
			table = new Table(grHouses, SWT.BORDER | SWT.V_SCROLL);
			table.setLinesVisible(true);
			table.setHeaderVisible(true);
			table.setSize(grHouses.getSize());
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(table);
			for (int i = 0; i < titles2.length; i++) {
				TableColumn column = new TableColumn (table, SWT.NONE);
				column.setText(titles2[i]);
			}	
		}
		for (Base base : event.getConfiguration().getHouses()) {
			House house = (House)base;
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, house.getName());		
			item.setText(1, String.valueOf(house.getCoord()));
		}
		for (int i = 0; i < titles2.length; i++)
			table.getColumn(i).pack();	
	}

	/**
	 * Перерисовка космограммы
	 */
	public void refreshCard() {
		List<String> params = new ArrayList<String>();
		params.add("NEUTRAL");

		params.add("POSITIVE");
		params.add("POSITIVE_HIDDEN");
		params.add("NEGATIVE");
		params.add("NEGATIVE_HIDDEN");
		params.add("CREATIVE");
		params.add("KARMIC");
		params.add("SPIRITUAL");
		params.add("ENSLAVEMENT");
		params.add("DAO");
		params.add("MAGIC");
		params.add("PROGRESSIVE");
		params.add("TEMPTATION");

//		for (MToolBarElement item : activePart.getToolbar().getChildren()) {
//			HandledToolItemImpl hti = (HandledToolItemImpl)item;
////			MHandledToolItem hti = (MHandledToolItem)item;
//			if (hti.isSelected())
//				switch (hti.getElementId()) {
//				case "kz.zvezdochet.handledtoolitem.harmonic":
//					params.add("POSITIVE");
//					params.add("POSITIVE_HIDDEN");
//					break;
//				case "kz.zvezdochet.handledtoolitem.disharmonic":
//					params.add("NEGATIVE");
//					params.add("NEGATIVE_HIDDEN");
//					break;
//				case "kz.zvezdochet.handledtoolitem.creative":
//					params.add("CREATIVE");
//					break;
//				case "kz.zvezdochet.handledtoolitem.karmic":
//					params.add("KARMIC");
//					break;
//				case "kz.zvezdochet.handledtoolitem.spiritual":
//					params.add("SPIRITUAL");
//					params.add("ENSLAVEMENT");
//					params.add("DAO");
//					params.add("MAGIC");
//					break;
//				case "kz.zvezdochet.handledtoolitem.progressive":
//					params.add("PROGRESSIVE");
//					params.add("TEMPTATION");
//					break;
//				}
//		}
		if (params.size() < 1) return;
		Event event = (Event)element;
		cmpCosmogram.paint(event.getConfiguration(), params);
	}

	/**
	 * Инициализация местностей
	 */
	private void setPlaces() {
	    PlaceProposalProvider proposalProvider = new PlaceProposalProvider();
	    proposalProvider.setFiltering(true);
	    ContentProposalAdapter adapter = new ContentProposalAdapter(
	        txPlace, new TextContentAdapter(),
	        proposalProvider, KeyStroke.getInstance(SWT.CTRL, 32), new char[] {' '});
	    adapter.setPropagateKeys(true);
	    adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
	    adapter.addContentProposalListener(new IContentProposalListener() {
			@Override
			public void proposalAccepted(IContentProposal proposal) {
				Place place = (Place)((PlaceContentProposal)proposal).getObject();
				if (place != null)
					setPlace(place);
			}
		});
	}

	@Override
	public void setElement(Base element, boolean refresh) {
		super.setElement(element, refresh);
		if (element != null && ((Event)element).getId() != null)
			setTitle(((Event)element).getFullName());
		else
			setTitle(Messages.getString("PersonView.New")); //$NON-NLS-1$
		Event event = (Event)element;
		Configuration conf = event.getConfiguration();
		if (conf != null)
			showCardDetails(conf);
	}

	private void showCardDetails(Configuration conf) {
		if (conf.getPlanets() != null && conf.getPlanets().size() > 0) {
//			lbSun.setText(string);
			//TODO сделать по-другому - идти по листу и динамически создавать надписи
		}		
	}
}
