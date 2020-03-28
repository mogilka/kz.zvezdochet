package kz.zvezdochet.bean;

import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.service.EventConfigurationService;

/**
 * Конфигурация аспектов события
 * @author Natalie Didenko
 */
public class EventConfiguration extends Model {
	private static final long serialVersionUID = -7760833047126133380L;

	@Override
	public ModelService getService() {
		return new EventConfigurationService();
	}

	/**
	 * Конфигурация
	 */
	private AspectConfiguration conf;

	public AspectConfiguration getConf() {
		return conf;
	}

	public void setConf(AspectConfiguration element) {
		this.conf = element;
	}

	/**
	 * Планеты на вершине
	 */
	private String vertex;
	/**
	 * Планеты слева внизу
	 */
	private String leftFoot;
	/**
	 * Планеты справа внизу
	 */
	private String rightFoot;
	/**
	 * Планеты в основании
	 */
	private String base;
	/**
	 * Планеты слева посередине
	 */
	private String leftHand;
	/**
	 * Планеты справа посередине
	 */
	private String rightHand;
	/**
	 * Планеты слева вверху
	 */
	private String leftHorn;
	/**
	 * Планеты справа вверху
	 */
	private String rightHorn;

	public String getVertex() {
		return vertex;
	}

	public void setVertex(String vertex) {
		this.vertex = vertex;
	}

	public String getLeftFoot() {
		return leftFoot;
	}

	public void setLeftFoot(String leftFoot) {
		this.leftFoot = leftFoot;
	}

	public String getRightFoot() {
		return rightFoot;
	}

	public void setRightFoot(String rightFoot) {
		this.rightFoot = rightFoot;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getLeftHand() {
		return leftHand;
	}

	public void setLeftHand(String leftHand) {
		this.leftHand = leftHand;
	}

	public String getRightHand() {
		return rightHand;
	}

	public void setRightHand(String rightHand) {
		this.rightHand = rightHand;
	}

	public String getLeftHorn() {
		return leftHorn;
	}

	public void setLeftHorn(String leftHorn) {
		this.leftHorn = leftHorn;
	}

	public String getRightHorn() {
		return rightHorn;
	}

	public void setRightHorn(String rightHorn) {
		this.rightHorn = rightHorn;
	}

	@Override
	public void init(boolean mode) {}

	/**
	 * Событие
	 */
	private Event event;

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	/**
	 * Дом для толкования ожерелья
	 */
	private long houseid;

	public long getHouseid() {
		return houseid;
	}

	public void setHouseid(long houseid) {
		this.houseid = houseid;
	}
}
