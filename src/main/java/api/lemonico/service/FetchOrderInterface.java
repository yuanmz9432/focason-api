package api.lemonico.service;



import java.util.List;

public interface FetchOrderInterface
{
    /**
     * 受注自動連携
     */
    void fetchOrder();

    /**
     * 入金ステータス変更
     */
    void fetchPaymentStatus();

    /**
     * 伝票番号を先方APIに連携
     */
    void sendTrackingNumber();

    /**
     * API区分を指定して、受注連携クライアントリストを取得
     *
     * @return 受注連携クライアントリスト
     */
    List<Object> getClients();


}
