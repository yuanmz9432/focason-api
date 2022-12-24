package com.lemonico.batch;



import com.lemonico.api.service.ColorMeServiceImpl;
import com.lemonico.api.service.MakeShopServiceImpl;
import com.lemonico.api.service.NextEngineServiceImpl;
import com.lemonico.api.service.ShopifyServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * SunLOGI定時タスクの定義クラス
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ScheduleTaskHandler
{
    private final static Logger logger = LoggerFactory.getLogger(ScheduleTaskHandler.class);

    // 受注検索スケジュール
    private final static String COLORME_SEARCH_ORDER_SCHEDULED = "0 1/10 * * * ?";
    private final static String MAKESHOP_SEARCH_ORDER_SCHEDULED = "0 2/10 * * * ?";
    private final static String NEXT_ENGINE_SEARCH_ORDER_SCHEDULED = "0 3/10 * * * ?";
    // 伝票番号連携スケジュール
    private final static String SHOPIFY_SEND_TRACKING_NO_SCHEDULED = "30 0/10 * * * ?";
    private final static String NEXT_ENGINE_SEND_TRACKING_NO_SCHEDULED = "30 3/10 * * * ?";
    // その他機能スケジュール
    private final static String UPDATE_NOT_DELIVERY_COUNT_SCHEDULED = "0 0 1 * * ?";
    private final static String CREATE_RECEIPT_AND_DELIVERY_SCHEDULED = "0 0/10 * * * ?";
    private final static String SEARCH_ERROR_DATA_SCHEDULED = "0 0 4 * * ?";
    private final static String UPLOAD_DATA_TO_S3_SCHEDULED = "0 0/20 * * * ?";

    private final ColorMeServiceImpl colorMeService;
    private final MakeShopServiceImpl makeShopService;
    private final ShopifyServiceImpl shopifyService;
    private final NextEngineServiceImpl nextEngineService;
    private final ScheduleTasks tasks;

    /**
     * Shopify伝票番号自動連携タスク
     *
     * <p>
     * 15分ごと自動起動( 0, 15, 30, 45 )
     */
    // @Scheduled(cron = SHOPIFY_SEND_TRACKING_NO_SCHEDULED)
    private void shopifySendTrackingNoTask() {
        logger.info("++++++++++++++++++++++ Shopify 伝票番号自動連携 開始 ++++++++++++++++++++++");
        try {
            shopifyService.executeSendTrackingNoProcess();
        } catch (Exception exception) {
            logger.error("Shopify 伝票番号自動連携 異常発生");
            exception.printStackTrace();
        }
        logger.info("++++++++++++++++++++++ Shopify 伝票番号自動連携 終了 ++++++++++++++++++++++");
    }

    /**
     * Next-Engine伝票番号自動連携タスク
     *
     * <p>
     * 10分ごと自動起動( 3, 13, 23, 33, 43, 53 )
     */
    // @Scheduled(cron = NEXT_ENGINE_SEARCH_ORDER_SCHEDULED)
    private void nextEngineSearchOrderTask() {
        logger.info("++++++++++++++++++++++ Next-Engine 受注バッチ連携 開始 ++++++++++++++++++++++");
        try {
            nextEngineService.executeFetchOrderProcess();
        } catch (Exception exception) {
            logger.error("Next-Engine 受注バッチ連携 異常発生");
            exception.printStackTrace();
        }
        logger.info("++++++++++++++++++++++ Next-Engine 受注バッチ連携 終了 ++++++++++++++++++++++");
    }

    /**
     * Next-Engine伝票番号自動連携タスク
     *
     * <p>
     * 10分ごと自動起動( 3, 13, 23, 33, 43, 53 )
     */
    // @Scheduled(cron = NEXT_ENGINE_SEND_TRACKING_NO_SCHEDULED)
    private void nextEngineSendTrackingNoTask() {
        logger.info("++++++++++++++++++++++ Next-Engine 伝票番号自動連携 開始 ++++++++++++++++++++++");
        try {
            nextEngineService.executeSendTrackingNoProcess();
        } catch (Exception exception) {
            logger.error("Next-Engine 伝票番号自動連携 異常発生");
            exception.printStackTrace();
        }
        logger.info("++++++++++++++++++++++ Next-Engine 伝票番号自動連携 終了 ++++++++++++++++++++++");
    }

    /**
     * カラーミー受注自動連携タスク
     *
     * <p>
     * 10分ごと自動起動( 1, 11, 21, 31, 41, 51 )
     */
    // //@Scheduled(cron = COLORME_SEARCH_ORDER_SCHEDULED)
    private void colorMeSearchOrderTask() {
        logger.info("++++++++++++++++++++++ ColorMe 受注バッチ連携 開始 ++++++++++++++++++++++");
        try {
            colorMeService.executeFetchOrderProcess();
        } catch (Exception exception) {
            logger.error("ColorMe 受注バッチ連携 異常発生");
            exception.printStackTrace();
        }
        logger.info("++++++++++++++++++++++ ColorMe 受注バッチ連携 終了 ++++++++++++++++++++++");
    }

    /**
     * メーカーショップ受注自動連携タスク
     *
     * <p>
     * 10分ごと自動起動( 3, 13, 23, 33, 43, 53 )
     */
    // //@Scheduled(cron = MAKESHOP_SEARCH_ORDER_SCHEDULED)
    private void makeShopSearchOrderTask() {
        logger.info("++++++++++++++++++++++ MakeShop 受注バッチ連携 開始 ++++++++++++++++++++++");
        try {
            makeShopService.executeFetchOrderProcess();
        } catch (Exception exception) {
            logger.error("MakeShop 受注バッチ連携 異常発生");
            exception.printStackTrace();
        }
        logger.info("++++++++++++++++++++++ MakeShop 受注バッチ連携 終了 ++++++++++++++++++++++");
    }

    /**
     * 配送不可数更新処理
     *
     * <p>
     * 1時間ごと自動起動
     */
    // @Scheduled(cron = UPDATE_NOT_DELIVERY_COUNT_SCHEDULED)
    private void updateNotDeliveryCount() {
        logger.info("++++++++++++++++++++++ 配送不可数 更新処理 開始 ++++++++++++++++++++++");
        try {
            tasks.updateNotDeliveryCount();
        } catch (Exception exception) {
            logger.error("配送不可数 定時更新処理 異常発生");
            exception.printStackTrace();
        }
        logger.info("++++++++++++++++++++++ 配送不可数 更新処理 終了 ++++++++++++++++++++++");
    }

    /**
     * 納品書と領収書生成処理
     *
     * <p>
     * 10分ごと自動起動( 0, 10, 20, 30, 40, 50 )
     */
    // @Scheduled(cron = CREATE_RECEIPT_AND_DELIVERY_SCHEDULED)
    private void createReceiptAndDeliveryPDF() {
        logger.info("++++++++++++++++++++++ 納品書と領収書 生成処理 開始 ++++++++++++++++++++++");
        try {
            tasks.createReceiptAndDeliveryPDF();
        } catch (Exception exception) {
            logger.error("納品書と領収書 生成処理 異常発生");
            exception.printStackTrace();
        }
        logger.info("++++++++++++++++++++++ 納品書と領収書 生成処理 終了 ++++++++++++++++++++++");
    }

    /**
     * 異常データ検索処理
     *
     * <p>
     * 毎日の4時自動起動
     */
    // @Scheduled(cron = SEARCH_ERROR_DATA_SCHEDULED)
    private void searchErrorData() {
        logger.info("++++++++++++++++++++++ 異常データ 検索処理 開始 ++++++++++++++++++++++");
        try {
            tasks.searchErrorData();
        } catch (Exception exception) {
            logger.error("異常データ 検索処理 異常発生");
            exception.printStackTrace();
        }
        logger.info("++++++++++++++++++++++ 異常データ 検索処理 終了 ++++++++++++++++++++++");
    }

    /**
     * S3に伝票番号アップロード処理
     *
     * <p>
     * 20分ごと自動起動( 0, 20, 40 )
     */
    // @Scheduled(cron = UPLOAD_DATA_TO_S3_SCHEDULED)
    private void uploadDataToS3() {
        logger.info("++++++++++++++++++++++ S3に伝票番号アップロード処理 開始 ++++++++++++++++++++++");
        try {
            tasks.uploadDataToS3();
        } catch (Exception exception) {
            logger.error("S3に伝票番号アップロード処理 異常発生");
            exception.printStackTrace();
        }
        logger.info("++++++++++++++++++++++ S3に伝票番号アップロード処理 終了 ++++++++++++++++++++++");
    }
}
