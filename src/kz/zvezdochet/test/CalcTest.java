package kz.zvezdochet.test;

/**
 * @author Nataly Didenko
 *
 */
public class CalcTest {

	public CalcTest() {}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double lat = -43.15;
		System.out.println("%\t" + lat % 1);
		System.out.println("cut\t" + (Math.abs(lat) - (int)Math.abs(lat)));
		System.out.println("rest\t" + Math.round(((Math.abs(lat) - (int)Math.abs(lat))) * 100));
	}
}
