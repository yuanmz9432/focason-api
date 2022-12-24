package com.lemonico.api.controller;



import com.lemonico.api.service.MakeShopServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * メーカーショップコントローラー
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MakeShopController extends AbstractController
{
    private final static Logger logger = LoggerFactory.getLogger(MakeShopController.class);

    private final static String MAKESHOP_SEARCH_ORDER_URI = "/orders/makeshop";
    private final static String MAKESHOP_SEARCH_PAYMENT_STATUS_URI = "/orders/makeshop/payment-status";
    private final static String MAKESHOP_SEARCH_PROCESS_TRACKING_NO_URI = "/orders/makeshop/process-tracking-no";

    private final MakeShopServiceImpl service;

    /**
     * 受注連携
     *
     * @return {@link ResponseEntity<Void>}
     */
    @Override
    @GetMapping(MAKESHOP_SEARCH_ORDER_URI)
    protected ResponseEntity<Void> fetchOrders() {
        logger.info("MakeShop 受注連携　開始");
        ResponseEntity<Void> responseEntity = null;
        try {
            responseEntity = service.executeFetchOrderProcess();
        } catch (Exception exception) {
            logger.error("MakeShop 受注連携 異常発生");
            exception.printStackTrace();
        }
        logger.info("MakeShop 受注連携　終了");
        return responseEntity;
    }

    /**
     * 支払ステータス自動連携
     *
     * @return {@link ResponseEntity<Void>}
     */
    @Override
    @GetMapping(MAKESHOP_SEARCH_PAYMENT_STATUS_URI)
    protected ResponseEntity<Void> fetchPaymentStatus() {
        logger.info("MakeShop 支払ステータス自動連携 開始");
        logger.info("MakeShop 支払ステータス自動連携 開発中");
        logger.info("MakeShop 支払ステータス自動連携 終了");
        return null;
    }

    /**
     * 送り状番号自動連携
     *
     * @return {@link ResponseEntity<Void>}
     */
    @Override
    @GetMapping(MAKESHOP_SEARCH_PROCESS_TRACKING_NO_URI)
    protected ResponseEntity<Void> processTrackingNo() {
        logger.info("MakeShop 伝票番号自動連携 開始");
        logger.info("MakeShop 伝票番号自動連携 開発中");
        logger.info("MakeShop 伝票番号自動連携 終了");
        return null;
    }
}
