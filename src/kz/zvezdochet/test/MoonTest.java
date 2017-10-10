package kz.zvezdochet.test;

import java.util.Arrays;

import kz.zvezdochet.core.util.DateUtil;
import swisseph.DblObj;
import swisseph.SweConst;
import swisseph.SweDate;
import swisseph.SweHel;
import swisseph.SwissEph;

/**
 * 
 * @author nataly
 * @see http://www.astrolab.ru/cgi-bin/informer/informer.cgi?type=8
 */
public class MoonTest {

	public static void main(String[] args) {
  		args = new String[] {"06.10.2017", "00:00:00", "6", "43.15", "76.55"};

  		//обрабатываем дату
  		int iyear, imonth, iday, ihour = 0, imin = 0, isec = 0;
  		String sdate = args[0];
  		iday = Integer.parseInt(sdate.substring(0, 2));
  		imonth = Integer.parseInt(sdate.substring(3, 5));
  		iyear = Integer.parseInt(sdate.substring(6, 10));

  		//обрабатываем время
  		String stime = args[1];
  		double timing = Double.parseDouble(trimLeadZero(stime.substring(0,2))); //час по местному времени
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

  		double tjd, tjdut, tjdet, dhour, deltat;
  		dhour = ihour + imin/60.0 + isec/3600.0;
  		tjd = SweDate.getJulDay(iyear, imonth, iday, dhour, true);
  		deltat = SweDate.getDeltaT(tjd);
  		//Universal Time
  		tjdet = tjd + deltat;

  		double glon, glat;
  		dhour = ihour + imin/60.0 + isec/3600.0;
  		tjd = SweDate.getJulDay(iyear, imonth, iday, dhour, true);
  		deltat = SweDate.getDeltaT(tjd);
  		//Universal Time
  		tjdut = tjd;
  		tjdet = tjd + deltat;

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

  		MoonTest test = new MoonTest();
//  		test.pheno(tjdut);
  		test.heliacal_pheno(glon, glat, tjdut);
//  		test.rise(glon, glat, tjdut, SweConst.SE_CALC_RISE);
//  		test.rise(glon, glat, tjdut, SweConst.SE_CALC_SET);
	}

  	private void pheno(double tjdut) {
  	  	SwissEph sweph = new SwissEph();
		long iflag = SweConst.SEFLG_TRUEPOS | SweConst.SEFLG_HELCTR;
  		sweph.swe_set_ephe_path("/home/nataly/workspace/kz.zvezdochet.sweph/lib/ephe");

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

  		sweph.swe_pheno_ut(tjdut, SweConst.SE_MOON, (int)iflag, xx, sb);
  		for (int i = 0; i < xx.length; i++) {
			System.out.print(i + "\t");
  			if (i < ss.length)
  				System.out.print(ss[i] + "\t");
		    System.out.println(xx[i]);
  		}
  	}

  	private static String trimLeadZero(String s) {
  		if (s.indexOf('0') == 0)
  			return String.valueOf(s.charAt(1));
  		else
  			return s;
  	}

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

		for (int i = 0; i < xx.length; i++)
		    System.out.println(i + "\t" + ss[i] + "\t" + xx[i]);
  	}

  	private void rise(double glon, double glat, double tjdut, int flag) {
  	  	SwissEph sweph = new SwissEph();
		long iflag = SweConst.SEFLG_TRUEPOS | SweConst.SEFLG_HELCTR;
  		sweph.swe_set_ephe_path("/home/nataly/workspace/kz.zvezdochet.sweph/lib/ephe");

  		String ss[] = {
  				"phase angle (earth-planet-sun)",
  				"phase (illumined fraction of disc)",
  				"elongation of planet",
  				"apparent diameter of disc",
  				"apparent magnitude"
  		};
  		DblObj xx = new DblObj();
  		char[] serr = new char[256];
  		StringBuffer sb = new StringBuffer(new String(serr));
  		double[] dgeo = {glon, glat, 0};

  		sweph.swe_rise_trans(tjdut, SweConst.SE_MOON, null, (int)iflag, flag, dgeo, 0.0, 0.0, xx, sb);
		System.out.print("\t" + xx.val);
  	}
}
