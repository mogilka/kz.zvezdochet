package kz.zvezdochet.part;

import java.util.List;

import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import kz.zvezdochet.util.Configuration;
import kz.zvezdochet.util.Cosmogram;

/**
 * Виджет космограммы
 * @author Nataly Didenko
 *
 */
public class CosmogramComposite extends Composite { 
	private Configuration conf;
	private Configuration conf2;
	private List<String> params;
	
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
        				new Cosmogram(conf, conf2, params, e.gc);
        			}
        		});
	        }
		});
	}

	/**
	 * Прорисовка космограммы
	 * @param conf расчётная конфигурация события
	 * @param conf2 расчётная конфигурация связанного события
	 * @param params массив параметров
	 * @todo если параметры не заданы, брать все по умолчанию
	 */
	public void paint(Configuration conf, Configuration conf2, List<String> params) {
		this.conf = conf;
		this.conf2 = conf2;
		this.params = params;
		redraw();
	}
} 
