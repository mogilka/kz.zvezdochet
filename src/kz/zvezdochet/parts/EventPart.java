/**
 * 
 */
package kz.zvezdochet.parts;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.Place;
import kz.zvezdochet.core.bean.BaseEntity;
import kz.zvezdochet.core.bean.Reference;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.ui.decoration.InfoDecoration;
import kz.zvezdochet.core.ui.decoration.RequiredDecoration;
import kz.zvezdochet.core.ui.listener.NumberInputListener;
import kz.zvezdochet.core.ui.provider.ReferenceProposalProvider;
import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.core.ui.util.GUIutil;
import kz.zvezdochet.core.ui.view.ElementView;
import kz.zvezdochet.core.util.CalcUtil;
import kz.zvezdochet.core.util.DateUtil;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Представление события
 * @author nataly
 */
public class EventPart extends ElementView {

	private ComboViewer cvGender;
	private Combo cmbGender;
	private ComboViewer cvHand;
	private Combo cmbHand;
	private ComboViewer cvRectification;
	private Combo cmbRectification;
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
	private Group secEvent;
	private Group secPlace;
	private Group secDescription;
	private Button btCelebrity;

	private Group grCosmogram;
	private CosmogramComposite cmpCosmogram;
	private Group grPlanets;
	private Group grHouses;
	
	private Label lbSun;
	private Label lbMoon;
	private Label lbRakhu;
	private Label lbKethu;
	private Label lbMercury;
	private Label lbVenus;
	private Label lbMars;
	private Label lbSelena;
	private Label lbLilith;
	private Label lbJupiter;
	private Label lbSaturn;
	private Label lbChiron;
	private Label lbUranus;
	private Label lbNeptune;
	private Label lbPluto;
	private Label lbProzerpine;
	
	private Label lbSunStat;
	private Label lbMoonStat;
	private Label lbRakhuStat;
	private Label lbKethuStat;
	private Label lbMercuryStat;
	private Label lbVenusStat;
	private Label lbMarsStat;
	private Label lbSelenaStat;
	private Label lbLilithStat;
	private Label lbJupiterStat;
	private Label lbSaturnStat;
	private Label lbChironStat;
	private Label lbUranusStat;
	private Label lbNeptuneStat;
	private Label lbPlutoStat;
	private Label lbProzerpineStat;
	
	private Label lbHouse1;
	private Label lbHouse2;
	private Label lbHouse3;
	private Label lbHouse4;
	private Label lbHouse5;
	private Label lbHouse6;
	private Label lbHouse7;
	private Label lbHouse8;
	private Label lbHouse9;
	private Label lbHouse10;
	private Label lbHouse11;
	private Label lbHouse12;
	
