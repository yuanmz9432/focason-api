package api.lemonico.controller;

import api.lemonico.service.ColorMeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ColorMeController extends OrderAbstractController {
    private static final Logger logger = LoggerFactory.getLogger(ColorMeController.class);

    /**
     * カラーミーURI
     */
    private static final String COLORME_URI = FETCH_ORDER_URI + "/colorme";

    private final ColorMeService colorMeService;

    @GetMapping(COLORME_URI)
    public ResponseEntity<Void> fetchColorMeOrder() {
        logger.info("============COLORME 受注自動連携 開始============");
        colorMeService.fetchOrder();
        logger.info("============COLORME 受注自動連携 終了============");
        return ResponseEntity.noContent().build();
    }
}
