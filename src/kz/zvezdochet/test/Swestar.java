package kz.zvezdochet.test;

import kz.zvezdochet.core.util.CalcUtil;
import swisseph.SweConst;
import swisseph.SweDate;
import swisseph.SwissEph;

public class Swestar {

  	public static void main(String[] argv) {
  		argv = new String[] {"08.12.2007", "08:08:08", "6", "43.15", "76.55"};
  		new Swestar().calculate(argv);
  		/*
  		 * argv = ["08.12.2007", "08:08:08", "6", "43.15", "76.55"]
  		 * $ java Swetest 08.12.2007 08:08:08 6 43.15 76.55
  		 */
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

		int iflag = SweConst.SEFLG_SIDEREAL | SweConst.SEFLG_SPEED;
  		int iyear, imonth, iday, ihour = 0, imin = 0, isec = 0;
  		
  	  	SwissEph sweph = new SwissEph();
  		sweph.swe_set_ephe_path("/home/nataly/workspace/kz.zvezdochet.sweph/lib/ephe");
  		sweph.swe_set_sid_mode(SweConst.SE_SIDM_DJWHAL_KHUL, 0, 0);

  		//обрабатываем дату
  		String sdate = argv[0];
  		iday = Integer.parseInt(sdate.substring(0, 2));
  		imonth = Integer.parseInt(sdate.substring(3, 5));
  		iyear = Integer.parseInt(sdate.substring(6, 10));
  		
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
  		
  		//расчёт эфемерид звёзд
  		long rflag;
  		double[] stars = new double[2];
  		double[] xx = new double[6];
  		char[] serr = new char[256];
  		StringBuffer sb = new StringBuffer(new String(serr));
  		for (int i = 0; i < snames.length; i++) {
  			String sname = snames[i];
  		    rflag = sweph.swe_fixstar(new StringBuffer(sname), tjdet, iflag, xx, sb);
  			System.out.println(i + "\t" + sname + "\t" + xx[0]);
  		}
  	}
  	
  	private String trimLeadZero(String s) {
  		if (s.indexOf('0') == 0)
  			return String.valueOf(s.charAt(1));
  		else
  			return s;
  	}
}
