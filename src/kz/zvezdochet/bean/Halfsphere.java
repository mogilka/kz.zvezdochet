package kz.zvezdochet.bean;

import java.util.HashMap;
import java.util.Map;

import kz.zvezdochet.core.bean.DiagramDictionary;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.service.HalfsphereService;

/**
 * Полусфера
 * @author Natalie Didenko
 */
public class Halfsphere extends DiagramDictionary {
	private static final long serialVersionUID = -4231996901977659751L;

	@Override
	public ModelService getService() {
		return new HalfsphereService();
	}

	public static Map<String, Double> getMap() {
		return new HashMap<String, Double>() {
			private static final long serialVersionUID = 2106400098038879004L;
			{
		        put("North", 0.0);
		        put("South", 0.0);
		        put("West", 0.0);
		        put("East", 0.0);
		    }
		};
	}

	/**
	 * Толкование для вида космограммы "Чаша"
	 */
	private String cup;

	public String getCup() {
		return cup;
	}

	public void setCup(String cup) {
		this.cup = cup;
	}
}
