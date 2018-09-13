package kz.zvezdochet.bean;

import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.service.MoonDayService;

/**
 * Толкование лунного дня
 * @author Nataly Didenko
 */
public class MoonDay extends Model {
	private static final long serialVersionUID = -2426149764703740664L;

	/**
	 * Символ
	 */
	private String symbol;
	/**
	 * Толкование родившегося в этот день
	 */
	private String birth;
	/**
	 * Минерал
	 */
	private String mineral;

	public MoonDay() {
		super();
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}	

	public void init(boolean mode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ModelService getService() {
		return new MoonDayService();
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getMineral() {
		return mineral;
	}

	public void setMineral(String mineral) {
		this.mineral = mineral;
	}
}
