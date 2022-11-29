package kz.zvezdochet.bean;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;

import kz.zvezdochet.core.bean.DiagramDictionary;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.service.ElementService;

/**
 * Стихия
 * @author Natalie Didenko
 */
public class Element extends DiagramDictionary {
	private static final long serialVersionUID = 2457703926076101583L;
	
	/**
	 * Темперамент
	 */
	private String temperament;

	public String getTemperament() {
		return temperament;
	}

	public void setTemperament(String temperament) {
		this.temperament = temperament;
	}

	@Override
	public ModelService getService() {
		return new ElementService();
	}

	/**
	 * Тёмный цвет
	 */
	private Color dimcolor;
	
	public Color getDimColor() {
		return dimcolor;
	}
	
	public void setDimColor(Color color) {
		this.dimcolor = color;
	}

	/**
	 * Человекопонятное описание
	 */
	private String shortname;

	public String getShortName() {
		return shortname;
	}

	public void setShortName(String shortname) {
		this.shortname = shortname;
	}

	/**
	 * Начало
	 */
	private YinYang yinyang;

	public YinYang getYinYang() {
		return yinyang;
	}

	public void setYinYang(YinYang yinyang) {
		this.yinyang = yinyang;
	}

	/**
	 * Светлый цвет
	 */
	private Color lightcolor;

	public Color getLightColor() {
		return lightcolor;
	}

	public void setLightColor(Color lightcolor) {
		this.lightcolor = lightcolor;
	}

	/**
	 * Толкование синастрии
	 */
	private String synastry;

	public String getSynastry() {
		return synastry;
	}

	public void setSynastry(String synastry) {
		this.synastry = synastry;
	}

	/**
	 * Толкование тригона
	 */
	private String triangle;

	public String getTriangle() {
		return triangle;
	}

	public void setTriangle(String triangle) {
		this.triangle = triangle;
	}

	/**
	 * Лояльность
	 */
	private boolean loyalty;

	public boolean isLoyalty() {
		return loyalty;
	}

	public void setLoyalty(boolean loyalty) {
		this.loyalty = loyalty;
	}

	public static Map<String, Double> getMap() {
		return new HashMap<String, Double>() {
			private static final long serialVersionUID = 3501100288331136747L;
			{
		        put("fire", 0.0);
		        put("earth", 0.0);
		        put("air", 0.0);
		        put("water", 0.0);
		    }
		};
	}

	public static String[] getTemperaments(String lang) {
		boolean rus = lang.equals("ru");
		return rus
			? new String[] {
				"Холерик (стихия Огня) – быстрый, порывистый, страстный, способный преодолевать значительные трудности, но неуравновешенный, склонный к бурным эмоциям и резким сменам настроения. Чувства возникают быстро и ярко отражаются в речи, жестах и мимике",
				"Флегматик (стихия Земли) – медлительный, спокойный, с устойчивыми стремлениями и более или менее постоянным настроением (внешне слабо выражает своё душевное состояние). Тип нервной системы: сильный, уравновешенный, инертный. Хорошая память, высокий интеллект, склонность к продуманным, взвешенным решениям, без риска",
				"Сангвиник (стихия Воздуха) – живой, подвижный, сравнительно легко переживающий неудачи и неприятности. Мимика разнообразна и богата, темп речи быстрый. Эмоции преимущественно положительные, – быстро возникают и меняются",
				"Меланхолик (стихия Воды) – легкоранимый, глубоко переживает даже незначительные неудачи, внешне вяло реагирует на происходящее. Тип нервной системы: высокочувствительный. Тонкая реакция на малейшие оттенки чувств. Переживания глубоки, эмоциональны и очень устойчивы"
			}
			: new String[] {
				"Choleric (Fire element) - fast, impulsive, passionate, able to overcome significant difficulties, but unbalanced, prone to violent emotions and sudden mood swings. Feelings arise quickly and are vividly reflected in speech, gestures and facial expressions",
				"Phlegmatic (Earth element) - slow, calm, with steady aspirations and a more or less constant mood (outwardly, he weakly expresses his state of mind). Type of nervous system: strong, balanced, inert. Good memory, high intellect, propensity for thoughtful, balanced decisions, without risk",
				"Sanguine (Air element) - lively, mobile, easy to experience failures and troubles. The facial expressions are varied and rich, the pace of speech is fast. Emotions are positive predominantly, they arise and change quickly",
				"Melancholic (Water element) - easily injured, deeply worries even minor failures, sluggishly reacts to what is happening. Type of nervous system: highly sensitive. Subtle reaction to the slightest shades of feelings. His experiences are deep, emotional and very stable"					
			};
	}
}
