package kz.zvezdochet.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import kz.zvezdochet.core.util.CalcUtil;

/**
 * @author Nataly
 * Класс для определения точных границ знаков Зодиака. 
 * За основу взяты эталонные даты 1977 года, 
 * когда Солнце входит в тот или иной знак
 */
public class Signs1977 {
	double[] signs;
	String[] dates = {
			"19.01.1977",
			"16.02.1977",
			"12.03.1977",
			"19.04.1977",
			"14.05.1977",
			"21.06.1977",
			"21.07.1977",
			"11.08.1977",
			"17.09.1977",
			"31.10.1977",
			"23.11.1977",
			"30.11.1977",
			"18.12.1977",
			"19.01.1978"};
	String[] names = {
			"овен", 
			"телец", 
			"близнецы", 
			"рак", 
			"лев", 
			"дева", 
			"весы", 
			"скорпион", 
			"змееносец", 
			"стрелец", 
			"козерог", 
			"водолей", 
			"рыбы", ""};
	
	public Signs1977() {
		signs = new double[14];
		for (int i = 0; i < signs.length; i++)
			signs[i] = 0.0;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Signs1977 s77 = new Signs1977();
		//так как начальная граница Овна соответствует 
		//точке отсчета астрологического Зодиака, 
		//начинаем расчет с Тельца
		double[] degrees = new double[14];
		for (int i = 1; i < degrees.length; i++) {
    		System.out.print(i + " ");
    		degrees[i] = dayToDeg(getDays(s77.dates[i - 1], s77.dates[i]));
			System.out.println(CalcUtil.decToDeg(degrees[i]) + "; " + degrees[i]);
		}
		for (int i = 4; i < degrees.length; i++)
    		s77.signs[i - 3] = s77.signs[i - 4] + degrees[i];
		for (int i = 1; i < 4; i++)
    		s77.signs[i + 10] = s77.signs[i + 9] + degrees[i];
		for (int i = 0; i < s77.signs.length; i++)
    		System.out.println(s77.names[i] + " = " + CalcUtil.decToDeg(s77.signs[i]));
	}

	public static double getDays(String d1, String d2) {
		long difference = 0;
        try {
        	//определяем, сколько дней солнце находилось в текущем знаке
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            Date initialDate = sdf.parse(d1);
            Date finalDate = sdf.parse(d2);
          //миллисекунды -> секунды -> часы -> дни
            difference = (((finalDate.getTime() - initialDate.getTime()) 
            					/ 1000) / 3600) / 24;
    		System.out.print("days = " + difference + "; ");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
		return (double)difference;
	}

	public static double dayToDeg(double l) {
		double d = 360 * l / 365;
		return d;
	}
	
	/** 
	 * Итоговые начальные градусы знаков Зодиака: 
	 * овен = 0.0 
	 * телец = 24.39 
	 * близнецы = 62.08 
	 * рак = 91.44 
	 * лев = 112.26 
	 * дева = 148.56 
	 * весы = 192.2 
	 * скорпион = 215.01 
	 * змееносец = 221.55 
	 * стрелец = 239.4 
	 * козерог = 271.14 
	 * водолей = 298.51 
	 * рыбы = 322.31 
	 */	
}
