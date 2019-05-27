package kz.zvezdochet.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import kz.zvezdochet.bean.Ingress;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.ModelService;

/**
 * Сервис ингрессий
 * @author Natalie Didenko
 */
public class IngressService extends ModelService {

	public IngressService() {
		tableName = "ingress";
	}

	@Override
	public Model create() {
		return new Ingress();
	}

	@Override
	public Model save(Model model) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model init(ResultSet rs, Model base) throws DataAccessException, SQLException {
		// TODO Auto-generated method stub
		return null;
	}
}
