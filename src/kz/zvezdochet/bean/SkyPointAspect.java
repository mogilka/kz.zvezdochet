package kz.zvezdochet.bean;

import java.util.Arrays;

import kz.zvezdochet.service.AspectTypeService;

/**
 * Аспект между объектами гороскопа
 * @author Natalie Didenko
 */
public class SkyPointAspect {
	/**
	 * Небесная точка 1
	 */
	private SkyPoint skyPoint1;
	
	/**
	 * Небесная точка 2
	 */
	private SkyPoint skyPoint2;
	
	/**
	 * Аспект
	 */
	private Aspect aspect;
	/**
	 * Расстояние между объектами в градусах
	 */
	private double score;
	/**
	 * Признак ретроградности
	 */
	private boolean retro = false;
	/**
	 * Условный возраст формирования аспекта
	 */
	private double age = 0.0;
	
	public boolean isRetro() {
		return retro;
	}

	public void setRetro(boolean retro) {
		this.retro = retro;
	}

	public SkyPoint getSkyPoint1() {
		return skyPoint1;
	}

	public void setSkyPoint1(SkyPoint skyPoint1) {
		this.skyPoint1 = skyPoint1;
	}

	public SkyPoint getSkyPoint2() {
		return skyPoint2;
	}

	public void setSkyPoint2(SkyPoint skyPoint2) {
		this.skyPoint2 = skyPoint2;
	}

	public Aspect getAspect() {
		return aspect;
	}

	public void setAspect(Aspect aspect) {
		this.aspect = aspect;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getAge() {
		return age;
	}

	public void setAge(double age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return skyPoint1.getName() + " " + 
			aspect.getType().getSymbol() + " " + 
			skyPoint2.getName();
	}

	/**
	 * Признак точного аспекта
	 */
	private boolean exact = false;
	/**
	 * Признак аппликации
	 */
	private boolean application = false;

	public boolean isExact() {
		return exact;
	}

	public void setExact(boolean exact) {
		this.exact = exact;
	}

	public boolean isApplication() {
		return application;
	}

	public void setApplication(boolean application) {
		this.application = application;
	}

	/**
	 * Корректировка типа аспекта для толкования
	 * @param state true - проверять соединение планеты с Лилит и Кету
	 * @return тип аспекта
	 */
	public AspectType checkType(boolean state) {
		AspectType type = aspect.getType();
		try {
			if (type.getCode().equals("NEUTRAL")) {
				Planet planet1 = (Planet)skyPoint1;
				Planet planet2 = (Planet)skyPoint2;
				String pcode1 = planet1.getCode();
				String pcode2 = planet2.getCode();

				AspectTypeService service = new AspectTypeService();

				String positive[] = {"Selena"};
				if (Arrays.asList(positive).contains(pcode1) ||
						Arrays.asList(positive).contains(pcode2))
					type = (AspectType)service.find("POSITIVE");

				if (state) {
					String negative[] = {"Lilith", "Kethu"};
					if (Arrays.asList(negative).contains(pcode1) ||
							Arrays.asList(negative).contains(pcode2))
						type = (AspectType)service.find("NEGATIVE");				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return type;
	}

	public SkyPointAspect(SkyPoint skyPoint1, SkyPoint skyPoint2, Aspect aspect) {
		super();
		this.skyPoint1 = skyPoint1;
		this.skyPoint2 = skyPoint2;
		this.aspect = aspect;
	}

	public SkyPointAspect() {}	

	public SkyPointAspect(SkyPointAspect spa) {
		skyPoint1 = spa.skyPoint1;
		skyPoint2 = spa.skyPoint2;
		aspect = spa.aspect;
		exact = spa.exact;
		application = spa.application;
	}

	/**
	 * Описание
	 */
	private String descr;

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	/**
	 * Поиск знака силы аспекта
	 * @return знак силы
	 */
	public String getMark() {
		if (exact)
			return "•";
		else if (application)
			return "→";
		else
			return "←";
	}

	/**
	 * Поиск описания силы аспекта
	 * @return описание силы
	 */
	public String getMarkDescr() {
		if (exact)
			return "точное сочетание, наиболее сильное и сконцентрированное";
		else if (application)
			return "нарастающее сочетание с тенденцией к усилению";
		else
			return "угасающее сочетание с тенденцией к ослаблению";
	}

	/**
	 * Поиск значения силы аспекта
	 * @return значение силы
	 */
	public int getMarkPoints() {
		if (exact)
			return 0;
		else if (application)
			return -1;
		else
			return 1;
	}
}
