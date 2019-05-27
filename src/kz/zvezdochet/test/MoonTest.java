package kz.zvezdochet.test;

import java.util.Arrays;
import java.util.Date;

import jodd.datetime.JDateTime;
import jodd.datetime.JulianDateStamp;
import kz.zvezdochet.core.util.DateUtil;
import swisseph.DblObj;
import swisseph.SweConst;
import swisseph.SweDate;
import swisseph.SweHel;
import swisseph.SwissEph;

/**
 * 
 * @author Natalie Didenko
 * @see http://www.astrolab.ru/cgi-bin/informer/informer.cgi?type=8
 * @link https://ru.wikipedia.org/wiki/Фазы_Луны
 * @link https://ru.wikipedia.org/wiki/Новолуние
 * @link http://www.abc-people.com/phenomenons/spiritism/v-5.htm
 * @link http://mirkosmosa.ru/lunar-calendar/phase-moon/2018/february
 * @link http://goroskop.org/luna/form.shtml
 * @link http://lunium.ru/
 * @link http://astrolab.ru/cgi-bin/moonphases.cgi.html
 * @link http://redday.ru/moon/
 * @link https://www.timeanddate.com/moon/uk/london
 * 🌑 🌒 🌓 🌔 🌕 🌖 🌗 🌘
 * новолуние — Луна не видна
 * молодая луна — первое появление Луны на небе после новолуния в виде узкого серпа не позднее чем через 3 дня
 * первая четверть — освещена половина Луны 50%
 * прибывающая луна
 * полнолуние — освещена вся целиком 100%
 * убывающая луна
 * последняя четверть — освещена половина луны 50%
 * старая луна
 */
public class MoonTest {

