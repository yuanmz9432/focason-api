package api.lemonico.service;



import java.util.List;

import api.lemonico.resource.APIResource;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ColorMeService extends OrderAbstractService implements OrderInterface
{
    private static final Logger logger = LoggerFactory.getLogger(ColorMeService.class);

    @Override
    public void fetchOrder() {
        // 連携対象店舗取得
        var clients = getClientAPIList();
        super.getProduct();
    }

    @Override
    public void fetchPaymentStatus() {
        logger.info("============COLORME 入金ステータス変更 開始============");
        logger.info("============COLORME 入金ステータス変更 終了============");
    }

    @Override
    public void sendTrackingNumber() {
        logger.info("============COLORME 伝票番号自動連携 開始============");
        logger.info("============COLORME 伝票番号自動連携 終了============");
    }

    @Override
    public List<APIResource> getClientAPIList() {
        return null;
    }
}
