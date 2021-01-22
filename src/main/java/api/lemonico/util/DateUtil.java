package api.lemonico.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * 日付フォーマットに関するメソッドをこちらにまとめる
 * 
 * @author aries_yuan
 *
 */
public class DateUtil {

	/**
	 * 日付変更 String → LocalDateTime
	 * 
	 * @param dateTimeStr
	 * @return LocalDateTime
	 */
	public static LocalDateTime stringToLocalDateTime(String dateTimeStr) {
		if (Objects.isNull(dateTimeStr) || dateTimeStr.isEmpty()) {
			return null;
		}
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
		LocalDateTime time = LocalDate.parse(dateTimeStr, formatter).atStartOfDay();
		return time;
	}

	/**
	 * 日付変更 String → LocalDate
	 * 
	 * @param dateTimeStr
	 * @return LocalDate
	 */
	public static LocalDate stringToLocalDate(String dateTimeStr) {
		if (Objects.isNull(dateTimeStr) || dateTimeStr.isEmpty()) {
			return null;
		}
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate time = LocalDate.parse(dateTimeStr, formatter);
		return time;
	}
}
