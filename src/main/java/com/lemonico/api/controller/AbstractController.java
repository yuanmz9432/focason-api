package com.lemonico.api.controller;



import org.springframework.http.ResponseEntity;

public abstract class AbstractController
{

    /**
     * 受注連携
     *
     * @return
     */
    abstract ResponseEntity<Void> fetchOrders();

    /**
     * 支払ステータス自動連携
     *
     * @return
     */
    abstract ResponseEntity<Void> fetchPaymentStatus();

    /**
     * 送り状番号自動連携
     *
     * @return
     */
    abstract ResponseEntity<Void> processTrackingNo();
}
