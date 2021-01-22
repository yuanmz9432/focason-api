package api.lemonico.util;

import org.apache.tomcat.util.codec.binary.Base64;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Base64変更工具
 * @author aries_yuan
 *
 */
public class Base64Util {

	/**
	 * 
	 * @param string
	 * @return Base64
	 */
	public static String stringToBase64(String string) {
		
		// エンコード前にバイト配列に置き換える際のCharset
		Charset charset = StandardCharsets.UTF_8;
		// エンコード処理
		String base64Str = Base64.encodeBase64String(string.getBytes(charset));
		
		return base64Str;
	}
	
}
