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

	@Override
	public ModelService getService() {
		return new CrossService();
	}
}
