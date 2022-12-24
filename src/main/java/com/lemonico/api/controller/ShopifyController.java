package com.lemonico.api.controller;



import com.lemonico.api.service.ShopifyServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Shopifyコントローラー
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShopifyController extends AbstractController
{
    private final static Logger logger = LoggerFactory.getLogger(ShopifyController.class);

    public final static String SHOPIFY_SEARCH_ORDER_URI = "/orders/shopify";
    public final static String SHOPIFY_SEARCH_PROCESS_TRACKING_NO_URI = "/orders/shopify/process-tracking-no";

    private final ShopifyServiceImpl service;

    /**
     * 受注連携
     *
     * @return {@link ResponseEntity<Void>}
     */
    @Override
    @GetMapping(SHOPIFY_SEARCH_ORDER_URI)
    protected ResponseEntity<Void> fetchOrders() {
        logger.info("++++++++++++++++++++++ Shopify 受注API連携 開始 ++++++++++++++++++++++");
        ResponseEntity<Void> responseEntity = null;
        try {
            responseEntity = service.executeFetchOrderProcess();
        } catch (Exception exception) {
            logger.error("Shopify 受注連携 異常発生");
            exception.printStackTrace();
        }
        logger.info("++++++++++++++++++++++ Shopify 受注API連携 終了 ++++++++++++++++++++++");
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
    @GetMapping(SHOPIFY_SEARCH_PROCESS_TRACKING_NO_URI)
    protected ResponseEntity<Void> processTrackingNo() {
        logger.info("++++++++++++++++++++++ Shopify 伝票番号自動連携 開始 ++++++++++++++++++++++");
        ResponseEntity<Void> responseEntity = service.executeSendTrackingNoProcess();
        logger.info("++++++++++++++++++++++ Shopify 伝票番号自動連携 終了 ++++++++++++++++++++++");
        return responseEntity;
    }
}
