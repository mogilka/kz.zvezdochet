package kz.zvezdochet.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.bean.Star;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.DictionaryService;

/**
 * Сервис звёзд
 * @author Natalie Didenko
 */
public class StarService extends DictionaryService {

	public StarService() {
		tableName = "stars";
	}

	@Override
	public Model create() {
		return new Star();
	}

	@Override
	public Star init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		Star star = (model != null) ? (Star)model : (Star)create();
		super.init(rs, star);
		star.setSign((Sign)new SignService().find(rs.getLong("signid")));
		return star;
	}
}
