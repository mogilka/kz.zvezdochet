package kz.zvezdochet.parts;

import java.util.Iterator;
import java.util.List;

import kz.zvezdochet.bean.Aspect;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.util.CalcUtil;
import kz.zvezdochet.service.AspectService;
import kz.zvezdochet.util.Configuration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Виджет космограммы
 * @author Nataly Didenko
 *
 */
public class CosmogramComposite extends Composite { 
	private int xcenter = 0;
	private int ycenter = 0;
	private final double INNER_CIRCLE = 120.0;
	
	private final Color HOUSE_COLOR = new Color(Display.getDefault(),  new RGB(153, 0, 0));
	private final Color HOUSEPART_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
	
	private Configuration conf;
	private List<String> params;
	
	public CosmogramComposite(Composite parent, int style) {
	    super(parent, style);
//	    setBackground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION));TODO
	    setSize(514, 514);
		xcenter = ycenter = getClientArea().width / 2;
		addPaintListener(new PaintListener() {
	        public void paintControl(final PaintEvent e) {
        		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
        			@Override
        			public void run() {
    	        		paintCard(e.gc);
        			}
        		});
	        }
		});
	}

	/**
	 * Прорисовка космограммы
	 * @param conf расчётная конфигурация события
	 * @param params массив параметров
	 * @todo если параметры не заданы, брать все по умолчанию
	 */
	public void paint(Configuration conf, List<String> params) {
		this.conf = conf;
		this.params = params;
		redraw();
	}

	/**
	 * Прорисовка космограммы
	 * @param conf расчётная конфигурация события
	 * @param params массив параметров
	 */
	private void paintCard(GC gc) {
   	    Image image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/card.png").createImage(); 
		gc.drawImage(image, 52, 53);
		if (conf != null) {
			if (conf.getHouses() != null && conf.getHouses().size() > 0) 
				drawHouses(conf, gc);
		    if (conf.getPlanets() != null && conf.getPlanets().size() > 0)
				try {
					drawPlanets(conf, gc);
				} catch (DataAccessException e) {
					e.printStackTrace();
				}
		}
	    gc.dispose(); 
	    image.dispose();
	}

	private double getXPoint(double radius, double gradus) {
		int minutes = (int)Math.round((gradus % 1) * 100);
		return radius * Math.cos((gradus + minutes / 60) * Math.PI / 180); //RoundTo -2
	}
		
	private double getYPoint(double radius, double gradus) {
		int minutes = (int)Math.round((gradus % 1) * 100);
		return radius * Math.sin((gradus + minutes / 60) * Math.PI / 180); //RoundTo -2
	}

	private void drawLine(GC gc, Color color, double penStyle, double outer, 
			double inner,	double gradus1, double gradus2, int lineStyle) {
		gc.setForeground(color);
		gc.setLineStyle(lineStyle);
		gc.drawLine((int)Math.round(getXPoint(outer, gradus1)) + xcenter,
					(int)Math.round(getYPoint(outer, gradus1)) + ycenter,
					(int)Math.round(getXPoint(inner, gradus2)) + xcenter,
					(int)Math.round(getYPoint(inner, gradus2)) + ycenter);
	}
	
	private void drawHouses(Configuration conf, GC gc) {
		Iterator<Model> i = conf.getHouses().iterator();
		while (i.hasNext()) {
			House h = (House)i.next();
			if (h.isMain()) {
	     		drawLine(gc, HOUSE_COLOR, 0, 210, INNER_CIRCLE, h.getCoord(), h.getCoord(), SWT.LINE_SOLID);
				drawHouseName(h.getDesignation(), h.getCoord(), gc);
			}
		}
		drawHouseParts(conf, gc);
	}
	
	private void drawHouseName(String name, double value, GC gc) {
		gc.setForeground(HOUSE_COLOR);
		gc.drawString(name, CalcUtil.trunc(getXPoint(230, value)) + xcenter - 5,
						CalcUtil.trunc(getYPoint(230, value)) + ycenter);
	}

	private void drawHouseParts(Configuration conf, GC gc) {
		Iterator<Model> i = conf.getHouses().iterator();
		while (i.hasNext()) {
			House h = (House)i.next();
			if (!h.isMain()) {
	     		drawLine(gc, HOUSEPART_COLOR, 0, 140.0, INNER_CIRCLE, h.getCoord(), h.getCoord(), SWT.LINE_SOLID);
			}
		}
	}

	private void drawPlanets(Configuration conf, GC gc) throws DataAccessException {
		Iterator<Model> i = conf.getPlanets().iterator();
		while (i.hasNext()) {
			Planet p = (Planet)i.next();
			int x = CalcUtil.trunc(getXPoint(135, p.getCoord())) + xcenter - 5;
			int y = CalcUtil.trunc(getYPoint(135, p.getCoord())) + ycenter - 5;
			//String tooltip = p.getName() + " (" + Utils.replace(String.valueOf(p.getCoord()), ".", "\u00b0") + "\u2032)";
			Image image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/planet/" + 
					p.getCode() + ".png").createImage();
			gc.drawImage(image, x, y);
			
			Iterator<Model> j = conf.getPlanets().iterator();
			while (j.hasNext()) {
				Planet p2 = (Planet)j.next();
				if (((!p.getCode().equals("Rakhu")) && (!p2.getCode().equals("Kethu"))) &&
						((!p.getCode().equals("Kethu")) && (!p2.getCode().equals("Rakhu")))) 
						getAspect(Math.abs(CalcUtil.degToDec(p.getCoord())),
									Math.abs(CalcUtil.degToDec(p2.getCoord())), gc);
			}
		}
	}
	
	private void drawAspect(Color color, double penStyle, double a, double b, GC gc, int lineStyle) {
		drawLine(gc, color, penStyle, 120.0, 120.0, a, b, lineStyle);
	}

	private void getAspect(double one, double two, GC gc) throws DataAccessException {
		double res = CalcUtil.getDifference(one, two);
		List<Model> aspects = new AspectService().getList();
		Iterator<Model> i = aspects.iterator();
		while (i.hasNext()) {
			Aspect a = (Aspect)i.next();
			if (params.contains(a.getType().getCode()) && a.isAspect(res))
				drawAspect(a.getType().getColor(), 0f, one, two, gc, 
						getAspectProtraction(a.getType().getProtraction().getCode()));
		}
	}
	
	private int getAspectProtraction(String code) {
		if (code.contains("SOLID")) return SWT.LINE_SOLID;
		else if (code.contains("DASHDOTDOT")) return SWT.LINE_DASHDOTDOT;
		else if (code.contains("DASHDOT")) return SWT.LINE_DASHDOT;
		else if (code.contains("DASH")) return SWT.LINE_DASH;
		else if (code.contains("DOT")) return SWT.LINE_DOT;
		return SWT.LINE_SOLID;
	}
} 
