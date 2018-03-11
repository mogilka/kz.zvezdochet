package kz.zvezdochet.service;

import kz.zvezdochet.bean.IngressType;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DictionaryService;

/**
 * Сервис типов ингрессий
 * @author Nataly Didenko
 */
public class IngressService extends DictionaryService {

	public IngressService() {
		tableName = "ingress";
	}

	@Override
	public Model create() {
		return new IngressType();
	}
}
