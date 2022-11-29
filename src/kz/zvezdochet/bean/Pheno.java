package kz.zvezdochet.bean;

import java.util.Date;

/**
 * Лунный день
 * @author Natalie Didenko
 *
 */
public class Pheno {

	/**
	 * Координата
	 */
    private double coord;
    /**
     * Возраст в днях
     */
    private int age;
    private Date date;
    private Date rise;
    private Date set;
    private Date pheno;
    private double percent;

	public double getCoord() {
		return coord;
	}

	public void setCoord(double coord) {
		this.coord = coord;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getRise() {
		return rise;
	}

	public void setRise(Date rise) {
		this.rise = rise;
	}

	public Date getSet() {
		return set;
	}

	public void setSet(Date set) {
		this.set = set;
	}

	public Date getPheno() {
		return pheno;
	}

	public void setPheno(Date pheno) {
		this.pheno = pheno;
	}

	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	/**
	 * Поиск момента начала лунного дня
	 * @return если новолуние, то момент новолуния, в противном случае время восхода
	 */
	public Date getStart() {
	    return null == pheno ? rise : pheno;
	}

	/**
	 * Поиск описания фазы
	 * @param lang язык ru|en
	 * @return описание фазы
	 */
	public String getPhase(String lang) {
		boolean rus = lang.equals("ru");
	    if (percent < 1 && 1 == age)
	        return rus ? "Новолуние, самая тёмная ночь" : "New moon, darkest night";
	    if (2 == age)
	        return "Молодая луна, первое появление лунного серпа после новолуния";
	    if (age < 9)
	        return "Растущая луна";
	    if (age > 8 && age < 11)
	        return "Первая четверть Лунной фазы";
	    if (age > 10 && age < 15)
	        return "Прибывающая луна";
	    if (15 == age)
	        return "Полнолуние";
	    if (age > 15 && age < 20)
	        return "Убывающая луна";
	    if (age > 19 && age < 22)
	        return "Последняя четверть Лунной фазы";
	    if (age > 21 && age < 29)
	        return "Стареющая луна";
	    if (age > 28)
	        return "Старая луна";
	    return "";
	}

	/**
	 * Поиск изображения фазы
	 * @return номер изображения
	 */
	public int getImageNumber() {
		switch (age) {
			case 3: return 4;
		    case 5: return 6;
		    case 7: return 8;
		    case 9: return 10;
		    case 11: return 12;
		    case 13: return 14;
		    case 17: return 16;
		    case 19: return 18;
		    case 21: return 20;
		    case 23: return 22;
		    case 25: return 24;
		    case 27: return 26;
		    case 29: return 28;
		}
		return age;
	}

	public Pheno(int age) {
		super();
		this.age = age;
	}

	public Pheno() {}
}
