package kz.zvezdochet.part;

import java.util.Map;

import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import kz.zvezdochet.bean.Event;
import kz.zvezdochet.util.Cosmogram;

/**
 * Виджет космограммы
 * @author Natalie Didenko
 *
 */
public class CosmogramComposite extends Composite { 
	private Event event;
	private Event event2;
	private Map<String, Object> params;
	
	public CosmogramComposite(Composite parent, int style) {
	    super(parent, style);
//	    setBackground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION));TODO
	    setSize(Cosmogram.HEIGHT, Cosmogram.HEIGHT);
	    //Canvas canvas = new Canvas(this, SWT.NO_REDRAW_RESIZE | SWT.NO_BACKGROUND);
		addPaintListener(new PaintListener() {
	        public void paintControl(final PaintEvent e) {
        		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
        			@Override
        			public void run() {
        				new Cosmogram(event, event2, params, e.gc, true);
        			}
        		});
	        }
		});
	}

	/**
	 * Прорисовка космограммы
	 * @param event событие
	 * @param event2 связанное событие
	 * @param params массив параметров
	 * @todo если параметры не заданы, брать все по умолчанию
	 */
	public void paint(Event event, Event event2, Map<String, Object> params) {
		this.event = event;
		this.event2 = event2;
		this.params = params;
		redraw();
	}
} 
