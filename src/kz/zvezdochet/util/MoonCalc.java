package kz.zvezdochet.util;

import java.util.Calendar;
import java.util.Date;

import kz.zvezdochet.bean.Pheno;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.util.CalcUtil;
import kz.zvezdochet.core.util.DateUtil;
import kz.zvezdochet.core.util.NumberUtil;
import kz.zvezdochet.core.util.PlatformUtil;
import kz.zvezdochet.sweph.Activator;
import swisseph.DblObj;
import swisseph.SweConst;
import swisseph.SwissEph;

/**
 * Расчёт лунного дня
 * @author Natalie Didenko
 */
public class MoonCalc {
	private String sdate;
	private String stime;
	private String szone;
	private String slat;
	private String slon;

	/**
	 * Расчёт лунного дня для заданного момента времени и места
	 * @param event событие
	 * @param date строковое значение даты
	 * @param time строковое значение времени
	 * @param zone строковое значение часового пояса
	 * @param latitude строковое значение широты местности
	 * @param longitude строковое значение долготы местности
	 * @throws DataAccessException 
	 */
	public MoonCalc(Date eventdate, String zone, String latitude, String longitude) throws DataAccessException {
  	  	sdate = DateUtil.formatCustomDateTime(eventdate, DateUtil.dbdf.toPattern());
  	  	stime = DateUtil.formatCustomDateTime(eventdate, DateUtil.stf.toPattern());
		szone = zone;
		slat = latitude;
		slon = longitude;
	}

