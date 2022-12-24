package com.lemonico.api.controller;



import com.lemonico.api.YahooAPI;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Yahooコントローラー
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class YahooController extends AbstractController
{
    public final static String YAHOO_SEARCH_ORDER_URI = "/orders/yahoo";
    public final static String YAHOO_SEARCH_PROCESS_TRACKING_NO_URI = "/orders/yahoo/process-tracking-no";
    private final static Logger logger = LoggerFactory.getLogger(YahooController.class);
    private final YahooAPI yahooAPI;

    /**
     * 受注連携
     *
     * @return {@link ResponseEntity<Void>}
     */
    @Override
    @GetMapping(YAHOO_SEARCH_ORDER_URI)
    protected ResponseEntity<Void> fetchOrders() {
        logger.info("++++++++++++++++++++++ Yahoo 受注API連携 開始 ++++++++++++++++++++++");
        ResponseEntity<Void> responseEntity = null;
        try {
            yahooAPI.fetchYahooOrders();
        } catch (Exception exception) {
            logger.error("Yahoo 受注連携 異常発生");
            exception.printStackTrace();
        }
        logger.info("++++++++++++++++++++++ Yahoo 受注API連携 終了 ++++++++++++++++++++++");
        return responseEntity;
    }

    @Override
    ResponseEntity<Void> fetchPaymentStatus() {
        return null;
    }

    /**
     * 伝票番号連携
     *
     * @return {@link ResponseEntity<Void>}
     */
    @Override
    @GetMapping(YAHOO_SEARCH_PROCESS_TRACKING_NO_URI)
    protected ResponseEntity<Void> processTrackingNo() {
        logger.info("++++++++++++++++++++++ Yahoo 伝票番号自動連携 開始 ++++++++++++++++++++++");
        logger.info("++++++++++++++++++++++ Yahoo 伝票番号自動連携 終了 ++++++++++++++++++++++");
        return null;
    }
}
