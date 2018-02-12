package kz.zvezdochet.test;

import java.util.Arrays;
import java.util.Date;

import jodd.datetime.JDateTime;
import jodd.datetime.JulianDateStamp;
import kz.zvezdochet.core.util.DateUtil;
import swisseph.SweConst;
import swisseph.SweDate;
import swisseph.SwissEph;

public class Sweclipse {

  	public static void main(String[] argv) {
  		argv = new String[] {"29.01.2018", "00:00:00", "6", "43.15", "76.55"};
  		new Sweclipse().calculate(argv);
  	}
  
	@SuppressWarnings("unused")
  	private void calculate(String[] argv) {

  		//обрабатываем координаты места
  		double lat = Double.parseDouble(argv[3]);
  		double lon = Double.parseDouble(argv[4]);
  		int ilondeg, ilonmin, ilonsec, ilatdeg, ilatmin, ilatsec;
  		ilondeg = (int)lon;
  		ilonmin = (int)Math.round((Math.abs(lon) - Math.abs(ilondeg)) * 100);
  		ilonsec = 0;
  		ilatdeg = (int)lat;
  		ilatmin = (int)Math.round((Math.abs(lat) - Math.abs(ilatdeg)) * 100);
  		ilatsec = 0;

  	  	SwissEph sweph = new SwissEph();
		sweph.swe_set_topo(lon, lat, 0);
		int iflag = SweConst.SEFLG_SWIEPH | SweConst.SEFLG_SIDEREAL | SweConst.SEFLG_SPEED | SweConst.SEFLG_TRUEPOS | SweConst.SEFLG_TOPOCTR;
  		sweph.swe_set_ephe_path("/home/nataly/workspace/kz.zvezdochet.sweph/lib/ephe");
  		sweph.swe_set_sid_mode(SweConst.SE_SIDM_DJWHAL_KHUL, 0, 0);

  		//обрабатываем дату
  		int iyear, imonth, iday, ihour = 0, imin = 0, isec = 0;
  		String sdate = argv[0];
  		iday = Integer.parseInt(sdate.substring(0, 2));
  		imonth = Integer.parseInt(sdate.substring(3, 5));
  		iyear = Integer.parseInt(sdate.substring(6, 10));
  		
  		//обрабатываем время
  		String stime = argv[1];
  		double timing = Double.parseDouble(trimLeadZero(stime.substring(0,2))); //час по местному времени
  		double zone = Double.parseDouble(argv[2]); //зона
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
  		
  		double tjd, tjdet, tjdut, tsid, armc, dhour, deltat;
  		double eps_true, nut_long, glon, glat;
  		dhour = ihour + imin/60.0 + isec/3600.0;
  		tjd = SweDate.getJulDay(iyear, imonth, iday, dhour, true);
  		deltat = SweDate.getDeltaT(tjd);
  		//Universal Time
  		tjdut = tjd;
  		tjdet = tjd + deltat;
  		
  		//расчёт затмений
  		double[] tret = new double[10];
  		double[] attr = new double[20];
  		double[] geopos = new double[10];

  		char[] serr = new char[256];
  		StringBuffer sb = new StringBuffer(new String(serr));

  		int whicheph = 0; /* default ephemeris */

  		double tjd_start = tjdut;

  		int ifltype = SweConst.SE_ECL_ALLTYPES_SOLAR | SweConst.SE_ECL_CENTRAL | SweConst.SE_ECL_NONCENTRAL;

  		/* find next Sun eclipse anywhere on earth */
  		int eclflag = sweph.swe_sol_eclipse_when_glob(tjd_start, whicheph, ifltype, tret, 0, sb);
  		if (eclflag != SweConst.ERR)
  			System.out.println("solar eclipse type: " + eclflag);
  		/*
  		 * 1 - SE_ECL_CENTRAL
  		 * 2 - SE_ECL_NONCENTRAL
  		 * 4 - SE_ECL_TOTAL
  		 * 8 - SE_ECL_ANNULAR
  		 * 16 - SE_ECL_PARTIAL
  		 * 32 - SE_ECL_ANNULAR_TOTAL
  		 */
  		System.out.println("the time of the next solar eclipse: " + tret[0] + " -> " + jul2date(tret[0]));

  		//now we can find geographical position of the eclipse maximum
  		tjd_start = tret[0];
  		eclflag = sweph.swe_sol_eclipse_where(tjd_start, whicheph, geopos, attr, sb);
//  		if (eclflag == ERR)
//  		  return ERR;
  		System.out.println("the geographical position of the eclipse maximum: " + geopos[0] + ", " + geopos[1]);

  		//now we can calculate the four contacts for this place.
  		//The start time is chosen a day before the maximum eclipse:
  		tjd_start = tret[0] - 1;
  		eclflag = sweph.swe_sol_eclipse_when_loc(tjd_start, whicheph, geopos, tret, attr, 0, sb);
//  		if (eclflag == ERR)
//  		  return ERR;
  		System.out.println("time of solar eclipse (Julian day number): " + tret[0] + " -> " + jul2date(tret[0]));
  		System.out.println("first contact: " + tret[1] + " -> " + jul2date(tret[1]));
  		System.out.println("second contact: " + tret[2] + " -> " + jul2date(tret[2]));
  		System.out.println("third contact: " + tret[3] + " -> " + jul2date(tret[3]));
  		System.out.println("fourth contact: " + tret[4] + " -> " + jul2date(tret[4]) + "\n");

  		/* find next Moon eclipse anywhere on earth */
  		ifltype = SweConst.SE_ECL_ALLTYPES_LUNAR;
  		eclflag = sweph.swe_lun_eclipse_when(tjd_start, whicheph, ifltype, tret, 0, sb);
  		if (eclflag != SweConst.ERR)
  		  System.out.println("lunar eclipse type: " + eclflag);
  		int i = SweConst.SE_ECL_PARTIAL;
  		/*
  		 * 4 - SE_ECL_TOTAL
  		 * 64 - SE_ECL_PENUMBRAL
  		 * 16 - SE_ECL_PARTIAL
  		 */
  		System.out.println("the time of the next lunar eclipse: " + tret[0] + " -> " + jul2date(tret[0]));
	}
  	
  	private String trimLeadZero(String s) {
  		if (s.indexOf('0') == 0)
  			return String.valueOf(s.charAt(1));
  		else
  			return s;
  	}

  	private static Date jul2date(double jd) {
  		JulianDateStamp julianStamp = new JulianDateStamp(jd);
  		JDateTime jdate = new JDateTime(julianStamp);
  		return new Date(jdate.getTimeInMillis());
  	}
}
