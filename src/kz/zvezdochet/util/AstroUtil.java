package kz.zvezdochet.util;

import java.util.List;

import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.service.SignService;

/**
 * Класс, предоставляющий методы для работы с объектами гороскопа
 * @author Nataly Didenko
 *
 */
public class AstroUtil {

	/**
	 * Определение знака, в котором находится объект
	 * @param point координата объекта
	 * @throws DataAccessException 
	 */
	public static Sign getSkyPointSign(double point) throws DataAccessException {
		List<Model> signs = new SignService().getList();
		for (Model model : signs) {
			Sign sign = (Sign)model;
			if (point >= sign.getInitialPoint() && point < sign.getCoord()) 
				return sign;
		}
		return null;
	}

	/**
	 * Определение дома, в котором находится объект
	 * @param house1 координата дома
	 * @param house2 координата следующего дома
	 * @param coord координата объекта
	 */ 
	public static boolean getSkyPointHouse(double house1, double house2, double coord) {
		//если границы находятся по разные стороны нуля
		if (house1 > 200 & house2 < 160) {
			//если градус планеты находится по другую сторону
			//от нуля относительно второй границы,
			//увеличиваем эту границу на 2*пи
			if (Math.abs(coord) > 200)
				house2 = house2 + 360;
			else if (Math.abs(coord) < 160) {
				//если градус планеты меньше 160,
				//увеличиваем его, а также вторую границу на 2*пи
		       coord = Math.abs(coord) + 360;
		       house2 = house2 + 360;
			}
		}
		//если же границы находятся по одну сторону от нуля,
		//оставляем всё как есть
		
		return house1 <= coord && coord <= house2;
	}

//	procedure MarginalValues(var one,two:real) stdcall; export;
//	//определение границ участка космограммы
//	begin
//	if(one>300)and(one<360)
//	  then if(two>0)and(two<60) then two:=two+360
//	  else if(two<0)and(two>-60) then two:=two*(-1)+360;
//	if(one<0)and(one>-60)
//	  then if(two<-300)and(two>-360) then one:=one-360
//	  else
//	  if(two>300)and(two<360)
//	    then
//	    begin
//	    two:=two*(-1);
//	    one:=one-360
//	    end;
//	end;
}
