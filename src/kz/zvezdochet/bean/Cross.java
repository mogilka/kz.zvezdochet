package kz.zvezdochet.bean;

import java.util.HashMap;
import java.util.Map;

import kz.zvezdochet.core.bean.DiagramDictionary;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.service.CrossService;

/**
 * Крест
 * @author Natalie Didenko
 */
public class Cross extends DiagramDictionary {
	private static final long serialVersionUID = -7024846494191754532L;

	/**
	 * Толкование конфигурации креста
	 */
	private String configuration;
	/**
	 * Толкование конфигурации тау-креста
	 */
	private String tau;

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public String getTau() {
		return tau;
	}

	public void setTau(String tau) {
		this.tau = tau;
	}

	@Override
	public ModelService getService() {
		return new CrossService();
	}

	public static Map<String, Double> getMap() {
		return new HashMap<String, Double>() {
			private static final long serialVersionUID = -5031149237694234316L;
			{
		        put("Cardinal", 0.0);
		        put("Fixed", 0.0);
		        put("Mutable", 0.0);
		    }
		};
	}
}
