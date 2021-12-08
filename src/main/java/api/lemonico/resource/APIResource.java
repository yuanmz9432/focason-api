package api.lemonico.resource;



import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * APIリソース
 *
 * @since 1.0.0
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class APIResource
{
    /** クライアントコード */
    private final String clientCode;
    /** API名称 */
    private final String apiName;
    /** 識別子 */
    private final String identification;
    /** 期間 */
    private final Integer period;
    /** アクセストークン */
    private final String accessToken;
    /** 依頼主情報 */
    private final Object sponsor;
    /** 出荷依頼フラグ */
    private final Integer shipmentFlag;
    /** ディフォルト配送情報 */
    private final Integer defaultDelivery;
    /** ディフォルト支払情報 */
    private final Integer defaultPayment;
    /** 受注読込履歴ID */
    private final Integer orderFetchHistoryId;
}
