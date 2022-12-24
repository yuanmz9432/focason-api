package com.lemonico.api.controller;



import com.lemonico.api.service.ColorMeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * カラーミーコントローラー
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ColorMeController extends AbstractController
{
    private final static Logger logger = LoggerFactory.getLogger(ColorMeController.class);

    private final static String COLORME_SEARCH_ORDER_URI = "/orders/colorme";
    private final static String COLORME_SEARCH_PAYMENT_STATUS_URI = "/orders/colorme/payment-status";
    private final static String COLORME_SEARCH_PROCESS_TRACKING_NO_URI = "/orders/colorme/process-tracking-no";

    private final ColorMeServiceImpl service;

    /**
     * 受注連携
     *
     * @return {@link ResponseEntity<Void>}
     */
    @Override
    @GetMapping(COLORME_SEARCH_ORDER_URI)
    protected ResponseEntity<Void> fetchOrders() {
        logger.info("++++++++++++++++++++++ ColorMe 受注API連携 開始 ++++++++++++++++++++++");
        ResponseEntity<Void> responseEntity = null;
        try {
            responseEntity = service.executeFetchOrderProcess();
        } catch (Exception exception) {
            logger.error("ColorMe 受注API連携 異常発生");
            exception.printStackTrace();
        }
        logger.info("++++++++++++++++++++++ ColorMe 受注API連携 終了 ++++++++++++++++++++++");
        return responseEntity;
    }

    /**
     * 支払ステータス自動連携(30分ごと自動起動 3, 33)
     *
     * @return {@link ResponseEntity<Void>}
     */
    @Override
    @GetMapping(COLORME_SEARCH_PAYMENT_STATUS_URI)
    protected ResponseEntity<Void> fetchPaymentStatus() {
        logger.info("++++++++++++++++++++++ ColorMe 支払ステータス自動連携 開始 ++++++++++++++++++++++");
        ResponseEntity<Void> responseEntity = service.executeFetchPaymentStatusProcess();
        logger.info("++++++++++++++++++++++ ColorMe 支払ステータス自動連携 終了 ++++++++++++++++++++++");
        return responseEntity;
    }

    /**
     * 送り状番号自動連携(15分ごと自動起動 0, 15, 30, 45)
     *
     * @return {@link ResponseEntity<Void>}
     */
    @Override
    @GetMapping(COLORME_SEARCH_PROCESS_TRACKING_NO_URI)
    protected ResponseEntity<Void> processTrackingNo() {
        logger.info("ColorMe 伝票番号自動連携 開始");
        ResponseEntity<Void> responseEntity = service.executeSendTrackingNoProcess();
        logger.info("ColorMe 伝票番号自動連携 終了");
        return responseEntity;
    }
}
