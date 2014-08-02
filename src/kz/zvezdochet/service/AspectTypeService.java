package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.Protraction;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.ReferenceService;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.core.util.CoreUtil;

/**
 * Реализация сервиса типов аспектов
 * @author Nataly Didenko
 *
 * @see ReferenceService Реализация сервиса справочников  
 */
public class AspectTypeService extends ReferenceService {

	public AspectTypeService() {
		tableName = "aspecttypes";
	}

	@Override
	public Model save(Model element) throws DataAccessException {
		AspectType reference = (AspectType)element;
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (element.getId() == null) 
				sql = "insert into " + tableName + 
					"(parenttypeid, protractionid, code, name, description, symbol, color, dimcolor) " +
					"values(?,?,?,?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"parenttypeid = ?, " +
					"protractionid = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"symbol = ?, " +
					"color = ?, " +
					"dimcolor = ? " +
					"where id = " + reference.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			if (reference.getParentType() != null)
				ps.setLong(1, reference.getParentType().getId());
			else
				ps.setLong(1, java.sql.Types.NULL);
			ps.setLong(2, reference.getProtraction().getId());
			ps.setString(3, reference.getCode());
			ps.setString(4, reference.getName());
			ps.setString(5, reference.getDescription());
			ps.setString(6, String.valueOf(reference.getSymbol()));
			ps.setString(7, CoreUtil.colorToRGB(reference.getColor()));
			ps.setString(8, CoreUtil.colorToRGB(reference.getDimColor()));
			result = ps.executeUpdate();
			if (result == 1) {
				if (element.getId() == null) { 
					Long autoIncKeyFromApi = -1L;
					ResultSet rsid = ps.getGeneratedKeys();
					if (rsid.next()) {
				        autoIncKeyFromApi = rsid.getLong(1);
				        element.setId(autoIncKeyFromApi);
					    //System.out.println("inserted " + tableName + "\t" + autoIncKeyFromApi);
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
			update();
		}
		return reference;
	}

	@Override
	public AspectType init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		AspectType type = (model != null) ? (AspectType)model : (AspectType)create();
		super.init(rs, type);
		type.setProtraction((Protraction)new ProtractionService().
				find(rs.getLong("ProtractionID")));
		type.setColor(CoreUtil.rgbToColor(rs.getString("Color")));
		type.setDimColor(CoreUtil.rgbToColor(rs.getString("DimColor")));
		if (rs.getString("ParentTypeID") != null) {
			Long typeId = rs.getLong("ParentTypeID");
			type.setParentType((AspectType)new AspectTypeService().find(typeId));
		}
		if (rs.getString("Symbol") != null)
			type.setSymbol(rs.getString("Symbol").charAt(0));
		if (rs.getString("image") != null)
			type.setImage(rs.getString("image"));
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
}
