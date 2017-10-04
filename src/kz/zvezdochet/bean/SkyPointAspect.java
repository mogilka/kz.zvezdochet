package kz.zvezdochet.bean;

import java.util.Arrays;

import kz.zvezdochet.service.AspectTypeService;

/**
 * Аспект между объектами гороскопа
 * @author Nataly Didenko
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
		Planet planet1 = (Planet)skyPoint1;
		Planet planet2 = (Planet)skyPoint2;
		String pcode1 = planet1.getCode();
		String pcode2 = planet2.getCode();
		AspectTypeService service = new AspectTypeService();

		String negative[] = {"Lilith", "Kethu"};
		String positive[] = {"Selena", "Sun", "Moon", "Rakhu", "Mercury", "Venus", "Jupiter", "Proserpina"};

		try {
			if (type.getCode().equals("NEUTRAL")) {
				boolean baded = state ? (planet1.isLilithed() || planet2.isLilithed()
					|| planet1.isKethued() || planet2.isKethued()) : false;

				if (Arrays.asList(negative).contains(pcode1) ||
						Arrays.asList(negative).contains(pcode2) || baded)
					type = (AspectType)service.find("NEGATIVE");

				else if (Arrays.asList(positive).contains(pcode1) ||
						Arrays.asList(positive).contains(pcode2))
					type = (AspectType)service.find("POSITIVE");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return type;
	}
}
