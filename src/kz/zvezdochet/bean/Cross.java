package kz.zvezdochet.bean;

import kz.zvezdochet.core.bean.DiagramDictionary;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.service.CrossService;

/**
 * Крест
 * @author Nataly Didenko
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
}