	@PostConstruct
	public void createComposite(Composite parent) {
		secEvent = new Group(parent, SWT.NONE);
		secEvent.setText(Messages.getString("PersonView.Options")); //$NON-NLS-1$

		Label lb = new Label(secEvent, SWT.NONE);
		lb.setText(Messages.getString("PersonView.Name")); //$NON-NLS-1$
		txName = new Text(secEvent, SWT.BORDER);

		lb = new Label(secEvent, SWT.NONE);
		lb.setText(Messages.getString("PersonView.Surname")); //$NON-NLS-1$
		txSurname = new Text(secEvent, SWT.BORDER);
		
		lb = new Label(secEvent, SWT.CENTER);
		lb.setText(Messages.getString("PersonView.Gender")); //$NON-NLS-1$
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
		
		secPlace = new Group(secEvent, SWT.NONE);
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
		
		//////////////////////////////////////////////////

		secDescription = new Group(secEvent, SWT.NONE);
		secDescription.setText(Messages.getString("PersonView.Biography")); //$NON-NLS-1$
		secEvent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txBiography = new Text(secDescription, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);

		btCelebrity = new Button(secDescription, SWT.BORDER | SWT.CHECK);
		txCelebrity = new Text(secDescription, SWT.BORDER);
		txCelebrity.setEditable(false);

		//////////////////////////////////////////////////

		grCosmogram = new Group(parent, SWT.NONE);
		grCosmogram.setText("Cosmogram");
		cmpCosmogram = new CosmogramComposite(grCosmogram, SWT.NONE);

		grPlanets = new Group(grCosmogram, SWT.NONE);
		grPlanets.setText("Planets");
		lb = new Label(grPlanets, SWT.NONE);
		lb.setText("Sun");
		lbSun = new Label(grPlanets, SWT.NONE);
		lbSunStat = new Label(grPlanets, SWT.NONE);

		lb = new Label(grPlanets, SWT.NONE);
		lb.setText("Moon");
		lbMoon = new Label(grPlanets, SWT.NONE);
		lbMoonStat = new Label(grPlanets, SWT.NONE);

		lb = new Label(grPlanets, SWT.NONE);
		lb.setText("Rakhu");
		lbRakhu = new Label(grPlanets, SWT.NONE);
		lbRakhuStat = new Label(grPlanets, SWT.NONE);

		lb = new Label(grPlanets, SWT.NONE);
		lb.setText("Kethu");
		lbKethu = new Label(grPlanets, SWT.NONE);
		lbKethuStat = new Label(grPlanets, SWT.NONE);

		lb = new Label(grPlanets, SWT.NONE);
		lb.setText("Mercury");
		lbMercury = new Label(grPlanets, SWT.NONE);
		lbMercuryStat = new Label(grPlanets, SWT.NONE);

		lb = new Label(grPlanets, SWT.NONE);
		lb.setText("Venus");
		lbVenus = new Label(grPlanets, SWT.NONE);
		lbVenusStat = new Label(grPlanets, SWT.NONE);

		lb = new Label(grPlanets, SWT.NONE);
		lb.setText("Mars");
		lbMars = new Label(grPlanets, SWT.NONE);
		lbMarsStat = new Label(grPlanets, SWT.NONE);

		lb = new Label(grPlanets, SWT.NONE);
		lb.setText("Selena");
		lbSelena = new Label(grPlanets, SWT.NONE);
		lbSelenaStat = new Label(grPlanets, SWT.NONE);
		
		lb = new Label(grPlanets, SWT.NONE);
		lb.setText("Lilith");
		lbLilith = new Label(grPlanets, SWT.NONE);
		lbLilithStat = new Label(grPlanets, SWT.NONE);
		
		lb = new Label(grPlanets, SWT.NONE);
		lb.setText("Jupiter");
		lbJupiter = new Label(grPlanets, SWT.NONE);
		lbJupiterStat = new Label(grPlanets, SWT.NONE);
		
		lb = new Label(grPlanets, SWT.NONE);
		lb.setText("Saturn");
		lbSaturn = new Label(grPlanets, SWT.NONE);
		lbSaturnStat = new Label(grPlanets, SWT.NONE);

		lb = new Label(grPlanets, SWT.NONE);
		lb.setText("Chiron");
		lbChiron = new Label(grPlanets, SWT.NONE);
		lbChironStat = new Label(grPlanets, SWT.NONE);

		lb = new Label(grPlanets, SWT.NONE);
		lb.setText("Uranus");
		lbUranus = new Label(grPlanets, SWT.NONE);
		lbUranusStat = new Label(grPlanets, SWT.NONE);

		lb = new Label(grPlanets, SWT.NONE);
		lb.setText("Neptune");
		lbNeptune = new Label(grPlanets, SWT.NONE);
		lbNeptuneStat = new Label(grPlanets, SWT.NONE);

		lb = new Label(grPlanets, SWT.NONE);
		lb.setText("Pluto");
		lbPluto = new Label(grPlanets, SWT.NONE);
		lbPlutoStat = new Label(grPlanets, SWT.NONE);

		lb = new Label(grPlanets, SWT.NONE);
		lb.setText("Prozerpine");
		lbProzerpine = new Label(grPlanets, SWT.NONE);
		lbProzerpineStat = new Label(grPlanets, SWT.NONE);

		//////////////////////////////////////////////////

		grHouses = new Group(grCosmogram, SWT.NONE);
		grHouses.setText("Houses");
		lb = new Label(grHouses, SWT.NONE);
		lb.setText("ASC");
		lbHouse1 = new Label(grHouses, SWT.NONE);

		lb = new Label(grHouses, SWT.NONE);
		lb.setText("II");
		lbHouse2 = new Label(grHouses, SWT.NONE);

		lb = new Label(grHouses, SWT.NONE);
		lb.setText("III");
		lbHouse3 = new Label(grHouses, SWT.NONE);

		lb = new Label(grHouses, SWT.NONE);
		lb.setText("IC");
		lbHouse4 = new Label(grHouses, SWT.NONE);

		lb = new Label(grHouses, SWT.NONE);
		lb.setText("V");
		lbHouse5 = new Label(grHouses, SWT.NONE);
		
		lb = new Label(grHouses, SWT.NONE);
		lb.setText("VI");
		lbHouse6 = new Label(grHouses, SWT.NONE);
		
		lb = new Label(grHouses, SWT.NONE);
		lb.setText("DSC");
		lbHouse7 = new Label(grHouses, SWT.NONE);
		
		lb = new Label(grHouses, SWT.NONE);
		lb.setText("VIII");
		lbHouse8 = new Label(grHouses, SWT.NONE);

		lb = new Label(grHouses, SWT.NONE);
		lb.setText("IX");
		lbHouse9 = new Label(grHouses, SWT.NONE);

		lb = new Label(grHouses, SWT.NONE);
		lb.setText("MC");
		lbHouse10 = new Label(grHouses, SWT.NONE);

		lb = new Label(grHouses, SWT.NONE);
		lb.setText("XI");
		lbHouse11 = new Label(grHouses, SWT.NONE);

		lb = new Label(grHouses, SWT.NONE);
		lb.setText("XII");
		lbHouse12 = new Label(grHouses, SWT.NONE);

		super.createComposite(parent);
		try {
			setDefaultElement();
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Инициализация события по умолчанию
	 * @throws DataAccessException 
	 */
	private void setDefaultElement() throws DataAccessException {
		element = new Event();
		((Event)element).setZone(6.0); //TODO задавать через конфиг
		Place place = (Place)Place.getService().getEntityById(115L); //TODO задавать через конфиг
		((Event)element).setPlace(place);
		setElement(element, true);
	}

	@Focus
	public void setFocus() {
//		tableViewer.getTable().setFocus();
	}

	protected void prepareView(Composite parent) {
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(parent);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(parent);

		GridLayoutFactory.swtDefaults().numColumns(4).applyTo(secEvent);
		GridDataFactory.fillDefaults().hint(500, SWT.DEFAULT).grab(false, true).applyTo(secEvent);

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

		GridLayoutFactory.swtDefaults().numColumns(4).applyTo(secPlace);
		GridDataFactory.fillDefaults().span(4, 1).grab(true, false).applyTo(secPlace);
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

		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(secDescription);
		GridDataFactory.fillDefaults().span(4, 1).grab(true, true).applyTo(secDescription);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).
			span(2, 1).grab(true, true).applyTo(txBiography);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			grab(true, false).applyTo(txCelebrity);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).
			grab(false, false).applyTo(btCelebrity);

		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(grCosmogram);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(grCosmogram);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL).
			hint(514, 514).span(3, 1).grab(true, false).applyTo(cmpCosmogram);
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(grPlanets);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(grPlanets);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(grHouses);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(grHouses);
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
		txZone.setText(CalcUtil.formatNumber("###.##", place.getZone())); //$NON-NLS-1$
	}
	
	private String[] genders = {
		"",
		Messages.getString("PersonView.Male"),
		Messages.getString("PersonView.Female")};
	private String[] hands = {
		"",
		Messages.getString("PersonView.Right-handed"),
		Messages.getString("PersonView.Left-handed")};
	private String[] calcs = {
		"",
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

		try {
			setPlaces(Place.getService().getList());
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean checkViewValues() throws Exception {
		String msgBody = ""; //$NON-NLS-1$
//		if (txName.getText().length() == 0) 
//			msgBody += lbName.getText();
//		if (cvGender.getSelection().isEmpty()) 
//			msgBody += lbGender.getText();
		if (dtBirth.getSelection() == null) 
			msgBody += lbBirth.getText();
		if (dtBirth.getSelection() != null && dtDeath.getSelection() != null)
			if (!DateUtil.isDateRangeValid(dtBirth.getSelection(), dtDeath.getSelection()))
				DialogUtil.alertWarning(GUIutil.INVALID_DATE_RANGE);
//		if (places.get(txPlace.getText()) == null) {
//			DialogUtil.alertError(Messages.getString("EventView.PlaceIsWrong")); //$NON-NLS-1$
//			return false;
//		}
		if (!msgBody.equals("")) { //$NON-NLS-1$
			DialogUtil.alertWarning(GUIutil.SOME_FIELDS_NOT_FILLED + msgBody);
			return false;
		} else return true;
	}

	protected void viewToModel() throws Exception {
		if (!checkViewValues()) return;
		element = (element == null) ? new Event() : element;
		Event event = (Event)element;
		event.setName(txName.getText());
		event.setSurname(txSurname.getText());
		event.setFemale(cmbGender.getSelectionIndex() == 0);
		event.setRightHanded(cmbHand.getSelectionIndex() == 0);
		event.setRectification(cmbRectification.getSelectionIndex());
		event.setBirth(dtBirth.getSelection());
		event.setDeath(dtDeath.getSelection());
		event.setText(txBiography.getText());
		event.setDescription(txCelebrity.getText());
		event.setCelebrity(btCelebrity.getSelection());
		event.setPlace(places.get(txPlace.getText()));
		event.setZone(Double.parseDouble(txZone.getText()));
	}
	
	protected void modelToView() {		
		clearView();
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
	
	public void clearView() {
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
	
	public Object createElement() {
		return new Event();
	}

	/**
	 * Перерисовка космограммы
	 * @param params параметры перерисовки
	 */
	public void refreshCard(List<String> params) {
		if (params.size() < 1) return;
		Event event = (Event)element;
		cmpCosmogram.paint(event.getConfiguration(), params);
	}

	/**
	 * Список для быстрого поиска местностей по названию
	 */
	Map<String, Place> places = new HashMap<String, Place>();
	
	/**
	 * Инициализация местностей
	 * @param input список мест
	 */
	private void setPlaces(List<BaseEntity> input) {
		List<Reference> list = new ArrayList<Reference>();
		for (BaseEntity entity : input) {
			Place place = (Place)entity;
			list.add(place);
			places.put(place.getName(), place);
		}
	    ReferenceProposalProvider proposalProvider = new ReferenceProposalProvider(list);
	    proposalProvider.setFiltering(true);
	    ContentProposalAdapter adapter = new ContentProposalAdapter(
	        txPlace, new TextContentAdapter(),
	        proposalProvider, KeyStroke.getInstance(SWT.CTRL, 32), new char[] {' '});
	    adapter.setPropagateKeys(true);
	    adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
	    adapter.addContentProposalListener(new IContentProposalListener() {
			@Override
			public void proposalAccepted(IContentProposal proposal) {
				Place place = places.get(txPlace.getText());
				if (place != null) 
					setPlace(place);
			}
		});
	}

	@Override
	public void setElement(BaseEntity element, boolean refresh) {
		super.setElement(element, refresh);
		if (element != null && ((Event)element).getId() != null)
			setViewTitle(((Event)element).getFullName());
		else
			setViewTitle(Messages.getString("PersonView.New")); //$NON-NLS-1$
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
