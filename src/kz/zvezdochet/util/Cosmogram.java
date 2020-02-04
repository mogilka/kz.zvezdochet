package kz.zvezdochet.util;

import java.util.ArrayList;
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
import kz.zvezdochet.bean.SkyPoint;
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
	
	private Map<String, Object> params;

	/**
	 * Прорисовка космограммы
	 * @param event событие
	 * @param event2 связанное событие
	 * @param params массив параметров
	 * @param gc графический контекст
	 * @param widget true - прорисовка виджета, false - прорисовка изображения для файла
	 * @todo если параметры не заданы, брать все по умолчанию
	 */
	public Cosmogram(Event event, Event event2, Map<String, Object> params, GC gc, boolean widget) {
		this.params = params;
		if (null == gc)
			gc = new GC(Display.getDefault());
		paintCard(gc, widget, event, event2);
	}

	/**
	 * Прорисовка космограммы
	 * @param gc графическая система
	 * @param widget true - прорисовка виджета, false - прорисовка изображения для файла
	 */
	private void paintCard(GC gc, boolean widget, Event event, Event event2) {
		xcenter = ycenter = 257;
   	    Image image = AbstractUIPlugin.imageDescriptorFromPlugin("kz.zvezdochet", "icons/card.png").createImage();
		gc.drawImage(image, 52, 53);
		if (event != null) {
			if (event.getHouses() != null && event.getHouses().size() > 0)
				drawHouses(event, gc, true, widget);
		    if (event.getPlanets() != null && event.getPlanets().size() > 0)
				try {
					drawPlanets(event, gc, 135);
				} catch (DataAccessException e) {
					e.printStackTrace();
				}
		}
		if (event2 != null) {
			if (event2.getHouses() != null && event2.getHouses().size() > 0)
				drawHouses(event2, gc, false, widget);
		    if (event2.getPlanets() != null && event2.getPlanets().size() > 0)
				try {
					drawPlanets(event2, gc, 160);
				} catch (DataAccessException e) {
					e.printStackTrace();
				}
		}
		try {
			drawAspects(gc, event, event2);
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

		if (arrow) {
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
	 * @param widget true - прорисовка виджета, false - прорисовка изображения для файла
	 */
	private void drawHouses(Event event, GC gc, boolean primary, boolean widget) {
		if (!event.isHousable()) return;
		for (House h : event.getHouses().values()) {
			if (h.isMain()) {
	     		drawLine(gc, primary ? HOUSE_COLOR : HOUSEPART_COLOR, 0, 210, INNER_CIRCLE, h.getLongitude(), h.getLongitude(), SWT.LINE_SOLID, false);
				drawHouseName(h.getDesignation(), h.getLongitude(), gc, primary, widget);
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
	 * @param widget true - прорисовка виджета, false - прорисовка изображения для файла
	 */
	private void drawHouseName(String name, double value, GC gc, boolean primary, boolean widget) {
		gc.setBackground(Display.getDefault().getSystemColor(widget ? SWT.COLOR_WIDGET_BACKGROUND : SWT.COLOR_TRANSPARENT));
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
		for (House h :event.getHouses().values()) {
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
		for (Planet p : event.getPlanets().values()) {
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
	private void drawAspects(GC gc, Event event, Event event2) throws DataAccessException {
		if (null == event || null == event.getPlanets()) return;

		List<Long> aspectypes = new ArrayList<>();
		List<Model> aspects = new AspectService().getList();

		List<String> aparams = new ArrayList<>();
		List<String> pparams = new ArrayList<>();
		List<String> hparams = new ArrayList<>();
		if (params != null) {
			if (params.get("aspects") != null)
				aparams = (List<String>)params.get("aspects");
			if (params.get("planets") != null)
				pparams = (List<String>)params.get("planets");
			if (params.get("houses") != null)
				hparams = (List<String>)params.get("houses");
		}

		for (Model model : aspects) {
			Aspect a = (Aspect)model;
			if (aparams.size() > 0 && !aparams.contains(a.getType().getCode()))
				continue;
			else
				aspectypes.add(a.getId());
		}

		boolean houseAspectable = false;
		if (params != null
				&& event.getHouses() != null
				&& event.getHouses().size() > 0)
			houseAspectable = params.get("houseAspectable") != null;

		Map<Long, Planet> planets = event.getPlanets();
		Map<Long, House> houses = event.getHouses();
		boolean single = (null == event2 || null == event2.getPlanets());
		if (single) {
			for (Planet p : planets.values()) {
				if (houseAspectable
						&& pparams != null
						&& !pparams.isEmpty()
						&& !pparams.contains(p.getCode()))
					continue;

				if (p.getCode().equals("Moon") && !event.isHousable())
					continue;
	
					List<SkyPointAspect> paspects = houseAspectable ? p.getAspectHouseList() : p.getAspectList();
					for (SkyPointAspect spa : paspects) {
						Aspect a = spa.getAspect();
						if (!aspectypes.contains(a.getId()))
							continue;

						Double coord = null;
						if (houseAspectable) {
							SkyPoint skyPoint = spa.getSkyPoint2();
							if (hparams != null && hparams.contains(skyPoint.getCode()))
								coord = houses.get(skyPoint.getId()).getLongitude();
						} else
							coord = planets.get(spa.getSkyPoint2().getId()).getLongitude();

						if (coord != null)
							drawAspect(
								a.getType().getColor(),
								0f,
								p.getLongitude(),
								coord,
								gc, 
								getLineStyle(a.getType().getProtraction()),
								!single && !a.getCode().equals("CONJUNCTION"));
					}
			}
		} else {
			Map<Long, Planet> planets2 = event2.getPlanets();
			List<SkyPointAspect> paspects = event2.getAspectList();

			for (SkyPointAspect spa : paspects) {
//				if (20 == spa.getSkyPoint1().getId() && 19 == spa.getSkyPoint2().getId())
//					System.out.println(spa.getSkyPoint1().getCode() + " " + spa.getSkyPoint2().getCode() + " = " + spa.getScore());
//				else if (19 == spa.getSkyPoint1().getId() && 20 == spa.getSkyPoint2().getId())
//					System.out.println(spa.getSkyPoint1().getCode() + " " + spa.getSkyPoint2().getCode() + " = " + spa.getScore());
//				else
//					continue;

				Aspect a = spa.getAspect();
				if (!aspectypes.contains(a.getId()))
					continue;

				if (spa.getSkyPoint1().getCode().equals("Moon") && !event.isHousable())
					continue;

				if (spa.getSkyPoint2().getCode().equals("Moon") && !event2.isHousable())
					continue;

				if (!houseAspectable && spa.getSkyPoint2() instanceof House)
					continue;

				boolean synastry = false, arrow = false;
				if (params != null
						&& params.get("type") != null
						&& params.get("type").equals("synastry"))
					synastry = true;

				double one = 0, two = 0;
				if (synastry) { //TODO
					one = planets.get(spa.getSkyPoint1().getId()).getLongitude();
					two = planets2.get(spa.getSkyPoint2().getId()).getLongitude();
				} else {
					one = spa.getSkyPoint1() instanceof Planet
						? planets2.get(spa.getSkyPoint1().getId()).getLongitude()
						: houses.get(spa.getSkyPoint1().getId()).getLongitude();

					two = spa.getSkyPoint2() instanceof Planet
						? planets.get(spa.getSkyPoint2().getId()).getLongitude()
						: houses.get(spa.getSkyPoint2().getId()).getLongitude();
					arrow = !a.getCode().equals("CONJUNCTION");
				}
				drawAspect(
					a.getType().getColor(),
					0f, 
					one,
					two,
					gc, 
					getLineStyle(a.getType().getProtraction()), 
					arrow);
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
