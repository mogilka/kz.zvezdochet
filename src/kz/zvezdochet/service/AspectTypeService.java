package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.Protraction;
import kz.zvezdochet.core.bean.BaseEntity;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.ReferenceService;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.core.util.CoreUtil;

/**
 * Реализация сервиса типов аспектов
 * @author nataly
 *
 * @see ReferenceService Реализация сервиса справочников  
 */
public class AspectTypeService extends ReferenceService {

	public AspectTypeService() {
		tableName = "aspecttypes";
	}

	@Override
	public BaseEntity getEntityById(Long id) throws DataAccessException {
        AspectType type = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
		String query;
		try {
			query = "select * from " + tableName + " where id = " + id;
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			rs = ps.executeQuery();
			if (rs.next()) 
				type = initEntity(rs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { 
				if (rs != null) rs.close();
				if (ps != null) ps.close();
			} catch (SQLException e) { 
				e.printStackTrace(); 
			}
		}
		return type;
	}

	@Override
	public List<BaseEntity> getOrderedEntities() throws DataAccessException {
        List<BaseEntity> list = new ArrayList<BaseEntity>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		String query;
		try {
			query = "select * from " + tableName + " order by name";
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
				AspectType type = initEntity(rs);
				list.add(type);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { 
				if (rs != null) rs.close();
				if (ps != null) ps.close();
			} catch (SQLException e) { 
				e.printStackTrace(); 
			}
		}
		return list;
	}

	@Override
	public BaseEntity saveEntity(BaseEntity element) throws DataAccessException {
		AspectType reference = (AspectType)element;
		int result = -1;
        PreparedStatement ps = null;
		try {
			String query;
			if (element.getId() == null) 
				query = "insert into " + tableName + 
					"(parenttypeid, protractionid, code, name, description, symbol, color, dimcolor) " +
					"values(?,?,?,?,?,?,?,?)";
			else
				query = "update " + tableName + " set " +
					"parenttypeid = ?, " +
					"protractionid = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"symbol = ?, " +
					"color = ?, " +
					"dimcolor = ? " +
					"where id = " + reference.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(query);
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
			updateDictionary();
		}
		return reference;
	}

	@Override
	public AspectType initEntity(ResultSet rs) throws DataAccessException, SQLException {
		AspectType type = (AspectType)super.initEntity(rs);
		type.setProtraction((Protraction)new ProtractionService().
				getEntityById(Long.parseLong(rs.getString("ProtractionID"))));
		type.setColor(CoreUtil.rgbToColor(rs.getString("Color")));
		type.setDimColor(CoreUtil.rgbToColor(rs.getString("DimColor")));
		if (rs.getString("ParentTypeID") != null) {
			Long typeId = Long.parseLong(rs.getString("ParentTypeID"));
			type.setParentType((AspectType)new AspectTypeService().getEntityById(typeId));
		}
		if (rs.getString("Symbol") != null)
			type.setSymbol(rs.getString("Symbol").charAt(0));
		return type;
	}

	@Override
	public BaseEntity createEntity() {
		return new AspectType();
	}
}