  	public Pheno calculate() {
  		Pheno pheno = new Pheno();
		try {
	  		pheno.setDate(DateUtil.getDatabaseDateTime(sdate + " " + stime));
			double dlat = Double.parseDouble(slat);
			double dlon = Double.parseDouble(slon);

	  		//обрабатываем координаты места
	  		if (0 == dlat && 0 == dlon)
	  			dlat = 51.48; //по умолчанию Гринвич
	  		int ilondeg, ilonmin, ilonsec, ilatdeg, ilatmin, ilatsec;
	  		ilondeg = (int)dlon;
	  		ilonmin = (int)Math.round((Math.abs(dlon) - Math.abs(ilondeg)) * 100);
	  		ilonsec = 0;
	  		ilatdeg = (int)dlat;
	  		ilatmin = (int)Math.round((Math.abs(dlat) - Math.abs(ilatdeg)) * 100);
	  		ilatsec = 0;

	  	  	int iyear, imonth, iday, ihour = 0, imin = 0, isec = 0;
	  		iday = Integer.parseInt(sdate.substring(8, 10));
	  		imonth = Integer.parseInt(sdate.substring(5, 7));
	  		iyear = Integer.parseInt(sdate.substring(0, 4));
	  		ihour = Integer.parseInt(NumberUtil.trimLeadZero(stime.substring(0, 2)));
	  		imin = Integer.parseInt(stime.substring(3, 5));
	  		isec = Integer.parseInt(stime.substring(6, 8));
	  		double dzone = Double.parseDouble(szone);

			double glon, glat;
	  		//Universal Time
			double tjdut = CalcUtil.getTdjut(iyear, imonth, iday, ihour, imin, isec, dlat, dlon, dzone);

	  		//{ geographic position }
	  		glon = Math.abs(ilondeg) + ilonmin/60.0 + ilonsec/3600.0;
	  		if (dlon < 0)
	  			glon = -glon;
	  		glat = Math.abs(ilatdeg) + ilatmin/60.0 + ilatsec/3600.0;
	  		if (dlat < 0)
	  			glat = -glat;

	  	  	SwissEph sweph = new SwissEph();
			String path = PlatformUtil.getPath(Activator.PLUGIN_ID, "/lib/ephe").getPath(); //$NON-NLS-1$
	  		sweph.swe_set_ephe_path(path);

	  	  	double percent = pheno(sweph, tjdut);
	  		Date rise = rise(sweph, glon, glat, tjdut, SweConst.SE_CALC_RISE, dzone);
	  		Date set = rise(sweph, glon, glat, tjdut, SweConst.SE_CALC_SET, dzone);

	  		//расчёт координаты Луны по восходу
	  		double[] xx = new double[6];
	  		char[] serr = new char[256];
	  		StringBuffer sb = new StringBuffer(new String(serr));
	  		int[] arr = DateUtil.splitDateTime(null == rise ? set : rise);
			tjdut = CalcUtil.getTdjut(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], dlat, dlon, dzone);
			long iflag = SweConst.SEFLG_SWIEPH | SweConst.SEFLG_SIDEREAL | SweConst.SEFLG_TRUEPOS | SweConst.SEFLG_TOPOCTR;
			@SuppressWarnings("unused")
			long rflag = sweph.swe_calc_ut(tjdut, SweConst.SE_MOON, (int)iflag, xx, sb);
	  		double mcoord = xx[0];

	  		//если близко к новолунию, пытаемся установить номер дня
	  		long age = 0;
	  		Date date = null;

  			//считаем координату Солнца по восходу Луны
  			xx = new double[6];
	  		serr = new char[256];
	  		sb = new StringBuffer(new String(serr));
			rflag = sweph.swe_calc_ut(tjdut, SweConst.SE_SUN, (int)iflag, xx, sb);
	  		double scoord = xx[0];

	  		if (percent < 1) {
		  		if (rise != null) {
			  		//если координата Солнца совпадает с Луной,
			  		//считаем, что это новолуние
			  		if (scoord == mcoord) {
			  			age = 1;
			  			date = rise;
			  		} else {
				  		boolean calcable = true;
			  			if (scoord < mcoord) {
			  				double res = mcoord - scoord;
				  			if (res >= 189)
				  				scoord += 360;
				  			else
				  				calcable = false;
				  		}
				  		if (calcable) {
						  	//считаем время следующего восхода Луны
				  			Calendar cal = Calendar.getInstance();
				  			cal.setTime(rise);
				  			cal.add(Calendar.DATE, 1);
						  	int[] arr2 = DateUtil.splitDateTime(cal.getTime());
						  	arr2[3] = arr2[4] = arr2[5] = 0;
							tjdut = CalcUtil.getTdjut(arr2[0], arr2[1], arr2[2], arr2[3], arr2[4], arr2[5], dlat, dlon, dzone);
				  		  	Date rise2 = rise(sweph, glon, glat, tjdut, SweConst.SE_CALC_RISE, dzone);
				
					  		//считаем координату Луны на следующем восходе
					  		arr2 = DateUtil.splitDateTime(rise2);
							tjdut = CalcUtil.getTdjut(arr2[0], arr2[1], arr2[2], arr2[3], arr2[4], arr2[5], dlat, dlon, dzone);
				  			xx = new double[6];
					  		serr = new char[256];
					  		sb = new StringBuffer(new String(serr));
							rflag = sweph.swe_calc_ut(tjdut, SweConst.SE_MOON, (int)iflag, xx, sb);
					  		double mcoord2 = xx[0];
				
					  		//если завтрашняя координата Луны меньше сегодняшней, приводим к единому виду
		  					if (mcoord2 < mcoord)
		  						mcoord2 += 360;
				  						
					  		//если Солнце находится в пределах двух Лун,
					  		//считаем, что это новолуние
							if (scoord < mcoord2) {
				  			  	age = 1;
				  				double deltas = scoord - mcoord;
				  				double deltam = mcoord2 - mcoord;
				  				long time = rise.getTime();
						  		long time2 = rise2.getTime();
				  				long deltat = time2 - time;
				  				long res = (long)(deltas * deltat / deltam);
				  				date = new Date(time + res);
							}
				  		}
			  		}
		  		}
	  		}
	  		if (0 == age) {
	  			double res = mcoord - scoord;
	  			double SYNMONTH = 29.53058812; //synodic month (new Moon to new Moon)
		  		double num = SYNMONTH * (fixangle(res) / 360);
		  		if (res > 0)
		  			++num;
		  		else if (Math.abs(res) > 201)
		  			++num;
		  		else {
	  	  			Date cdate = DateUtil.jul2date(tjdut);
	  	  			if (dzone != 0.0)
	  	  				cdate = DateUtil.zoneDateTime(cdate, dzone);
	  	  			String strdate = DateUtil.formatDate(cdate);
	  	  			Date crise = rise;
	  	  			if (dzone != 0.0)
	  	  				crise = DateUtil.zoneDateTime(crise, dzone);
	  	  			String strdate2 = DateUtil.formatDate(crise);
	  	  			if (!strdate.equals(strdate2))
	  	  				++num;
		  		}
		  		age = (int)num;
	  		}
	  		pheno.setAge((int)age);
	  		pheno.setRise(rise);
	  		pheno.setSet(set);
	  		pheno.setPheno(date);
	  		pheno.setPercent(percent);
	  		pheno.setCoord(mcoord);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pheno;
  	}
  	