	public static void main(String[] args) {
  		args = new String[] {"15.02.2018", "00:00:00", "6", "43.15", "76.56"};

  		//обрабатываем дату
  		int iyear, imonth, iday, ihour = 0, imin = 0, isec = 0;
  		String sdate = args[0];
  		int lday = Integer.parseInt(sdate.substring(0, 2));
  		int lmonth = Integer.parseInt(sdate.substring(3, 5));
  		int lyear = Integer.parseInt(sdate.substring(6, 10));
  		iday = lday;
  		imonth = lmonth;
  		iyear = lyear;

  		//обрабатываем время
  		String stime = args[1];
  		double lhour = Double.parseDouble(trimLeadZero(stime.substring(0,2))); //час по местному времени
  		double timing = lhour;
  		double zone = Double.parseDouble(args[2]); //зона
  		if (zone < 0) {
  			if (timing < (24 + zone))
  				timing -= zone;
  			else {
  				/*
  				 * Если час больше разности 24 часов и зоны, значит по Гринвичу будет следующий день,
  				 * поэтому нужно увеличить указанную дату на 1 день
  				 */
  				timing = timing - zone - 24;
  				if (iday < 28)
  					++iday;
  				else if (31 == iday) {
  					iday = 1;  							
  					if (12 == imonth) {
  	  					++iyear;
  	  					imonth = 1;
  					} else
  						++imonth;
  				} else if (30 == iday) {
  					if (Arrays.asList(new Integer[] {4,6,9,11}).contains(imonth)) {
  						++imonth;
  						iday = 1;
  					} else
  						iday = 31;
  				} else if (2 == imonth) {
  					if (29 == iday) {
  	  					imonth = 3;
  	  					iday = 1;
  					} else if (28 == iday) {
  						if (DateUtil.isLeapYear(iyear))
  							iday = 29;
  						else {
  							imonth = 3;
  							iday = 1;
  						}
  					}
  				} else //28 и 29 числа месяцев кроме февраля
  					++iday;
  			}
  		} else {
  			if (timing >= zone)
  				timing -= zone;
  			else {
  				/*
  				 * Если час меньше зоны, значит по Гринвичу будет предыдущий день,
  				 * поэтому нужно уменьшить указанную дату на 1 день
  				 */
  				timing = timing + 24 - zone;
  				if (iday > 1)
  					--iday;
  				else {
  					if (1 == imonth) {
  						--iyear;
  						imonth = 12;
  						iday = 31;
  					} else if (3 == imonth) {
  						imonth = 2;
  						iday = DateUtil.isLeapYear(iyear) ? 29 : 28;
  					} else if (Arrays.asList(new Integer[] {2,4,6,8,9,11}).contains(imonth)) {
  						--imonth;
  						iday = 31;
  					} else if (Arrays.asList(new Integer[] {5,7,10,12}).contains(imonth)) {
  						--imonth;
  						iday = 30;
  					}
  				}
  			}
  		}
  		if (timing >= 24)
  			timing -= 24;

  		ihour = (int)timing; //гринвичский час
  		imin = Integer.parseInt(trimLeadZero(stime.substring(3,5)));
  		isec = Integer.parseInt(trimLeadZero(stime.substring(6,8)));

  		@SuppressWarnings("unused")
		double tjd, tjdut, tjdet, dhour, deltat;
  		dhour = ihour + imin/60.0 + isec/3600.0;
  		double glon, glat;
  		tjd = SweDate.getJulDay(iyear, imonth, iday, dhour, true);
  		deltat = SweDate.getDeltaT(tjd);
  		//Universal Time
  		tjdut = tjd;
  		tjdet = tjd + deltat;
  		System.out.println("tjdut " + tjdut);

  		//обрабатываем координаты места
  		double lat = Double.parseDouble(args[3]);
  		double lon = Double.parseDouble(args[4]);
  		int ilondeg, ilonmin, ilonsec, ilatdeg, ilatmin, ilatsec;
  		ilondeg = (int)lon;
  		ilonmin = (int)Math.round((Math.abs(lon) - Math.abs(ilondeg)) * 100);
  		ilonsec = 0;
  		ilatdeg = (int)lat;
  		ilatmin = (int)Math.round((Math.abs(lat) - Math.abs(ilatdeg)) * 100);
  		ilatsec = 0;

  		//{ geographic position }
  		glon = Math.abs(ilondeg) + ilonmin/60.0 + ilonsec/3600.0;
  		if (lon < 0)
  			glon = -glon;
  		//if (combo_EW.ItemIndex > 0) then glon := -glon;
  		glat = Math.abs(ilatdeg) + ilatmin/60.0 + ilatsec/3600.0;
  		if (lat < 0)
  			glat = -glat;

  	  	SwissEph sweph = new SwissEph();
  		sweph.swe_set_ephe_path("/home/nataly/workspace/kz.zvezdochet.sweph/lib/ephe");

  		MoonTest test = new MoonTest();
  		test.pheno(sweph, tjdut);
//  		test.heliacal_pheno(glon, glat, tjdut);

  		//используем локальное время
  		dhour = lhour + imin/60.0 + isec/3600.0;
  		tjd = SweDate.getJulDay(lyear, lmonth, lday, dhour, true);
  		tjdut = tjd;
  		System.out.println("tjdut2 " + tjdut);

  		test.rise(sweph, glon, glat, tjdut, SweConst.SE_CALC_RISE);
  		test.rise(sweph, glon, glat, tjdut, SweConst.SE_CALC_SET);
	}

	//compute phase, phase angle, elongation, apparent diameter, apparent magnitude
	//for the Sun, the Moon, all planets and asteroids
  	private void pheno(SwissEph sweph, double tjdut) {
  		String ss[] = {
  			"phase angle (earth-planet-sun)",
  			"phase (illumined fraction of disc)",
  			"elongation of planet",
  			"apparent diameter of disc",
  			"apparent magnitude"
  		};
  		double[] xx = new double[20];
  		char[] serr = new char[256];
  		StringBuffer sb = new StringBuffer(new String(serr));
  		int iflag = SweConst.SEFLG_SWIEPH | SweConst.SEFLG_TRUEPOS | SweConst.SEFLG_HELCTR;

  		try {
  	  		int res = sweph.swe_pheno_ut(tjdut, SweConst.SE_MOON, iflag, xx, sb);
  	  		if (SweConst.ERR == res)
  	  			System.out.println(sb);
  	  		else
  		  		for (int i = 0; i < 6; i++) {
  					System.out.print(i + "\t");
  		  			if (i < ss.length)
  		  				System.out.print(ss[i] + "\t");
  				    System.out.println(xx[i]);
  		  		}
		} catch (Exception e) {
			e.printStackTrace();
		}
  	}

