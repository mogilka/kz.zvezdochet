package kz.zvezdochet.bean;

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
}