package kz.zvezdochet.test;

import java.util.Arrays;

import kz.zvezdochet.core.util.DateUtil;
import swisseph.SweConst;
import swisseph.SweDate;
import swisseph.SwissEph;
import swisseph.SwissLib;

/**
 * Тестовый расчёт по Швейцарским эфемеридам
 * @author Natalie Didenko
 * 
 * Юлианский календарь начал действовать 1 января 45 года до н.э., но нормально начал функционировать с 1 марта 4 года н.э. (см. ниже).
 * Чтобы восстановить соответствие календаря временам года, Цезарь вставил 67 дополнительных дней между ноябрём и декабрём, 
 * разделив их на два месяца: intercalaris prior и intercalaris posterior. Вместе с mensis intercalaris 46 год до н. э. длился 445 дней.
 * Римские жрецы по неизвестной причине объявляли високосным каждый третий год календаря.
 * В итоге с 44 до 9 года до н. э. было введено 12 високосных годов вместо 9.
 * Первый год нового календаря — 45 год до н. э. — был високосным.
 * После первого года (45 года до н. э.) високосными должны были быть 41, 37, 33, …, 13, 9 годы до н.э.
 * Но жрецы високосными годами сделали 42, 39, …, 12 и 9 годы до н. э.
 * Эту ошибку исправил император Август: на протяжении 16 лет — с 9 года до н. э. по 8 год н. э. високосных годов не было,
 * так как 5 и 1 годы до н. э. и 4 год н. э. (то есть, 749, 753 и 757 годы от «основания Рима») были приняты как невисокосные.
 * @link https://ru.wikipedia.org/wiki/Юлианский_календарь
 * 
 * В 1582 году папа Григорий XIII издал постановление о реформе календаря.
 * В Григорианском календаре уменьшили количество високосных лет.
 * Для возврата весеннего равноденствия на 21 марта было пропущено 10 дней, и в 1582 году следующий после 4 октября день был объявлен 15 октября.
 */
public class Swetest {

