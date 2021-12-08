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
abstract class OrderAbstractController
{
    /**
     * 受注連携URI
     */
    protected static final String FETCH_ORDER_URI = "/order";
}
