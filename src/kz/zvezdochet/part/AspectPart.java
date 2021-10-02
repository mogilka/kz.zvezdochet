package kz.zvezdochet.part;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
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

	private TableViewer tableViewer2;
	private TableViewer tableViewer3;

	@Inject
	public AspectPart() {}
	
	@PostConstruct @Override
	public View create(Composite parent) {
		return super.create(parent);
	}

	@Override
	protected void init(Composite parent) {
		parent.setLayout(new FormLayout());
		initFilter(parent);

		sashForm = new SashForm(parent, SWT.VERTICAL);
		tableViewer = new TableViewer(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tableViewer2 = new TableViewer(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
		Table table2 = tableViewer2.getTable();
		table2.setHeaderVisible(true);
		table2.setLinesVisible(true);

		tableViewer3 = new TableViewer(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
		Table table3 = tableViewer3.getTable();
		table3.setHeaderVisible(true);
		table3.setLinesVisible(true);

		addColumns();
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(getLabelProvider());
		tableViewer2.setContentProvider(new ArrayContentProvider());
		tableViewer2.setLabelProvider(getLabelProvider());
		tableViewer3.setContentProvider(new ArrayContentProvider());
		tableViewer3.setLabelProvider(getLabelProvider());

		ListSelectionListener listener = getSelectionListener();
		tableViewer.addSelectionChangedListener(listener);
		tableViewer.addDoubleClickListener(listener);
		tableViewer2.addSelectionChangedListener(listener);
		tableViewer2.addDoubleClickListener(listener);
		tableViewer3.addSelectionChangedListener(listener);
		tableViewer3.addDoubleClickListener(listener);
		initTable();
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
			Table table = tableViewer.getTable();
			TableColumn tableColumn = new TableColumn(table, SWT.NONE);
			Collection<Planet> planets = event.getPlanets().values();
			for (Planet planet : planets) {
				tableColumn = new TableColumn(table, SWT.NONE);
				tableColumn.setText(planet.getSymbol() + " " + CalcUtil.roundTo(planet.getLongitude(), 1) + "");
				tableColumn.setToolTipText(planet.getName());
			}
			Table table2 = tableViewer2.getTable();
			Table table3 = tableViewer3.getTable();
			TableColumn tableColumn2 = new TableColumn(table2, SWT.NONE);
			TableColumn tableColumn3 = new TableColumn(table3, SWT.NONE);
			Collection<House> houses = event.getHouses().values();
			for (House house : houses) {
				tableColumn2 = new TableColumn(table2, SWT.NONE);
				String title = CalcUtil.roundTo(house.getLongitude(), 1) + "";
				tableColumn.setText(title);
				tableColumn2.setText(house.getCode());
				tableColumn2.setToolTipText(house.getName());

				tableColumn3 = new TableColumn(table3, SWT.NONE);
				tableColumn3.setText(title);
				tableColumn3.setText(house.getCode());
				tableColumn3.setToolTipText(house.getName());
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
	protected void removeColumns() {
		super.removeColumns();
		Table table = tableViewer2.getTable();
		if (table.getColumns() != null)
			for (TableColumn column : table.getColumns())
				column.dispose();

		table = tableViewer3.getTable();
		if (table.getColumns() != null)
			for (TableColumn column : table.getColumns())
				column.dispose();
	}

	/**
	 * Инициализация содержимого таблицы домов
	 * @param data массив данных
	 */
	public void setDatap2h(Object data) {
		try {
			showBusy(true);
			this.datap2h = data;
			initTable2();	
		} finally {
			showBusy(false);
		}
	}

	/**
	 * Массив данных таблицы домов
	 */
	protected Object datap2h;
	protected Object datah2h;

	@Override
	protected void arrange(Composite parent) {
		super.arrange(parent);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(tableViewer2.getTable());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(tableViewer3.getTable());
	}

	@Override
	public void refresh() {
		super.refresh();
		tableViewer2.refresh();
		tableViewer3.refresh();
	}

	@Override
	public void reset() {
		super.reset();
		tableViewer2.getTable().removeAll();
		tableViewer3.getTable().removeAll();
	}

	/**
	 * Инициализация содержимого таблицы куспидов
	 * @param data массив данных
	 */
	public void setDatah2h(Object data) {
		try {
			showBusy(true);
			this.datah2h = data;
			initTable3();	
		} finally {
			showBusy(false);
		}
	}

	private void initTable2() {
		if (!event.isHousable())
			return;
		try {
			showBusy(true);
			if (datap2h != null)
				tableViewer2.setInput(datap2h);
			Table table = tableViewer2.getTable();
			for (int i = 0; i < table.getColumnCount(); i++)
				table.getColumn(i).pack();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			showBusy(false);
		}
	}

	private void initTable3() {
		if (!event.isHousable())
			return;
		try {
			showBusy(true);
			if (datah2h != null)
				tableViewer3.setInput(datah2h);
			Table table = tableViewer3.getTable();
			for (int i = 0; i < table.getColumnCount(); i++)
				table.getColumn(i).pack();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			showBusy(false);
		}
	}
}