  	private static String trimLeadZero(String s) {
  		if (s.indexOf('0') == 0)
  			return String.valueOf(s.charAt(1));
  		else
  			return s;
  	}

  	//provides data that are relevant for the calculation of heliacal risings and settings
  	@SuppressWarnings("unused")
	private void heliacal_pheno(double glon, double glat, double tjdut) {
  		String ss[] = {
  				"topocentric altitude of object (unrefracted)",
  				"apparent altitude of object (refracted)",
  				"geocentric altitude of object",
  				"azimuth of object",
  				"topocentric altitude of Sun",
  				"azimuth of Sun",
  				"actual topocentric arcus visionis",
  				"actual (geocentric) arcus visionis",
  				"actual difference between object's and sun's azimuth",
  				"actual longitude difference between object and sun",
  				"extinction coefficient",
  				"smallest topocentric arcus visionis",
  				"first time object is visible, according to VR",
  				"optimum time the object is visible, according to VR",
  				"last time object is visible, according to VR",
  				"best time the object is visible, according to Yallop",
  				"cresent width of moon",
  				"q-test value of Yallop",
  				"q-test criterion of Yallop",
  				"parallax of object",
  				"magnitude of object",
  				"rise/set time of object",
  				"rise/set time of sun",
  				"rise/set time of object minus rise/set time of sun",
  				"visibility duration",
  				"cresent length of moon",
  				"CVAact [deg]",
  				"Illum [%] 'new",
  				"CVAact [deg] 'new",
  				"MSk [-]"
  		};
  		double[] xx = new double[ss.length];
  		char[] serr = new char[256];
  		StringBuffer sb = new StringBuffer(new String(serr));
  		double[] dgeo = {glon, glat, 0};
  		double[] datm = {0,0,0,0};
  		double[] dobs = {36, 1, 1, 0, 0, 0};
  		new SweHel().swe_heliacal_pheno_ut(tjdut, dgeo, datm, dobs, new StringBuffer("Moon"), SweConst.SE_MORNING_LAST, SweConst.SE_HELFLAG_OPTICAL_PARAMS, xx, sb);

		for (int i = 0; i < xx.length; i++) {
			String sdate = (21 == i) ? " -> " + jul2date(xx[i]) : "";
		    System.out.println(i + "\t" + ss[i] + "\t" + xx[i] + sdate);
		}
  	}

  	//computes the times of rising, setting and meridian transits for all planets, asteroids, the moon, and the fixed stars
  	private void rise(SwissEph sweph, double glon, double glat, double tjdut, int flag) {
  		DblObj xx = new DblObj();
  		char[] serr = new char[256];
  		StringBuffer sb = new StringBuffer(new String(serr));
  		double[] dgeo = {glon, glat, 0};

  		//Нормальное атмосферное давление – 760 мм рт. столба. Это давление воздуха на уровне моря при температуре 0°С на широте 45°.
  		//760 mm = 101325 Pa = 1013.25 hPa
		long iflag = SweConst.SEFLG_SWIEPH | SweConst.SEFLG_TRUEPOS | SweConst.SEFLG_TOPOCTR;
  		int res = sweph.swe_rise_trans(tjdut, SweConst.SE_MOON, null, (int)iflag, flag, dgeo, 1013.25, 0.0, xx, sb);
  		if (0 == res) {
  			String type = SweConst.SE_CALC_RISE == flag ? "rise" : "set";
  			System.out.print("\t" + type + ":" + xx.val + " -> " + jul2date(xx.val));
  		} else if (SweConst.ERR == res)
  			System.out.println("error occurred (usually an ephemeris problem)");
  		else if (-2 == res)
  			System.out.println("rising or setting event was not found because the object is circumpolar");
  	}

  	/**
  	 * Конвертируем Юлианский день в дату
  	 * @param jd номер Юлианского дня
  	 * @return дата
  	 */
  	private static Date jul2date(double jd) {
  		JulianDateStamp julianStamp = new JulianDateStamp(jd);
  		JDateTime jdate = new JDateTime(julianStamp);
  		return new Date(jdate.getTimeInMillis());
  	}
}
