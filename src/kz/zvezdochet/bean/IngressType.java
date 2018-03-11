package kz.zvezdochet.bean;

import kz.zvezdochet.core.bean.Dictionary;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.service.IngressService;

/**
 * Тип ингрессии
 * @author Nataly Didenko
 */
public class IngressType extends Dictionary {
	private static final long serialVersionUID = 5901272355855042085L;

	@Override
	public ModelService getService() {
		return new IngressService();
	}
}
