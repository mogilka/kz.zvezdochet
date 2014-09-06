package kz.zvezdochet.bean;

/**
 * Аспект между объектами гороскопа
 * @author Nataly Didenko
 */
public class SkyPointAspect {

	/**
	 * Небесная точка 1
	 */
	private SkyPoint skyPoint1;
	
	/**
	 * Небесная точка 2
	 */
	private SkyPoint skyPoint2;
	
	/**
	 * Аспект
	 */
	private Aspect aspect;
	/**
	 * Расстояние между объектами в градусах
	 */
	private double score;
	/**
	 * Признак ретроградности
	 */
	private boolean retro = false;
	
	public boolean isRetro() {
		return retro;
	}

	public void setRetro(boolean retro) {
		this.retro = retro;
	}

	public SkyPoint getSkyPoint1() {
		return skyPoint1;
	}

	public void setSkyPoint1(SkyPoint skyPoint1) {
		this.skyPoint1 = skyPoint1;
	}

	public SkyPoint getSkyPoint2() {
		return skyPoint2;
	}

	public void setSkyPoint2(SkyPoint skyPoint2) {
		this.skyPoint2 = skyPoint2;
	}

	public Aspect getAspect() {
		return aspect;
	}

	public void setAspect(Aspect aspect) {
		this.aspect = aspect;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
}
