package com.lemonico.api.controller;



import com.lemonico.api.service.NextEngineServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Next-Engineコントローラー
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NextEngineController extends AbstractController
{
    public final static String NEXT_ENGINE_SEARCH_ORDER_URI = "/orders/next-engine";
    public final static String NEXT_ENGINE_SEARCH_PROCESS_TRACKING_NO_URI = "/orders/next-engine/process-tracking-no";
    private final static Logger logger = LoggerFactory.getLogger(NextEngineController.class);
    private final NextEngineServiceImpl service;

    /**
     * 受注連携
     *
     * @return {@link ResponseEntity<Void>}
     */
    @Override
    @GetMapping(NEXT_ENGINE_SEARCH_ORDER_URI)
    protected ResponseEntity<Void> fetchOrders() {
        logger.info("++++++++++++++++++++++ Next-Engine 受注API連携 開始 ++++++++++++++++++++++");
        ResponseEntity<Void> responseEntity = null;
        try {
            responseEntity = service.executeFetchOrderProcess();
        } catch (Exception exception) {
            logger.error("Next-Engine 受注連携 異常発生");
            exception.printStackTrace();
        }
        logger.info("++++++++++++++++++++++ Next-Engine 受注API連携 終了 ++++++++++++++++++++++");
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
    @GetMapping(NEXT_ENGINE_SEARCH_PROCESS_TRACKING_NO_URI)
    protected ResponseEntity<Void> processTrackingNo() {
        logger.info("++++++++++++++++++++++ Next-Engine 伝票番号自動連携 開始 ++++++++++++++++++++++");
        ResponseEntity<Void> responseEntity = null;
        try {
            responseEntity = service.executeSendTrackingNoProcess();
        } catch (Exception exception) {
            logger.error("Next-Engine 伝票番号自動連携 異常発生");
            exception.printStackTrace();
        }
        logger.info("++++++++++++++++++++++ Next-Engine 伝票番号自動連携 終了 ++++++++++++++++++++++");
        return responseEntity;
    }
}
