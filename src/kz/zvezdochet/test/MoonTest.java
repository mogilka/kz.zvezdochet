package kz.zvezdochet.test;

import java.util.Arrays;

import kz.zvezdochet.core.util.DateUtil;
import swisseph.SweConst;
import swisseph.SweDate;
import swisseph.SwissEph;

public class MoonTest {

	public static void main(String[] args) {
  		args = new String[] {"31.10.2008", "00:00:00", "6", "43.15", "76.55"};
  		new MoonTest().calculate(args);
	}

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
		long iflag = SweConst.SEFLG_TRUEPOS | SweConst.SEFLG_HELCTR;
  		sweph.swe_set_ephe_path("/home/nataly/workspace/kz.zvezdochet.sweph/lib/ephe");

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

  		double[] xx = new double[20];
  		char[] serr = new char[256];
  		StringBuffer sb = new StringBuffer(new String(serr));
  		long rflag = sweph.swe_pheno_ut(tjdet, SweConst.SE_MOON, (int)iflag, xx, sb);

  		for (int i = 0; i < xx.length; i++) {
  		    System.out.println(xx[i]);
  		}
  	}

  	private String trimLeadZero(String s) {
  		if (s.indexOf('0') == 0)
  			return String.valueOf(s.charAt(1));
  		else
  			return s;
  	}
}
