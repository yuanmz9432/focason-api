package com.lemonico.api.service;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemonico.api.bean.ClientInfo;
import com.lemonico.common.bean.Tc203_order_client;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

public interface APIInterface
{
    /**
     * 受注連携処理起動
     *
     * @return {@link ResponseEntity<Void>}
     */
    ResponseEntity<Void> executeFetchOrderProcess();

    /**
     * 事前処理
     *
     * @return {@link ResponseEntity<Void>}
     */
    Object preProcess(Object... objects);

    /**
     * ECサイトから受注情報取得
     *
     * @param tc203OrderClient 店舗API情報
     * @return 受注情報
     */
    JSONArray fetchOrders(Tc203_order_client tc203OrderClient);

    /**
     * 受注情報をテーブルに登録
     *
     * @param newOrders 新規受注情報
     * @param clientInfo 店舗情報
     */
    @Transactional
    void insertOrder(List<JSONObject> newOrders, ClientInfo clientInfo);

    /**
     * 受注情報をTc200Orderテーブルに登録
     *
     * @param jsonObject 受注情報
     * @param clientInfo 店舗情報
     * @param purchaseOrderNo 受注番号
     */
    void insertTc200Order(JSONObject jsonObject, ClientInfo clientInfo, String purchaseOrderNo);

    /**
     * 受注情報をTc201OrderDetailテーブルに登録
     *
     * @param jsonObject 受注情報
     * @param clientInfo 店舗情報
     * @param purchaseOrderNo 受注番号
     */
    void insertTc201OrderDetail(JSONObject jsonObject, ClientInfo clientInfo, String purchaseOrderNo);

    /**
     * 取得した受注情報から、キャンセル済み受注と既存受注情報を排除
     *
     * @param jsonArray 受注情報
     * @param clientInfo 店舗情報
     * @return 加工済み受注情報
     */
    List<JSONObject> filterNewOrders(JSONArray jsonArray, ClientInfo clientInfo);

    /**
     * 支払ステータス自動連携
     *
     * @return {@link ResponseEntity}
     */
    ResponseEntity<Void> executeFetchPaymentStatusProcess();

    /**
     * 送り状番号自動連携
     *
     * @return {@link ResponseEntity}
     */
    ResponseEntity<Void> executeSendTrackingNoProcess();
}
