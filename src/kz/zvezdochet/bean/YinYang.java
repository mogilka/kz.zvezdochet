package kz.zvezdochet.bean;

import java.util.HashMap;
import java.util.Map;

import kz.zvezdochet.core.bean.DiagramDictionary;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.service.YinYangService;

/**
 * Женское и мужское начало Инь-Ян
 * @author Natalie Didenko
 */
public class YinYang extends DiagramDictionary {
	private static final long serialVersionUID = 8688080691435619306L;

	@Override
	public ModelService getService() {
		return new YinYangService();
	}

	public static Map<String, Double> getMap() {
		return new HashMap<String, Double>() {
			private static final long serialVersionUID = 9179267417802759621L;
			{
		        put("Male", 0.0);
		        put("Female", 0.0);
		    }
		};
	}
}