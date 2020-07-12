package kz.zvezdochet.bean;

import java.util.HashMap;
import java.util.Map;

import kz.zvezdochet.core.bean.DiagramDictionary;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.service.ZoneService;

/**
 * Зона
 * @author Natalie Didenko
 */
public class Zone extends DiagramDictionary {
	private static final long serialVersionUID = 3220756716128544984L;

	@Override
	public ModelService getService() {
		return new ZoneService();
	}

	public static Map<String, Double> getMap() {
		return new HashMap<String, Double>() {
			private static final long serialVersionUID = -2074527580863832522L;
			{
		        put("Accumulate", 0.0);
		        put("Creative", 0.0);
		        put("Transform", 0.0);
		    }
		};
	}
}