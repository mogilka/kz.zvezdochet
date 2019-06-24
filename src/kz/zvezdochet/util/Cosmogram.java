package kz.zvezdochet.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import kz.zvezdochet.bean.Aspect;
import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.SkyPointAspect;
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
	
	private Event event;
	private Event event2;
	private Map<String, Object> params;

	/**
	 * Прорисовка космограммы
	 * @param event событие
	 * @param event2 связанное событие
	 * @param params массив параметров
	 * @param gc графический контекст
	 * @todo если параметры не заданы, брать все по умолчанию
	 */
	public Cosmogram(Event event, Event event2, Map<String, Object> params, GC gc) {
		this.event = event;
		this.event2 = event2;
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
		if (event != null) {
			if (event.getHouses() != null && event.getHouses().size() > 0)
				drawHouses(event, gc, true);
		    if (event.getPlanets() != null && event.getPlanets().size() > 0)
				try {
					drawPlanets(event, gc, 135);
				} catch (DataAccessException e) {
					e.printStackTrace();
				}
		}
		if (event2 != null) {
			if (event2.getHouses() != null && event2.getHouses().size() > 0)
				drawHouses(event2, gc, false);
		    if (event2.getPlanets() != null && event2.getPlanets().size() > 0)
				try {
					drawPlanets(event2, gc, 160);
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
	 * @param arrow true - чертить стрелку на конце линии
	 */
	private void drawLine(GC gc, Color color, double penStyle, double outer, 
			double inner, double gradus1, double gradus2, int lineStyle, boolean arrow) {
		gc.setForeground(color);
		gc.setLineStyle(lineStyle);

		int startx = (int)Math.round(getXPoint(outer, gradus1)) + xcenter;
		int starty = (int)Math.round(getYPoint(outer, gradus1)) + ycenter;
		int destx = (int)Math.round(getXPoint(inner, gradus2)) + xcenter;
		int desty = (int)Math.round(getYPoint(inner, gradus2)) + ycenter;
		gc.drawLine(startx, starty, destx, desty);

		if (arrow && event2 != null && event2.getPlanets() != null) {
			gc.setBackground(color);
			Path path = drawLineArrow(gc.getDevice(), new Point(destx, desty), getRotation(gradus1, gradus2), 10, 15);
		    gc.fillPath(path);
		    path.dispose();
		}
	}

	/**
	 * Прорисовка астрологических домов
	 * @param event событие
	 * @param gc графическая система
	 * @param primary true - первый уровень домов
	 */
	private void drawHouses(Event event, GC gc, boolean primary) {
		if (!event.isHousable()) return;
		Iterator<Model> i = event.getHouses().iterator();
		while (i.hasNext()) {
			House h = (House)i.next();
			if (h.isMain()) {
	     		drawLine(gc, primary ? HOUSE_COLOR : HOUSEPART_COLOR, 0, 210, INNER_CIRCLE, h.getLongitude(), h.getLongitude(), SWT.LINE_SOLID, false);
				drawHouseName(h.getDesignation(), h.getLongitude(), gc, primary);
			}
		}
		drawHouseParts(event, gc, primary);
	}

	/**
	 * Прорисовка названий астрологических домов
	 * @param name имя дома
	 * @param value градус дома
	 * @param gc графическая система
	 * @param primary true - первый уровень домов
	 */
	private void drawHouseName(String name, double value, GC gc, boolean primary) {
		gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		gc.setForeground(primary ? HOUSE_COLOR : HOUSEPART_COLOR);
		gc.drawString(name, CalcUtil.trunc(getXPoint(230, value)) + xcenter - 5,
			CalcUtil.trunc(getYPoint(primary ? 230 : 210, value)) + ycenter);
	}

	/**
	 * Прорисовка триплицетов астрологических домов
	 * @param event событие
	 * @param gc графическая система
	 * @param primary true - первый уровень домов
	 */
	private void drawHouseParts(Event event, GC gc, boolean primary) {
		Iterator<Model> i = event.getHouses().iterator();
		while (i.hasNext()) {
			House h = (House)i.next();
			if (!h.isMain())
	     		drawLine(gc, primary ? HOUSE_COLOR : HOUSEPART_COLOR, 0, 140.0, INNER_CIRCLE, h.getLongitude(), h.getLongitude(), SWT.LINE_SOLID, false);
		}
	}

	/**
	 * Прорисовка планет
	 * @param event событие
	 * @param gc графическая система
	 * @param radius радиус окружности
	 * @throws DataAccessException
	 */
	private void drawPlanets(Event event, GC gc, int radius) throws DataAccessException {
		Iterator<Planet> i = event.getPlanets().values().iterator();
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
	@SuppressWarnings("unchecked")
	private void drawAspects(GC gc) throws DataAccessException {
		if (null == event || null == event.getPlanets()) return;

		List<Long> aspectypes = new ArrayList<>();
		List<Aspect> aspectlist = new ArrayList<>();
		List<Model> aspects = new AspectService().getList();
		Iterator<Model> ia = aspects.iterator();

		List<String> aparams = new ArrayList<>();
		if (params != null) {
			if (params.get("aspects") != null)
				aparams = (List<String>)params.get("aspects");
		}
		while (ia.hasNext()) {
			Aspect a = (Aspect)ia.next();
			if (aparams.size() > 0 && !aparams.contains(a.getType().getCode()))
				continue;
			else {
				aspectypes.add(a.getId());
				aspectlist.add(a);
			}
		}

		boolean single = (null == event2 || null == event2.getPlanets());
		Iterator<Planet> i = event.getPlanets().values().iterator();
		Map<Long, Planet> planets = single ? event.getPlanets() : event2.getPlanets();

		while (i.hasNext()) {
			Planet p = i.next();
			if (p.getCode().equals("Moon") && !event.isHousable())
				continue;

			if (single) {
				List<SkyPointAspect> paspects = p.getAspectList();
				for (SkyPointAspect spa : paspects) {
					Aspect a = spa.getAspect();
					if (!aspectypes.contains(a.getId()))
						continue;
					drawAspect(a.getType().getColor(), 0f, p.getLongitude(), planets.get(spa.getSkyPoint2().getId()).getLongitude(), gc, 
						getLineStyle(a.getType().getProtraction()), !a.getCode().equals("CONJUNCTION"));
				}
			} else {
				boolean houseAspectable = false;
				if (params != null
						&& event.getHouses() != null
						&& event.getHouses().size() > 0)
					houseAspectable = params.get("houseAspectable") != null;

				Iterator<Planet> j = planets.values().iterator();
				while (j.hasNext()) {
					Planet p2 = j.next();
					if (p2.getCode().equals("Moon") && !event2.isHousable())
						continue;
					getAspect(p.getLongitude(), p2.getLongitude(), aspectlist, gc);

					if (houseAspectable)
						for (Model model : event.getHouses()) {
							House house = (House)model;
							getAspect(p2.getLongitude(), house.getLongitude(), aspectlist, gc);
						}
				}
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
	 * @param arrow true - чертить стрелку на конце линии
	 */
	private void drawAspect(Color color, double penStyle, double a, double b, GC gc, int lineStyle, boolean arrow) {
		drawLine(gc, color, penStyle, 120.0, 120.0, a, b, lineStyle, arrow);
	}

	/**
	 * Динамическое определение аспекта между небесными точками (только для парных космограмм)
	 * @param one градус первой точки
	 * @param two градус второй точки
	 * @param gc графическая система
	 * @throws DataAccessException
	 */
	private void getAspect(double one, double two, List<Aspect> aspects, GC gc) throws DataAccessException {
		double res = CalcUtil.getDifference(one, two);
		Iterator<Aspect> i = aspects.iterator();

		boolean exact = false;
		if (params != null)
			exact = params.get("exact") != null;

		while (i.hasNext()) {
			Aspect a = i.next();
			if (a.isAspect(res)) {
				if (exact && !a.isExact(res))
					continue;
				if (!aspects.contains(a))
					continue;
				drawAspect(a.getType().getColor(), 0f, one, two, gc, 
					getLineStyle(a.getType().getProtraction()), !a.getCode().equals("CONJUNCTION"));
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

	/**
	 * Рисование стрелки на конце линии
	 * @param device устройство
	 * @param point точка прорисовки
	 * @param rotationDeg направление стрелки
	 * @param length длина стрелки
	 * @param wingsAngleDeg угол крыльев стрелки
	 * @return фигура стрелки
	 * @author Rüdiger Herrmann
	 * @link https://stackoverflow.com/questions/34159006/how-to-draw-a-line-with-arrow-in-swt-on-canvas
	 */
	private Path drawLineArrow(Device device, Point point, double rotationDeg, double length, double wingsAngleDeg) {
		double ax = point.x;
		double ay = point.y;
		double radB = Math.toRadians(-rotationDeg + wingsAngleDeg);
		double radC = Math.toRadians(-rotationDeg - wingsAngleDeg);
		Path resultPath = new Path(device);
		resultPath.moveTo((float)(length * Math.cos(radB) + ax), (float)(length * Math.sin(radB) + ay));
		resultPath.lineTo((float)ax, (float)ay);
		resultPath.lineTo((float)(length * Math.cos(radC) + ax), (float)(length * Math.sin(radC) + ay));
		return resultPath;
	}

	/**
	 * Вычисления угла поворота
	 * @param gradus1 начальный градус окружности
	 * @param gradus2 конечный градус окружности
	 * @return угол
	 */
	private double getRotation(double gradus1, double gradus2) {
		double res = 180;
		if (gradus1 < gradus2)
			res = 360 - gradus1 + (360 - gradus2);
		else
			res = 360 - (360 - gradus2) + gradus1;
		return res;
	}
} 
