package kz.zvezdochet.test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Nataly Didenko
 * Вычисление границ сидерических знаков Зодиака. 
 * За основу взяты эталонные даты 1977 года, 
 * когда Солнце входит в тот или иной знак.
 * 
 * Тропическая астрология основана на работах Птолемея.
 * Сидерический год больше, чем тропический из-за прецессии оси вращения Земли,
 * т.е. медленного смещения к западу точки равноденствия
 * вдоль плоскости эклиптики в размере 50,27 дуговой секунды в год.
 * 
 * По причине прецессии земной оси «неподвижные звёзды» равномерно смещаются
 * в направлении зодиакального движения небесных светил, проходя 1° за 71,6 года
 */
public class Signs1977 {
	double[] signs;

	String[] dates = {
			"19.01.1977 21:00:00",
			"16.02.1977 08:15:00",
			"12.03.1977 13:00:00",
			"18.04.1977 04:54:48",
			"14.05.1977 07:23:00",
			"21.06.1977 12:00:00",
			"20.07.1977 21:00:00",
			"10.08.1977 15:00:00",
			"16.09.1977 19:00:00",
			"31.10.1977 08:00:00",
			"23.11.1977 06:00:00",
			"30.11.1977 01:00:00",
			"18.12.1977 07:00:00",
			"20.01.1978 05:00:00"};

	String[] names = {
			"aries", 
			"taurus", 
			"gemini", 
			"cancer", 
			"leo", 
			"virgo", 
			"libra", 
			"scorpio", 
			"ophiuchus", 
			"sagittarius", 
			"capricornus", 
			"aquarius", 
			"pisces",
			""};
	
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
			System.out.println(degrees[i]);
		}
		for (int i = 4; i < degrees.length; i++)
    		s77.signs[i - 3] = s77.signs[i - 4] + degrees[i];
		for (int i = 1; i < 4; i++)
    		s77.signs[i + 10] = s77.signs[i + 9] + degrees[i];
		for (int i = 0; i < s77.signs.length; i++)
    		System.out.println(s77.names[i] + " = " + s77.signs[i]);
	}

	/**
	 * Определяем разницу между датами в сутках
	 * @param d1 первая дата
	 * @param d2 вторая дата
	 * @return количество суток
	 */
	private static double getDays(String d1, String d2) {
		double difference = 0.0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            Date initialDate = sdf.parse(d1);
            Date finalDate = sdf.parse(d2);
            //миллисекунды -> секунды -> часы -> сутки
            difference = (((finalDate.getTime() - initialDate.getTime()) 
            					/ 1000) / 3600) / 24;
    		System.out.print("days = " + difference + "; ");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
		return difference;
	}

	/**
	 * Вычисление количества градусов, через которые
	 * Солнце проходит за указанное количество суток
	 * @param days количество суток
	 * @return количество градусов
	 */
	private static double dayToDeg(double days) {
		double GREGORIAN_YEAR_DAYS = 365.2425; //среднее количество суток в григорианском году
		double ZODIAC_DEGREES = 360; //количество градусов в окружности Зодиака
		return ZODIAC_DEGREES * days / GREGORIAN_YEAR_DAYS;
	}

/*
1 days = 28.0; 27.598102630444156
2 days = 24.0; 23.655516540380706
3 days = 38.0; 37.45456785560278
4 days = 25.0; 24.641163062896567
5 days = 37.0; 36.46892133308692
6 days = 31.0; 30.555042197991746
7 days = 20.0; 19.712930450317256
8 days = 37.0; 36.46892133308692
9 days = 45.0; 44.354093513213826
10 days = 23.0; 22.66987001786484
11 days = 7.0; 6.899525657611039
12 days = 18.0; 17.74163740528553
13 days = 32.0; 31.540688720507607
aries = 0.0
taurus = 24.641163062896567
gemini = 61.110084395983485
cancer = 91.66512659397523
leo = 111.37805704429249
virgo = 147.8469783773794
libra = 192.20107189059323
scorpio = 214.87094190845806
ophiuchus = 221.7704675660691
sagittarius = 239.51210497135463
capricornus = 271.05279369186223
aquarius = 298.6508963223064
pisces = 322.30641286268707
 = 359.76098071828983
 */
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
