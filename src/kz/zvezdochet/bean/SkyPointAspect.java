package kz.zvezdochet.bean;

import java.util.Arrays;
import java.util.List;

import kz.zvezdochet.core.bean.ITextGender;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.bean.TextGenderModel;
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

				if (state) {
					String positive[] = {"Selena"};
					if (Arrays.asList(positive).contains(pcode1) ||
							Arrays.asList(positive).contains(pcode2))
						type = (AspectType)service.find("POSITIVE");

					String negative[] = {"Lilith", "Kethu"};
					if (Arrays.asList(negative).contains(pcode1) ||
							Arrays.asList(negative).contains(pcode2))
						type = (AspectType)service.find("NEGATIVE");
				}
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
			return "точный аспект (наиболее сильное и сконцентрированное сочетание планет)";
		else if (application)
			return "аппликация (нарастающее сочетание с тенденцией к усилению)";
		else
			return "сепарация (угасающее сочетание с тенденцией к ослаблению)";
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

    /**
     * Возвращает длительность транзита
     * @return длительность транзита в текстовом виде
     */
    public String getTransitDuration() {
    	String res = "";
    	String acode = aspect.getCode();
    	String pcode = skyPoint1.getCode();
    	boolean housable = (skyPoint2 instanceof House);

    	String[] innerPlanets = new String[] {"Sun", "Venus"};
    	String[] urans = new String[] {"Chiron", "Uranus"};
    	String[] nodes = new String[] {"Rakhu", "Kethu"};
    	String[] moons = new String[] {"Selena", "Lilith"};

    	if (acode.equals("CONJUNCTION")) {
   			if (pcode.equals("Mercury")) {
   				if (housable)
					res = "2 дня";
   			} else if (Arrays.asList(innerPlanets).contains(pcode))
   				res = housable ? "2 дня" : "2 дня";
   			else if (pcode.equals("Mars"))
   				res = housable ? "2 дня" : "2 дня";
   			else if (pcode.equals("Jupiter"))
   				res = housable ? "10 дней" : "5 дней";
   			else if (pcode.equals("Saturn"))
   				res = housable ? "3 недели" : "2 недели";
   			else if (Arrays.asList(urans).contains(pcode))
   				res = housable ? "1 месяц" : "3 недели";
   			else if (pcode.equals("Neptune"))
   				res = housable ? "3 месяца" : "1 месяц";
   			else if (pcode.equals("Pluto"))
   				res = housable ? "1 месяц" : "1 месяц";
   			else if (pcode.equals("Proserpina"))
   				res = housable ? "3 месяца" : "1 месяц";
   			else if (Arrays.asList(nodes).contains(pcode))
   				res = housable ? "1 декада" : "1 неделя";
   			else if (Arrays.asList(moons).contains(pcode))
   				res = housable ? "3 дня" : "2 дня";
    	} else {
   			if (pcode.equals("Mercury")) {
   				if (housable)
					res = "2 дня";
   			} else if (Arrays.asList(innerPlanets).contains(pcode)) {
   				if (housable)
   					res = "2 дня";
   			} else if (pcode.equals("Mars"))
   				res = housable ? "2 дня" : "2 дня";
   			else if (pcode.equals("Jupiter"))
   				res = housable ? "5 дней" : "4 дня";
   			else if (pcode.equals("Saturn"))
   				res = housable ? "10 дней" : "1 неделя";
   			else if (Arrays.asList(urans).contains(pcode))
   				res = housable ? "2 недели" : "2 недели";
   			else if (pcode.equals("Neptune"))
   				res = housable ? "1 месяц" : "1 месяц";
   			else if (pcode.equals("Pluto"))
   				res = housable ? "2 недели" : "2 недели";
   			else if (pcode.equals("Proserpina"))
   				res = housable ? "3 месяца" : "1 декада";
   			else if (Arrays.asList(nodes).contains(pcode))
   				res = housable ? "2 дня" : "2 дня";
   			else if (Arrays.asList(moons).contains(pcode))
   				res = housable ? "2 дня" : "2 дня";
    	}
    	return res;
    }

    /**
     * Генерация идентификатора аспекта
     * @return код аспекта
     */
	public String getCode() {
		return reverse
			? skyPoint2.getCode() + "_" + 
				aspect.getCode() + "_" + 
				skyPoint1.getCode()
			: skyPoint1.getCode() + "_" + 
				aspect.getCode() + "_" + 
				skyPoint2.getCode();
	}

	/**
	 * Порядок аспектирующей и аспектируемой планеты не изменён
	 */
	private boolean reverse = false;

	public boolean isReverse() {
		return reverse;
	}

	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}

	/**
	 * Толкование аспекта
	 */
	private ITextGender text;

	public ITextGender getText() {
		return text;
	}

	public void setText(ITextGender text) {
		this.text = text;
	}

	/**
	 * Толкования аспекта
	 */
	private List<Model> texts;

	public List<Model> getTexts() {
		return texts;
	}

	public void setTexts(List<Model> texts) {
		this.texts = texts;
	}

	/**
	 * Сокращённое представление аспекта
	 * @return текст аспекта в символах
	 */
	public String getSymbol() {
		SkyPoint skyPoint = getSkyPoint1();
		String text = skyPoint.getSymbol();
		if (skyPoint.isRetrograde())
			text += "®";
		text += getAspect().getSymbol();
		skyPoint = getSkyPoint2();
		boolean housable = skyPoint instanceof House;
		text += (housable ? skyPoint.getCode() : skyPoint.getSymbol());
		return text;
	}

	/**
	 * Проверка, является ли аспект негативным
	 * @return true - если сам аспект негативный, либо в соединении одна из планет негативна
	 */
	public boolean isNegative() {
		AspectType type = aspect.getType();
		if (type.getPoints() < 0)
			return true;

		if (texts != null && texts.size() > 0) {
			TextGenderModel dict = (TextGenderModel)texts.get(0);
			return !dict.isPositive();
		} else if (type.getCode().equals("NEUTRAL"))
			return ((Planet)skyPoint1).isBad()
				|| ((Planet)skyPoint2).isBad();

		return false;
	}
}