	/**
	 * Расчёт фазы Луны, угла фазы, элонгации, размер видимого диаметра диска, видимого размера диска
  	 * @param sweph эфемериды
  	 * @param tjdut универсальное время
	 * @return освещённость луны в процентах
	 */
  	private double pheno(SwissEph sweph, double tjdut) {
//		"phase angle (earth-planet-sun)",
//		"phase (illumined fraction of disc)",
//  	"elongation of planet",
//  	"apparent diameter of disc",
//  	"apparent magnitude"

  		try {
  	  		double[] xx = new double[20];
  	  		char[] serr = new char[256];
  	  		StringBuffer sb = new StringBuffer(new String(serr));
  	  		int iflag = SweConst.SEFLG_SWIEPH | SweConst.SEFLG_TRUEPOS | SweConst.SEFLG_HELCTR;
  	  		int res = sweph.swe_pheno_ut(tjdut, SweConst.SE_MOON, iflag, xx, sb);
  	  		if (SweConst.ERR == res)
  	  			System.out.println(sb);
  	  		else
  	  		return xx[1] * 100;
		} catch (Exception e) {
			e.printStackTrace();
		}
  		return 0;
  	}

  	/**
  	 * Расчёт времени восхода и захода Луны
  	 * @param sweph эфемериды
  	 * @param glon долгота
  	 * @param glat широта
  	 * @param tjdut универсальное время
  	 * @param flag тип события восход|заход
  	 * @return дата и время
  	 */
  	private Date rise(SwissEph sweph, double glon, double glat, double tjdut, int flag, double zone) {
  		DblObj xx = new DblObj();
  		char[] serr = new char[256];
  		StringBuffer sb = new StringBuffer(new String(serr));
  		double[] dgeo = {glon, glat, 0};

  		//Нормальное атмосферное давление – 760 мм рт. столба. Это давление воздуха на уровне моря при температуре 0°С на широте 45°.
  		//760 mm = 101325 Pa = 1013.25 hPa
  		try {
  			int iflag = SweConst.SEFLG_SWIEPH | SweConst.SEFLG_TRUEPOS | SweConst.SEFLG_TOPOCTR;
  	  		int res = sweph.swe_rise_trans(tjdut, SweConst.SE_MOON, null, iflag, flag, dgeo, 1013.25, 0.0, xx, sb);
  	  		if (0 == res)
  	  			return DateUtil.jul2date(xx.val);
  	  		else if (SweConst.ERR == res)
  	  			System.out.println("error occurred (usually an ephemeris problem)");
  	  		else if (-2 == res)
  	  			System.out.println("rising or setting event was not found because the object is circumpolar");
		} catch (Exception e) {
			e.printStackTrace();
		}
  		return null;
  	}

  	private double fixangle(double arg) {
  		return arg - 360 * (Math.floor(arg / 360)); 
  	}
}
