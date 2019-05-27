package kz.zvezdochet.part;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.ui.listener.ListSelectionListener;
import kz.zvezdochet.core.ui.view.ListView;
import kz.zvezdochet.core.ui.view.View;
import kz.zvezdochet.core.util.CalcUtil;
import kz.zvezdochet.provider.AspectLabelProvider;

/**
 * Таблица аспектов
 * @author Natalie Didenko
 *
 */
public class AspectPart extends ListView {
	/**
	 * Конфигурация события
	 */
	protected Event event;

	private Table table2;
	private TableViewer tableViewer2;

	@Inject
	public AspectPart() {}
	
	@PostConstruct @Override
	public View create(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new FormLayout());
		initFilter(parent);

		tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tableViewer2 = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		table2 = tableViewer2.getTable();
		table2.setHeaderVisible(true);
		table2.setLinesVisible(true);

		addColumns();
		init(parent);
		try {
			initControls();
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(getLabelProvider());

		tableViewer2.setContentProvider(new ArrayContentProvider());
		tableViewer2.setLabelProvider(getLabelProvider());

		ListSelectionListener listener = getSelectionListener();
		tableViewer.addSelectionChangedListener(listener);
		tableViewer.addDoubleClickListener(listener);

		tableViewer2.addSelectionChangedListener(listener);
		tableViewer2.addDoubleClickListener(listener);

		initTable();
		return null;
	}

	/**
	 * Инициализация события
	 * @param event событиy
	 */
	public void setEvent(Event event) {
		this.event = event;
		addColumns();
	}

	@Override
	protected void addColumns() {
		removeColumns();
		if (event != null) {
			TableColumn tableColumn = new TableColumn(table, SWT.NONE);
			Collection<Planet> planets = event.getPlanets().values();
			for (Planet planet : planets) {
				tableColumn = new TableColumn(table, SWT.NONE);
				tableColumn.setText(CalcUtil.roundTo(planet.getLongitude(), 1) + "");
				tableColumn.setImage(planet.getImage());
				tableColumn.setToolTipText(planet.getName());
			}
			tableColumn = new TableColumn(table2, SWT.NONE);
			for (int i = 0; i < event.getHouses().size(); i++) {
				House house = (House)event.getHouses().get(i);
				tableColumn = new TableColumn(table2, SWT.NONE);
				tableColumn.setText(CalcUtil.roundTo(house.getLongitude(), 1) + "");
				tableColumn.setText(house.getCode());
				tableColumn.setToolTipText(house.getName());
			}
		}
	}

	@Override
	protected IBaseLabelProvider getLabelProvider() {
		return new AspectLabelProvider();
	}

	@Override
	protected String[] initTableColumns() {
		return null;
	}

	@Override
	public boolean check(int mode) throws Exception {
		return false;
	}

	@Override
	protected void removeColumns() {
		super.removeColumns();
		if (table2.getColumns() != null)
			for (TableColumn column : table2.getColumns())
				column.dispose();
	}

	/**
	 * Инициализация содержимого таблицы домов
	 * @param data массив данных
	 */
	public void setDatah(Object data) {
		try {
			showBusy(true);
			this.datah = data;
			initTable();	
		} finally {
			showBusy(false);
		}
	}

	/**
	 * Массив данных таблицы домов
	 */
	protected Object datah;

	@Override
	protected void initTable() {
		super.initTable();
		try {
			showBusy(true);
			if (datah != null)
				tableViewer2.setInput(datah);
			for (int i = 0; i < table2.getColumnCount(); i++)
				table2.getColumn(i).pack();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			showBusy(false);
		}
	}

	@Override
	protected void init(Composite parent) {
		GridLayoutFactory.swtDefaults().applyTo(parent);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(container);
		GridLayoutFactory.swtDefaults().applyTo(container);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(table);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(table2);
	}

	@Override
	public void refresh() {
		super.refresh();
		tableViewer2.refresh();
	}

	@Override
	public void reset() {
		super.reset();
		table2.removeAll();
	}
}
