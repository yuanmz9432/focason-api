package com.lemonico.api.utils.Amazon;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * AWS署名生成ツール
 */
public class AmazonSignature
{
    private final static Logger logger = LoggerFactory.getLogger(AmazonSignature.class);

    private final static String AWS_ACCESS_KEY = "AKIAYXUW5PNFOWETK743";
    private final static String AWS_ACCESS_SECRET_KEY = "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";

    private final static String NEW_LINE = "\n";
    private final static String PATH_SEPARATOR = "/";

    private final static String HOST = "sellingpartnerapi-fe.amazon.com";
    private final static String SIGNED_HEADERS = "content-type;host;x-amz-access-token;x-amz-date";
    private final static String REGION = "us-west-2";
    private final static String SERVICE = "execute-api";
    private final static String AWS4_REQUEST = "aws4_request";
    private final static String ALGORITHM = "AWS4-HMAC-SHA256";

    /**
     * AWS署名生成処理
     * <p>
     * https://docs.aws.amazon.com/ja_jp/general/latest/gr/sigv4_signing.html
     * </p>
     *
     * @param params
     *        <p>
     *        xAmzDate⇒リクエストを行う日時
     *        </p>
     *        <p>
     *        accessToken⇒アクセストークン
     *        </p>
     *        <p>
     *        canonicalUri⇒請求パス
     *        </p>
     *        <p>
     *        canonicalQueryString⇒クエリ文字例
     *        </p>
     *        <p>
     *        requestPayload⇒請求体
     *        </p>
     * @return 署名文字列
     */
    public static String generate(JSONObject params) {
        /*
         * タスク1. 署名バージョン4の正規リクエストを作成する.
         * https://docs.aws.amazon.com/ja_jp/general/latest/gr/sigv4-create-canonical-request.html
         * CanonicalRequest =
         * HTTPRequestMethod + '\n' +
         * CanonicalURI + '\n' +
         * CanonicalQueryString + '\n' +
         * CanonicalHeaders + '\n' +
         * SignedHeaders + '\n' +
         * HexEncode(Hash(RequestPayload))
         */
        final String xAmzDate = params.getString("xAmzDate");
        final String accessToken = params.getString("accessToken");
        final String canonicalUri = params.getString("canonicalUri");
        final String canonicalQueryString = params.getString("canonicalQueryString");
        final String requestPayload = params.getString("requestPayload");
        final String canonicalHeaders =
            "content-type:" + NEW_LINE +
                "host:" + HOST + NEW_LINE +
                "x-amz-access-token:" + accessToken + NEW_LINE +
                "x-amz-date:" + xAmzDate;
        final String canonicalRequest =
            RequestMethod.GET.name() + NEW_LINE +
                canonicalUri + NEW_LINE +
                canonicalQueryString + NEW_LINE +
                canonicalHeaders + NEW_LINE +
                NEW_LINE +
                SIGNED_HEADERS + NEW_LINE +
                DigestUtils.sha256Hex(requestPayload);
        final String hashedCanonicalRequest = DigestUtils.sha256Hex(canonicalRequest);

        /*
         * タスク2. 署名バージョン4の署名文字列を作成する.
         * https://docs.aws.amazon.com/ja_jp/general/latest/gr/sigv4-create-string-to-sign.html
         * StringToSign =
         * Algorithm + \n +
         * RequestDateTime + \n +
         * CredentialScope + \n +
         * HashedCanonicalRequest
         */
        final String TODAY = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        final String credentialScope =
            TODAY + PATH_SEPARATOR + REGION + PATH_SEPARATOR + SERVICE + PATH_SEPARATOR + AWS4_REQUEST;
        final String stringToSign =
            ALGORITHM + NEW_LINE +
                xAmzDate + NEW_LINE +
                credentialScope + NEW_LINE +
                hashedCanonicalRequest;

        /*
         * タスク3. 署名バージョン4の署名を計算する.
         * https://docs.aws.amazon.com/ja_jp/general/latest/gr/sigv4-calculate-signature.html
         *
         * kSecret = your secret access key
         * kDate = HMAC("AWS4" + kSecret, Date)
         * kRegion = HMAC(kDate, Region)
         * kService = HMAC(kRegion, Service)
         * kSigning = HMAC(kService, "aws4_request")
         */
        final String signature;
        try {
            /*
             * Javaを使用して署名キーを取得
             * https://docs.aws.amazon.com/ja_jp/general/latest/gr/signature-v4-examples.html#signature-v4-examples-java
             */
            byte[] key = getSignatureKey(AWS_ACCESS_SECRET_KEY, TODAY, REGION, SERVICE);
            signature = String.valueOf(Hex.encodeHex(hmacSHA256(stringToSign, key)));
        } catch (Exception e) {
            throw new BaseException(ErrorCode.E_AM001);
        }

        /*
         * タスク4. 署名を返却する
         * https://docs.aws.amazon.com/ja_jp/general/latest/gr/sigv4-add-signature-to-request.html
         *
         * Authorization: algorithm Credential=access key ID/credential scope, SignedHeaders=SignedHeaders,
         * Signature=signature
         */
        logger.info("Amazon 署名生成 成功");
        return ALGORITHM + " Credential=" + AWS_ACCESS_KEY + PATH_SEPARATOR + credentialScope +
            ", SignedHeaders=" + SIGNED_HEADERS +
            ", Signature=" + signature;
    }

    /**
     * 計算処理
     *
     * @param data 署名対象
     * @param key 署名キー
     * @return 計算した署名
     * @throws Exception 計算異常
     */
    public static byte[] hmacSHA256(String data, byte[] key) throws Exception {
        final String algorithm = "HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 署名の計算処理
     *
     * @param key AWSシークレットキー
     * @param dateStamp 日付（yyyyMMdd）
     * @param regionName AWSリージョン
     * @param serviceName サービス名
     * @return 計算された署名
     * @throws Exception 計算異常
     */
    public static byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName)
        throws Exception {
        byte[] kSecret = ("AWS4" + key).getBytes(StandardCharsets.UTF_8);
        byte[] kDate = hmacSHA256(dateStamp, kSecret);
        byte[] kRegion = hmacSHA256(regionName, kDate);
        byte[] kService = hmacSHA256(serviceName, kRegion);
        return hmacSHA256(AWS4_REQUEST, kService);
    }
}
