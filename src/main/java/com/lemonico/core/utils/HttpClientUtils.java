package com.lemonico.core.utils;



import com.lemonico.core.exception.BaseException;
import java.io.IOException;
import java.io.StringReader;
import java.security.cert.X509Certificate;
import java.util.*;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

/**
 * @author YuanMingZe
 * @className HttpClientUtils
 * @description HTTP通信共通パーツ
 * @date 2021/07/15 18:30
 **/
public class HttpClientUtils
{

    private final static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    // HTTPクライアント
    private static CloseableHttpClient httpClient;

    /**
     * GETリクエストを発送する
     *
     * @param url エンドポイント
     * @param tokenName トークン名
     * @param token トークン
     * @return JSONObject レスポンス
     * @author YuanMingZe
     * @date 2021/7/15
     */
    public static String sendGet(String url, String tokenName, String token, List<String> errList) {
        // GETリクエストを作成
        HttpGet httpGet;
        try {
            httpGet = new HttpGet(url);
        } catch (Exception e) {
            logger.error("HttpGetのインスタンス初期化失敗。");
            logger.error(BaseException.print(e));
            return null;
        }
        // ヘッダーにトークンを設定
        if (!Objects.isNull(tokenName) && !Objects.isNull(token)) {
            httpGet.setHeader(tokenName, token);
        }
        // リクエスト送信
        try {
            return execute(httpGet, errList);
        } catch (Exception e) {
            logger.error("HTTPリクエスト送信失敗, url={}, HTTP情報={}", url, httpGet);
            logger.error(BaseException.print(e));
        }
        return null;
    }

    /**
     * POSTリクエストを発送する
     *
     * @param url エンドポイント
     * @param entity リクエストボディ
     * @param tokenName トークン名
     * @param token トークン
     * @return JSONObject レスポンス
     * @author YuanMingZe
     * @date 2021/7/15
     */
    public static String sendPost(String url, HttpEntity entity, String tokenName, String token, List<String> errList) {
        // POSTリクエストを作成
        HttpPost httpPost;
        try {
            httpPost = new HttpPost(url);
        } catch (Exception e) {
            logger.error("HttpGetのインスタンス初期化失敗。");
            logger.error(BaseException.print(e));
            return null;
        }
        // ヘッダーとボディ設定
        if (!Objects.isNull(tokenName) && !Objects.isNull(token)) {
            httpPost.setHeader(tokenName, token);
        }
        httpPost.addHeader("User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
        httpPost.setEntity(entity);
        // リクエスト送信
        try {
            return execute(httpPost, errList);
        } catch (Exception e) {
            logger.error("HTTPリクエスト送信失敗, url={}, HTTP情報={}", url, httpPost);
            logger.error(BaseException.print(e));
        }
        return null;
    }

    /**
     * HTTPリクエスト送信実行
     *
     * @param requestBase HTTP請求
     * @return JSONObject HTTPレスポンス
     * @author YuanMingZe
     * @date 2021/07/15
     */
    public static String execute(HttpRequestBase requestBase, List<String> errList) {
        // HTTPクライアントを取得
        httpClient = getHttpClient();
        // HTTPレスポンス
        CloseableHttpResponse response = null;
        // レスポンスの文字列
        String responseStr = null;
        // Shopify:ページ情報
        String linkInfo = "";
        try {
            response = httpClient.execute(requestBase);
            int status = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == status) {
                responseStr = EntityUtils.toString(response.getEntity(), "utf-8");
                // Shopify対応：次のページのデータを取得ため、link情報を取得
                Header[] links = response.getHeaders("link");
                for (Header link : links) {
                    linkInfo = link.getValue();
                }
            } else {
                logger.error("HttpClient接続失敗応答コード={}", status);
                if (!StringTools.isNullOrEmpty(errList)) {
                    errList.add("HttpClient接続失敗応答コード=" + status);
                }
            }
        } catch (Exception e) {
            logger.error("HTTPクライアントでリクエストを送信失敗。リクエスト請求：{}", requestBase);
            if (!StringTools.isNullOrEmpty(errList)) {
                errList.add("HTTPクライアントでリクエストを送信失敗。リクエスト請求：" + requestBase);
            }
            logger.error(BaseException.print(e));
        } finally {
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            if (isXmlDocument(responseStr)) {
                org.json.JSONObject xmlJSONObj = XML.toJSONObject(responseStr);
                return xmlJSONObj.toString(4);
            } else {
                return responseStr;
            }
        } catch (NoSuchMethodError e) {
            logger.error("レスポンスをJSONに転換する際に、NoSuchMethodErrorが発生しました。");
        } catch (Exception e) {
            logger.error("レスポンスをJSONに転換する際に、異常が発生しました。");
            if (!StringTools.isNullOrEmpty(errList)) {
                errList.add("レスポンスをJSONに転換する際に、異常が発生しました。");
            }
            logger.error(BaseException.print(e));
        }
        return null;
    }

    /**
     * レスポンスの型式はXMLであるがどうか
     *
     * @param responseStr レスポンスの文字列
     * @return boolean XML式である：true
     */
    private static boolean isXmlDocument(String responseStr) {
        boolean isXmlDocument = true;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            builder.parse(new InputSource(new StringReader(responseStr)));
        } catch (Exception e) {
            isXmlDocument = false;
        }
        return isXmlDocument;
    }

    /**
     * HTTPクライアント取得
     *
     * @return httpClient HTTPクライアント
     * @author YuanMingZe
     * @date 2021/7/15
     */
    private static CloseableHttpClient getHttpClient() {
        if (Objects.isNull(httpClient)) {
            try {
                httpClient = createIgnoreVerifyHttpClient();
            } catch (Exception e) {
                logger.error("CloseableHttpClientのインスタンス初期化失敗！");
                logger.error(BaseException.print(e));
            }
        }
        return httpClient;
    }

    /**
     * TLS接続
     *
     * @return org.apache.http.impl.client.CloseableHttpClient
     * @author YuanMingZe
     * @date 2021/07/15
     */
    private static CloseableHttpClient createIgnoreVerifyHttpClient() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        // X509TrustManagerのインタフェースを実現する
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) {}

            @Override
            public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) {}

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        sslContext.init(null, new TrustManager[] {
            trustManager
        }, null);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.INSTANCE)
            .register("https", new SSLConnectionSocketFactory(sslContext)).build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        return HttpClients.custom().setConnectionManager(connManager).setConnectionManagerShared(true).build();
    }

    /**
     * HttpEntityを作成
     *
     * @param map パラメータマップ
     * @return HttpEntity対象
     */
    public static HttpEntity createHttpEntity(HashMap<String, String> map) {
        List<BasicNameValuePair> valuePairs = new ArrayList<>();
        HttpEntity httpEntity = null;
        if (null != map && !map.isEmpty()) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                valuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            try {
                httpEntity = new UrlEncodedFormEntity(valuePairs, "UTF-8");
            } catch (Exception e) {
                logger.error("HttpEntity作成する際に、異常が発生しました。");
                logger.error(BaseException.print(e));
                return null;
            }
        }
        return httpEntity;
    }
}
