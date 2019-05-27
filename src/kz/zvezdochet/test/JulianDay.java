package kz.zvezdochet.test;

import java.util.Date;

import jodd.datetime.JDateTime;
import jodd.datetime.JulianDateStamp;

/**
 * Конвертация Юлианского дня в дату
 * @author Natalie Didenko
 *
 */
public class JulianDay {

  	public static void main(String[] argv) {
  		double jd = 2452082.002572339;
  		JulianDateStamp julianStamp = new JulianDateStamp(jd);
  		JDateTime jdate = new JDateTime(julianStamp);
  		Date date = new Date(jdate.getTimeInMillis());
  		System.out.println(jd + " -> " + date);
  	}
}
