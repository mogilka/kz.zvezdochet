package kz.zvezdochet.bean;

import java.util.HashMap;
import java.util.Map;

import kz.zvezdochet.core.bean.DiagramDictionary;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.service.SquareService;

/**
 * Квадрат
 * @author Natalie Didenko
 */
public class Square extends DiagramDictionary {
	private static final long serialVersionUID = 5558399056763429548L;

	@Override
	public ModelService getService() {
		return new SquareService();
	}

	public static Map<String, Double> getMap() {
		return new HashMap<String, Double>() {
			private static final long serialVersionUID = 7191513234414149484L;
			{
		        put("Childhood", 0.0);
		        put("Youth", 0.0);
		        put("Maturity", 0.0);
		        put("Oldage", 0.0);
		    }
		};
	}
}