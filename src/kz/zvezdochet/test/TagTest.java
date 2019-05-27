package kz.zvezdochet.test;

/**
 * Очистка HTML-текста от тегов
 * @author Natalie Didenko
 *
 */
public class TagTest {

	public TagTest() {}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String text = "<p>Время неожиданных радостей, надежд и сюрпризов. Вы направите свое сознание в в сферу мечтаний, начнете с надеждой смотреть в будущее, строить планы, которые на первый взгляд кажутся утопичными, искать перспективные проекты, воплощать оригинальные идеи, ожидать интересных событий, встреч и приключений. <p>";
		text = text.replaceAll("\\<.*?>", "");
//		text.replaceAll("\\<[^>]*>", "");
		System.out.println(text);
	}

}
