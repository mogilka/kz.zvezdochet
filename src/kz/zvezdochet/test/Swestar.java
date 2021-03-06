package kz.zvezdochet.test;

import java.util.Arrays;

import kz.zvezdochet.core.util.DateUtil;
import swisseph.SweConst;
import swisseph.SweDate;
import swisseph.SwissEph;

public class Swestar {

  	public static void main(String[] argv) {
  		argv = new String[] {"08.12.2007", "08:08:08", "6", "43.15", "76.55"};
  		new Swestar().calculate(argv);
  	}
  
	@SuppressWarnings("unused")
  	private void calculate(String[] argv) {
//		SE_STARFILE    = 'fixstars.cat';

		String[] snames = new String[] {
			"Aldebaran",
			"Algol",
			"Antares",
			"Regulus",
			"Polaris",
			"Rigel",
			"Alpheratz",
			"Mirach",
			"Altair",
			"Canopus",
			"Capella",
			"Arcturus",
			"Sirius",
			"Murzim",
			"Procyon",
			"Schedar",
			"Toliman",
			"Hadar",
			"Alderamin",
			"Menkar",
			"Diadem",
			"Alfecca Meridiana",
			"Alkes",
			"Acrux",
			"Deneb Adige",
			"Sualocin",
			"Thuban",
			"Achernar",
			"Ras Algethi",
			"Alphard",
			"Vega",
			"Rasalhague",
			"Betelgeuse",
			"Bellatrix",
			"Markab",
			"Scheat",
			"Mirfak",
			"Capulus",
			"Fomalhaut",
			"Dubhe",
			"Phact",
			"Ankaa",
			"Hamal",
			"El Nath",
			"Alcyone",
			"Castor",
			"Pollux",
			"Alhena",
			"Acubens",
			"Denebola",
			"Zosma",
			"Spica",
			"Vindemiatrix",
			"Zuben Elgenubi",
			"Zuben Eshamali",
			"Aculeus",
			"Acumen",
			"Rukbat",
			"Facies",
			"Deneb Algedi",
			"Sadalmelek",
			"Sadalsuud",
			"Alrischa"
		};

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
  		sweph.swe_set_ephe_path("/home/natalie/workspace/kz.zvezdochet.sweph/lib/ephe");
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
  		
  		//расчёт эфемерид звёзд
  		long rflag;
  		double[] stars = new double[2];
  		double[] xx = new double[6];
  		char[] serr = new char[256];
  		StringBuffer sb = new StringBuffer(new String(serr));
  		for (int i = 0; i < snames.length; i++) {
  			String sname = snames[i];
  		    rflag = sweph.swe_fixstar_ut(new StringBuffer(sname), tjdut, iflag, xx, sb);
  			System.out.println(i + "\t" + sname + "\t" + xx[0]);
  		}
/*
star		=name of fixed star to be searched, returned name of found star
tjd_ut		=Julian day in Universal Time (swe_fixstar_ut())
tjd_et		=Julian day in Ephemeris Time (swe_fixstar())
iflag		=an integer containing several flags that indicate what        kind of computation is wanted
xx			=array of 6 doubles for longitude, latitude, distance, speed in long., speed in lat., and speed in dist.
serr[256]	=character string to contain error messages in case of error.
 */
  	}
  	
  	private String trimLeadZero(String s) {
  		if (s.indexOf('0') == 0)
  			return String.valueOf(s.charAt(1));
  		else
  			return s;
  	}
}
