package kz.zvezdochet.bean;

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
}