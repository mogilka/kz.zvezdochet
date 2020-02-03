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

	@Inject
	public AspectPart() {}
	
	@PostConstruct @Override
	public View create(Composite parent) {
		return super.create(parent);
	}

	@Override
	protected void init(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new FormLayout());
		initFilter(parent);

		tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tableViewer2 = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		Table table2 = tableViewer2.getTable();
		table2.setHeaderVisible(true);
		table2.setLinesVisible(true);

		addColumns();
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
				tableColumn.setText(CalcUtil.roundTo(planet.getLongitude(), 1) + "");
				tableColumn.setImage(planet.getImage());
				tableColumn.setToolTipText(planet.getName());
			}
			Table table2 = tableViewer2.getTable();
			tableColumn = new TableColumn(table2, SWT.NONE);
			Collection<House> houses = event.getHouses().values();
			for (House house : houses) {
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
		Table table = tableViewer2.getTable();
		if (table.getColumns() != null)
			for (TableColumn column : table.getColumns())
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
			Table table = tableViewer2.getTable();
			for (int i = 0; i < table.getColumnCount(); i++)
				table.getColumn(i).pack();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			showBusy(false);
		}
	}

	@Override
	protected void arrange(Composite parent) {
		GridLayoutFactory.swtDefaults().applyTo(parent);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(container);
		GridLayoutFactory.swtDefaults().applyTo(container);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(tableViewer.getTable());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(tableViewer2.getTable());
	}

	@Override
	public void refresh() {
		super.refresh();
		tableViewer2.refresh();
	}

	@Override
	public void reset() {
		super.reset();
		tableViewer2.getTable().removeAll();
	}
}
