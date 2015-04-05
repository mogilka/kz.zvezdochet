package kz.zvezdochet.bean;

import kz.zvezdochet.core.bean.Dictionary;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.service.PositionTypeService;

/**
 * Вид позиции небесного тела
 * @author Nataly Didenko
 *
 */
public class PositionType extends Dictionary {
	private static final long serialVersionUID = 1657691867756967158L;

	@Override
	public ModelService getService() {
		return new PositionTypeService();
	}
}