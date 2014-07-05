package kz.zvezdochet.test;

import kz.zvezdochet.core.util.CalcUtil;
import swisseph.SweConst;
import swisseph.SweDate;
import swisseph.SwissEph;
import swisseph.SwissLib;

public class Swetest {

  	public static void main(String[] argv) {
  		argv = new String[] {"07.08.1975", "22:43:35", "7", "43.15", "76.55"};
  		new Swetest().calculate(argv);
  		/*
  		 * argv = ["07.08.1975", "22:43:35", "7", "43.15", "76.55"]
  		 * $ java Swetest 07.08.1975 22:43:35 7 43.15 76.55
  		 */
  	}
  
	@SuppressWarnings("unused")
  	private void calculate(String[] argv) {
//		final int SE_ECL_NUT = 		-1;
//	  	final int SE_SUN  =          0;
//	  	final int SE_MOON  =         1;
//	  	final int SE_MERCURY  =      2;
//	  	final int SE_VENUS  =        3;
//	  	final int SE_MARS  =         4;
//	  	final int SE_JUPITER  =      5;
//	  	final int SE_SATURN  =       6;
//	  	final int SE_URANUS  =       7;
//	  	final int SE_NEPTUNE  =      8;
//	  	final int SE_PLUTO  =        9;
//	  	final int SE_MEAN_NODE  =    10;
//	  	final int SE_TRUE_NODE  =    11;
//	  	final int SE_MEAN_APOG  =    12;
//	  	final int SE_OSCU_APOG  =    13;
//	  	final int SE_EARTH  =        14;
//	  	final int SE_CHIRON  =       15;
//	  	final int SE_PHOLUS  =       16;
//	  	final int SE_CERES  =        17;
//	  	final int SE_PALLAS  =       18;
//	  	final int SE_JUNO  =         19;
//	  	final int SE_VESTA  =        20;
//	  	final int SE_INTP_APOG  =    21;
//	  	final int SE_INTP_PERG  =    22;
//	
//	  	final int SE_NPLANETS  =     21;
//	  	final int SE_AST_OFFSET  =   10000;
//	  	final int SE_FICT_OFFSET  =  40;
//	  	final int SE_NFICT_ELEM  =   15;
//	
//	    // Hamburger or Uranian "planets" 
//	  	final int SE_CUPIDO  =       	40;
//	  	final int SE_HADES  =        	41;
//	  	final int SE_ZEUS  =         	42;
//	  	final int SE_KRONOS  =       	43;
//	  	final int SE_APOLLON  =      	44;
//	  	final int SE_ADMETOS  =      	45;
//	  	final int SE_VULKANUS  =     	46;
//	  	final int SE_POSEIDON  =     	47;
//	    // other ficticious bodies 
//	  	final int SE_ISIS  =                 48;
//	  	final int SE_NIBIRU  =               49;
//	  	final int SE_HARRINGTON  =           50;
//	  	final int SE_NEPTUNE_LEVERRIER  =    51;
//	  	final int SE_NEPTUNE_ADAMS  =        52;
//	  	final int SE_PLUTO_LOWELL  =         53;
//	  	final int SE_PLUTO_PICKERING  =      54;
//	  	final int SE_VULCAN  =               55;
//	  	final int SE_WHITE_MOON  =           56;
//	  	final int SE_PROSERPINA  =           57;
//	
//	    /*  flag bits for parameter iflag in function swe_calc()
//	    The flag bits are defined in such a way that iflag = 0 delivers what one
//	    usually wants:
//	      - the default ephemeris (SWISS) is used,
//	      - apparent geocentric positions referring to the true equinox of date are returned.
//	      If not only coordinates, but also speed values are required, use iflag = SEFLG_SPEED.
//	    */
//	  	final long SEFLG_JPLEPH =        1;          // use JPL ephemeris 
//	  	final long SEFLG_SWIEPH =    	   2;          // use SWISSEPH ephemeris 
//	  	final long SEFLG_MOSEPH =    	   4;          // use Moshier ephemeris 
//	  	final long SEFLG_HELCTR =        8;          // return heliocentric position 
//	  	final long SEFLG_TRUEPOS =       16;         // return true positions, not apparent 
//	  	final long SEFLG_J2000 =         32;         // no precession, i.e. give J2000 equinox 
//	  	final long SEFLG_NONUT =         64;         // no nutation, i.e. mean equinox of date 
//	  	final long SEFLG_SPEED =         256;        // high precision speed (analytical computation) 
//	  	final long SEFLG_NOGDEFL =       512;        // turn off gravitational deflection 
//	  	final long SEFLG_NOABERR =       1024;       // turn off 'annual' aberration of light 
//	  	final long SEFLG_EQUATORIAL =    (2*1024);   // equatorial positions are wanted 
//	  	final long SEFLG_XYZ =           (4*1024);   // cartesian, not polar, coordinates are wanted
//	  	final long SEFLG_RADIANS =       (8*1024);   // coordinates are wanted in radians, not degrees 
//	  	final long SEFLG_BARYCTR =       (16*1024);  // barycentric positions 
//	  	final long SEFLG_TOPOCTR =       (32*1024);  // topocentric positions 
  		
  		long iflag = 0;
  		int iyear, imonth, iday, ihour = 0, imin = 0, isec = 0;
  		
  	  	SwissEph sweph = new SwissEph();
  		sweph.swe_set_ephe_path("/home/cyber/ephe");
  		
  		//обрабатываем дату
  		String sdate = argv[0];
  		iday = Integer.parseInt(sdate.substring(0, 2));
  		imonth = Integer.parseInt(sdate.substring(3, 5));
  		iyear = Integer.parseInt(sdate.substring(6, 10));
  		/**
  		 * correction - переменная целого типа
  		 */
  		int correction = CalcUtil.trunc(iyear / 70);
  		
  		//обрабатываем время
  		String stime = argv[1];
  		double timing = Double.parseDouble(trimLeadZero(stime.substring(0,2)));
  		double zone = Double.parseDouble(argv[2]);
  		if (zone < 0) {
  			timing = timing - zone; //TODO не учтено то что зона мб не целым числом!
  		} else {
  			if (timing >= zone) {
  				timing = timing - zone;
  			} else {
  				timing = timing + 24 - zone;
  	  		}
  		}
  		if (timing >= 24) timing -= 24;
  		ihour = (int)Math.round(timing / 1);
  		imin = Integer.parseInt(trimLeadZero(stime.substring(3,5)));
  		isec = Integer.parseInt(trimLeadZero(stime.substring(6,8)));

  		//обрабатываем координаты места
  		double lat = Double.parseDouble(argv[3]);
  		double lon = Double.parseDouble(argv[4]);
  		int ilondeg, ilonmin, ilonsec, ilatdeg, ilatmin, ilatsec;
  		ilondeg = CalcUtil.trunc(Math.abs(lon));
  		ilonmin = CalcUtil.trunc(Math.abs(lon) - ilondeg) * 100;
  		ilonsec = 0;
  		ilatdeg = CalcUtil.trunc(Math.abs(lat));
  		ilatmin = CalcUtil.trunc(Math.abs(lat) - ilatdeg) * 100;
  		ilatsec = 0;
  		
  		double tjd, tjdet, tjdut, tsid, armc, dhour, deltat;
  		double eps_true, nut_long, glon, glat;
  		dhour = ihour + imin/60.0 + isec/3600.0;
  		tjd = SweDate.getJulDay(iyear, imonth, iday, dhour, true);
  		deltat = SweDate.getDeltaT(tjd);
  		//Universal Time
  		tjdut = tjd;
  		tjdet = tjd + deltat;
  		
  		//расчёт эфемерид планет
  		long rflag;
  		double[] planets = new double[15];
  		String[] pnames = new String[15];
  		double[] xx = new double[6];
  		char[] serr = new char[256];
  		StringBuffer sb = new StringBuffer(new String(serr));
  		int[] list = getPlanetList();
  		for (int i = 0; i < list.length; i++) {
  		    rflag = sweph.swe_calc(tjdet, list[i], (int)iflag, xx, sb);
  		    planets[i] = correctValue(xx[0], correction);
  		    pnames[i] = sweph.swe_get_planet_name(list[i]);
  		}

  		//расчёт куспидов домов
  		//{ for houses: ecliptic obliquity and nutation }
  		rflag = sweph.swe_calc(tjdet, SweConst.SE_ECL_NUT, 0, xx, sb);
  		eps_true = xx[0];
  		nut_long = xx[2];
  		//{ geographic position }
  		glon = ilondeg + ilonmin/60.0 + ilonsec/3600.0;
  		if (lon < 0) glon = -glon;
  		//if (combo_EW.ItemIndex > 0) then glon := -glon;
  		glat = ilatdeg + ilatmin/60.0 + ilatsec/3600.0;
  		if (lat < 0) glat = -glat;
  		//if (combo_NS.ItemIndex > 0) then glat := -glat;
  		//{ sidereal time }
  		tsid = new SwissLib().swe_sidtime(tjdut);
  		tsid = tsid + glon / 15;
  		armc = tsid * 15;
  		//{ house method }
  		double[] ascmc = new double[10];
  		double[] hcusps = new double[13];
  		//используем систему Плацидуса
  		sweph.swe_houses_armc(armc, glat, eps_true, 'P', hcusps, ascmc);
  		for (int i = 0; i < 13; i++)
  			hcusps[i] = correctValue(hcusps[i], correction);
  		
  		for (int i = 0; i < planets.length; i++) {
  			System.out.println(i + " " + pnames[i] + " = " + planets[i]);
  		}
  		System.out.println("\n");
  		for (int i = 1; i < hcusps.length; i++) {
  			System.out.println("house " + i + " = " + hcusps[i]);
  		}
  		System.out.println("\n");
  		for (int i = 0; i < ascmc.length; i++) {
  			System.out.println("ascmc " + i + " " + correctValue(ascmc[i], correction));
  		}
  	}
  	
  	private double correctValue(double n, int correction) {
  		if (n - correction > 0)
  			return CalcUtil.decToDeg(n - correction);
  		else
  			return CalcUtil.decToDeg(n + 360 - correction);
  	}

  	private String trimLeadZero(String s) {
  		if (s.indexOf('0') == 0)
  			return String.valueOf(s.charAt(1));
  		else
  			return s;
  	}

  	private int[] getPlanetList() {
  		int[] list = new int[15];
  		for (int i = SweConst.SE_SUN; i < (SweConst.SE_MEAN_NODE); i++)
  			list[i] = i;
  		list[10] = SweConst.SE_TRUE_NODE;
  		list[11] = SweConst.SE_MEAN_APOG;
  		list[12] = SweConst.SE_CHIRON;
  		list[13] = SweConst.SE_WHITE_MOON;
  		list[14] = SweConst.SE_PROSERPINA;
  		return list;
  	}
}