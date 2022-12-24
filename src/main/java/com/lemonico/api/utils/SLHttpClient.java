package com.lemonico.api.utils;



import com.lemonico.core.exception.BaseException;
import com.lemonico.core.utils.StringTools;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.*;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

public class SLHttpClient
{

    private final static Logger logger = LoggerFactory.getLogger(SLHttpClient.class);

    /**
     * HTTPクライアント
     */
    private static CloseableHttpClient httpClient;

    /**
     * GET請求
     *
     * @param url リクエストURL
     * @param headerMap ヘッダーマップ
     * @return 実行レスポンス文字列
     */
    public static String get(final String url, HashMap<String, String> headerMap) {
        String response = null;
        // リクエスト送信
        try {
            response = execute(createHttpGet(url, headerMap));
        } catch (Exception e) {
            logger.error("GETリクエスト送信失敗, url={}, ヘッダー情報={}", url, headerMap);
        }

        return response;
    }

    /**
     * POST請求
     *
     * @param url リクエストURI
     * @param headerMap ヘッダーマップ
     * @param httpEntity ボディ
     * @return 実行レスポンス文字列
     */
    public static String post(String url, HashMap<String, String> headerMap, HttpEntity httpEntity) {
        String response = null;
        // リクエスト送信
        try {
            response = execute(createHttpPost(url, headerMap, httpEntity));
        } catch (Exception e) {
            logger.error("POSTリクエスト送信失敗, url={}, ヘッダー情報={}, ボディ情報={}", url, headerMap, httpEntity);
        }

        return response;
    }

    /**
     * UrlEncodedFormEntityを作成
     *
     * @param map パラメータマップ
     * @return {@link HttpEntity}
     */
    public static HttpEntity createUrlEncodedFormEntity(HashMap<String, String> map) {
        List<BasicNameValuePair> valuePairs = new ArrayList<>();
        HttpEntity httpEntity = null;
        if (null != map && !map.isEmpty()) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                valuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            try {
                httpEntity = new UrlEncodedFormEntity(valuePairs, "UTF-8");
            } catch (Exception e) {
                logger.error("UrlEncodedFormEntity作成する際に、異常が発生しました。");
                logger.error(BaseException.print(e));
                return null;
            }
        }
        return httpEntity;
    }

    /**
     * XmlEntityを作成
     *
     * @param xml パラメータマップ
     * @return {@link HttpEntity}
     */
    public static HttpEntity createStringEntity(final String xml) {
        HttpEntity httpEntity;
        try {
            httpEntity = new StringEntity(xml, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("XmlEntity作成する際に、異常が発生しました。");
            logger.error(BaseException.print(e));
            return null;
        }
        return httpEntity;
    }

    /**
     * GETリクエスト作成
     *
     * @param url リクエストURI
     * @param headerMap ヘッダー
     * @return {@link HttpGet}
     */
    private static HttpGet createHttpGet(final String url, HashMap<String, String> headerMap) {
        HttpGet httpGet = new HttpGet(url);
        // ヘッダーにトークン設定
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            httpGet.setHeader(entry.getKey(), entry.getValue());
        }
        logger.info("GETリクエスト作成 成功");
        return httpGet;
    }

    /**
     * POSTリクエスト作成
     *
     * @param url リクエストURI
     * @param headers ヘッダー
     * @param body ボディ
     * @return {@link HttpGet}
     */
    private static HttpPost createHttpPost(final String url, HashMap<String, String> headers, HttpEntity body) {
        HttpPost httpPost = new HttpPost(url);
        // ヘッダー設定
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpPost.setHeader(entry.getKey(), entry.getValue());
        }
        // ボディ設定
        httpPost.setEntity(body);
        logger.info("POSTリクエスト作成 成功");
        return httpPost;
    }

    /**
     * リクエスト送信
     *
     * @param requestBase {@link HttpRequestBase}
     * @return 実行レスポンス文字列
     */
    private static String execute(HttpRequestBase requestBase) throws IOException {
        // HTTPクライアントを取得
        httpClient = getHttpClient();
        // HTTPレスポンス
        CloseableHttpResponse response = null;
        // レスポンスの文字列
        String responseStr = null;
        try {
            response = httpClient.execute(requestBase);
            int status = response.getStatusLine().getStatusCode();
            switch (status) {
                case HttpStatus.SC_OK:
                case HttpStatus.SC_CREATED:
                case HttpStatus.SC_UNPROCESSABLE_ENTITY:
                    responseStr = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                    logger.info("リクエスト送信 成功");
                    break;
                case HttpStatus.SC_BAD_REQUEST:
                    logger.error("リクエスト送信 失敗 HttpStatus:400 リクエスト不正");
                    break;
                case HttpStatus.SC_UNAUTHORIZED:
                    logger.error("リクエスト送信 失敗 HttpStatus:401 認証失敗");
                    break;
                case HttpStatus.SC_FORBIDDEN:
                    logger.error("リクエスト送信 失敗 HttpStatus:403 訪問禁止");
                    break;
                case HttpStatus.SC_NOT_FOUND:
                    logger.error("リクエスト送信 失敗 HttpStatus:404 パスが間違えた");
                    break;
                case HttpStatus.SC_INTERNAL_SERVER_ERROR:
                    logger.error("リクエスト送信 失敗 HttpStatus:500 訪問先のシステムエラー");
                    break;
                default:
                    logger.error("リクエスト送信 失敗 HttpStatus:{}", status);
            }
        } catch (Exception exception) {
            logger.error("HttpClient実行失敗しました。");
            exception.printStackTrace();
            throw exception;
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
        // レスポンスについて、XMLだったら、JSONObjectに転換する
        try {
            if (!StringTools.isNullOrEmpty(responseStr) && isXmlDocument(responseStr)) {
                org.json.JSONObject xmlJSONObj = XML.toJSONObject(responseStr);
                responseStr = xmlJSONObj.toString(4);
            }
        } catch (Exception e) {
            return null;
        }
        return responseStr;
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
     * @return {@link CloseableHttpClient}
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
     * @return {@link CloseableHttpClient}
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
}
