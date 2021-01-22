package api.lemonico.enums;

import java.util.stream.Stream;

/**
 * 列挙クラスのインタフェース。
 * @author aries_yuan
 *
 */
public interface CodeEnum<T> {

	/**
	 * ラベル取得メソッド
	 * 
	 */
	public String getLabel();
	
	/**
	 * 値取得メソッド
	 * 
	 */
	public T getValue();

	static <T, E extends Enum<?> & CodeEnum<T>> E of(Class<E> clazz, T value) {
		if (value == null) {
			return null;
		}
		return Stream.of(clazz.getEnumConstants()).filter(e -> e.getValue().equals(value)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException(value.toString()));
	}
}
