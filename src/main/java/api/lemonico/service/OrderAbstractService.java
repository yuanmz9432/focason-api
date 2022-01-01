package api.lemonico.service;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class OrderAbstractService
{
    private static final Logger logger = LoggerFactory.getLogger(OrderAbstractService.class);

    public void getProduct() {
        logger.info("商品マッピング処理中");
    }
}
