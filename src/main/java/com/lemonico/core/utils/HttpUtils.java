package com.lemonico.core.utils;



import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.lemonico.core.exception.BaseException;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import javax.net.ssl.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

// import java.net.HttpURLConnection;


/**
 * @className HttpUtils
 * @description HTTP通信共通パーツ
 * @date 2020/9/8 11:25
 **/
public class HttpUtils
{
    private final static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    // yahoo認証用ファイルのパス
    private final static String YAHOO_AUTHENTICATION_FILE_PATH = "./";
    // HTTPクライアント
    private static CloseableHttpClient httpClient = null;

    /**
     * HTTPクライアント取得
     * 
     * @return httpClient HTTPクライアント
     */
    public static CloseableHttpClient getHttpClient() {
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
     * @Param: url 接続URL
     * @description: HTTPS接続方法(GET)※認証無
     * @return: java.lang.String
     * @date: 2020/9/8
     */
    public static JSONObject sendHttpsGet(String url, String tokenName, String token, List<String> errList) {
        CloseableHttpClient httpClient = null;
        try {
            // logger.info("第三方请求的httpGet已创建！！");
            httpClient = createIgnoreVerifyHttpClient();
        } catch (Exception e) {
            logger.error("HTTP情報取得失敗, url={}", url);
            logger.error(BaseException.print(e));
        }
        return doGet(url, httpClient, tokenName, token, errList);
    }

    public static JSONObject sendHttpsPost(String url, HashMap<String, String> map, String tokenName, String token) {
        CloseableHttpClient ignoreVerifyHttpClient = null;
        try {
            ignoreVerifyHttpClient = createIgnoreVerifyHttpClient();
            // logger.info("第三方请求的httpPost已创建！！");
        } catch (Exception e) {
            logger.error("HTTP情報取得失敗, url={}", url);
            logger.error(BaseException.print(e));
        }
        return doPost(url, ignoreVerifyHttpClient, map, tokenName, token);
    }

    private static JSONObject doGet(String url, CloseableHttpClient httpClient, String tokenName, String token,
        List<String> errList) {
        HttpGet httpGet = new HttpGet(url);
        if (!StringTools.isNullOrEmpty(tokenName) && !StringTools.isNullOrEmpty(token)) {
            httpGet.setHeader(tokenName, token);
        }
        try {
            // logger.info("第三方get请求的httpClient已创建！！");
            return execute(httpClient, httpGet, errList);
        } catch (Exception e) {
            logger.error("HTTP情報取得失敗, url={}, HTTP情報={}", url, httpClient);
            logger.error(BaseException.print(e));
        }
        // logger.info("请求失败！！");
        return null;
    }

    /**
     * @description 发送post请求
     * @param url 资源链接
     * @param map 参数
     * @param customHeader 自定义http头
     * @param paramType 参数提交类型
     * @return: JSONObject
     * @date 2021/6/3
     **/
    public static JSONObject sendPostRequest(String url, HashMap<String, String> map, List<Header> customHeader,
        String paramType) {
        CloseableHttpClient closeableHttpClient = null;
        try {
            closeableHttpClient = createIgnoreVerifyHttpClient();
        } catch (Exception e) {
            logger.error("HTTP情報取得失敗, url={}", url);
            logger.error(BaseException.print(e));
        }
        HttpResponse response = doPost2(url, closeableHttpClient, map, customHeader, paramType);
        if (null != response && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            try {
                JSONObject res = new JSONObject();
                String body = EntityUtils.toString(response.getEntity(), "UTF-8");
                if (paramType.equals("xml")) {
                    res.put("value", body);
                } else {
                    res = JSONObject.parseObject(body);
                }
                return res;
            } catch (Exception e) {
                logger.error("HTTP Entity Type Conversion Fail, url={}", url);
                logger.error(BaseException.print(e));
            }
        }
        // XXX: 读取响应body后再关闭client, 否则异常：Premature end of Content-Length delimited message body (expected: 209;
        // received: 0
        if (null != closeableHttpClient) {
            try {
                // logger.info("第三方请求的httpPost已关闭！！");
                closeableHttpClient.close();
            } catch (IOException e) {
                logger.error("httpClient close失敗");
            }
        }
        return null;
    }

    private static JSONObject doPost(String url, CloseableHttpClient httpClient, HashMap<String, String> map,
        String tokenName, String token) {
        HttpPost httpPost = new HttpPost(url);

        if (!StringTools.isNullOrEmpty(token) && !StringTools.isNullOrEmpty(tokenName)) {
            httpPost.addHeader(tokenName, token);
        }
        try {
            // 装填参数
            List<BasicNameValuePair> valuePairs = new ArrayList<>();
            if (map != null) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    valuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(valuePairs, "UTF-8"));
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.addHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
            HttpResponse response = httpClient.execute(httpPost);
            logger.info("HttpClient响应码={}", response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 返回json格式
                String res = EntityUtils.toString(response.getEntity(), "UTF-8");
                return JSONObject.parseObject(res);
            }
        } catch (Exception e) {
            logger.error("HTTP情報取得失敗, url={}, HTTP情報={}", url, httpClient);
            logger.error(BaseException.print(e));
        } finally {
            if (null != httpClient) {
                try {
                    // logger.info("第三方请求的httpPost已关闭！！");
                    httpClient.close();
                } catch (IOException e) {
                    logger.error("httpClient 失敗");
                }
            }
        }
        return null;
    }

