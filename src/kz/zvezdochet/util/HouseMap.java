package kz.zvezdochet.util;

/**
 * Вспомогательный класс для отчётов
 * @author Nataly Didenko
 *
 */
public class HouseMap {

	public String name;
	public Long[] houseids;

	protected HouseMap(String name, Long[] houseids) {
		super();
		this.name = name;
		this.houseids = houseids;
	}

	/**
	 * Карта домов с разбитием на трети
	 * @return карта основных домов
	 */
	public static HouseMap[] getMap() {
		HouseMap[] map = new HouseMap[12];
		map[0] = new HouseMap("Личность", new Long[] {142L,143L,144L});
		map[1] = new HouseMap("Материальное положение", new Long[] {145L,146L,147L});
		map[2] = new HouseMap("Привычное окружение", new Long[] {148L,149L,150L});
		map[3] = new HouseMap("Семья", new Long[] {151L,152L,153L});
		map[4] = new HouseMap("Развлечения", new Long[] {154L,155L,156L});
		map[5] = new HouseMap("Обязанности", new Long[] {157L,158L,159L});
		map[6] = new HouseMap("Партнёрство", new Long[] {160L,161L,162L});
		map[7] = new HouseMap("Риск", new Long[] {163L,164L,165L});
		map[8] = new HouseMap("Непривычное окружение", new Long[] {166L,167L,168L});
		map[9] = new HouseMap("Достижение целей", new Long[] {169L,170L,171L});
		map[10] = new HouseMap("Социум", new Long[] {172L,173L,174L});
		map[11] = new HouseMap("Внутренняя жизнь", new Long[] {175L,176L,177L});
		return map;
	}
}
