package kz.zvezdochet.bean;

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
}
