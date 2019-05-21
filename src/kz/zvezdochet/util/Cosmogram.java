package kz.zvezdochet.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import kz.zvezdochet.bean.Aspect;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.util.CalcUtil;
import kz.zvezdochet.service.AspectService;

/**
 * Космограмма
 * @author Natalie Didenko
 *
 */
public class Cosmogram {
	public static int HEIGHT = 514;
	private int xcenter = 0;
	private int ycenter = 0;
	private final double INNER_CIRCLE = 120.0;
	
	private final Color HOUSE_COLOR = new Color(Display.getDefault(), new RGB(153, 0, 0));
	private final Color HOUSEPART_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
	
	private Configuration conf;
	private Configuration conf2;
	private Map<String, Object> params;

	/**
	 * Прорисовка космограммы
	 * @param conf расчётная конфигурация события
	 * @param conf2 расчётная конфигурация связанного события
	 * @param params массив параметров
	 * @param gc графический контекст
	 * @todo если параметры не заданы, брать все по умолчанию
	 */
	public Cosmogram(Configuration conf, Configuration conf2, Map<String, Object> params, GC gc) {
		this.conf = conf;
		this.conf2 = conf2;
		this.params = params;
		paintCard(gc);
	}

	/**
	 * Прорисовка космограммы
	 * @param gc графическая система
	 */
	private void paintCard(GC gc) {
		xcenter = ycenter = 257;
   	    Image image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/card.png").createImage();
		gc.drawImage(image, 52, 53);
		if (conf != null) {
			if (conf.getHouses() != null && conf.getHouses().size() > 0)
				drawHouses(conf, gc, true);
		    if (conf.getPlanets() != null && conf.getPlanets().size() > 0)
				try {
					drawPlanets(conf, gc, 135);
				} catch (DataAccessException e) {
					e.printStackTrace();
				}
		}
		if (conf2 != null) {
			if (conf2.getHouses() != null && conf2.getHouses().size() > 0)
				drawHouses(conf2, gc, false);
		    if (conf2.getPlanets() != null && conf2.getPlanets().size() > 0)
				try {
					drawPlanets(conf2, gc, 160);
				} catch (DataAccessException e) {
					e.printStackTrace();
				}
		}
		try {
			drawAspects(gc);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	    gc.dispose(); 
	    image.dispose();
	}

	/**
	 * Вычисление координаты x небесной точки
	 * @param radius радиус окружности
	 * @param gradus градус небесной точки
	 * @return координата x
	 */
	private double getXPoint(double radius, double gradus) {
		int minutes = (int)Math.round((gradus % 1) * 100);
		return radius * Math.cos((gradus + minutes / 60) * Math.PI / 180); //RoundTo -2
	}
		
	/**
	 * Вычисление координаты y небесной точки
	 * @param radius радиус окружности
	 * @param gradus градус небесной точки
	 * @return координата y
	 */
	private double getYPoint(double radius, double gradus) {
		int minutes = (int)Math.round((gradus % 1) * 100);
		return radius * Math.sin((gradus + minutes / 60) * Math.PI / 180); //RoundTo -2
	}

	/**
	 * Прорисовка линии
	 * @param gc графическая система
	 * @param color цвет
	 * @param penStyle стиль пера
	 * @param outer внешний радиус
	 * @param inner внутренний радиус
	 * @param gradus1 градус начальной точки
	 * @param gradus2 градус конечной точки
	 * @param lineStyle стиль начертания линии
	 */
	private void drawLine(GC gc, Color color, double penStyle, double outer, 
			double inner,	double gradus1, double gradus2, int lineStyle) {
		gc.setForeground(color);
		gc.setLineStyle(lineStyle);
		gc.drawLine((int)Math.round(getXPoint(outer, gradus1)) + xcenter,
					(int)Math.round(getYPoint(outer, gradus1)) + ycenter,
					(int)Math.round(getXPoint(inner, gradus2)) + xcenter,
					(int)Math.round(getYPoint(inner, gradus2)) + ycenter);
	}

	/**
	 * Прорисовка астрологических домов
	 * @param conf конфигурация домов
	 * @param gc графическая система
	 * @param primary true - первый уровень домов
	 */
	private void drawHouses(Configuration conf, GC gc, boolean primary) {
		if (!conf.getEvent().isHousable()) return;
		Iterator<Model> i = conf.getHouses().iterator();
		while (i.hasNext()) {
			House h = (House)i.next();
			if (h.isMain()) {
	     		drawLine(gc, primary ? HOUSE_COLOR : HOUSEPART_COLOR, 0, 210, INNER_CIRCLE, h.getLongitude(), h.getLongitude(), SWT.LINE_SOLID);
				drawHouseName(h.getDesignation(), h.getLongitude(), gc, primary);
			}
		}
		drawHouseParts(conf, gc, primary);
	}

	/**
	 * Прорисовка названий астрологических домов
	 * @param name имя дома
	 * @param value градус дома
	 * @param gc графическая система
	 * @param primary true - первый уровень домов
	 */
	private void drawHouseName(String name, double value, GC gc, boolean primary) {
		gc.setForeground(primary ? HOUSE_COLOR : HOUSEPART_COLOR);
		gc.drawString(name, CalcUtil.trunc(getXPoint(230, value)) + xcenter - 5,
			CalcUtil.trunc(getYPoint(primary ? 230 : 210, value)) + ycenter);
	}

	/**
	 * Прорисовка триплицетов астрологических домов
	 * @param conf конфигурация домов
	 * @param gc графическая система
	 * @param primary true - первый уровень домов
	 */
	private void drawHouseParts(Configuration conf, GC gc, boolean primary) {
		Iterator<Model> i = conf.getHouses().iterator();
		while (i.hasNext()) {
			House h = (House)i.next();
			if (!h.isMain()) {
	     		drawLine(gc, primary ? HOUSE_COLOR : HOUSEPART_COLOR, 0, 140.0, INNER_CIRCLE, h.getLongitude(), h.getLongitude(), SWT.LINE_SOLID);
			}
		}
	}

	/**
	 * Прорисовка планет
	 * @param configuration расчётная конфигурация
	 * @param gc графическая система
	 * @param radius радиус окружности
	 * @throws DataAccessException
	 */
	private void drawPlanets(Configuration configuration, GC gc, int radius) throws DataAccessException {
		Iterator<Planet> i = configuration.getPlanets().values().iterator();
		while (i.hasNext()) {
			Planet p = i.next();
			int x = CalcUtil.trunc(getXPoint(radius, p.getLongitude())) + xcenter - 5;
			int y = CalcUtil.trunc(getYPoint(radius, p.getLongitude())) + ycenter - 5;
			//String tooltip = p.getName() + " (" + Utils.replace(String.valueOf(p.getCoord()), ".", "\u00b0") + "\u2032)";
			gc.drawImage(p.getImage(), x, y);
		}
	}

	/**
	 * Прорисовка аспектов планет.
	 * Если строится одиночная карта, используем аспекты самого события;
	 * в противном случае отображаются аспкты планет двух событий друг к другу
	 * @param gc графическая система
	 * @throws DataAccessException
	 */
	private void drawAspects(GC gc) throws DataAccessException {
		if (null == conf || null == conf.getPlanets()) return;
		Iterator<Planet> i = conf.getPlanets().values().iterator();
		boolean single = (null == conf2 || null == conf2.getPlanets());
		Map<Long, Planet> planets = single ? conf.getPlanets() : conf2.getPlanets();
		while (i.hasNext()) {
			Planet p = i.next();
			if (p.getCode().equals("Moon") && !conf.getEvent().isHousable())
				continue;
			Iterator<Planet> j = planets.values().iterator();
			while (j.hasNext()) {
				Planet p2 = j.next();
				if (p2.getCode().equals("Moon") && !conf.getEvent().isHousable())
					continue;
				if (single && p.getNumber() > p2.getNumber()) continue;
				getAspect(Math.abs(p.getLongitude()), Math.abs(p2.getLongitude()), gc);
			}
		}
	}

	/**
	 * Прорисовка аспекта
	 * @param color цвет
	 * @param penStyle стиль пера
	 * @param a координата первой точки
	 * @param b координата второй точки
	 * @param gc графическая система
	 * @param lineStyle стиль начертания линии
	 */
	private void drawAspect(Color color, double penStyle, double a, double b, GC gc, int lineStyle) {
		drawLine(gc, color, penStyle, 120.0, 120.0, a, b, lineStyle);
	}

	/**
	 * Определение аспекта между небесными точками
	 * @param one градус первой точки
	 * @param two градус второй точки
	 * @param gc графическая система
	 * @throws DataAccessException
	 */
	private void getAspect(double one, double two, GC gc) throws DataAccessException {
		double res = CalcUtil.getDifference(one, two);
		List<Model> aspects = new AspectService().getList();
		Iterator<Model> i = aspects.iterator();
		while (i.hasNext()) {
			Aspect a = (Aspect)i.next();
			if (a.isAspect(res)) {
				if (params != null) {
					boolean exact = params.get("exact") != null;
					if (exact && !a.isExact(res))
						continue;
					if (params != null && params.get("aspects") != null) {
						@SuppressWarnings("unchecked")
						List<String> aparams = (List<String>)params.get("aspects");
						if (aparams.size() > 0 && !aparams.contains(a.getType().getCode()))
							continue;
					}
				}
				drawAspect(a.getType().getColor(), 0f, one, two, gc, 
						getLineStyle(a.getType().getProtraction()));
			}
		}
	}

	/**
	 * Определяем стиля начертания аспекта
	 * @param code код типа аспекта
	 * @return стиль начертания линии
	 */
	private int getLineStyle(String code) {
		switch (code) {
			case "SOLID": return SWT.LINE_SOLID;
			case "DASH": return SWT.LINE_DASH;
			case "DOT": return SWT.LINE_DOT;
			case "DASHDOT": return SWT.LINE_DASHDOT;
			case "DASHDOTDOT": return SWT.LINE_DASHDOTDOT;
			default: return SWT.LINE_SOLID;
		}
	}
} 
