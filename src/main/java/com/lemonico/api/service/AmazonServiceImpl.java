package com.lemonico.api.service;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemonico.api.bean.ClientInfo;
import com.lemonico.api.utils.Amazon.AmazonSignature;
import com.lemonico.api.utils.SLHttpClient;
import com.lemonico.common.bean.Tc203_order_client;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import javax.validation.constraints.NotNull;
import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * アマゾンAPIサービス実現クラス
 */
@Service
public class AmazonServiceImpl extends APIService implements APIInterface
{
    private final static Logger logger = LoggerFactory.getLogger(AmazonServiceImpl.class);

    private final static String REFRESH_TOKEN_URI = "https://api.amazon.com/auth/o2/token";
    private final static String REFRESH_TOKEN =
        "Atzr|IwEBIFK3GzMKtw0H0hVUzR5Y1mgpEE8KYBG5WEv8vkeTluzxPFjkq0Esk0AbHDjQhlDe2VR3V8NljAKYRRedNQHvOifg-5QZ0KXsFO5YphIVo8f4i82ElOiRCIU3P7EWbuX7QbTCRz83KjoCVEY6AWv0_zH9MVmS8T0Ax83aIf-tjfKY6bIz-qUzmgda_hv1Ql1FLecs0Mh6EkedghE3QxjFIQDJvAYV1JcqvmyUTQsSmUrFT3Dqu6NZEeRwTQCfxTX0-txnCWSfd_vHyK5yQ_BLaAOD3ZwVCn4wEQoSAZ_4uM8qMTPCeqrKCWgPdUgKoGiQCW1ZrTOJ3urdZNfoLId0PVhS";

    private final static String USER_AGENT = "SunLOGI/1.0 (Language=Java/1.8.0.221; Platform=Windows/10)";
    private final static String HOST = "sellingpartnerapi-fe.amazon.com";
    private final static String SEARCH_ORDERS_URI = "/orders/v0/orders";
    private final static String APP_CLIENT_ID = "amzn1.application-oa2-client.7dc8146bc16446e4a7826f8aded5e7c5";
    private final static String APP_CLIENT_SECRET = "827b3e92cac14c967adc7196836c91c18a7fd1c52d027a804d838737cbcc1d63";

    private static String generateXAmzDate() {
        SimpleDateFormat xAmzDateFormatter = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        xAmzDateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return xAmzDateFormatter.format(new Date()).trim();
    }

    @Override
    public Object preProcess(Object... objects) {
        return null;
    }

    @Override
    public ResponseEntity<Void> executeFetchOrderProcess() {
        logger.info("Amazon 受注連携処理 開始");
        fetchOrders(null);
        logger.info("Amazon 受注連携処理 終了");
        return null;
    }

    @Override
    public JSONArray fetchOrders(@NotNull Tc203_order_client tc203OrderClient) {
        // ①アクセストークン生成
        final String accessToken = generateAccessToken();
        // ②UTC日付生成
        final String xAmzDate = generateXAmzDate();
        // ③Authorization認証情報生成
        JSONObject params = new JSONObject();
        params.put("accessToken", accessToken);
        params.put("xAmzDate", xAmzDate);
        params.put("canonicalUri", SEARCH_ORDERS_URI);
        // ※注意点：クエリ文字列がA~Zのソート順で追加必要
        final String createdAfter = LocalDate.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        final String queryString =
            "CreatedAfter=" + createdAfter + "&MarketplaceIds=A1VC38T7YXB528&OrderStatuses=Shipped";
        params.put("canonicalQueryString", queryString);
        params.put("requestPayload", "");
        final String authorization = AmazonSignature.generate(params);
        // ④リクエストヘッダー作成
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("host", HOST);
        headerMap.put("x-amz-access-token", accessToken);
        headerMap.put("user-agent", USER_AGENT);
        headerMap.put("Authorization", authorization);
        headerMap.put("x-amz-date", xAmzDate);
        // ⑤リクエストURL作成
        final String searchOrderUrl = "https://" + HOST + SEARCH_ORDERS_URI + "?" + queryString;
        String responseStr = SLHttpClient.get(searchOrderUrl, headerMap);
        JSONObject response = JSONObject.parseObject(responseStr);
        logger.info(response.toJSONString());

        return null;
    }

    private String generateAccessToken() {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-type", "application/x-www-form-urlencoded");

        HashMap<String, String> bodyMap = new HashMap<>();
        bodyMap.put("grant_type", "refresh_token");
        bodyMap.put("refresh_token", REFRESH_TOKEN);
        bodyMap.put("client_id", APP_CLIENT_ID);
        bodyMap.put("client_secret", APP_CLIENT_SECRET);

        HttpEntity httpEntity = SLHttpClient.createUrlEncodedFormEntity(bodyMap);

        String responseStr = SLHttpClient.post(REFRESH_TOKEN_URI, headerMap, httpEntity);
        JSONObject response = JSONObject.parseObject(responseStr);

        return response.getString("access_token");
    }

    @Override
    public void insertOrder(List<JSONObject> newOrders, ClientInfo clientInfo) {

    }

    @Override
    public void insertTc200Order(JSONObject jsonObject, ClientInfo clientInfo, String purchaseOrderNo) {

    }

    @Override
    public void insertTc201OrderDetail(JSONObject jsonObject, ClientInfo clientInfo, String purchaseOrderNo) {

    }

    @Override
    public List<JSONObject> filterNewOrders(JSONArray jsonArray, ClientInfo clientInfo) {
        return null;
    }

    @Override
    public ResponseEntity<Void> executeFetchPaymentStatusProcess() {
        return null;
    }

    @Override
    public ResponseEntity<Void> executeSendTrackingNoProcess() {
        return null;
    }
}
