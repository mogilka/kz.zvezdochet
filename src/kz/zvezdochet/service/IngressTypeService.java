package kz.zvezdochet.service;

import kz.zvezdochet.bean.IngressType;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DictionaryService;

/**
 * Сервис типов ингрессий
 * @author Nataly Didenko
 */
public class IngressTypeService extends DictionaryService {

	public IngressTypeService() {
		tableName = "ingresstype";
	}

	@Override
	public Model create() {
		return new IngressType();
	}
}
