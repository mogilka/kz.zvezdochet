package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.core.bean.Dictionary;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.core.util.CoreUtil;

/**
 * Сервис типов аспектов
 * @author Nataly Didenko
 */
public class AspectTypeService extends DictionaryService {

	public AspectTypeService() {
		tableName = "aspecttypes";
	}

	@Override
	public Model save(Model model) throws DataAccessException {
		AspectType dict = (AspectType)model;
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (null == model.getId()) 
				sql = "insert into " + tableName + 
					"(parenttypeid, protractionid, code, name, description, symbol, color, dimcolor) " +
					"values(?,?,?,?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"parenttypeid = ?, " +
					"protraction = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"symbol = ?, " +
					"color = ?, " +
					"dimcolor = ? " +
					"where id = " + dict.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			if (dict.getParentType() != null)
				ps.setLong(1, dict.getParentType().getId());
			else
				ps.setLong(1, java.sql.Types.NULL);
			ps.setString(2, dict.getProtraction());
			ps.setString(3, dict.getCode());
			ps.setString(4, dict.getName());
			ps.setString(5, dict.getDescription());
			ps.setString(6, String.valueOf(dict.getSymbol()));
			ps.setString(7, CoreUtil.colorToRGB(dict.getColor()));
			ps.setString(8, CoreUtil.colorToRGB(dict.getDimColor()));
			result = ps.executeUpdate();
			if (result == 1) {
				if (null == model.getId()) { 
					Long autoIncKeyFromApi = -1L;
					ResultSet rsid = ps.getGeneratedKeys();
					if (rsid.next()) {
				        autoIncKeyFromApi = rsid.getLong(1);
				        model.setId(autoIncKeyFromApi);
					}
					if (rsid != null) rsid.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)	ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			afterSave();
		}
		return dict;
	}

	@Override
	public AspectType init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		AspectType type = (model != null) ? (AspectType)model : (AspectType)create();
		super.init(rs, type);
		type.setProtraction(rs.getString("Protraction"));
		type.setColor(CoreUtil.rgbToColor(rs.getString("Color")));
		type.setDimColor(CoreUtil.rgbToColor(rs.getString("DimColor")));
		type.setFontColor(rs.getString("fontcolor"));
		if (rs.getString("ParentTypeID") != null) {
			Long typeId = rs.getLong("ParentTypeID");
			type.setParentType((AspectType)new AspectTypeService().find(typeId));
		}
		if (rs.getString("Symbol") != null)
			type.setSymbol(rs.getString("Symbol").charAt(0));
		if (rs.getString("image") != null)
			type.setImage(rs.getString("image"));
		if (rs.getString("text") != null)
			type.setText(rs.getString("text"));
		return type;
	}

	@Override
	public Model create() {
		return new AspectType();
	}

	@Override
	public List<Model> getList() throws DataAccessException {
		if (null == list)
			list = super.getList();
		return list;
	}

	/**
	 * Поиск главных типов аспектов
	 * @return список аспектов
	 * @throws DataAccessException
	 */
	public List<AspectType> getMainList() throws DataAccessException {
		Map<String, String[]> types = AspectType.getHierarchy();
		List<AspectType> main = new ArrayList<AspectType>();
		for (Model model : getList())
			if (types.containsKey(((Dictionary)model).getCode()))
				main.add((AspectType)model);
		return main;
	}
}
