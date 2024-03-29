package kz.zvezdochet.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import kz.zvezdochet.bean.AspectConfiguration;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.TextGenderDictionaryService;
import kz.zvezdochet.core.util.CoreUtil;

/**
 * Сервис конфигурации аспектов
 * @author Natalie Didenko
 */
public class AspectConfigurationService extends TextGenderDictionaryService {

	public AspectConfigurationService() {
		String lang = Locale.getDefault().getLanguage();
		tableName = lang.equals("ru") ? "aspectconfigurations" : "us_aspectconfigurations";
	}

	@Override
	public Model create() {
		return new AspectConfiguration();
	}

	@Override
	public AspectConfiguration init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		AspectConfiguration type = (model != null) ? (AspectConfiguration)model : (AspectConfiguration)create();
		super.init(rs, model);
		type.setColor(CoreUtil.rgbToColor(rs.getString("Color")));
		String s = rs.getString("positive");
		type.setPositive(s.equals("1") ? true : false);

		s = rs.getString("vertex");
		type.setVertexPositive(s.equals("1") ? true : false);

		s = rs.getString("leftfoot");
		type.setLeftFootPositive(s.equals("1") ? true : false);

		s = rs.getString("rightfoot");
		type.setRightFootPositive(s.equals("1") ? true : false);

		s = rs.getString("base");
		type.setBasePositive(s.equals("1") ? true : false);

		s = rs.getString("lefthand");
		type.setLeftHandPositive(s.equals("1") ? true : false);

		s = rs.getString("righthand");
		type.setRightHandPositive(s.equals("1") ? true : false);

		s = rs.getString("lefthorn");
		type.setLeftHornPositive(s.equals("1") ? true : false);

		s = rs.getString("righthorn");
		type.setRightHornPositive(s.equals("1") ? true : false);

		type.setDegree(rs.getString("degree"));
		type.setShape(rs.getString("shape"));
		type.setElementid(rs.getLong("elementid"));
		type.setSynastry(rs.getString("synastry"));

		s = rs.getString("cross");
		type.setCross(s.equals("1") ? true : false);
		return type;
	}
}
