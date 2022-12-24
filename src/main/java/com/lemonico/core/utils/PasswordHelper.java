package com.lemonico.core.utils;



import com.lemonico.common.bean.Ms200_customer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * パスワードチェックツール
 *
 * @since 1.0.0
 */
public class PasswordHelper
{

    /**
     * 基底ハッシュアルゴリズム
     */
    public static final String ALGORITHM_NAME = "md5";
    /**
     * ハッシュの回数をカスタマイズする
     */
    public static final int HASH_ITERATIONS = 2;

    public static final String SOURCE = "0123456789";

    private static Cipher cipher = null;

    /**
     * エンコード
     *
     * @param content エンコード対象
     * @return エンコードされた対象
     * @since 1.0.0
     */
    public static String AESEncode(String content) {
        try {
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            byte[] encodedContent = Objects.requireNonNull(getCipherInstance()).doFinal(contentBytes);
            return Arrays.toString(Base64.getEncoder().encode(encodedContent));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ディコード
     *
     * @param content ディコード対象
     * @return ディコードされた対象
     * @since 1.0.0
     */
    public static String AESDecode(String content) {
        try {
            byte[] byteContent = Base64.getDecoder().decode(content);
            byte[] deCodedContent = Objects.requireNonNull(getCipherInstance()).doFinal(byteContent);
            return new String(deCodedContent, StandardCharsets.UTF_8);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Cipherインスタンスを取得する
     *
     * @return {@link Cipher}
     * @since 1.0.0
     */
    private static Cipher getCipherInstance() {
        if (cipher != null) {
            return cipher;
        }
        try {
            // 1.构造密钥生成器，指定为AES算法,不区分大小写
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            // 2.根据decode规则初始化密钥生成器
            // 生成一个128位的随机源,根据传入的字节数组
            keygen.init(128, new SecureRandom(SOURCE.getBytes()));
            // 3.产生原始对称密钥
            SecretKey originalKey = keygen.generateKey();
            // 4.获得原始对称密钥的字节数组
            byte[] raw = originalKey.getEncoded();
            // 5.根据字节数组生成AES密钥
            SecretKey key = new SecretKeySpec(raw, "AES");
            cipher = Cipher.getInstance("AES");
            // 7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.DECRYPT_MODE, key);
            // 6.根据指定算法AES自成密码器
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * パスワード転換
     *
     * @param user {@link Ms200_customer}
     * @since 1.0.0
     */
    public static void encryptPassword(Ms200_customer user) {

        RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
        // 随机字符串作为salt因子，实际参与运算的salt我们还引入其它干扰因子
        // ランダム文字列はsaltファクターとして，実際に演算に参加するsaltには他の干渉ファクターも導入した。
        user.setEncode_key(randomNumberGenerator.nextBytes().toHex());
        final String newPassword = new SimpleHash(ALGORITHM_NAME, user.getLogin_pw(),
            ByteSource.Util.bytes(user.getCredentialsSalt()), HASH_ITERATIONS).toHex();
        user.setLogin_pw(newPassword);
    }

    /**
     * ユーザーのパスワードのハッシュ値を生成する。
     *
     * @param user {@link Ms200_customer}
     * @return 入力されたパスワードのハッシュ値
     * @since 1.0.0
     */
    public String toHashPassword(Ms200_customer user) {
        return new SimpleHash(ALGORITHM_NAME, user.getLogin_pw(),
            ByteSource.Util.bytes(user.getCredentialsSalt()), HASH_ITERATIONS).toHex();
    }

}
