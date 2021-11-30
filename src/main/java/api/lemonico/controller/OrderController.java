package api.lemonico.controller;



import api.lemonico.service.ColorMeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 受注連携コントローラー
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderController
{
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    /**
     * 受注連携URI
     */
    private static final String FETCH_ORDER_URI = "/order";
    /**
     * カラーミーURI
     */
    private static final String COLORME = FETCH_ORDER_URI + "/colorme";

    private final ColorMeService colorMeService;

    @GetMapping(COLORME)
    public ResponseEntity<Void> fetchColorMeOrder() {
        logger.info("============COLORME 受注自動連携 開始============");
        colorMeService.fetchOrder();
        logger.info("============COLORME 受注自動連携 終了============");
        return ResponseEntity.noContent().build();
    }
}