  	public static void main(String[] argv) {
  		argv = new String[] {"15.06.1520", "06:28:00", "1", "41.53", "12.3"};
  		new Swetest().calculate(argv);
  		// $ java Swetest 08.12.2007 08:08:08 6 43.15 76.55
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
		long iflag = SweConst.SEFLG_SWIEPH | SweConst.SEFLG_SIDEREAL | SweConst.SEFLG_SPEED | SweConst.SEFLG_TRUEPOS | SweConst.SEFLG_TOPOCTR;
  		sweph.swe_set_ephe_path("/home/natalie/workspace/kz.zvezdochet.sweph/lib/ephe");
  		sweph.swe_set_sid_mode(SweConst.SE_SIDM_DJWHAL_KHUL, 0, 0);

//SE_SIDM_DJWHAL_KHUL			215.10		106.50		328.32
//SE_SIDM_DELUCE				215.65		107.05		328.87
//SE_SIDM_GALCENT_0SAG			216.61		108.00		329.83
//SE_SIDM_BABYL_KUGLER1			217.63		109.02		330.85
//SE_SIDM_ALDEBARAN_15TAU		218.70		110.10		331.92
//SE_SIDM_FAGAN_BRADLEY			218.72		110.12		331.94
//SE_SIDM_BABYL_HUBER			218.83		110.22		332.05
//SE_SIDM_BABYL_ETPSC			218.94		110.34		332.16
//SE_SIDM_BABYL_KUGLER2			219.03		110.42		332.25
//SE_SIDM_LAHIRI				219.61		111.00		332.82
//SE_SIDM_TRUE_CITRA			219.62		111.02		332.84
//SE_SIDM_KRISHNAMURTI			219.70		111.10		332.92
//SE_SIDM_BABYL_KUGLER3			219.88		111.27		333.10
//SE_SIDM_SS_CITRA				220.46		111.85		333.67
//SE_SIDM_JN_BHASIN				220.70		112.10		333.92
//SE_SIDM_YUKTESHWAR			220.98		112.38		334.20
//SE_SIDM_RAMAN					221.05		112.45		334.27
//SE_SIDM_SURYASIDDHANTA		222.57		113.96		335.78
//SE_SIDM_ARYABHATA				222.57		113.96		335.78
//SE_SIDM_SURYASIDDHANTA_MSUN	222.78		114.18		336.00
//SE_SIDM_ARYABHATA_MSUN		222.81		114.20		336.02
//SE_SIDM_HIPPARCHOS			223.22		114.61		336.43
//SE_SIDM_SS_REVATI				223.36		114.75		336.58
//SE_SIDM_USHASHASHI			223.41		114.80		336.62
//SE_SIDM_SASSANIAN				223.47		114.86		336.69
//SE_SIDM_TRUE_REVATI			223.59		114.98		336.80
//SE_SIDM_TRUE_PUSHYA			?
//SE_SIDM_J1900					242.07		133.46		355.28
//SE_SIDM_B1950					242.76		134.16		355.98
//SE_SIDM_J2000					243.46		134.86		356.68

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
  		boolean julian = (iyear < 1582);
  		tjd = julian
  			? SweDate.getJulDay(iyear, imonth, iday, dhour, false)
  			: SweDate.getJulDay(iyear, imonth, iday, dhour, true);
//  		System.out.println("greg: " + SweDate.getJulDay(iyear, imonth, iday, dhour)
//  				+ " jul: "+ SweDate.getJulDay(iyear, imonth, iday, dhour, false));
  		deltat = SweDate.getDeltaT(tjd);
  		//Universal Time
  		tjdut = tjd;
  		tjdet = tjd + deltat;
  		
  		//расчёт эфемерид планет
  		long rflag;
  		double[] planets = new double[51];
  		String[] pnames = new String[51];
  		double[] xx = new double[6]; //array of 6 doubles for longitude, latitude, distance, speed in long., speed in lat., and speed in dist.
  		char[] serr = new char[256];
  		StringBuffer sb = new StringBuffer(new String(serr));
  		int[] list = getPlanetList();
  		for (int i = 0; i < list.length; i++) {
  		    rflag = sweph.swe_calc_ut(tjdut, list[i], (int)iflag, xx, sb);
  		    planets[i] = xx[0];
  		    if (xx[3] < 0)
  		    	planets[i] *= -1;
  		    pnames[i] = sweph.swe_get_planet_name(list[i]);
  		}
  		for (int i = 0; i < planets.length; i++)
  			System.out.println(i + " " + pnames[i] + " = " + planets[i]);

  		//расчёт куспидов домов
  		//{ for houses: ecliptic obliquity and nutation }
  		sb = new StringBuffer(new String(serr));
  		rflag = sweph.swe_calc_ut(tjdut, SweConst.SE_ECL_NUT, 0, xx, sb);
  		eps_true = xx[0];
  		nut_long = xx[2];
  		//{ geographic position }
  		glon = Math.abs(ilondeg) + ilonmin/60.0 + ilonsec/3600.0;
  		if (lon < 0)
  			glon = -glon;
  		//if (combo_EW.ItemIndex > 0) then glon := -glon;
  		glat = Math.abs(ilatdeg) + ilatmin/60.0 + ilatsec/3600.0;
  		if (lat < 0)
  			glat = -glat;
  		//if (combo_NS.ItemIndex > 0) then glat := -glat;
  		//{ sidereal time }
  		tsid = new SwissLib().swe_sidtime(tjdut);
  		tsid = tsid + glon / 15;
  		armc = tsid * 15;
  		//{ house method }
  		double[] ascmc = new double[10];
  		double[] hcusps = new double[13];
  		//используем систему Плацидуса
  		sweph.swe_houses(tjdut, SweConst.SEFLG_SIDEREAL, glat, glon, 'P', hcusps, ascmc);
/*
  		‘B’         Alcabitus
        ‘Y’         APC houses
        ‘X’         Axial rotation system / Meridian system / Zariel
        ‘H’         Azimuthal or horizontal system
        ‘C’         Campanus
        ‘F’         Carter "Poli-Equatorial"
        ‘A’ or ‘E’  Equal (cusp 1 is Ascendant)
        ‘D’         Equal MC (cusp 10 is MC)
        ‘N’         Equal/1=Aries
        ‘G’         Gauquelin sector
                     Goelzer -> Krusinski
                     Horizontal system -> Azimuthal system
        ‘I’         Sunshine (Makransky, solution Treindl)
        ‘i’         Sunshine (Makransky, solution Makransky)
        ‘K’         Koch
        ‘U’         Krusinski-Pisa-Goelzer
                    Meridian system -> axial rotation
        ‘M’         Morinus
                     Neo-Porphyry -> Pullen SD
                     Pisa -> Krusinski
        ‘P’         Placidus
                     Poli-Equatorial -> Carter
        ‘T’         Polich/Page (“topocentric” system)
        ‘O’         Porphyrius
        ‘L’         Pullen SD (sinusoidal delta) – ex Neo-Porphyry
        ‘Q’         Pullen SR (sinusoidal ratio)
        ‘R’         Regiomontanus
        ‘S’         Sripati
                     “Topocentric” system -> Polich/Page
        ‘V’         Vehlow equal (Asc. in middle of house 1)
        ‘W’         Whole sign
              		Zariel -> Axial rotation system
*/
  		System.out.println("\n");
  		for (int i = 1; i < hcusps.length; i++)
  			System.out.println("house " + i + " = " + hcusps[i]);
  		System.out.println("\n");
  		for (int i = 0; i < ascmc.length; i++)
  			System.out.println("ascmc " + i + " " + ascmc[i]);

  		//расчёт координат звёзд
  		String[] scodes = {"Achernar", "Acrux", "Acubens", "Aculeus", "Acumen", "Adhab", "Adhil", "Ain", "Akrab", "Al Hecka", "Alcyone", "Aldebaran", "Alderamin",
  				"Alfecca Meridiana", "Algenib", "Algieba", "Algol", "Algorab", "Alhena", "Alioth", "Alkaid", "Alkes", "Almaak", "Alphard", "Alpheratz", "Alrischa",
  				"Altair", "Andromeda Galaxy", "Ankaa", "Antares", "Arcturus", "Ascella", "Asellus Australis", "Baten Kaitos", "Bellatrix", "Betelgeuse", "Canopus",
  				"Capella", "Capulus", "Castor", "Dabih", "Deneb", "Deneb Algedi", "Denebola", "Diadem", "Difda", "Dschubba", "Dubhe", "El Nath", "Etamin", "Facies",
  				"Fomalhaut", "Gal. Center", "Gemma", "Grafias", "Great Attractor", "Hadar", "Hamal", "Kaus Borealis", "Kochab", "Lesath", "Markab", "Menkalinan",
  				"Menkar", "Merak", "Mimosa", "Mintaka", "Mira", "Mirach", "Mirfak", "Mirzam", "Mizar", "Nunki", "Phact", "Phecda", "Polaris", "Pollux", "Porrima",
  				"Praesepe", "Praesepe Cluster", "Procyon", "Propus etaGem", "Ras Algethi", "Ras Elased Australis", "Rasalhague", "Regulus", "Rigel", "Rukbat",
  				"Sadalmelek", "Sadalsuud", "Scheat", "Schedar", "Sheratan", "Sinistra", "Sirius", "Skat", "Spica", "Sualocin", "Tejat", "Thuban", "Toliman",
  				"Unukalhai", "Vega", "Vindemiatrix", "Virgo Cluster", "Wasat", "Zaniah", "Zaurak", "Zavijava", "Zosma", "Zuben Elgenubi", "Zuben Eshamali"
  			};
  		System.out.println("\n");
  		for (String scode: scodes) {
	  		sb = new StringBuffer(new String(serr));
	  		rflag = sweph.swe_fixstar_ut(new StringBuffer(scode), tjdut, (int)iflag, xx, sb);
  			System.out.println(scode + " = " + xx[0] + "\t" + xx[1] + "\t" + xx[2] + "\t" + xx[3] + "\t" + xx[4] + "\t" + xx[5]);
  		}
  	}
  	
  	private String trimLeadZero(String s) {
  		if (s.indexOf('0') == 0)
  			return String.valueOf(s.charAt(1));
  		else
  			return s;
  	}

  	private int[] getPlanetList() {
  		int[] list = new int[51];
  		for (int i = SweConst.SE_SUN; i <= (SweConst.SE_INTP_PERG); i++)
  			list[i] = i;
  		for (int i = SweConst.SE_CUPIDO; i <= (SweConst.SE_PROSERPINA); i++)
  			list[i - 17] = i;
  		return list;
  	}
}
