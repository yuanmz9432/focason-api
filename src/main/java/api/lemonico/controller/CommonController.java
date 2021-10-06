package api.lemonico.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CommonController extends AbstractController {
    
    private static final Logger logger = LoggerFactory.getLogger(CommonController.class);

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, path = "/heartbeat", produces = "text/plain;charset=UTF-8")
    public String healthCheck() {
        logger.info("The application is working now.");
        return "OK";
    }
}
