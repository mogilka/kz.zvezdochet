package kz.zvezdochet.part;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.EventConfiguration;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.handler.Handler;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.ui.decoration.InfoDecoration;
import kz.zvezdochet.core.ui.listener.ListSelectionListener;
import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.core.ui.util.GUIutil;
import kz.zvezdochet.core.ui.view.ModelLabelProvider;
import kz.zvezdochet.core.ui.view.ModelListView;
import kz.zvezdochet.core.ui.view.View;
import kz.zvezdochet.service.EventConfigurationService;

/**
 * Список конфигураций аспектов персоны
 * @author Natalie Didenko
 */
public class ConfPart extends ModelListView {
	@Inject
	public ConfPart() {}

	private Event event;
	private EventConfiguration conf;

	private Label lbName;
	private Text txName;
	private Text txPlace;
	private Text txLatitude;
	private Text txLongitude;
	private Text txZone;
	private Text txGreenwich;
	private Label lbBirth;
	private CDateTime dtBirth;
	private Text txDescr;
	
	@PostConstruct @Override
	public View create(Composite parent) {
		return super.create(parent);
	}

	@Override
	protected void init(Composite parent) {
		parent.setLayout(new FillLayout());

		sashForm = new SashForm(parent, SWT.HORIZONTAL);
		Group gr = new Group(sashForm, SWT.NONE);
		initFilter(gr);
		tableViewer = new TableViewer(gr, SWT.BORDER | SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		addColumns();

		group = new Group(sashForm, SWT.NONE);
	}

	@Override
	protected String[] initTableColumns() {
		return new String[] {
			"Имя",
			"Дата" };
	}

	@Override
	protected IBaseLabelProvider getLabelProvider() {
		return new ModelLabelProvider() {
			@Override
			public String getColumnText(Object element, int columnIndex) {
				EventConfiguration conf = (EventConfiguration)element;
				switch (columnIndex) {
					case 0: return conf.getBase();
					case 1: return conf.getVertex();
				}
				return null;
			}
		};
	}

	/**
	 * Инициализация персоны
	 * @param event персона
	 */
	public void setEvent(Event event) {
		try {
			this.event = event;
			setData(new EventConfigurationService().findByEvent(event.getId()));
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initFilter(Composite parent) {
		Group secEvent = new Group(parent, SWT.NONE);
		secEvent.setText("Новая фигура");

		lbName = new Label(secEvent, SWT.NONE);
		lbName.setText(Messages.getString("PersonView.Name")); //$NON-NLS-1$
		txName = new Text(secEvent, SWT.BORDER);

		lbBirth = new Label(secEvent, SWT.NONE);
		lbBirth.setText(Messages.getString("PersonView.BirthDate")); //$NON-NLS-1$
		dtBirth = new CDateTime(secEvent, CDT.BORDER | CDT.COMPACT | CDT.DROP_DOWN | CDT.DATE_LONG | CDT.TIME_MEDIUM);
		dtBirth.setNullText(""); //$NON-NLS-1$
		dtBirth.setSelection(new Date());

		Group secPlace = new Group(secEvent, SWT.NONE);
		secPlace.setText(Messages.getString("PersonView.Place")); //$NON-NLS-1$
		txPlace = new Text(secPlace, SWT.BORDER);
		new InfoDecoration(txPlace, SWT.TOP | SWT.LEFT);

		Label lb = new Label(secPlace, SWT.NONE);
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

		txDescr = new Text(secEvent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);

		Button bt = new Button(secEvent, SWT.NONE);
		bt.setText("Добавить");
		bt.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				syncModel(Handler.MODE_SAVE);
				addModel(conf);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		GridLayoutFactory.swtDefaults().numColumns(4).applyTo(secPlace);
		GridDataFactory.fillDefaults().span(4, 1).grab(true, false).applyTo(secPlace);
		secEvent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(secEvent);
		GridDataFactory.fillDefaults().span(2, 1).align(SWT.FILL, SWT.CENTER).
			grab(true, false).applyTo(txName);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).
			grab(true, false).applyTo(dtBirth);
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
			grab(false, false).applyTo(bt);
		GridDataFactory.fillDefaults().span(3, 1).align(SWT.FILL, SWT.CENTER).
			hint(SWT.DEFAULT, 48).grab(true, false).applyTo(txDescr);
	}

	@Override
	public void addModel(Model model) {
		try {
			super.addModel(model);
			EventConfiguration econf = (EventConfiguration)model;
//			econf.setEventid(model.getId());
			new EventConfigurationService().save(econf);
			reset();
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Синхронизация модели с представлением
	 * @param mode режим отображения транзитов
	 */
	private void syncModel(int mode) {
		try {
			if (!check(mode)) return;
			conf = new EventConfiguration();
//			conf.setEventid(event.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Проверка правильности атрибутов события
	 * @return true|false параметры заполнены верно|не верно
	 * @throws Exception
	 */
	@Override
	public boolean check(int mode) throws Exception {
		StringBuffer msgBody = new StringBuffer();
		if (null == dtBirth.getSelection())
			msgBody.append(lbBirth.getText());
		if (Handler.MODE_SAVE == mode) {
			if (txName.getText().length() == 0) 
				msgBody.append(lbName.getText());
		}
		if (msgBody.length() > 0) {
			DialogUtil.alertWarning(GUIutil.SOME_FIELDS_NOT_FILLED + msgBody);
			return false;
		} else return true;
	}

	@Override
	public void reset() {
		txName.setText(""); //$NON-NLS-1$
		txPlace.setText(""); //$NON-NLS-1$
		txLatitude.setText(""); //$NON-NLS-1$
		txLongitude.setText(""); //$NON-NLS-1$
		txZone.setText(""); //$NON-NLS-1$
		txGreenwich.setText(""); //$NON-NLS-1$
		dtBirth.setSelection(new Date());
		txDescr.setText(""); //$NON-NLS-1$
	}

	/**
	 * Поиск столбцов таблицы транзитов
	 * @return массив наименований столбцов
	 */
	public static String[] getTableColumns() {
		return new String[] {
			"Возраст",
			"Транзитная точка",
			"Аспект",
			"Натальная точка",
			"Направление",
			"Величина аспекта",
			"Знак Зодиака",
			"Дом",
			"Описание"
		};
	}

	@Override
	public ListSelectionListener getSelectionListener() {
		return new ListSelectionListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (!event.getSelection().isEmpty()) {
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					if (selection.getFirstElement() != null)
						conf = (EventConfiguration)selection.getFirstElement();
				}
			}
		};
	}

	@Override
	public EventConfiguration getModel() {
		if (null == conf)
			syncModel(0);
		return conf;
	}

	public void setModel(EventConfiguration econf) {
		conf = econf;
	}

	public void resetConf() {
		conf = null;
	}

	@Override
	public Model createModel() {
		return null;
	}
}