    /**
     * @description: 发送post方式的http请求
     * @param url 资源地址
     * @param httpClient httpclient
     * @param map 参数
     * @param customHeader 自定义http头
     * @param paramType 指定参数提交方式
     * @return HttpResponse
     * @date: 2021/4/12
     */
    private static HttpResponse doPost2(String url, CloseableHttpClient httpClient, HashMap<String, String> map,
        List<Header> customHeader, String paramType) {
        HttpPost httpPost = new HttpPost(url);

        Optional.ofNullable(customHeader)
            .ifPresent(
                headers -> {
                    headers.stream()
                        .filter(Objects::nonNull)
                        .forEach(header -> {
                            httpPost.addHeader(header);
                        });
                });

        try {
            switch (paramType) {
                case "json":
                    JSONObject params = new JSONObject();
                    if (null != map && map.size() > 0) {
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            params.put(entry.getKey(), entry.getValue());
                        }
                    }
                    StringEntity entity = new StringEntity(params.toString(), "UTF-8");
                    entity.setContentType("application/json");
                    httpPost.setEntity(entity);
                    httpPost.setHeader("Content-type", "application/json;charset=UTF-8");
                    break;
                case "form":
                    // 装填参数
                    List<BasicNameValuePair> valuePairs = new ArrayList<>();
                    if (map != null) {
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            valuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                        }
                    }
                    httpPost.setEntity(new UrlEncodedFormEntity(valuePairs, "UTF-8"));
                    httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
                    break;
                case "xml":
                    String value = map.get("value");
                    StringEntity stringEntity = new StringEntity(value, "UTF-8");
                    httpPost.setEntity(stringEntity);
                    httpPost.setHeader("Content-type", "application/xml");
                    break;
                default:
                    break;
            }
            httpPost.addHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
            return httpClient.execute(httpPost);
        } catch (Exception e) {
            logger.error("HTTP情報取得失敗, url={}, HTTP情報={}", url, httpClient);
            logger.error(BaseException.print(e));
        }
        return null;
    }

    /**
     * @description: TLS接続
     * @return: org.apache.http.impl.client.CloseableHttpClient
     * @date: 2020/9/8
     */
    public static CloseableHttpClient createIgnoreVerifyHttpClient() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        // 实现一个X509TrustManager接口
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
                throws CertificateException {}

            @Override
            public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
                throws CertificateException {}

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        // logger.info("请求初始化！！");
        sslContext.init(null, new TrustManager[] {
            trustManager
        }, null);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.INSTANCE)
            .register("https", new SSLConnectionSocketFactory(sslContext)).build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(50000)
            .setSocketTimeout(50000)
            .setConnectTimeout(50000)
            .build();
        CloseableHttpClient httpClient =
            HttpClients.custom().setConnectionManager(connManager).setDefaultRequestConfig(requestConfig).build();
        return httpClient;
    }

    /**
     * @description: HTTP送信実行
     * @Param: file
     * @return: java.util.List<java.lang.String>
     * @date: 2020/9/14
     */
    private static JSONObject execute(CloseableHttpClient httpClient, HttpRequestBase requestBase, List<String> errList)
        throws Exception {
        String result = null;
        CloseableHttpResponse response = null;
        String linkInfo = "";
        try {
            response = httpClient.execute(requestBase);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                result = EntityUtils.toString(response.getEntity(), "utf-8");
                // 对应 shopify 获取下一页的数据
                Header[] links = response.getHeaders("link");
                for (Header link : links) {
                    linkInfo = link.getValue();
                }
            } else {
                logger.error("HttpClient接続失敗応答コード={}", statusCode);
                if (!StringTools.isNullOrEmpty(errList)) {
                    errList.add("HttpClient接続失敗応答コード=" + statusCode);
                }
            }
        } catch (Exception e) {
            logger.error(BaseException.print(e));
        } finally {
            if (null != httpClient) {
                httpClient.close();
            }
            if (null != response) {
                response.close();
            }
        }
        JSONObject jsonObject = null;
        try {
            if (isXmlDocument(result)) {
                org.json.JSONObject xmlJSONObj = XML.toJSONObject(result);
                result = xmlJSONObj.toString(4);
                jsonObject = JSONObject.parseObject(result);
            } else {
                jsonObject = JSONObject.parseObject(result);
            }
        } catch (JSONException e) {
        }
        jsonObject.put("link", linkInfo);
        return jsonObject;
    }



    private static boolean isXmlDocument(String rtnMsg) {

        boolean flag = true;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            builder.parse(new InputSource(new StringReader(rtnMsg)));
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * @Param: file
     * @description: 读取静态文件
     * @return: java.util.List<java.lang.String>
     * @date: 2020/9/14
     */
    public static List<String> txtString(File file) throws Exception {
        // 初期化
        ArrayList<String> arrayList = new ArrayList<>();
        BufferedReader br = null;
        try {
            // 构造一个BufferedReader类来读取文件
            br = new BufferedReader(new FileReader(file));
            String s = null;
            // 使用readLine方法，一次读一行
            while ((s = br.readLine()) != null) {
                arrayList.add((System.lineSeparator() + s).trim());
            }
        } catch (Exception e) {
            logger.error("設定ファイル(" + file.getName() + ")の読込失敗");
            e.printStackTrace();
        } finally {
            if (br != null) {
                br.close();
            }
        }

        // 戻り値
        return arrayList;
    }

    /**
     * @Param: file
     * @description: 都道府県設定値取得
     * @return: java.util.HashMap<java.lang.String,java.lang.String>
     * @throws Exception
     * @date: 2020/9/22
     */
    public static HashMap<String, String> todouTxt(File file) throws Exception {
        // 初期化
        BufferedReader br = null;
        HashMap<String, String> map = new HashMap<>();
        try {
            // 构造一个BufferedReader类来读取文件
            br = new BufferedReader(new FileReader(file));
            String s = null;
            // 使用readLine方法，一次读一行
            while ((s = br.readLine()) != null) {
                String trim = (System.lineSeparator() + s).trim();
                List<String> list = Splitter.on("=").omitEmptyStrings().trimResults().splitToList(trim);
                map.put(list.get(0), list.get(1));
            }
        } catch (Exception e) {
            logger.error("都道府県設定ファイル(" + file.getName() + ")処理失敗");
            e.printStackTrace();
        } finally {
            // 必ず閉じること
            if (br != null) {
                br.close();
            }
        }
        // 戻り値
        return map;
    }

    // put请求
    public static String sendPut(String url) {
        String encode = "utf-8";
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        HttpPut httpput = new HttpPut(url);
        httpput.setHeader("Accept", "*/*");
        httpput.setHeader("Accept-Encoding", "gzip, deflate");
        httpput.setHeader("Cache-Control", "no-cache");
        httpput.setHeader("Connection", "keep-alive");
        httpput.setHeader("Content-Type", "application/json;charset=UTF-8");
        // a请求参数处理
        // StringEntity stringEntity = new StringEntity(JSON.toJSONString(map), encode);
        // httpput.setEntity(stringEntity);
        String content = null;
        CloseableHttpResponse httpResponse = null;
        try {
            // a响应信息
            httpResponse = closeableHttpClient.execute(httpput);
            HttpEntity entity = httpResponse.getEntity();
            content = EntityUtils.toString(entity, encode);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpResponse.close();
            } catch (IOException e) {
                e.getMessage();
            }
        }
        try {
            closeableHttpClient.close(); // 关闭连接、释放资源
        } catch (IOException e) {
            e.getMessage();
        }
        return content;
    }

    /**
     * @description: Ecforce対応HTTP送信(Get)
     * @Param: URL
     * @Param: Json
     * @Param: Token
     * @return: JSONObject
     * @author: wang
     * @throws Exception
     * @date: 2020/9/22
     */
    public static String sendGetEcforce(String URL, String token) throws Exception {
        // 初期化
        CloseableHttpClient client = HttpClients.createDefault();
        // HTTP接続URL(Get)
        HttpGet get = new HttpGet(URL);
        // 認証トークンがない場合、設定無
        if (!StringTools.isNullOrEmpty(token)) {
            get.setHeader("Authorization", token);
        }
        String result = "";
        // HTTP送信(Get)
        CloseableHttpResponse response = client.execute(get);
        try {
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                response = client.execute(get);
            }
            // 获取响应输入流
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }

        } catch (Exception e) {
            logger.error("Ecforce請求失敗(GET)" + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            response.close();
            client.close();
        }
        // 戻り値
        return result;
    }

    /**
     * @description: HTTP送信(POST)
     * @Param: URL
     * @Param: Json
     * @Param: Token
     * @return: String
     * @author: wang
     * @throws Exception
     * @date: 2020/9/22
     */
    public static String sendJsonPost(String URL, StringBuilder json, String token) throws Exception {
        // 戻り値
        String result = "";
        String line;
        // 初期化
        BufferedReader rd = null;
        InputStream in = null;
        CloseableHttpClient client = HttpClients.createDefault();
        // リクエスト設定(POST)
        HttpPost post = new HttpPost(URL);
        // Header設定
        post.setHeader("Content-Type", "application/json");
        // 認証トークンがない場合、設定無
        if (!Strings.isNullOrEmpty(token)) {
            post.setHeader("Authorization", token);
        }
        try {
            StringEntity s = new StringEntity(json.toString(), "utf-8");
            s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(s);
            // レスポンス実行
            HttpResponse httpResponse = client.execute(post);
            // レスポンス値を取得
            in = httpResponse.getEntity().getContent();
            rd = new BufferedReader(new InputStreamReader(in, "utf-8"));

            StringBuilder strber = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                strber.append(line + "\n");
            }
            // 文字列として、結果を返却
            result = strber.toString();
            // 戻る値
            return result;
        } catch (Exception e) {
            logger.error("請求失敗(POST)" + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            // 異常発生した際、InputStream閉じること
            if (in != null) {
                in.close();
            }
            if (rd != null) {
                rd.close();
            }
        }
    }

    /**
     * @description: HTTP送信(POST)
     * @Param: URL
     * @Param: Json
     * @Param: Token
     * @return: String
     * @author: wang
     * @throws Exception
     * @date: 2020/9/22
     */
    public static String sendJsonPost(String URL, StringBuilder json, String tokenName, String token) throws Exception {
        // 戻り値
        String result = "";
        String line;
        // 初期化
        BufferedReader rd = null;
        InputStream in = null;
        CloseableHttpClient client = HttpClients.createDefault();
        // リクエスト設定(POST)
        HttpPost post = new HttpPost(URL);
        // Header設定
        post.setHeader("Content-Type", "application/json");
        // 認証トークンがない場合、設定無
        if (!Strings.isNullOrEmpty(token)) {
            post.setHeader(tokenName, token);
        }
        try {
            StringEntity s = new StringEntity(json.toString(), "utf-8");
            s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(s);
            // レスポンス実行
            HttpResponse httpResponse = client.execute(post);
            // レスポンス値を取得
            in = httpResponse.getEntity().getContent();
            rd = new BufferedReader(new InputStreamReader(in, "utf-8"));

            StringBuilder strber = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                strber.append(line + "\n");
            }
            // 文字列として、結果を返却
            result = strber.toString();
            // 戻る値
            return result;
        } catch (Exception e) {
            logger.error("請求失敗(POST)" + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            // 異常発生した際、InputStream閉じること
            if (in != null) {
                in.close();
            }
            if (rd != null) {
                rd.close();
            }
        }
    }

    /**
     * @description: HTTP送信(PUT)
     * @Param: URL
     * @Param: Json
     * @Param: Token
     * @return: String
     * @author: wang
     * @throws Exception
     * @date: 2020/9/22
     */
    public static String sendJsonPut(String URL, StringBuilder json, String token) throws Exception {
        // 戻り値
        String result = "";
        String line;
        // 初期化
        BufferedReader rd = null;
        InputStream in = null;
        // リクエスト設定(POST)
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPut put = new HttpPut(URL);
        // Header設定
        put.setHeader("Content-Type", "application/json");
        // 認証トークンがない場合、設定無
        if (!Strings.isNullOrEmpty(token)) {
            put.setHeader("Authorization", token);
        }
        try {
            StringEntity entity = new StringEntity(json.toString(), "utf-8");
            entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            put.setEntity(entity);
            // レスポンス実行
            HttpResponse httpResponse = client.execute(put);
            // レスポンス値を取得
            in = httpResponse.getEntity().getContent();
            rd = new BufferedReader(new InputStreamReader(in, "utf-8"));

            StringBuilder strber = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                strber.append(line + "\n");
            }
            // InputStreamを閉じる
            in.close();
            // 文字列として、結果を返却
            result = strber.toString();
            // 戻る値
            return result;
        } catch (Exception e) {
            logger.error("請求失敗(POST)" + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            // 異常発生した際、InputStream閉じること
            if (in != null) {
                in.close();
            }
            if (rd != null) {
                rd.close();
            }
        }
    }

    /**
     * @description: Ecforce対応HTTP送信(POST)
     * @Param: URL
     * @Param: Json
     * @Param: Token
     * @return: JSONObject
     * @author: wang
     * @throws Exception
     * @date: 2020/9/22
     */
    public static JSONObject sendPostEcforce(String url, Map<String, String> param, String token) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        JSONObject jsonObject = null;
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Accept-Language", "en-US,en;q=0.5");

            // 認証トークンがない場合、設定無
            if (!Strings.isNullOrEmpty(token)) {
                httpPost.addHeader("Authorization", token);
            }

            // パラメータ生成
            if (!StringTools.isNullOrEmpty(param)) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // パラメータを生成
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "utf-8");
                httpPost.setEntity(entity);
            }
            // リクエスト実施
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");

            jsonObject = JSONObject.parseObject(resultString);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (Exception e) {
                // 異常出力
                e.printStackTrace();
            }
        }
        // 戻り値
        return jsonObject;
    }

    public static JSONObject sendXmlPost(String apiurl, String param, String tokenName, String token, String fileName,
        String tokenPasswd, List<String> errList) {
        URL url;
        HttpsURLConnection con;
        BufferedReader br;
        String line;
        String result = "";
        char[] PASSWORD = tokenPasswd.toCharArray();
        String P12FILE = YAHOO_AUTHENTICATION_FILE_PATH + fileName;
        try (FileInputStream inputStream = new FileInputStream(P12FILE)) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(inputStream, PASSWORD);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keyStore, PASSWORD);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, null);
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            url = new URL(apiurl);
            con = (HttpsURLConnection) url.openConnection();
            con.setSSLSocketFactory(ssf);
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
            con.setRequestProperty(tokenName, token);
            con.connect();
            PrintWriter pw = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(
                    con.getOutputStream(), "utf-8")));
            pw.print(param);// content
            pw.close();
            br = new BufferedReader(new InputStreamReader(
                con.getInputStream(), "utf-8"));
            while ((line = br.readLine()) != null) {
                result = result + line;
            }

            result = result.replace("<![CDATA[", "").replace("]]>", "").replace("&", "&amp;");
            org.json.JSONObject xmlJSONObj = XML.toJSONObject(result);
            result = xmlJSONObj.toString(4);
            br.close();
            con.disconnect();
        } catch (FileNotFoundException e) {
            logger.error("Yahooの認証ファイルが見つかりませんでした。ファイルパス【{}】", YAHOO_AUTHENTICATION_FILE_PATH + fileName);
            if (!StringTools.isNullOrEmpty(errList)) {
                errList.add("Yahooの認証ファイルが見つかりませんでした。ファイルパス【" + YAHOO_AUTHENTICATION_FILE_PATH + fileName + "】");

            }
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException
            | UnrecoverableKeyException | KeyManagementException | org.json.JSONException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        return jsonObject;
    }


    // TODO 共通パーツに変更したので、問題なければ、後日削除必要
    public static JSONObject sendYahooPost(String url, String param, String tokenName, String token) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty(tokenName, token);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("エラーが発生しました。！" + e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        return jsonObject;
    }

    public static void download(String src, String dst) throws Exception {
        URL remoteUrl = new URL(src);
        HttpsURLConnection http = (HttpsURLConnection) remoteUrl.openConnection();

        TrustManager[] trustManagers = new TrustManager[] {
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
                    throws CertificateException {}

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
                    throws CertificateException {}

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }
        };

        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, trustManagers, null);

        http.setRequestMethod("GET");
        http.setSSLSocketFactory(ctx.getSocketFactory());

        http.setConnectTimeout(3000);
        String contentType = http.getContentType();

        InputStream inputStream = http.getInputStream();
        byte[] buffer = new byte[1024 * 10];
        OutputStream outputStream = new FileOutputStream(new File(dst));
        int len;
        int count = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
            outputStream.flush();
            ++count;
        }

        outputStream.close();
        inputStream.close();
        http.disconnect();
    }
}
