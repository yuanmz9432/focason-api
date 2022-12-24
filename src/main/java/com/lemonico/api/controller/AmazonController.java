package com.lemonico.api.controller;



import com.lemonico.api.service.AmazonServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * アマゾンコントローラー
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmazonController extends AbstractController
{
    private final static Logger logger = LoggerFactory.getLogger(AmazonController.class);

    private final static String AMAZON_SEARCH_ORDER_URI = "/orders/amazon";
    private final static String AMAZON_SEARCH_PAYMENT_STATUS_URI = "/orders/amazon/payment-status";
    private final static String AMAZON_SEARCH_PROCESS_TRACKING_NO_URI = "/orders/amazon/process-tracking-no";

    private final AmazonServiceImpl service;

    /**
     * 受注連携
     *
     * @return {@link ResponseEntity<Void>}
     */
    @Override
    @GetMapping(AMAZON_SEARCH_ORDER_URI)
    protected ResponseEntity<Void> fetchOrders() {
        logger.info("++++++++++++++++++++++ Amazon 受注API連携 開始 ++++++++++++++++++++++");
        ResponseEntity<Void> responseEntity = null;
        try {
            responseEntity = service.executeFetchOrderProcess();
        } catch (Exception exception) {
            logger.error("Amazon 受注連携 異常発生");
            exception.printStackTrace();
        }
        logger.info("++++++++++++++++++++++ Amazon 受注API連携 終了 ++++++++++++++++++++++");
        return responseEntity;
    }

    /**
     * 支払ステータス自動連携
     *
     * @return {@link ResponseEntity<Void>}
     */
    @Override
    @GetMapping(AMAZON_SEARCH_PAYMENT_STATUS_URI)
    protected ResponseEntity<Void> fetchPaymentStatus() {
        logger.info("ColorMe 支払ステータス自動連携 開始");
        logger.info("ColorMe 支払ステータス自動連携 開発中");
        logger.info("ColorMe 支払ステータス自動連携 終了");
        return null;
    }

    /**
     * 送り状番号自動連携
     *
     * @return {@link ResponseEntity<Void>}
     */
    @Override
    @GetMapping(AMAZON_SEARCH_PROCESS_TRACKING_NO_URI)
    protected ResponseEntity<Void> processTrackingNo() {
        logger.info("ColorMe 伝票番号自動連携 開始");
        logger.info("ColorMe 伝票番号自動連携 開発中");
        logger.info("ColorMe 伝票番号自動連携 終了");
        return null;
    }
}
