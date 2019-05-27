package kz.zvezdochet.bean;

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
}